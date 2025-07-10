package com.ecommerce.ecommercehub.productmodule.services;

import com.ecommerce.ecommercehub.productmodule.dtos.ProductRequestDto;
import com.ecommerce.ecommercehub.productmodule.dtos.ProductDto;
import com.ecommerce.ecommercehub.productmodule.dtos.ProductImageDto;
import com.ecommerce.ecommercehub.productmodule.exceptions.ProductAlreadyExistsException;
import com.ecommerce.ecommercehub.productmodule.models.Category;
import com.ecommerce.ecommercehub.productmodule.models.Image;
import com.ecommerce.ecommercehub.productmodule.models.Product;
import com.ecommerce.ecommercehub.productmodule.repositories.CategoryRepo;
import com.ecommerce.ecommercehub.productmodule.repositories.ImageRepo;
import com.ecommerce.ecommercehub.productmodule.repositories.ProductRepo;
import com.ecommerce.ecommercehub.utility.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;


import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    
    private final ProductRepo productRepo;
    private final ModelMapper modelMapper;
    private final ImageRepo imageRepo;
    private final CategoryRepo categoryRepo;

    @Override
    public Product getProductById(Long id) {
        return productRepo.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Product not found!"));
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepo.findAll();
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
            throw new ProductAlreadyExistsException(
                String.format("Product '%s' by brand '%s' already exists. Please update the existing product instead.", request.getTitle(), request.getManufacturer())
            );
        }
        Category category = categoryRepo.findByName(request.getCategoryRef().getTitle());
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

        Category category = categoryRepo.findByName(request.getCategoryRef().getTitle());
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
      return productRepo.findByCategoryRefAndManufacturer(categoryRef, manufacturer);
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
        return productRepo.findByCategoryRef(category);
    }

    @Override
    public Long countProductsByManufacturerAndTitle(String manufacturer, String title) {
        return productRepo.countProductsByManufacturerAndTitle(manufacturer, title);
    }


}
