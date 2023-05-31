package com.newfold.appstore2;

import com.newfold.appstore2.entities.Product;
import com.newfold.appstore2.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class Appstore2Application {

	public static void main(String[] args) {
		SpringApplication.run(Appstore2Application.class, args);
	}

	@Autowired
	ProductRepository productRepository;

	@PostConstruct
	private void postConstruct() {
		Product product1 = Product.builder().description("Tennis Racket")
				.price(120D)
				.build();
		Product product2 = Product.builder().description("Basket Ball")
				.price(50D)
				.build();
		List<Product> productList = Arrays.asList(product1, product2);
		productRepository.saveAll(productList);
	}


}
