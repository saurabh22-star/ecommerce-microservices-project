package com.ecommerce.ecommercehub.productmodule.services;

import com.ecommerce.ecommercehub.productmodule.dtos.ProductResponseDTO;
import com.ecommerce.ecommercehub.productmodule.entities.Cart;
import com.ecommerce.ecommercehub.productmodule.entities.Category;
import com.ecommerce.ecommercehub.productmodule.entities.Product;
import com.ecommerce.ecommercehub.productmodule.dtos.CartDTO;
import com.ecommerce.ecommercehub.productmodule.dtos.ProductDTO;
import com.ecommerce.ecommercehub.productmodule.repositories.CartRepo;
import com.ecommerce.ecommercehub.productmodule.repositories.CategoryRepo;
import com.ecommerce.ecommercehub.productmodule.repositories.ImageRepo;
import com.ecommerce.ecommercehub.productmodule.repositories.ProductRepo;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.modelmapper.ModelMapper;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import com.ecommerce.ecommercehub.productmodule.exceptions.APIException;
import com.ecommerce.ecommercehub.productmodule.exceptions.ResourceNotFoundException;

@Transactional
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private ModelMapper modelMapper;

    private ImageRepo imageRepo;

    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private CartRepo cartRepo;

    @Autowired
    private CartService cartService;

    @Autowired
    private FileService fileService;

    @Value("${project.image}")
    private String path;

    /*
     * @Override
     * public Product getProductById(Long id) {
     * return productRepo.findById(id)
     * .orElseThrow(()-> new ResourceNotFoundException("Product not found!"));
     * }
     */

    @Override
    public ProductResponseDTO getAllProducts(Integer pageNo, Integer pageSize, String sortBy, String sortOrder) {

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNo, pageSize, sortByAndOrder);

        Page<Product> pageProducts = productRepo.findAll(pageDetails);

        List<Product> products = pageProducts.getContent();

        return getProductResponseDTO(pageProducts, products);
    }

    @Override
    public ProductResponseDTO searchProductByCategory(Long catId, Integer pageIdx, Integer pageLimit, String sortField,
            String sortOrder) {

        Category foundCategory = categoryRepo.findById(catId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "ID", catId));

        Sort sort = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(pageIdx, pageLimit, sort);

        Page<Product> productPage = productRepo.findAll(pageable);

        List<Product> productList = productPage.getContent();

        if (productList.isEmpty()) {
            throw new APIException("No products available in category: " + foundCategory.getCategoryName());
        }

        return getProductResponseDTO(productPage, productList);
    }

    @Override
    public ProductResponseDTO searchProductByKeyword(String searchTerm, Integer pageIdx, Integer pageLimit,
            String sortField, String sortOrder) {
        Sort sort = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageIdx, pageLimit, sort);

        Page<Product> productPage = productRepo.searchByProductNameContaining(searchTerm, pageable);
        List<Product> productList = productPage.getContent();

        if (productList.isEmpty()) {
            throw new APIException("No products found for keyword: " + searchTerm);
        }

        return getProductResponseDTO(productPage, productList);
    }

    @Override
    public ProductDTO addProduct(Long catId, Product newProduct) {

        Category foundCategory = categoryRepo.findById(catId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "catId", catId));

        boolean uniqueProduct = true;

        List<Product> existingProducts = foundCategory.getProductList();

        for (Product item : existingProducts) {
            if (item.getProductName().equals(newProduct.getProductName())
                    && item.getDescription().equals(newProduct.getDescription())) {
                uniqueProduct = false;
                break;
            }
        }

        if (uniqueProduct) {
            newProduct.setImage("default.png");
            newProduct.setCategory(foundCategory);

            double calculatedSpecialPrice = newProduct.getPrice()
                    - ((newProduct.getDiscount() * 0.01) * newProduct.getPrice());
            newProduct.setSpecialPrice(calculatedSpecialPrice);

            Product persistedProduct = productRepo.save(newProduct);

            return modelMapper.map(persistedProduct, ProductDTO.class);
        } else {
            throw new APIException("A product with the same name and description already exists.");
        }
    }

    @Override
    public ProductDTO updateProduct(Long prodId, Product updatedProduct) {
        Product dbProduct = productRepo.findById(prodId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "prodId", prodId));

        if (dbProduct == null) {
            throw new APIException("No product found for id: " + prodId);
        }

        updatedProduct.setImage(dbProduct.getImage());
        updatedProduct.setProductId(prodId);
        updatedProduct.setCategory(dbProduct.getCategory());

        double calcSpecialPrice = updatedProduct.getPrice()
                - ((updatedProduct.getDiscount() * 0.01) * updatedProduct.getPrice());
        updatedProduct.setSpecialPrice(calcSpecialPrice);

        Product persistedProduct = productRepo.save(updatedProduct);

        List<Cart> relatedCarts = cartRepo.findCartsByProductId(prodId);

        List<CartDTO> cartDTOList = relatedCarts.stream().map(cart -> {
            CartDTO dto = modelMapper.map(cart, CartDTO.class);

            List<ProductDTO> prodList = cart.getCartItems().stream()
                    .map(item -> modelMapper.map(item.getProduct(), ProductDTO.class))
                    .collect(Collectors.toList());

            dto.setProducts(prodList);

            return dto;
        }).toList();

        cartDTOList.forEach(cart -> cartService.updateProductInCarts(cart.getCartId(), prodId));

        return modelMapper.map(persistedProduct, ProductDTO.class);
    }

    @Override
    public ProductDTO updateProductImage(Long prodId, MultipartFile newImage) throws IOException {
        Product existingProduct = productRepo.findById(prodId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "prodId", prodId));

        if (existingProduct == null) {
            throw new APIException("No product found for id: " + prodId);
        }

        String uploadedFileName = fileService.uploadImage(path, newImage);

        existingProduct.setImage(uploadedFileName);

        Product savedProduct = productRepo.save(existingProduct);

        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    @Override
    public String deleteProduct(Long prodId) {

        Product foundProduct = productRepo.findById(prodId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "prodId", prodId));

        List<Cart> relatedCarts = cartRepo.findCartsByProductId(prodId);

        relatedCarts.forEach(cart -> cartService.removeProductFromCart(cart.getCartId(), prodId));

        productRepo.delete(foundProduct);

        return "Product with ID: " + prodId + " has been removed successfully.";
    }

    private ProductResponseDTO getProductResponseDTO(Page<Product> pageProducts, List<Product> products) {
        List<ProductDTO> productDTOs = products.stream().map(p -> modelMapper.map(p, ProductDTO.class))
                .collect(Collectors.toList());

        ProductResponseDTO productResponseDTO = new ProductResponseDTO();

        productResponseDTO.setContent(productDTOs);
        productResponseDTO.setPageNumber(pageProducts.getNumber());
        productResponseDTO.setPageSize(pageProducts.getSize());
        productResponseDTO.setTotalElements(pageProducts.getTotalElements());
        productResponseDTO.setTotalPages(pageProducts.getTotalPages());
        productResponseDTO.setLastPage(pageProducts.isLast());

        return productResponseDTO;
    }
