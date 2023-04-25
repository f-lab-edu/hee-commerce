package com.hcommerce.heecommerce.product;

import java.util.List;

public interface ProductRepository {
    List<Product> findByCenterId(Long center_id);
}
