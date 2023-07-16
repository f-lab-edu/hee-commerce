package com.hcommerce.heecommerce.order;

import com.hcommerce.heecommerce.common.utils.TypeConversionUtils;
import com.hcommerce.heecommerce.order.dto.OrderAfterApproveDto;
import com.hcommerce.heecommerce.order.entity.OrderApproveEntity;
import com.hcommerce.heecommerce.order.entity.OrderFormSavedInAdvanceEntity;
import com.hcommerce.heecommerce.order.mapper.OrderCommandMapper;
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

    public UUID updateOrderAfterApprove(OrderAfterApproveDto orderAfterApproveDto) {

        byte[] orderUuid = orderAfterApproveDto.getOrderUuid();

        OrderApproveEntity orderApproveEntity = OrderApproveEntity.builder()
            .orderUuid(orderUuid)
            .realOrderQuantity(orderAfterApproveDto.getRealOrderQuantity())
            .paymentApprovedAt(orderAfterApproveDto.getPaymentApprovedAt())
            .build();

        orderCommandMapper.updateOrderAfterApprove(orderApproveEntity);

        return TypeConversionUtils.convertBinaryToUuid(orderUuid);
    }
}

