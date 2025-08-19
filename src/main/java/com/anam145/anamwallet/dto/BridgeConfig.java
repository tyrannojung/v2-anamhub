package com.anam145.anamwallet.dto;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BridgeConfig {
    private String script;
    private String namespace;
}