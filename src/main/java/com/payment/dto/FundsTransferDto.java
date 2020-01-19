package com.payment.dto;

import com.payment.model.FundsTransfer.Status;
import org.joda.money.Money;

/**
 * @author Vinod Kandula
 */
public class FundsTransferDto {
    public String remitterAccount;
    public String beneficiaryAccount;
    public Money amount;

    public Status status;
}
