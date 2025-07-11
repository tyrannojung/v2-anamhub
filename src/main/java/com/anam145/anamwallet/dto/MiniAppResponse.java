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
    private String iconUrl;
    
    public static MiniAppResponse from(MiniAppEntity entity) {
        MiniAppResponse response = new MiniAppResponse();
        response.setAppId(entity.getAppId());
        response.setFileName(entity.getFileName());
        response.setType(entity.getType());
        response.setCreatedAt(entity.getCreatedAt());
        response.setName(entity.getManifestName());
        response.setVersion(entity.getManifestVersion());
        // 아이콘 URL 생성 - 별도 엔드포인트로 제공
        response.setIconUrl("/miniapps/" + entity.getAppId() + "/icon");
        return response;
    }
}