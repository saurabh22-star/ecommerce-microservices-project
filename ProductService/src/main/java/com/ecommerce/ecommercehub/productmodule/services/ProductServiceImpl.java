package com.ecommerce.ecommercehub.productmodule.services;

import com.ecommerce.ecommercehub.productmodule.dtos.ProductDto;
import com.ecommerce.ecommercehub.productmodule.dtos.ProductImageDto;
import com.ecommerce.ecommercehub.productmodule.models.Image;
import com.ecommerce.ecommercehub.productmodule.models.Product;
import com.ecommerce.ecommercehub.productmodule.repositories.ImageRepo;
import com.ecommerce.ecommercehub.productmodule.repositories.ProductRepo;
import com.ecommerce.ecommercehub.utility.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;


import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepo productRepo;
    private final ModelMapper modelMapper;
    private final ImageRepo imageRepo;

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
        List<ProductImageDto> imageDtos = images.stream()
                .map(image -> modelMapper.map(image, ProductImageDto.class))
                .toList();
        productDto.setImageList(imageDtos);
        return productDto;
    }
}
