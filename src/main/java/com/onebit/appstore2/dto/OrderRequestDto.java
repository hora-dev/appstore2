package com.onebit.appstore2.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OrderRequestDto {
    Long id;
    List<OrderLineDto> orderLineDtoList;
    String customerName;
    String customerAddress;
    String customerEmail;
}
