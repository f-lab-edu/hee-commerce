package com.hcommerce.heecommerce.order;

import com.hcommerce.heecommerce.common.utils.TypeConversionUtils;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class OrderCommandRepository {

    private final OrderCommandMapper orderCommandMapper;

    @Autowired
    public OrderCommandRepository(OrderCommandMapper orderCommandMapper) {
        this.orderCommandMapper = orderCommandMapper;
    }

    public UUID saveOrderInAdvance(OrderFormSavedInAdvanceEntity orderFormSavedInAdvanceEntity) {
        orderCommandMapper.saveOrderInAdvance(orderFormSavedInAdvanceEntity);

        return TypeConversionUtils.convertBinaryToUuid(orderFormSavedInAdvanceEntity.getUuid());
    }

    public UUID updateOrderAfterApprove(byte[] orderUuid, OrderApproveEntity orderApproveEntity) {
        orderCommandMapper.updateOrderAfterApprove(orderUuid, orderApproveEntity);

        return TypeConversionUtils.convertBinaryToUuid(orderUuid);
    }
}

