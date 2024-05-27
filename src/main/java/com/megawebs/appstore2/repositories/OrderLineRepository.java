package com.megawebs.appstore2.repositories;

import com.megawebs.appstore2.entities.OrderLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface OrderLineRepository extends JpaRepository<OrderLine, Long>, PagingAndSortingRepository<OrderLine, Long> {
    @Override
    <S extends OrderLine> S save(S entity);
}
