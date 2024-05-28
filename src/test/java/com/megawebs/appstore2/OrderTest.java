package com.megawebs.appstore2;


import com.megawebs.appstore2.dto.OrderDto;
import com.megawebs.appstore2.dto.OrderLineDto;
import com.megawebs.appstore2.dto.OrderRequestDto;
import com.megawebs.appstore2.dto.ProductOrderLineDto;
import com.megawebs.appstore2.dto.ProductResponseDto;
import com.megawebs.appstore2.entities.Order;
import com.megawebs.appstore2.entities.OrderLine;
import com.megawebs.appstore2.entities.Product;
import com.megawebs.appstore2.exception.ErrorCreatingOrderException;
import com.megawebs.appstore2.exception.OrderNotFoundException;
import com.megawebs.appstore2.exception.OrderStatusException;
import com.megawebs.appstore2.repositories.OrderLineRepository;
import com.megawebs.appstore2.repositories.OrderRepository;
import com.megawebs.appstore2.repositories.ProductRepository;
import com.megawebs.appstore2.service.StoreService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.Silent.class)
public class OrderTest {

    @Mock
    OrderRepository orderRepository;

    @Mock
    ProductRepository productRepository;

    @Mock
    OrderLineRepository orderLineRepository;

    @InjectMocks
    StoreService storeService;

    private Order order;
    private Product product;
    private OrderLine orderLine;

