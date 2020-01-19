package com.payment.exception;

/**
 * @author Vinod Kandula
 */
public class AccountNoFoundException extends PaymentException {
    /**
     * Instantiates a new {@link PaymentException} exception.
     *
     * @param message the log message
     */
    public AccountNoFoundException(String message) {
        super(message);
    }
}
