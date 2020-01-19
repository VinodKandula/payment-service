package com.payment.model;

import lombok.Data;
import org.joda.money.Money;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * @author Vinod Kandula
 *
 * The {@link Account} class holds the data of each Account
 */
@Entity
@Data
public class Account implements Serializable {

    @Id
    @GeneratedValue
    private Long id;
    private String number;
    private Money balance;

}
