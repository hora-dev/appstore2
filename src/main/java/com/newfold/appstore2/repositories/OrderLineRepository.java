package com.newfold.appstore2.repositories;

import com.newfold.appstore2.entities.OrderLine;
import com.newfold.appstore2.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface OrderLineRepository extends JpaRepository<OrderLine, Long>, PagingAndSortingRepository<OrderLine, Long> {
}
