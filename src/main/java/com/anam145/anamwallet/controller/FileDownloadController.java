package com.anam145.anamwallet.controller;

import com.anam145.anamwallet.domain.MiniAppEntity;
import com.anam145.anamwallet.service.FileService;
import com.anam145.anamwallet.service.MiniAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FileDownloadController {

    @Autowired
    private FileService fileService;
    @Autowired
    private MiniAppService miniAppService;

    @GetMapping("/miniapps/{id}/download")
    public ResponseEntity<Resource> getMiniAppFile(@PathVariable String id) {
        try {
            MiniAppEntity miniAppEntity = miniAppService.get(id);
            Resource resource = fileService.fetchMiniAppFile(miniAppEntity.getFileName());
            
            // Check if resource exists and is readable
            if (!resource.exists() || !resource.isReadable()) {
                throw new RuntimeException("Could not read file: " + miniAppEntity.getFileName());
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + miniAppEntity.getFileName() + "\"")
                    .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(resource.contentLength()))
                    .body(resource);
        } catch (Exception e) {
            throw new RuntimeException("File download failed: " + e.getMessage());
        }
    }
}
