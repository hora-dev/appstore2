package com.newfold.appstore2.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductResponseDto {
    Long id;
    String description;
    Double price;
}
