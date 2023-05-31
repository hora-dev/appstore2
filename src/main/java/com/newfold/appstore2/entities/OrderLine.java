package com.newfold.appstore2.entities;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderLine {
    @Id
    Long id;
    @OneToOne
    private Product product;
    private long quantity;
}
