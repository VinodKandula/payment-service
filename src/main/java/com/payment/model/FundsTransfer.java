package com.payment.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.joda.money.Money;

/**
 * @author Vinod Kandula
 */
@Data
public class FundsTransfer {

    public enum Status {
        SUCCESS,
        FAIL
    }

    private Status status;
    private Account remitter;
    private Account beneficiary;
    private Money amount;

}
