package com.micro.orderservice.application.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) { super(message); }
}