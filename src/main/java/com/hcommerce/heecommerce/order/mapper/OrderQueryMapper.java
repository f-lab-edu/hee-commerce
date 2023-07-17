package com.hcommerce.heecommerce.order.mapper;

import com.hcommerce.heecommerce.order.dto.OrderForOrderApproveValidationDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OrderQueryMapper {

    OrderForOrderApproveValidationDto findOrderEntityForOrderApproveValidation(@Param("orderUuid") byte[] orderUuid);
}
