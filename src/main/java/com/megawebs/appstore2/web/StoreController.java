package com.megawebs.appstore2.web;

import com.megawebs.appstore2.dto.OrderDto;
import com.megawebs.appstore2.dto.OrderRequestDto;
import com.megawebs.appstore2.dto.ProductResponseDto;
import com.megawebs.appstore2.entities.Order;
import com.megawebs.appstore2.service.StoreService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@ApiOperation("Store for NewFold")
@RequestMapping("/store")
public class StoreController {

    @Autowired
    StoreService storeService;

    @GetMapping("order/status/{id}")
    public ResponseEntity<Order.Status> getOrderStatus(@PathVariable Long id) {
        return ResponseEntity.ok(storeService.getOrderStatus(id));
    }

    @PostMapping("order")
    public ResponseEntity<Long> createOrder(@RequestBody OrderRequestDto orderRequestDto) {
        return ResponseEntity.ok(storeService.createOrder(orderRequestDto));
    }

    @GetMapping("stock")
    public ResponseEntity<List<ProductResponseDto>> getProducts() {
        return ResponseEntity.ok( storeService.getProducts());
    }

    @Operation(summary = "Get list of all orders")
    @GetMapping("orders")
    public ResponseEntity<List<OrderDto>> getOrders() {
        return ResponseEntity.ok( storeService.getAllOrders() );
    }

    @GetMapping("order/{id}")
    public ResponseEntity<OrderDto> getOrder(@PathVariable Long id) {
        return ResponseEntity.ok( storeService.getOrder(id) );
    }

    @PostMapping("order/cancel/{id}")
    public ResponseEntity<String> cancelOrder(@PathVariable Long id) {
        return ResponseEntity.ok(storeService.cancelOrder(id));
    }

    @PutMapping("order")
    public ResponseEntity<OrderDto> updateOrder(@RequestBody OrderRequestDto orderRequestDto) {
        return ResponseEntity.ok(storeService.updateOrder(orderRequestDto));
    }

}
