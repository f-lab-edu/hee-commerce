package com.hcommerce.heecommerce.order.dto;

import java.util.UUID;
import lombok.Getter;

@Getter
public class OrderUuid {

    private final UUID orderUuid;

    public OrderUuid(UUID orderUuid) {
        this.orderUuid = orderUuid;
    }
}
