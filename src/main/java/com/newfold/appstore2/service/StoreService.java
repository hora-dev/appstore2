package com.newfold.appstore2.service;

import com.newfold.appstore2.entities.Order;
import com.newfold.appstore2.exception.OrderNotFoundException;
import com.newfold.appstore2.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StoreService {

    @Autowired
    OrderRepository orderRepository;
    public Order.Status getOrderStatus(Long id) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if(optionalOrder.isPresent()) {
            return optionalOrder.get().getStatus();
        }
        throw new OrderNotFoundException("Order not found");
    }
}
