package com.anam145.anamwallet.dto;

import com.anam145.anamwallet.domain.MiniAppEntity;
import com.anam145.anamwallet.domain.MiniAppType;
import lombok.Data;
import java.util.Date;

@Data
public class MiniAppResponse {
    private String appId;
    private String fileName;
    private MiniAppType type;
    private Date createdAt;
    private String name;
    private String version;
    
    public static MiniAppResponse from(MiniAppEntity entity) {
        MiniAppResponse response = new MiniAppResponse();
        response.setAppId(entity.getAppId());
        response.setFileName(entity.getFileName());
        response.setType(entity.getType());
        response.setCreatedAt(entity.getCreatedAt());
        response.setName(entity.getManifestName());
        response.setVersion(entity.getManifestVersion());
        return response;
    }
}