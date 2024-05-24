package com.example.servicoderemessawallet.exception;

public class ExchangeRateException extends RuntimeException {
    public ExchangeRateException(String message) {
        super(message);
    }

    public ExchangeRateException(String message, Throwable cause) {
        super(message, cause);
    }
}
