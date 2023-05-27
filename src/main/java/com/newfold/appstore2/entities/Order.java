package com.newfold.appstore2.entities;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.List;

@Entity(name = "order_table")
@Data
public class Order {
    @Id
    Long id;

    @OneToMany
    List<Product> productList;

    Status status;
    public enum Status {
        CREATED, CANCELED, SHIPPED;
    }


}
