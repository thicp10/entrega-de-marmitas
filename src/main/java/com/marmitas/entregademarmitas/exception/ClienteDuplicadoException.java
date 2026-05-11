package com.marmitas.entregademarmitas.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ClienteDuplicadoException extends RuntimeException {
    
    public ClienteDuplicadoException(String message) {
        super(message);
    }
    
    public ClienteDuplicadoException(String message, Throwable cause) {
        super(message, cause);
    }
}
