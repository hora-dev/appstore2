package com.newfold.appstore2.entities;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Product {
    @Id
    Long id;

    String description;
    Double price;
    int quantity;
}
