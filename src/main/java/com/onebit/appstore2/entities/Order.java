package com.onebit.appstore2.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity(name = "order_table")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(cascade = CascadeType.ALL)
    private List<OrderLine> orderLineList;

    private String customerName;
    private String customerAddress;
    private String customerEmail;

    /*  The information associated with an order should include at least an order id; customer name, address, and email; and the quantity of the product ordered.

     */

    private Status status;
    public enum Status {
        CREATED, CANCELED, SHIPPED;
    }


}
