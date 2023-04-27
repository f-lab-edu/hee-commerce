package com.hcommerce.heecommerce.order;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CompleteOrderReceiptResponse {
    UUID order_uuid;
}
