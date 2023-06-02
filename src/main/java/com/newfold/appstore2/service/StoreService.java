package com.newfold.appstore2.service;

import com.newfold.appstore2.dto.OrderDto;
import com.newfold.appstore2.dto.OrderLineDto;
import com.newfold.appstore2.dto.ProductOrderLineDto;
import com.newfold.appstore2.dto.ProductResponseDto;
import com.newfold.appstore2.entities.Order;
import com.newfold.appstore2.entities.OrderLine;
import com.newfold.appstore2.entities.Product;
import com.newfold.appstore2.exception.ErrorCreatingOrderException;
import com.newfold.appstore2.exception.OrderNotFoundException;
import com.newfold.appstore2.exception.OrderStatusException;
import com.newfold.appstore2.repositories.OrderLineRepository;
import com.newfold.appstore2.repositories.OrderRepository;
import com.newfold.appstore2.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StoreService {

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

    public Long createOrder(OrderDto orderDto) {

        Order order = Order.builder()
                .customerName(orderDto.getCustomerName())
                .customerAddress(orderDto.getCustomerAddress())
                .customerEmail(orderDto.getCustomerEmail())
                .status(Order.Status.CREATED)
                .orderLineList(new ArrayList<>())
                .build();
            Order orderCreated = orderRepository.save(order);

        List<OrderLine> orderLineList = new ArrayList<>();
        orderDto.getOrderLineDtoList().stream().forEach(orderLineDto -> addOrderLines(orderLineList, orderLineDto, order));
        order.setOrderLineList(orderLineList);
        orderRepository.save(order);

        return orderCreated.getId();
    }

    private void addOrderLines(List<OrderLine> orderLineList, OrderLineDto orderLineDto, Order order) {
        Optional<Product> productOptional = productRepository.findById(orderLineDto.getProductDto().getId());
        if(productOptional.isPresent()) {
            if( productOptional.get().getStock() < orderLineDto.getQuantity()) {
                throw new ErrorCreatingOrderException("Error creating order, productOptional stock is " + productOptional.get().getStock() +
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
        productList.stream().forEach( product -> productResponseDtoList.add( ProductResponseDto.builder()
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
        orderList.stream().forEach( order -> orderDtoList.add(OrderDto.builder()
                .id(order.getId())
                .customerName(order.getCustomerName())
                .customerAddress(order.getCustomerAddress())
                .customerEmail(order.getCustomerEmail())
                .orderLineDtoList(transform(order.getOrderLineList()))
                .build())
        );
        return orderDtoList;
    }

    private List<OrderLineDto> transform(List<OrderLine> orderLineList) {
        List<OrderLineDto> orderLineDtoList = new ArrayList<>();
        orderLineList.stream().forEach( orderLine ->  orderLineDtoList.add(
                                        OrderLineDto.builder()
                                            .productDto(ProductOrderLineDto.builder()
                                                    .id( orderLine.getProduct().getId() )
                                                    .description( orderLine.getProduct().getDescription())
                                                    .price( orderLine.getProduct().getPrice() ).build())
                                                .quantity(orderLine.getQuantity())
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
                orderRepository.save(order);
                return "Order status id: " + id + " is now cancelled";
            } else {
                throw new OrderStatusException("Order id: " + id + " status is already canceled");
            }
        } else {
            throw new OrderNotFoundException("Order id: " + id + " not found");
        }
    }
}
