package com.anam145.anamwallet.controller;

import com.anam145.anamwallet.dto.ModuleDto;
import com.anam145.anamwallet.service.FileService;
import com.anam145.anamwallet.service.ModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/download")
public class FileDownloadController {

    @Autowired
    private FileService fileService;

    @GetMapping("/apk/{moduleName}")
    public ResponseEntity<Resource> getApkFile(@PathVariable String moduleName) {
        Resource resource = fileService.fetchApkFile(moduleName);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + moduleName + "\"")
                .body(resource);
    }

    @GetMapping("/img/{moduleName}")
    public ResponseEntity<Resource> getImgFile(@PathVariable String moduleName){
        Resource resource = fileService.fetchImgFile(moduleName);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + moduleName + "\"")
                .body(resource);
    }



}
