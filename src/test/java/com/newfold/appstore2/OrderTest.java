package com.newfold.appstore2;


import com.newfold.appstore2.entities.Order;
import com.newfold.appstore2.repositories.OrderRepository;
import com.newfold.appstore2.service.StoreService;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

@RunWith(MockitoJUnitRunner.Silent.class)
public class OrderTest {

    @Mock
    OrderRepository orderRepository;

    @InjectMocks
    StoreService storeService;

    @Test
    public void whenOrderStatusCreatedTest() {

        Order orderStatusCreated = Order.builder().status(Order.Status.CREATED).build();
        Mockito.when(orderRepository.findById(1L)).thenReturn(Optional.of(orderStatusCreated));

        Order.Status orderStatus = storeService.getOrderStatus(1L);
        Assertions.assertEquals(Order.Status.CREATED, orderStatus);
    }
}
