package com.newfold.appstore2.entities;


import lombok.Builder;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
@Builder
public class OrderLine {
    @Id
    Long id;
    @OneToOne
    private Product product;
    private long quantity;
}
