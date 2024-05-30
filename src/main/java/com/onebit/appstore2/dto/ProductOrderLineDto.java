package com.onebit.appstore2.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductOrderLineDto {
    Long id;
    String description;
    Double price;
}
