package com.hcommerce.heecommerce.order;

import com.hcommerce.heecommerce.order.entity.OrderForOrderApproveValidationEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OrderQueryMapper {

    OrderForOrderApproveValidationEntity findOrderEntityForOrderApproveValidation(@Param("orderUuid") byte[] orderUuid);
}
