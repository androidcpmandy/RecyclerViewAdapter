package com.mandy.recyclerview.exception;

public class InvalidMethodException extends RuntimeException {

    public InvalidMethodException(){
        super("should init recyclerView in initComponent");
    }
}
