package com.hcommerce.heecommerce.order;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OrderCommandMapper {

    void saveOrderInAdvance(OrderFormSavedInAdvanceEntity orderFormSavedInAdvanceEntity);

    void updateOrderAfterApprove(@Param("orderApproveEntity") OrderApproveEntity orderApproveEntity);
}
