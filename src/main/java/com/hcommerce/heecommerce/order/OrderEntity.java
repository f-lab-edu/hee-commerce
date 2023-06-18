package com.hcommerce.heecommerce.order;

import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderEntity {

  private final byte[] uuid;
  private final int userId;
  private final String recipientName;
  private final String recipientPhoneNumber;
  private final String recipientAddress;
  private final String recipientDetailAddress;
  private final String shippingRequest;
  private final OutOfStockHandlingOption outOfStockHandlingOption;
  private final byte[] dealProductUuid;
  private final int orderQuantity;
  private final byte[] paymentUuid;
  private final OrderStatus orderStatus;

  @Builder
  public OrderEntity(
      byte[] uuid,
      int userId,
      String recipientName,
      String recipientPhoneNumber,
      String recipientAddress,
      String recipientDetailAddress,
      String shippingRequest,
      OutOfStockHandlingOption outOfStockHandlingOption,
      byte[] dealProductUuid,
      int orderQuantity,
      byte[] paymentUuid,
      OrderStatus orderStatus
  ) {
    this.uuid = uuid;
    this.userId = userId;
    this.recipientName = recipientName;
    this.recipientPhoneNumber = recipientPhoneNumber;
    this.recipientAddress = recipientAddress;
    this.recipientDetailAddress = recipientDetailAddress;
    this.shippingRequest = shippingRequest;
    this.outOfStockHandlingOption = outOfStockHandlingOption;
    this.dealProductUuid = dealProductUuid;
    this.orderQuantity = orderQuantity;
    this.paymentUuid = paymentUuid;
    this.orderStatus = orderStatus;
  }
}
