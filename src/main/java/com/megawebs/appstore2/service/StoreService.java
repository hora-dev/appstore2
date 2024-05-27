package com.megawebs.appstore2.service;

import com.megawebs.appstore2.dto.*;
import com.megawebs.appstore2.entities.Order;
import com.megawebs.appstore2.entities.OrderLine;
import com.megawebs.appstore2.entities.Product;
import com.megawebs.appstore2.exception.ErrorCreatingOrderException;
import com.megawebs.appstore2.exception.OrderNotFoundException;
import com.megawebs.appstore2.exception.OrderStatusException;
import com.megawebs.appstore2.repositories.OrderLineRepository;
import com.megawebs.appstore2.repositories.OrderRepository;
import com.megawebs.appstore2.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StoreService {

    public static final String ORDER_ID = "Order id: ";
    public static final String NOT_FOUND = " not found";
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    OrderLineRepository orderLineRepository;

    public Order.Status getOrderStatus(Long id) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if(optionalOrder.isPresent()) {
            return optionalOrder.get().getStatus();
        }
        throw new OrderNotFoundException("Order not found");
    }

    public Long createOrder(OrderRequestDto orderRequestDto) {

        Order order = Order.builder()
                .customerName(orderRequestDto.getCustomerName())
                .customerAddress(orderRequestDto.getCustomerAddress())
                .customerEmail(orderRequestDto.getCustomerEmail())
                .status(Order.Status.CREATED)
                .orderLineList(new ArrayList<>())
                .build();
            Order orderCreated = orderRepository.save(order);

        List<OrderLine> orderLineList = new ArrayList<>();
        orderRequestDto.getOrderLineDtoList().forEach(orderLineDto -> addOrderLines(orderLineList, orderLineDto, order));
        order.setOrderLineList(orderLineList);
        orderRepository.save(order);

        return orderCreated.getId();
    }

    private void addOrderLines(List<OrderLine> orderLineList, OrderLineDto orderLineDto, Order order) {
        Optional<Product> productOptional = productRepository.findById(orderLineDto.getProductDto().getId());
        if(productOptional.isPresent()) {
            if( productOptional.get().getStock() < orderLineDto.getQuantity() ) {
                throw new ErrorCreatingOrderException("Error creating order, product stock is " + productOptional.get().getStock() +
                        " and attempting to order : " + orderLineDto.getQuantity() + " products.");
            }
            productOptional.get().setStock(  productOptional.get().getStock() - orderLineDto.getQuantity());
            productRepository.save(productOptional.get());
            OrderLine orderLine = OrderLine.builder()
                    .product( productOptional.get() )
                    .quantity(orderLineDto.getQuantity())
                    .order(order)
                    .build();
            orderLineRepository.save(orderLine);
            orderLineList.add(orderLine);

        } else {
            throw new ErrorCreatingOrderException("Error saving entity, productOptional id not in stock, id: " + orderLineDto.getProductDto());
        }
    }

    public List<ProductResponseDto> getProducts() {
        List<Product> productList = productRepository.findAll();
        List<ProductResponseDto> productResponseDtoList = new ArrayList<>();
        productList.forEach( product -> productResponseDtoList.add( ProductResponseDto.builder()
                .description(product.getDescription())
                .price(product.getPrice())
                .id(product.getId())
                .stock(product.getStock())
                .build()));
        return productResponseDtoList;
    }

    public List<OrderDto> getAllOrders() {

        List<Order> orderList = orderRepository.findAll();
        List<OrderDto> orderDtoList = new ArrayList<>();
        orderList.forEach( order -> orderDtoList.add(OrderDto.builder()
                .id(order.getId())
                .customerName(order.getCustomerName())
                .customerAddress(order.getCustomerAddress())
                .customerEmail(order.getCustomerEmail())
                .orderLineDtoList(transformOrderLineToOrderLineDto(order.getOrderLineList()))
                .status(order.getStatus().name())
                .build())
        );
        return orderDtoList;
    }

    private List<OrderLineDto> transformOrderLineToOrderLineDto(List<OrderLine> orderLineList) {
        List<OrderLineDto> orderLineDtoList = new ArrayList<>();
        orderLineList.forEach( orderLine ->  orderLineDtoList.add(
                                        OrderLineDto.builder()
                                            .productDto(ProductOrderLineDto.builder()
                                                    .id( orderLine.getProduct().getId() )
                                                    .description( orderLine.getProduct().getDescription())
                                                    .price( orderLine.getProduct().getPrice() ).build())
                                                .quantity(orderLine.getQuantity())
                                                .id(orderLine.getId())
                                                .build()
                ));
        return orderLineDtoList;
    }

    public String cancelOrder(Long id) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if(optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            if(!order.getStatus().equals(Order.Status.CANCELED) ) {
                order.setStatus(Order.Status.CANCELED);
                restoreProductsToStock(order);
                orderRepository.save(order);
                return "Order status id: " + id + " is now cancelled";
            } else {
                throw new OrderStatusException(ORDER_ID + id + " status is already canceled");
            }
        } else {
            throw new OrderNotFoundException(ORDER_ID + id + NOT_FOUND);
        }
    }

    private void restoreProductsToStock(Order order) {
        order.getOrderLineList().forEach( orderLine ->
                addStock(orderLine.getProduct(), orderLine.getQuantity()));
    }

    private void addStock(Product product, long quantity) {
        product.setStock( product.getStock() + quantity );
        productRepository.save(product);
    }

    public OrderDto updateOrder(OrderRequestDto orderRequestDto) {
        Optional<Order> optionalOrder = orderRepository.findById(orderRequestDto.getId());
        if(optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            List<OrderLineDto> changedOrderList = orderRequestDto.getOrderLineDtoList();
            if( null != changedOrderList && !changedOrderList.isEmpty()) {
                changedOrderList.forEach(this::updateOrderLine);
            }
            orderRepository.save(order);
            return OrderDto.builder()
                    .id(order.getId())
                    .customerName(order.getCustomerName())
                    .customerAddress(order.getCustomerAddress())
                    .customerEmail(order.getCustomerEmail())
                    .orderLineDtoList(transformOrderLineToOrderLineDto(order.getOrderLineList()))
                    .status(order.getStatus().name())
                    .build();
        } else {
            throw new OrderNotFoundException(ORDER_ID + orderRequestDto.getId() + NOT_FOUND);
        }
    }

    private void updateOrderLine(OrderLineDto orderLineDto) {
        Optional<OrderLine> optionalOrderLine = orderLineRepository.findById(orderLineDto.getId());
        if(optionalOrderLine.isPresent()) {
            OrderLine orderLine = optionalOrderLine.get();
            // check available stock before change order quantity
            if(orderLine.getProduct().getStock() < orderLineDto.getQuantity())
                throw new ErrorCreatingOrderException("Error updating order, product stock is " + orderLine.getProduct().getStock() +
                        " and attempting to order : " + orderLineDto.getQuantity() + " products.");

            if( orderLine.getQuantity() > orderLineDto.getQuantity()) {
                subtractStock(orderLine.getProduct(), orderLineDto.getQuantity());
            } else if (orderLine.getQuantity() < orderLineDto.getQuantity()) {
                addStock(orderLine.getProduct(), orderLineDto.getQuantity());
            }
            orderLine.setQuantity( orderLineDto.getQuantity() );
            orderLineRepository.save(orderLine);
        }
    }

    private void subtractStock(Product product, long quantity) {
        product.setStock( product.getStock() - quantity );
        productRepository.save(product);
    }

    public OrderDto getOrder(long id) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if(optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            return OrderDto.builder()
                    .id(order.getId())
                    .customerName(order.getCustomerName())
                    .customerAddress(order.getCustomerAddress())
                    .customerEmail(order.getCustomerEmail())
                    .orderLineDtoList(transformOrderLineToOrderLineDto(order.getOrderLineList()))
                    .status(order.getStatus().name())
                    .build();
        } else {
            throw new OrderNotFoundException(ORDER_ID + id + NOT_FOUND);
        }
    }
}
