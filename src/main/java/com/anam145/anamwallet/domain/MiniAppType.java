package com.anam145.anamwallet.domain;

import lombok.Getter;

@Getter
public enum MiniAppType {
    BLOCKCHAIN("BLOCKCHAIN"),
    APP("APP");

    private final String value;

    MiniAppType(String value) {
        this.value = value;
    }
}

