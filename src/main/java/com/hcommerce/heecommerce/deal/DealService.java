package com.hcommerce.heecommerce.deal;

import com.hcommerce.heecommerce.product.ProductsSort;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DealService {

    private final DealQueryRepository dealQueryRepository;

    @Autowired
    public DealService(DealQueryRepository dealQueryRepository) {
        this.dealQueryRepository = dealQueryRepository;
    }

    public List<TimeDealProductSummary> getDealProductsByDealType(DealType dealType, int pageNumber, ProductsSort sort) {

        List<TimeDealProductSummary> dealProducts = dealQueryRepository.getDealProductsByDealType(dealType, pageNumber, sort);

        return dealProducts;
    }

    public TimeDealProductDetail getTimeDealProductDetailByDealProductUuid(UUID dealProductUuid) {

        TimeDealProductDetail timeDealProductDetail = dealQueryRepository.getTimeDealProductDetailByDealProductUuid(dealProductUuid);

        return timeDealProductDetail;
    }
}
