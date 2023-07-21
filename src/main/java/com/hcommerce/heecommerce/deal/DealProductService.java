package com.hcommerce.heecommerce.deal;

import com.hcommerce.heecommerce.deal.dto.DealProductSummary;
import com.hcommerce.heecommerce.deal.dto.TimeDealProductDetail;
import com.hcommerce.heecommerce.deal.enums.DealType;
import com.hcommerce.heecommerce.product.ProductsSort;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DealProductService {

    private final DealProductQueryRepository dealProductQueryRepository;

    @Autowired
    public DealProductService(DealProductQueryRepository dealProductQueryRepository) {
        this.dealProductQueryRepository = dealProductQueryRepository;
    }

    public List<DealProductSummary> getDealProductsByDealType(DealType dealType, int pageNumber, ProductsSort sort) {

        List<DealProductSummary> dealProducts = dealProductQueryRepository.getDealProductsByDealType(dealType, pageNumber, sort);

        return dealProducts;
    }

    public TimeDealProductDetail getTimeDealProductDetailByDealProductUuid(UUID dealProductUuid) {

        TimeDealProductDetail timeDealProductDetail = dealProductQueryRepository.getTimeDealProductDetailByDealProductUuid(dealProductUuid);

        return timeDealProductDetail;
    }
}
