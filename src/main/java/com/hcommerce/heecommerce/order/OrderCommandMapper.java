package com.hcommerce.heecommerce.order;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderCommandMapper {

    void saveOrderInAdvance(OrderFormSavedInAdvanceEntity orderFormSavedInAdvanceEntity);

    void updateOrderAfterApprove(byte[] orderUuid, OrderApproveEntity orderApproveEntity);
}
