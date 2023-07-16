package com.hcommerce.heecommerce.order.mapper;

import com.hcommerce.heecommerce.order.entity.OrderApproveEntity;
import com.hcommerce.heecommerce.order.entity.OrderFormSavedInAdvanceEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OrderCommandMapper {

    void saveOrderInAdvance(OrderFormSavedInAdvanceEntity orderFormSavedInAdvanceEntity);

    void updateOrderAfterApprove(@Param("orderApproveEntity") OrderApproveEntity orderApproveEntity);
}