/* 
    @Override
    public List<ProductDto> getConvertedProducts(List<Product> products) {
        return products.stream().map(this::convertToDto).toList();
    }

    @Override
    public ProductDto convertToDto(Product product) {
        ProductDto productDto = modelMapper.map(product, ProductDto.class);
        List<Image> images = imageRepo.findByProductId(product.getId());
        List<ProductImageDto> imageDtoList = images.stream()
                .map(image -> modelMapper.map(image, ProductImageDto.class))
                .toList();
        productDto.setImageList(imageDtoList);
        return productDto;
    }

    private Product updateExistingProduct(Product existingProduct, ProductRequestDto request) {
        existingProduct.setTitle(request.getTitle());
        existingProduct.setManufacturer(request.getManufacturer());
        existingProduct.setCost(request.getCost());
        existingProduct.setStockCount(request.getStockCount());
        existingProduct.setDetails(request.getDetails());

        Category category = categoryRepo.findByTitle(request.getCategoryRef().getTitle());
        existingProduct.setCategoryRef(category);
        return existingProduct;

    }

    private Product createProduct(ProductRequestDto request, Category category) {
        return new Product(
                request.getTitle(),
                request.getManufacturer(),
                request.getCost(),
                request.getStockCount(),
                request.getDetails(),
                category);

    }

    private boolean productExists(String title, String manufacturer) {

        return productRepo.existsByTitleAndManufacturer(title, manufacturer);

    }

    @Override
    public void deleteProductById(Long id) {
        Optional<Product> productOpt = productRepo.findById(id);
        if (productOpt.isPresent()) {
            productRepo.delete(productOpt.get());
        } else {
            throw new ResourceNotFoundException("Product not found!");
        }
    }

    @Override
    public List<Product> getProductsByManufacturerAndTitle(String manufacturer, String title) {
        return productRepo.findByManufacturerAndTitle(manufacturer, title);
    }

    @Override
    public List<Product> getProductsByCategoryRefAndManufacturer(String categoryRef, String manufacturer) {
        // return productRepo.findByCategoryRefAndManufacturer(categoryRef,
        // manufacturer);
        return null;
    }

    @Override
    public List<Product> getProductsByTitle(String title) {
        return productRepo.findByTitle(title);
    }

    @Override
    public List<Product> getProductsByManufacturer(String manufacturer) {
        return productRepo.findByManufacturer(manufacturer);
    }

    @Override
    public List<Product> getProductsByCategoryRef(String category) {
        return null;
        // return productRepo.findByCategoryRef(category);
    }

    @Override
    public Long countProductsByManufacturerAndTitle(String manufacturer, String title) {
        return productRepo.countProductsByManufacturerAndTitle(manufacturer, title);
    } */

}
