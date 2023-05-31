package com.newfold.appstore2.service;

import com.newfold.appstore2.dto.OrderDto;
import com.newfold.appstore2.dto.OrderLineDto;
import com.newfold.appstore2.dto.ProductResponseDto;
import com.newfold.appstore2.entities.Order;
import com.newfold.appstore2.entities.OrderLine;
import com.newfold.appstore2.entities.Product;
import com.newfold.appstore2.exception.ErrorCreatingOrderException;
import com.newfold.appstore2.exception.OrderNotFoundException;
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

    public Order.Status getOrderStatus(Long id) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if(optionalOrder.isPresent()) {
            return optionalOrder.get().getStatus();
        }
        throw new OrderNotFoundException("Order not found");
    }

    public Long createOrder(OrderDto orderDto) {
        List<OrderLine> orderLineList = new ArrayList<>();
        orderDto.getOrderLineDtoList().stream().forEach(orderLineDto -> addOrderLines(orderLineList, orderLineDto));

        Order order = Order.builder()
                .customerName(orderDto.getCustomerName())
                .customerAddress(orderDto.getCustomerAddress())
                .email(orderDto.getEmail())
                .status(Order.Status.CREATED)
                .orderLineList(orderLineList)
                .build();
        Order orderCreated = orderRepository.save(order);
        if( null != orderCreated ){
            return orderCreated.getId();
        }
        throw new ErrorCreatingOrderException("Error saving entity, request received " + orderDto);
    }

    private void addOrderLines(List<OrderLine> orderLineList, OrderLineDto orderLineDto) {
        Optional<Product> product = productRepository.findById(orderLineDto.getProductDto().getId());
        if(product.isPresent()) {
            orderLineList.add(
                    OrderLine.builder()
                            .product(Product.builder()
                                    .description(product.get().getDescription())
                                    .price(product.get().getPrice())
                                    .build() )
                            .quantity(orderLineDto.getQuantity())
                            .build());
        }
        // ignore products with not matching id's
    }

    public List<ProductResponseDto> getProducts() {
        List<Product> productList = productRepository.findAll();
        List<ProductResponseDto> productResponseDtoList = new ArrayList<ProductResponseDto>();
        productList.stream().forEach( product -> productResponseDtoList.add( ProductResponseDto.builder()
                .description(product.getDescription())
                .price(product.getPrice())
                .id(product.getId())
                .build()));
        return productResponseDtoList;
    }
}
