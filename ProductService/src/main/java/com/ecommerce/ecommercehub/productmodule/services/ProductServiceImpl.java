package com.ecommerce.ecommercehub.productmodule.services;

import com.ecommerce.ecommercehub.productmodule.dtos.ProductRequestDto;
import com.ecommerce.ecommercehub.productmodule.dtos.ProductResponseDTO;
import com.ecommerce.ecommercehub.productmodule.entities.Category;
import com.ecommerce.ecommercehub.productmodule.entities.Image;
import com.ecommerce.ecommercehub.productmodule.entities.Product;
import com.ecommerce.ecommercehub.productmodule.dtos.ProductDTO;
import com.ecommerce.ecommercehub.productmodule.dtos.ProductImageDto;
import com.ecommerce.ecommercehub.productmodule.repositories.CategoryRepo;
import com.ecommerce.ecommercehub.productmodule.repositories.ImageRepo;
import com.ecommerce.ecommercehub.productmodule.repositories.ProductRepo;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.ecommerce.ecommercehub.productmodule.exceptions.APIException;
import com.ecommerce.ecommercehub.productmodule.exceptions.DuplicateResourceException;
import com.ecommerce.ecommercehub.productmodule.exceptions.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    
    private final ProductRepo productRepo;
    private final ModelMapper modelMapper;
    private final ImageRepo imageRepo;
    private final CategoryRepo categoryRepo;

/*     @Override
    public Product getProductById(Long id) {
        return productRepo.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Product not found!"));
    } */

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
    public ProductResponseDTO searchProductByCategory(Long catId, Integer pageIdx, Integer pageLimit, String sortField, String sortOrder) {

    Category foundCategory = categoryRepo.findById(catId)
            .orElseThrow(() -> new ResourceNotFoundException("Category", "ID", catId));

    Sort sort = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();

    Pageable pageable = PageRequest.of(pageIdx, pageLimit, sort);

    Page<Product> productPage = productRepo.findAll(pageable);

    List<Product> productList = productPage.getContent();

    if (productList.isEmpty()) {
        throw new APIException("No products available in category: " + foundCategory.getCategoryName());
    }

    return getProductResponseDTO(productPage, productList);
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

    @Override
    public Product addProduct(ProductRequestDto request) {
        if (productExists(request.getTitle(), request.getManufacturer())) {
            throw new DuplicateResourceException(
                String.format("Product '%s' by brand '%s' already exists. Please update the existing product instead.", request.getTitle(), request.getManufacturer())
            );
        }
        Category category = categoryRepo.findByTitle(request.getCategoryRef().getTitle());
        if (category == null) {
            category = new Category(request.getCategoryRef().getTitle());
            category = categoryRepo.save(category);
        }
        request.setCategoryRef(category);
        return productRepo.save(createProduct(request, category));
    }

    @Override
    public Product updateProduct(ProductRequestDto updateRequest, Long productId) {
        Optional<Product> productOptional = productRepo.findById(productId);
        if (productOptional.isEmpty()) {
            throw new ResourceNotFoundException("Product not found!");
        }
        Product productToUpdate = updateExistingProduct(productOptional.get(), updateRequest);
        return productRepo.save(productToUpdate);
    }

    private Product updateExistingProduct(Product existingProduct, ProductRequestDto request) {
        existingProduct.setTitle(request.getTitle());
        existingProduct.setManufacturer(request.getManufacturer());
        existingProduct.setCost(request.getCost());
        existingProduct.setStockCount(request.getStockCount());
        existingProduct.setDetails(request.getDetails());

        Category category = categoryRepo.findByTitle(request.getCategoryRef().getTitle());
        existingProduct.setCategoryRef(category);
        return  existingProduct;

    }

    private Product createProduct(ProductRequestDto request, Category category) {
           return new Product(
                    request.getTitle(),
                    request.getManufacturer(),
                    request.getCost(),
                    request.getStockCount(),
                    request.getDetails(),
                    category
            );


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
      //return productRepo.findByCategoryRefAndManufacturer(categoryRef, manufacturer);
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
        //return productRepo.findByCategoryRef(category);
    }

    @Override
    public Long countProductsByManufacturerAndTitle(String manufacturer, String title) {
        return productRepo.countProductsByManufacturerAndTitle(manufacturer, title);
    }


}
