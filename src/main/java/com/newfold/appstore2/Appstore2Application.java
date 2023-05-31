package com.newfold.appstore2;

import com.newfold.appstore2.entities.Order;
import com.newfold.appstore2.entities.OrderLine;
import com.newfold.appstore2.entities.Product;
import com.newfold.appstore2.repositories.OrderLineRepository;
import com.newfold.appstore2.repositories.OrderRepository;
import com.newfold.appstore2.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class Appstore2Application {

	public static void main(String[] args) {
		SpringApplication.run(Appstore2Application.class, args);
	}

	@Autowired
	ProductRepository productRepository;

	@Autowired
	OrderRepository orderRepository;

	@Autowired
	OrderLineRepository orderLineRepository;

	@PostConstruct
	private void postConstruct() {
		Product product1 = Product.builder().description("Tennis Racket")
				.id(1L)
				.price(120D)
				.stock(1L)
				.build();
		Product product2 = Product.builder().description("Basket Ball")
				.id(2L)
				.price(50D)
				.stock(3L)
				.build();
		List<Product> productList = Arrays.asList(product1, product2);
		productRepository.saveAll(productList);

		OrderLine orderLine = OrderLine.builder()
				.id(1L)
				.product(product1)
				.quantity(1L)
				.build();
		orderLineRepository.save(orderLine);

		product1.setStock(0L);
		productRepository.save(product1);


		List<OrderLine> orderLineList = new ArrayList<>();
		orderLineList.add(orderLine);
		Order order = Order.builder()
				.id(1L)
				.orderLineList( orderLineList )
				.status(Order.Status.CREATED)
				.customerName("Emily Watson")
				.customerAddress("104 Darwin Street")
				.customerEmail("emilywatson@gmail.com")
				.build();
		orderRepository.save(order);
	}


}
