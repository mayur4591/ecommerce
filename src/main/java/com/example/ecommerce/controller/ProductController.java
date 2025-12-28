package com.example.ecommerce.controller;

import com.example.ecommerce.entity.Product;
import com.example.ecommerce.exception.ProductException;
import com.example.ecommerce.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Slf4j
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/products")
    public ResponseEntity<Page<Product>> findProductByCategoryHandler(
            @RequestParam String category,
            @RequestParam List<String> color,
            @RequestParam List<String> size,
            @RequestParam Integer minPrice,
            @RequestParam Integer maxPrice,
            @RequestParam Integer minDiscount,
            @RequestParam String sort,
            @RequestParam String stock,
            @RequestParam Integer pageNumber,
            @RequestParam Integer pageSize) {

        log.info("Fetch products request received with filters: category={}, minPrice={}, maxPrice={}, sort={}, stock={}, pageNumber={}, pageSize={}",
                category, minPrice, maxPrice, sort, stock, pageNumber, pageSize);

        Page<Product> res = productService.getAllProduct(
                category, color, size, minPrice, maxPrice,
                minDiscount, sort, stock, pageNumber, pageSize);

        log.info("Products fetched successfully. Total elements={}", res.getTotalElements());

        return new ResponseEntity<>(res, HttpStatus.ACCEPTED);
    }

    @GetMapping("/products/all")
    public ResponseEntity<Page<Product>> getAllProductsHandler(
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "10") Integer pageSize) {

        log.info("Fetch all products request received. pageNumber={}, pageSize={}",
                pageNumber, pageSize);

        Page<Product> products = productService.getAllProduct(pageNumber, pageSize);

        log.info("All products fetched successfully. Total elements={}",
                products.getTotalElements());

        return new ResponseEntity<>(products, HttpStatus.ACCEPTED);
    }

    @GetMapping("/products/id/{productId}")
    public ResponseEntity<Product> findProductByIdHandler(
            @PathVariable Long productId) throws ProductException {

        log.info("Fetch product by id request received. productId={}", productId);

        Product product = productService.findProductById(productId);

        log.info("Product fetched successfully for productId={}", productId);

        return new ResponseEntity<Product>(product, HttpStatus.ACCEPTED);
    }
}
