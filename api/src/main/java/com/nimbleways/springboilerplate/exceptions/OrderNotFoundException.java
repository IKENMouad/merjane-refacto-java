package com.nimbleways.springboilerplate.exceptions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderNotFoundException extends Exception {

    private String message;

    public OrderNotFoundException(String message) {
        super(message);
        this.message = message;
    }

}
