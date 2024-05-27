package com.megawebs.appstore2.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderLineDto {
    long id;
    ProductOrderLineDto productDto;
    long quantity;
}
