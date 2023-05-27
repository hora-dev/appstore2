package com.newfold.appstore2.web;

import com.newfold.appstore2.dto.StatusDto;
import com.newfold.appstore2.entities.Order;
import com.newfold.appstore2.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/store")
public class StoreController {

    @Autowired
    StoreService storeService;

    @GetMapping("order/status/")
    public ResponseEntity<Order.Status> getOrderStatus(@PathVariable( name = "id" ) Long id) {
        return ResponseEntity.ok(storeService.getOrderStatus(id));
    }
}
