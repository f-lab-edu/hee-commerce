package com.hcommerce.heecommerce.inventory.mapper;

import com.hcommerce.heecommerce.inventory.entity.InventoryEventHistoryEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface InventoryEventHistoryMapper {

    void save(InventoryEventHistoryEntity inventoryEventHistoryEntity);
}
