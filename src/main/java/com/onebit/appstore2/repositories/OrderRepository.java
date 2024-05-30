package com.onebit.appstore2.repositories;


import com.onebit.appstore2.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface OrderRepository extends JpaRepository<Order, Long>, PagingAndSortingRepository<Order, Long> {

}
