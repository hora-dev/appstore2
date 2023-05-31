package com.newfold.appstore2.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OrderDto {
    List<OrderLineDto> orderLineDtoList;
    String customerName;
    String customerAddress;
    String email;
}
