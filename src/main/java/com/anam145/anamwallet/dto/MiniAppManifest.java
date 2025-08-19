package com.anam145.anamwallet.dto;

import lombok.Data;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MiniAppManifest {
    private String app_id;
    private String type;
    private String name;
    private String version;
    private String icon;
    private List<String> pages;
    private BridgeConfig bridge;
}