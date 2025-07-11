package com.anam145.anamwallet.controller;

import com.anam145.anamwallet.domain.MiniAppEntity;
import com.anam145.anamwallet.exception.FileProcessingException;
import com.anam145.anamwallet.service.FileService;
import com.anam145.anamwallet.service.MiniAppService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("/miniapps")
@RequiredArgsConstructor
public class FileDownloadController {

    private final FileService fileService;
    private final MiniAppService miniAppService;

    @GetMapping("/{appId}/download")
    public ResponseEntity<Resource> downloadMiniApp(@PathVariable String appId) {
        log.info("Download request for mini-app: {}", appId);
        
        try {
            MiniAppEntity miniApp = miniAppService.get(appId);
            Resource resource = fileService.fetchMiniAppFile(miniApp.getFileName());
            
            if (!resource.exists() || !resource.isReadable()) {
                throw new FileProcessingException("Could not read file: " + miniApp.getFileName());
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                           String.format("attachment; filename=\"%s\"", miniApp.getFileName()))
                    .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(resource.contentLength()))
                    .body(resource);
                    
        } catch (Exception e) {
            log.error("Failed to download mini-app {}: ", appId, e);
            throw new FileProcessingException("File download failed: " + e.getMessage(), e);
        }
    }
    
    @GetMapping("/{appId}/icon")
    public ResponseEntity<Resource> getIcon(@PathVariable String appId) {
        log.info("Icon request for mini-app: {}", appId);
        
        try {
            MiniAppEntity miniApp = miniAppService.get(appId);
            String iconPath = miniApp.getManifestIcon();
            
            if (iconPath == null || iconPath.isEmpty()) {
                throw new FileProcessingException("No icon path specified for mini-app: " + appId);
            }
            
            // 파일 확장자 추출
            String extension = "";
            int lastDotIndex = iconPath.lastIndexOf('.');
            if (lastDotIndex > 0) {
                extension = iconPath.substring(lastDotIndex);
            }
            
            // 저장된 아이콘 파일명
            String iconFileName = appId + extension;
            
            // 저장된 아이콘 파일 조회
            Resource iconResource = fileService.fetchIconFile(iconFileName);
            
            if (!iconResource.exists() || !iconResource.isReadable()) {
                throw new FileProcessingException("Could not read icon file: " + iconFileName);
            }
            
            // Content-Type 결정
            MediaType mediaType = MediaType.IMAGE_PNG; // 기본값
            if (iconPath.toLowerCase().endsWith(".jpg") || iconPath.toLowerCase().endsWith(".jpeg")) {
                mediaType = MediaType.IMAGE_JPEG;
            } else if (iconPath.toLowerCase().endsWith(".gif")) {
                mediaType = MediaType.IMAGE_GIF;
            } else if (iconPath.toLowerCase().endsWith(".svg")) {
                mediaType = MediaType.valueOf("image/svg+xml");
            }
            
            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .header(HttpHeaders.CACHE_CONTROL, "public, max-age=86400") // 24시간 캐시
                    .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(iconResource.contentLength()))
                    .body(iconResource);
                    
        } catch (Exception e) {
            log.error("Failed to get icon for mini-app {}: ", appId, e);
            throw new FileProcessingException("Icon retrieval failed: " + e.getMessage(), e);
        }
    }
}
