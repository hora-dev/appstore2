package com.newfold.appstore2.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

@Entity(name = "order_table")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    private Long id;

    @OneToMany
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
