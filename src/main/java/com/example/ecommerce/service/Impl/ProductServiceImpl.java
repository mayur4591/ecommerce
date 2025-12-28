package com.example.ecommerce.service.Impl;

import com.example.ecommerce.entity.Category;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.exception.ProductException;
import com.example.ecommerce.repository.CategoryRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.request.CreateProductRequest;
import com.example.ecommerce.service.ProductService;
import com.example.ecommerce.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Product createProduct(CreateProductRequest req) {

        log.info("Creating product with title='{}'", req.getTitle());

        Category topLevel = categoryRepository.findByName(req.getTopLevelCategory());

        if (topLevel == null) {
            log.info("Top-level category '{}' not found. Creating new category",
                    req.getTopLevelCategory());

            Category topLevelCategory = new Category();
            topLevelCategory.setName(req.getTopLevelCategory());
            topLevelCategory.setLevel(1);

            topLevel = categoryRepository.save(topLevelCategory);
            log.info("Top-level category created with id={}", topLevel.getId());
        }

        Category secondLevel = categoryRepository.findByNameAndParent(
                req.getSecondLevelCategory(), topLevel.getName());

        if (secondLevel == null) {
            log.info("Second-level category '{}' not found. Creating new category",
                    req.getSecondLevelCategory());

            Category secondLevelCatecory = new Category();
            secondLevelCatecory.setName(req.getSecondLevelCategory());
            secondLevelCatecory.setParentCategory(topLevel);
            secondLevelCatecory.setLevel(2);

            secondLevel = categoryRepository.save(secondLevelCatecory);
            log.info("Second-level category created with id={}", secondLevel.getId());
        }

        Category thirdLevel = categoryRepository.findByNameAndParent(
                req.getThirdLevelCategory(), secondLevel.getName());

        if (thirdLevel == null) {
            log.info("Third-level category '{}' not found. Creating new category",
                    req.getThirdLevelCategory());

            Category thirdLevelCategory = new Category();
            thirdLevelCategory.setName(req.getThirdLevelCategory());
            thirdLevelCategory.setParentCategory(secondLevel);
            thirdLevelCategory.setLevel(3);

            thirdLevel = categoryRepository.save(thirdLevelCategory);
            log.info("Third-level category created with id={}", thirdLevel.getId());
        }

        Product product = new Product();
        product.setTitle(req.getTitle());
        product.setColor(req.getColor());
        product.setDescription(req.getDescription());
        product.setDiscountedPrice(req.getDiscountedPrice());
        product.setDiscountPercent(req.getDiscountPersent());
        product.setImageUrl(req.getImageUrl());
        product.setBrand(req.getBrand());
        product.setPrice(req.getPrice());
        product.setSizes(req.getSize());
        product.setQuantity(req.getQuantity());
        product.setCategory(thirdLevel);
        product.setCreatedAt(LocalDateTime.now());

        Product savedProduct = productRepository.save(product);

        log.info("Product created successfully with productId={}", savedProduct.getId());

        return savedProduct;
    }

    @Override
    public String deleteProduct(Long productId) throws ProductException {

        log.info("Deleting product with productId={}", productId);

        Product product = findProductById(productId);
        product.getSizes().clear();
        productRepository.delete(product);

        log.info("Product deleted successfully with productId={}", productId);

        return "Product deleted Successfully";
    }

    @Override
    public Product updateProduct(Long productId, Product req) throws ProductException {

        log.info("Updating product with productId={}", productId);

        Product product = findProductById(productId);

        if (req.getQuantity() != 0) {
            log.debug("Updating quantity for productId={} to {}",
                    productId, req.getQuantity());
            product.setQuantity(req.getQuantity());
        }

        Product updatedProduct = productRepository.save(product);

        log.info("Product updated successfully with productId={}", productId);

        return updatedProduct;
    }

    @Override
    public Product findProductById(Long id) throws ProductException {

        log.debug("Finding product by productId={}", id);

        Optional<Product> opt = productRepository.findById(id);

        if (opt.isPresent()) {
            log.debug("Product found with productId={}", id);
            return opt.get();
        }

        log.warn("Product not found with productId={}", id);
        throw new ProductException("Product not found with id " + id);
    }

    @Override
    public List<Product> findProductByCategory(String category) {
        log.info("Finding products by category='{}'", category);
        return List.of();
    }

    @Override
    public Page<Product> getAllProduct(String category, List<String> colors, List<String> sizes,
                                       Integer minPrice, Integer maxPrice, Integer minDiscount,
                                       String sort, String stock, Integer pageNumber,
                                       Integer pageSize) {

        log.info("Fetching products | category={} | page={} | size={}",
                category, pageNumber, pageSize);

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        List<Product> products = productRepository
                .filterProducts(category, minPrice, maxPrice, minDiscount, sort);

        if (!colors.isEmpty()) {
            log.debug("Applying color filters: {}", colors);
            products = products.stream()
                    .filter(p -> colors.stream()
                            .anyMatch(c -> c.equalsIgnoreCase(p.getColor())))
                    .toList();
        }

        if (stock != null) {
            log.debug("Applying stock filter: {}", stock);

            if (stock.equals("in_stock")) {
                products = products.stream()
                        .filter(p -> p.getQuantity() > 0)
                        .toList();
            } else if (stock.equals("out_of_stock")) {
                products = products.stream()
                        .filter(p -> p.getQuantity() < 1)
                        .toList();
            }
        }

        int startIndex = (int) pageable.getOffset();
        int endIndex = Math.min(startIndex + pageable.getPageSize(), products.size());

        List<Product> pageContent = products.subList(startIndex, endIndex);

        Page<Product> filteredProducts =
                new PageImpl<>(pageContent, pageable, products.size());

        log.info("Products fetched successfully | totalElements={}",
                filteredProducts.getTotalElements());

        return filteredProducts;
    }

    @Override
    public Page<Product> getAllProduct(Integer pageNumber, Integer pageSize) {

        log.info("Fetching all products | page={} | size={}", pageNumber, pageSize);

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return productRepository.findAll(pageable);
    }

    @Override
    public List<Product> findAllProducts() {

        log.info("Fetching all products without pagination");
        return productRepository.findAll();
    }
}
