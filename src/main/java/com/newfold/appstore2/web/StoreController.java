package com.newfold.appstore2.web;

import com.newfold.appstore2.dto.OrderDto;
import com.newfold.appstore2.dto.OrderRequestDto;
import com.newfold.appstore2.dto.ProductResponseDto;
import com.newfold.appstore2.entities.Order;
import com.newfold.appstore2.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
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
