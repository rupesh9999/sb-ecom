package com.ecommerce.project.service;

import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProductService {
     ProductDTO addProduct(Long categoryId, ProductDTO product);

     ProductResponse getAllProducts(int pageNo, int pageSize, String sortBy, String sortDir);

     ProductResponse searchByCategory(Long categoryId, int pageNo, int pageSize, String sortBy, String sortDir);

     ProductResponse searchProductByKeyword(String keyword, int pageNo, int pageSize, String sortBy, String sortDir);

     ProductDTO updateProduct(ProductDTO product, Long productId);

     ProductDTO deleteProduct(Long productId);

     ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException;
}
