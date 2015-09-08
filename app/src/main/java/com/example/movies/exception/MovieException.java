package com.example.movies.exception;

public class MovieException extends Exception {

    public MovieException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public MovieException(String detailMessage) {
        super(detailMessage);
    }
}
