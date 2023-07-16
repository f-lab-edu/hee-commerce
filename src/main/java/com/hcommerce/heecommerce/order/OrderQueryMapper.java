package com.hcommerce.heecommerce.order;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OrderQueryMapper {

    OrderForOrderApproveValidationEntity findOrderEntityForOrderApproveValidation(@Param("orderUuid") byte[] orderUuid);
}
