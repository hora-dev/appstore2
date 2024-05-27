package com.megawebs.appstore2;


import com.megawebs.appstore2.dto.OrderDto;
import com.megawebs.appstore2.dto.OrderLineDto;
import com.megawebs.appstore2.dto.OrderRequestDto;
import com.megawebs.appstore2.dto.ProductOrderLineDto;
import com.megawebs.appstore2.entities.Order;
import com.megawebs.appstore2.entities.Product;
import com.megawebs.appstore2.exception.ErrorCreatingOrderException;
import com.megawebs.appstore2.repositories.OrderRepository;
import com.megawebs.appstore2.repositories.ProductRepository;
import com.megawebs.appstore2.service.StoreService;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.Silent.class)
public class OrderTest {

    @Mock
    OrderRepository orderRepository;

    @Mock
    ProductRepository productRepository;


    @InjectMocks
    StoreService storeService;

    @Test
    public void whenOrderStatusCreatedTest() {

        Order orderStatusCreated = Order.builder().status(Order.Status.CREATED).build();
        Mockito.when(orderRepository.findById(1L)).thenReturn(Optional.of(orderStatusCreated));

        Order.Status orderStatus = storeService.getOrderStatus(1L);
        Assertions.assertEquals(Order.Status.CREATED, orderStatus);
    }

    @Test
    public void whenOrderStatusCancelledTest() {

        Order orderStatusCreated = Order.builder().status(Order.Status.CANCELED).build();
        Mockito.when(orderRepository.findById(2L)).thenReturn(Optional.of(orderStatusCreated));

        Order.Status orderStatus = storeService.getOrderStatus(2L);
        Assertions.assertEquals(Order.Status.CANCELED, orderStatus);
    }

    @Test
    @Ignore
    public void whenCreatedOrderOk() {

        // set some initial stock
        Mockito.when(productRepository.findById(2L)).thenReturn(
                        Optional.of(Product.builder().id(2L)
                        .stock(3L)
                        .price(50D)
                        .description("Basket Ball")
                        .build()) );

        Order orderCreated = Order.builder()
                .id(1L)
                .customerAddress("53 Filomena Street")
                .customerName("Jeff Bridges")
                .customerEmail("jeffb@gmail.com")
                .build();
        Mockito.when(orderRepository.save(Mockito.any(Order.class))).thenReturn(orderCreated);

        Long idNewOrder = storeService.createOrder(OrderRequestDto.builder()
                .customerAddress("53 Filomena Street")
                .customerName("Jeff Bridges")
                .customerEmail("jeffb@gmail.com")
                .orderLineDtoList(
                        List.of(OrderLineDto.builder()
                                .productDto(
                                        ProductOrderLineDto
                                                .builder()
                                                .id(2L)
                                                .build())
                                .quantity(1L)
                                .build() )).build());
        Mockito.when(orderRepository.findById(idNewOrder)).thenReturn(Optional.of(orderCreated));


        List<OrderLineDto> orderLineList = List.of(OrderLineDto.builder()
                .productDto(
                        ProductOrderLineDto
                                .builder()
                                .id(2L)
                                .build())
                .quantity(1L)
                .build());

        Mockito.when(storeService.getOrder(idNewOrder)).thenReturn(OrderDto.builder()
                .customerAddress("53 Filomena Street")
                .customerName("Jeff Bridges")
                .customerEmail("jeffb@gmail.com")
                .orderLineDtoList(orderLineList ).build());
        OrderDto order = storeService.getOrder(idNewOrder);
        Assertions.assertEquals(1L, idNewOrder);
        Assertions.assertEquals( "53 Filomena Street", order.getCustomerAddress());
        Assertions.assertEquals( "Jeff Bridges", order.getCustomerName());
        Assertions.assertEquals("jeffb@gmail.com", order.getCustomerEmail());
    }

    @Test
    public void testCreateOrderWithNullCustomerName() {
        OrderRequestDto orderRequestDto = OrderRequestDto.builder()
               .customerName(null)
               .customerAddress("123 Main St")
               .customerEmail("test@example.com")
               .orderLineDtoList(List.of(createOrderLineDto()))
               .build();

        Assertions.assertThrows(ErrorCreatingOrderException.class, () -> storeService.createOrder(orderRequestDto));
    }

    private OrderLineDto createOrderLineDto() {
        return OrderLineDto.builder()
               .productDto(createProductDto())
               .quantity(1)
               .build();
    }

    private ProductOrderLineDto createProductDto() {
        return ProductOrderLineDto.builder()
               .id(1L)
               .description("Test Product")
               .price(10.0)
               .build();
    }
    @Test
    public void testCreateOrderWithEmptyCustomerName() {
        OrderRequestDto orderRequestDto = OrderRequestDto.builder()
               .customerName("")
               .customerAddress("123 Main St")
               .customerEmail("test@example.com")
               .orderLineDtoList(List.of(createOrderLineDto()))
               .build();

        Assertions.assertThrows(ErrorCreatingOrderException.class, () -> storeService.createOrder(orderRequestDto));
    }

    @Test
    public void testCreateOrderWithNullCustomerEmail() {
        OrderRequestDto orderRequestDto = OrderRequestDto.builder()
               .customerName("John Doe")
               .customerAddress("123 Main St")
               .customerEmail(null)
               .orderLineDtoList(List.of(createOrderLineDto()))
               .build();

        Assertions.assertThrows(ErrorCreatingOrderException.class, () -> storeService.createOrder(orderRequestDto));
    }
    @Test
    public void testCreateOrderWithEmptyCustomerAddress() {
        OrderRequestDto orderRequestDto = OrderRequestDto.builder()
               .customerName("John Doe")
               .customerAddress("")
               .customerEmail("test@example.com")
               .orderLineDtoList(List.of(createOrderLineDto()))
               .build();

        Assertions.assertThrows(ErrorCreatingOrderException.class, () -> storeService.createOrder(orderRequestDto));
    }

    @Test
    public void testCreateOrderWithNullCustomerAddress() {
        OrderRequestDto orderRequestDto = OrderRequestDto.builder()
               .customerName("John Doe")
               .customerAddress(null)
               .customerEmail("test@example.com")
               .orderLineDtoList(List.of(createOrderLineDto()))
               .build();

        Assertions.assertThrows(ErrorCreatingOrderException.class, () -> storeService.createOrder(orderRequestDto));
    }

}
