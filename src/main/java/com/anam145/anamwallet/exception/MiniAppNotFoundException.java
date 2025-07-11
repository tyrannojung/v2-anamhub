package com.anam145.anamwallet.exception;

public class MiniAppNotFoundException extends RuntimeException {
    public MiniAppNotFoundException(String appId) {
        super("MiniApp not found with id: " + appId);
    }
}