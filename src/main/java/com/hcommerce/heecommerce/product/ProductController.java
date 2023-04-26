package com.hcommerce.heecommerce.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

// src/docs/asciidoc/index.adoc
@RestController
@RequestMapping("products")
public class ProductController {

    private ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("{center_id}")
    public ResponseEntity<ProductDTO> getProductsByCenterId(
            @PathVariable("center_id") Long center_id
//            @RequestParam final Integer page,
//            @RequestParam final Integer size
            ) {
        List<Product> products = new ArrayList<>();
//        List<Product> products = productService.findProducts(center_id);

        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("상품1");
        product1.setQuantity(4L);
        products.add(product1);

        ProductDTO productDTO = new ProductDTO();
        productDTO.setProducts(products);
//        productDTO.setPage(0);
//        productDTO.setSize(20);

        return new ResponseEntity<>(productDTO, HttpStatus.OK);
    }

}

