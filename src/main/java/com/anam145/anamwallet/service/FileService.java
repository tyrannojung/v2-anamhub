package com.anam145.anamwallet.service;

import com.anam145.anamwallet.exception.FileProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Slf4j
@Service
public class FileService {

    @Value("${file.mini-app.provide.dir}")
    private String MINI_APP_UPLOAD_DIR;

    private void saveFile(MultipartFile file, String miniAppName, String uploadDir) {
        try {
            Path uploadPath = Paths.get(uploadDir);

            if (!uploadPath.toFile().exists()) {
                boolean created = uploadPath.toFile().mkdirs();
                if (!created) {
                    throw new FileProcessingException("Failed to create upload directory: " + uploadDir);
                }
            }

            // miniAppName이 이미 전체 파일명이므로 그대로 사용
            File destFile = new File(uploadPath.toFile(), miniAppName);
            file.transferTo(destFile);
            
            log.info("File saved successfully: {}", destFile.getAbsolutePath());
        } catch (IOException e) {
            log.error("Failed to save file: {}", miniAppName, e);
            throw new FileProcessingException("Failed to save file: " + miniAppName, e);
        }
    }

    private Resource fetchFile(String miniAppName, String dir) {
        try {
            Path directoryPath = Paths.get(dir);
            Optional<Path> filePath = Files.walk(directoryPath)
                    .filter(path -> path.getFileName().toString().equals(miniAppName))  // 정확한 파일명 매칭
                    .filter(path -> !Files.isDirectory(path))  // 디렉토리 제외
                    .findFirst();  // 첫 번째 매칭 파일 반환

            if (filePath.isPresent()) {
                Resource resource = new UrlResource(filePath.get().toUri());
                log.info("File found: {}", filePath.get().toAbsolutePath());
                return resource;
            } else {
                throw new FileProcessingException("File not found: " + miniAppName);
            }
        } catch (IOException e) {
            log.error("Error while searching file: {}", miniAppName, e);
            throw new FileProcessingException("Error while searching file: " + miniAppName, e);
        }
    }


    public void saveMiniAppFile(MultipartFile file, String miniAppName) {
        saveFile(file, miniAppName, MINI_APP_UPLOAD_DIR);
    }

    public Resource fetchMiniAppFile(String fileName) {
        return fetchFile(fileName, MINI_APP_UPLOAD_DIR);
    }
}
