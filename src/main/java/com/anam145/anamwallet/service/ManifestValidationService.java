package com.anam145.anamwallet.service;

import com.anam145.anamwallet.dto.MiniAppManifest;
import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class ManifestValidationService {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public MiniAppManifest validateAndParseManifest(MultipartFile zipFile) throws IOException {
        try (InputStream is = zipFile.getInputStream();
             ZipInputStream zis = new ZipInputStream(is)) {
            
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                // manifest.json 파일을 루트에서 찾기
                if ("manifest.json".equals(entry.getName())) {
                    return parseManifest(zis);
                }
            }
            
            throw new IllegalArgumentException("manifest.json not found in ZIP root directory. Please check your ZIP file structure.");
        }
    }
    
    private MiniAppManifest parseManifest(InputStream inputStream) throws IOException {
        MiniAppManifest manifest = objectMapper.readValue(inputStream, MiniAppManifest.class);
        
        // 필수 필드 검증 - name, icon, pages 모두 필수
        if (manifest.getName() == null || manifest.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Missing 'name' field in manifest.json. Please specify a mini-app name.");
        }
        
        // name 길이 제한 (최대 20자, 공백 포함)
        if (manifest.getName().length() > 20) {
            throw new IllegalArgumentException("The 'name' field in manifest.json must be 20 characters or less. Current length: " + manifest.getName().length() + " characters");
        }
        
        // icon 필수 검증
        if (manifest.getIcon() == null || manifest.getIcon().trim().isEmpty()) {
            throw new IllegalArgumentException("Missing 'icon' field in manifest.json. Please specify an icon file path.");
        }
        
        // pages 필수 검증
        if (manifest.getPages() == null || manifest.getPages().isEmpty()) {
            throw new IllegalArgumentException("Missing or empty 'pages' field in manifest.json. Please specify at least one page.");
        }
        
        // index 페이지 필수 검증
        boolean hasIndexPage = manifest.getPages().stream()
            .anyMatch(page -> page.equals("pages/index/index"));
        
        if (!hasIndexPage) {
            throw new IllegalArgumentException("Required page 'pages/index/index' not found in manifest.json 'pages' array. This page must be included.");
        }
        
        return manifest;
    }
    
    public String generateAppId(String manifestAppId) {
        // manifest에 app_id가 있고 com.anam으로 시작하면 그대로 사용
        if (manifestAppId != null && manifestAppId.startsWith("com.anam.")) {
            return manifestAppId;
        }
        // 그렇지 않으면 자동 생성
        return "com.anam." + NanoIdUtils.randomNanoId();
    }
}