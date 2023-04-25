package com.hcommerce.heecommerce.product;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class InMemoryProductRepository implements ProductRepository {
    private static Map<Long, Product> center1Store = new HashMap<>();
    private static Map<Long, Product> center2Store = new HashMap<>();


    private static long sequence = 0L;

    private Map<Long, Product> storeByCenterId(Long centerId) {
        if(centerId == 1) {
            return center1Store;
        }
        return center2Store;
    }

    public void init() {

        long centerId = 2L;

        Product product1 = new Product();
        product1.setId(++sequence);
        product1.setName("상품1");
        product1.setQuantity(4L);

        center1Store.put(centerId, product1);
    }

    @Override
    public List<Product> findByCenterId(Long center_id) {
        List<Product> products = new ArrayList<>();


        return products;
    }
}
