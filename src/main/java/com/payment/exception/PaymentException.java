package com.payment.exception;

/**
 * @author Vinod Kandula
 *
 * The {@link PaymentException} exception is the parent of all custom exceptions
 */
public class PaymentException extends RuntimeException {
    /**
     * The message associated with the exception.
     */
    private String message;

    /**
     * Instantiates a new {@link PaymentException} exception.
     *
     * @param message the log message
     */
    public PaymentException(final String message) {
        super(message);
    }

}
