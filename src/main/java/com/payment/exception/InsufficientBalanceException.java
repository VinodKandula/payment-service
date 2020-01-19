package com.payment.exception;

/**
 * @author Vinod Kandula
 */
public class InsufficientBalanceException extends PaymentException {
    /**
     * Instantiates a new {@link PaymentException} exception.
     *
     * @param message the message that contains information about the raised exception
     */
    public InsufficientBalanceException(String message) {
        super(message);
    }
}
