package com.hcommerce.heecommerce.deal.dto;

import java.beans.ConstructorProperties;
import lombok.Getter;

@Getter
public class InitTestDealPrdouctsDto {
    private final int month;

    @ConstructorProperties({
        "month",
    })
    public InitTestDealPrdouctsDto(int month) {
        this.month = month;
    }
}
