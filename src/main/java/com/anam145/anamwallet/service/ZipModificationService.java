package com.anam145.anamwallet.service;

import com.anam145.anamwallet.domain.MiniAppType;
import com.anam145.anamwallet.dto.MiniAppManifest;
import com.anam145.anamwallet.exception.FileProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ZipModificationService {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public File modifyAndRepackageZip(MultipartFile originalZip, MiniAppManifest originalManifest, 
                                      String appId, MiniAppType type) throws IOException {
        
        // 임시 파일 생성
        Path tempFile = Files.createTempFile("modified-", ".zip");
        
        try (InputStream is = originalZip.getInputStream();
             ZipInputStream zis = new ZipInputStream(is);
             FileOutputStream fos = new FileOutputStream(tempFile.toFile());
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            
            ZipEntry entry;
            
            while ((entry = zis.getNextEntry()) != null) {
                if ("manifest.json".equals(entry.getName())) {
                    // manifest.json을 완전히 새로 생성
                    ZipEntry newEntry = new ZipEntry("manifest.json");
                    zos.putNextEntry(newEntry);
                    
                    // 새로운 manifest 객체 생성 (필요한 필드만 포함)
                    MiniAppManifest newManifest = new MiniAppManifest();
                    newManifest.setName(originalManifest.getName());
                    newManifest.setIcon(originalManifest.getIcon());
                    newManifest.setPages(originalManifest.getPages());
                    newManifest.setApp_id(appId);
                    newManifest.setType(type.toString().toLowerCase());
                    newManifest.setVersion("1.0.0");
                    
                    // 새로운 manifest를 ZIP에 쓰기
                    byte[] modifiedManifest = objectMapper.writerWithDefaultPrettyPrinter()
                            .writeValueAsBytes(newManifest);
                    zos.write(modifiedManifest);
                    
                    log.info("Created new manifest.json with app_id: {}, type: {}, version: 1.0.0", appId, type);
                } else {
                    // 다른 파일들은 그대로 복사
                    ZipEntry newEntry = new ZipEntry(entry.getName());
                    zos.putNextEntry(newEntry);
                    
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        zos.write(buffer, 0, len);
                    }
                }
                zos.closeEntry();
            }
        } catch (Exception e) {
            Files.deleteIfExists(tempFile);
            throw new FileProcessingException("Failed to modify ZIP file: " + e.getMessage(), e);
        }
        
        return tempFile.toFile();
    }
    
    public boolean validateIconPath(MultipartFile zipFile, String iconPath) throws IOException {
        if (iconPath == null || iconPath.trim().isEmpty()) {
            return false; // icon은 필수
        }
        
        try (InputStream is = zipFile.getInputStream();
             ZipInputStream zis = new ZipInputStream(is)) {
            
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (iconPath.equals(entry.getName())) {
                    log.info("Icon file found at path: {}", iconPath);
                    return true;
                }
            }
        }
        
        log.warn("Icon file not found at path: {}", iconPath);
        return false;
    }
    
    public boolean validatePagesPath(MultipartFile zipFile, List<String> pages) throws IOException {
        if (pages == null || pages.isEmpty()) {
            return false; // pages는 필수
        }
        
        // index 페이지가 포함되어 있는지 확인
        boolean hasIndexPage = pages.contains("pages/index/index");
        if (!hasIndexPage) {
            log.error("Required index page 'pages/index/index' not found in manifest");
            return false;
        }
        
        Set<String> pageFiles = new HashSet<>();
        for (String page : pages) {
            // pages/index/index -> pages/index/index.html
            String htmlPath = page + ".html";
            pageFiles.add(htmlPath);
        }
        
        Set<String> foundFiles = new HashSet<>();
        
        try (InputStream is = zipFile.getInputStream();
             ZipInputStream zis = new ZipInputStream(is)) {
            
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String entryName = entry.getName();
                if (pageFiles.contains(entryName)) {
                    foundFiles.add(entryName);
                    log.info("Page file found: {}", entryName);
                }
            }
        }
        
        // 모든 페이지 파일이 존재하는지 확인
        boolean allFound = foundFiles.size() == pageFiles.size();
        
        if (!allFound) {
            Set<String> missingFiles = new HashSet<>(pageFiles);
            missingFiles.removeAll(foundFiles);
            log.warn("Missing page files: {}", missingFiles);
            
            // index 페이지가 누락된 경우 특별히 표시
            if (missingFiles.contains("pages/index/index.html")) {
                log.error("Critical: Required index page file is missing!");
            }
        }
        
        return allFound;
    }
}