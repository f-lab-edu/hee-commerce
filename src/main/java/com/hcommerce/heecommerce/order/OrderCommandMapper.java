package com.hcommerce.heecommerce.order;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderCommandMapper {

    void save(OrderEntity orderEntity);
}