    @Before
    public void setUp() {
        product = Product.builder()
                .id(1L)
                .description("Test Product")
                .price(100.0)
                .stock(50L)
                .build();

        orderLine = OrderLine.builder()
                .id(1L)
                .product(product)
                .quantity(5)
                .build();

        List<OrderLine> orderLineList = new ArrayList<>();
        orderLineList.add(orderLine);

        order = Order.builder()
                .id(1L)
                .customerName("Test Customer")
                .customerAddress("123 Test St")
                .customerEmail("test@example.com")
                .status(Order.Status.CREATED)
                .orderLineList(orderLineList)
                .build();
    }

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
                        .build()));

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
                                .build())).build());
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
                .orderLineDtoList(orderLineList).build());
        OrderDto order = storeService.getOrder(idNewOrder);
        Assertions.assertEquals(1L, idNewOrder);
        Assertions.assertEquals("53 Filomena Street", order.getCustomerAddress());
        Assertions.assertEquals("Jeff Bridges", order.getCustomerName());
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

    @Test
    public void testGetOrderStatus_OrderExists() {
        Mockito.when(orderRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(order));

        Order.Status status = storeService.getOrderStatus(1L);

        Assertions.assertEquals(Order.Status.CREATED, status);
        Mockito.verify(orderRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    public void testGetOrderStatus_OrderNotFound() {
        Mockito.when(orderRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(OrderNotFoundException.class, () -> storeService.getOrderStatus(1L));
        Mockito.verify(orderRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    public void testCreateOrder_Success() {
        OrderRequestDto orderRequestDto = OrderRequestDto.builder().build();
        orderRequestDto.setCustomerName("Test Customer");
        orderRequestDto.setCustomerAddress("123 Test St");
        orderRequestDto.setCustomerEmail("test@example.com");

        ProductOrderLineDto productOrderLineDto = ProductOrderLineDto.builder()
                .id(1L)
                .description("Test Product")
                .price(100.0)
                .build();

        OrderLineDto orderLineDto = OrderLineDto.builder()
                .productDto(productOrderLineDto)
                .quantity(5)
                .build();

        List<OrderLineDto> orderLineDtoList = new ArrayList<>();
        orderLineDtoList.add(orderLineDto);
        orderRequestDto.setOrderLineDtoList(orderLineDtoList);

        Mockito.when(orderRepository.save(Mockito.any(Order.class))).thenReturn(order);
        Mockito.when(productRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(product));
        Mockito.when(orderLineRepository.save(Mockito.any(OrderLine.class))).thenReturn(orderLine);

        Long orderId = storeService.createOrder(orderRequestDto);

        assertEquals(order.getId(), orderId);
        Mockito.verify(orderRepository, Mockito.times(2)).save(Mockito.any(Order.class));
        Mockito.verify(productRepository, Mockito.times(1)).findById(Mockito.anyLong());
        Mockito.verify(orderLineRepository, Mockito.times(1)).save(Mockito.any(OrderLine.class));
        Mockito.verify(productRepository, Mockito.times(1)).save(Mockito.any(Product.class));
    }

    @Test
    public void testCreateOrder_InsufficientStock() {
        OrderRequestDto orderRequestDto = OrderRequestDto.builder().build();
        orderRequestDto.setCustomerName("Test Customer");
        orderRequestDto.setCustomerAddress("123 Test St");
        orderRequestDto.setCustomerEmail("test@example.com");

        ProductOrderLineDto productOrderLineDto = ProductOrderLineDto.builder()
                .id(1L)
                .description("Test Product")
                .price(100.0)
                .build();

        OrderLineDto orderLineDto = OrderLineDto.builder()
                .productDto(productOrderLineDto)
                .quantity(55)
                .build();

        List<OrderLineDto> orderLineDtoList = new ArrayList<>();
        orderLineDtoList.add(orderLineDto);
        orderRequestDto.setOrderLineDtoList(orderLineDtoList);

        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));

        assertThrows(ErrorCreatingOrderException.class, () -> storeService.createOrder(orderRequestDto));

        verify(orderRepository, times(1)).save(any(Order.class));
        verify(productRepository, times(1)).findById(anyLong());
    }

    @Test
    public void testCancelOrder_Success() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        String response = storeService.cancelOrder(1L);

        assertEquals("Order status id: 1 is now cancelled", response);
        assertEquals(Order.Status.CANCELED, order.getStatus());
        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    public void testCancelOrder_AlreadyCanceled() {
        order.setStatus(Order.Status.CANCELED);
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));

        assertThrows(OrderStatusException.class, () -> storeService.cancelOrder(1L));

        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    public void testCancelOrder_OrderNotFound() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> storeService.cancelOrder(1L));

        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    public void testUpdateOrder() {
        // Mocking data
        OrderRequestDto orderRequestDto = OrderRequestDto.builder()
                .id(1L)
                .orderLineDtoList(Collections.singletonList(OrderLineDto.builder()
                        .id(1L)
                        .quantity(5)
                        .build()))
                .build();

        Order order = Order.builder()
                .id(1L)
                .customerName("John Doe")
                .customerAddress("123 Main St")
                .customerEmail("john@example.com")
                .orderLineList(Collections.singletonList(OrderLine.builder()
                        .id(1L)
                        .product(Product.builder().description("Product A").stock(10L).build()) // Product with 10 stock
                        .quantity(3)
                        .build()))
                .status(Order.Status.CREATED)
                .build();

        // Mocking repository behavior
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderLineRepository.findById(1L)).thenReturn(Optional.of(order.getOrderLineList().get(0)));

        // Calling the method under test
        OrderDto updatedOrderDto = storeService.updateOrder(orderRequestDto);

        // Verifying the result
        assertEquals(Long.valueOf(1), updatedOrderDto.getId());
        assertEquals("John Doe", updatedOrderDto.getCustomerName());
        assertEquals("123 Main St", updatedOrderDto.getCustomerAddress());
        assertEquals("john@example.com", updatedOrderDto.getCustomerEmail());
        assertEquals(5, updatedOrderDto.getOrderLineDtoList().get(0).getQuantity());

        // Verifying repository interactions
        verify(orderRepository, times(1)).findById(1L);
        verify(orderLineRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(order);
        verify(orderLineRepository, times(1)).save(order.getOrderLineList().get(0));
    }

    @Test
    public void testGetOrder() {
        // Mocking data
        long orderId = 1L;
        Order order = Order.builder()
                .id(orderId)
                .customerName("John Doe")
                .customerAddress("123 Main St")
                .customerEmail("john@example.com")
                .status(Order.Status.CREATED)
                .build();

        List<OrderLine> orderLineList = new ArrayList<>();
        Product product = Product.builder()
                .build();
        OrderLine orderLine = OrderLine.builder()
                .id(1L)
                .product(product)
                .quantity(1)
                .order(order)
                .build();
        orderLineList.add(orderLine);

        order.setOrderLineList(orderLineList);

        // Mocking repository behavior
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderLineRepository.findById(orderId)).thenReturn((Optional.of(OrderLine.builder().build())));


        // Calling the method under test
        OrderDto retrievedOrderDto = storeService.getOrder(orderId);

        // Verifying the result
        assertEquals(Long.valueOf(1), retrievedOrderDto.getId());
        assertEquals("John Doe", retrievedOrderDto.getCustomerName());
        assertEquals("123 Main St", retrievedOrderDto.getCustomerAddress());
        assertEquals("john@example.com", retrievedOrderDto.getCustomerEmail());

        // Verifying repository interaction
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test(expected = OrderNotFoundException.class)
    public void testGetOrder_OrderNotFound() {
        // Mocking data
        long orderId = 1L;

        // Mocking repository behavior
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Calling the method under test
        storeService.getOrder(orderId);
    }

    @Test
    public void testGetAllOrders() {
        // Mocking data
        Order order = Order.builder()
                .id(1L)
                .customerName("John Doe")
                .customerAddress("123 Main St")
                .customerEmail("john@example.com")
                .status(Order.Status.CREATED)
                .build();

        List<OrderLine> orderLineList = new ArrayList<>();
        Product product = Product.builder()
                .build();
        OrderLine orderLine = OrderLine.builder()
                .id(1L)
                .product(product)
                .quantity(1)
                .order(order)
                .build();
        orderLineList.add(orderLine);

        order.setOrderLineList(orderLineList);

        // Mocking repository behavior
        when(orderRepository.findAll()).thenReturn(List.of(order));

        // Calling the method under test
        List<OrderDto> retrievedOrderDtoList = storeService.getAllOrders();

        // Verifying the result
        assertEquals(Long.valueOf(1), retrievedOrderDtoList.get(0).getId());
        assertEquals("John Doe", retrievedOrderDtoList.get(0).getCustomerName());
        assertEquals("123 Main St", retrievedOrderDtoList.get(0).getCustomerAddress());
    }

    @Test
    public void testGetProducts() {
        // Mocking data
        Product product1 = Product.builder().id(1L).description("Product A").stock(10L).build();
        Product product2 = Product.builder().id(2L).description("Product B").stock(20L).build();
        List<Product> productList = Arrays.asList(product1, product2);

        // Mocking repository behavior
        when(productRepository.findAll()).thenReturn(productList);

        // Calling the method under test
        List<ProductResponseDto> retrievedProducts = storeService.getProducts();

        // Verifying the result
        assertEquals(2, retrievedProducts.size());

        ProductResponseDto retrievedProduct1 = retrievedProducts.get(0);
        assertEquals(product1.getId(), retrievedProduct1.getId());
        assertEquals(product1.getDescription(), retrievedProduct1.getDescription());
        assertEquals(product1.getStock(), retrievedProduct1.getStock());

        ProductResponseDto retrievedProduct2 = retrievedProducts.get(1);
        assertEquals(product2.getId(), retrievedProduct2.getId());
        assertEquals(product2.getDescription(), retrievedProduct2.getDescription());
        assertEquals(product2.getStock(), retrievedProduct2.getStock());

        // Verifying repository interaction
        verify(productRepository, times(1)).findAll();
    }

    @Test
    public void testUpdateOrder_OrderNotFound() {
        // Mocking data
        OrderRequestDto orderRequestDto = OrderRequestDto.builder()
                .id(1L)
                .build();

        // Mocking repository behavior
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        // Verifying that OrderNotFoundException is thrown
        assertThrows(OrderNotFoundException.class, () -> storeService.updateOrder(orderRequestDto));

        // Verifying repository interaction
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    public void testUpdateOrder_ProductOutOfStock() {
        // Mocking data
        long orderId = 1L;
        long orderLineId = 1L;
        long productId = 1L;

        Product product = Product.builder().id(productId).stock(5L).build();
        OrderLine orderLine = OrderLine.builder().id(orderLineId).product(product).quantity(5).build();
        Order order = Order.builder().id(orderId).build();
        order.setOrderLineList(List.of(orderLine));

        OrderRequestDto orderRequestDto = OrderRequestDto.builder()
                .id(orderId)
                .orderLineDtoList(Arrays.asList(OrderLineDto.builder().id(orderLineId).quantity(10).build()))
                .build();

        // Mocking repository behavior
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderLineRepository.findById(orderLineId)).thenReturn(Optional.of(orderLine));

        // Verifying that ErrorCreatingOrderException is thrown
        assertThrows(ErrorCreatingOrderException.class, () -> storeService.updateOrder(orderRequestDto));

        // Verifying repository interactions
        verify(orderRepository, times(1)).findById(orderId);
        verify(orderLineRepository, times(1)).findById(orderLineId);
    }
}
