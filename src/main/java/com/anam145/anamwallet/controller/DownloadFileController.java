package com.anam145.anamwallet.controller;

import com.anam145.anamwallet.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/download")
public class DownloadFileController {

    @Autowired
    private FileService fileService;


    @GetMapping("/apk/{filename}")
    public ResponseEntity<Resource> getApk(@PathVariable String moduleName) {
        Resource resource = fileService.fetchFile(moduleName);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + moduleName + "\"")
                .body(resource);
    }

    @GetMapping("/img/{filename}")
    public ResponseEntity<Resource> getImg(@PathVariable String moduleName){
        Resource resource = fileService.fetchFile(null);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + moduleName + "\"")
                .body(resource);
    }




}
