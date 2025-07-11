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
}
