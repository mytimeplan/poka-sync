package com.mytimeplan.pokasync.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DefaultException extends Exception {
    private String message;

    public DefaultException(String message) {
        super(message);
        this.message = message;
    }
}
