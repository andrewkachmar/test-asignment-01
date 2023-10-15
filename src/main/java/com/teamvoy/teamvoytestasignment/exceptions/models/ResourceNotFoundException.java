package com.teamvoy.teamvoytestasignment.exceptions.models;

public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
