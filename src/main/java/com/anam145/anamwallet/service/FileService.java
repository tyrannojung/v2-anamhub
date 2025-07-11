package com.anam145.anamwallet.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Service
public class FileService {

    @Value("${file.mini-app.provide.dir}")
    private String MINI_APP_UPLOAD_DIR;

    private int saveFile(MultipartFile file, String miniAppName, String uploadDir){
        try {
            Path uploadPath = Paths.get(uploadDir);

            if (!uploadPath.toFile().exists()) {
                uploadPath.toFile().mkdirs();
            }

            // miniAppName이 이미 전체 파일명이므로 그대로 사용
            File destFile = new File(uploadPath.toFile(), miniAppName);
            file.transferTo(destFile);

            return 0;
        } catch (IOException e) {
            System.err.print(e.toString());
            return 1;
        }
    }

    private Resource fetchFile(String miniAppName, String dir) {
        try {
            Path directoryPath = Paths.get(dir);
            Optional<Path> filePath = Files.walk(directoryPath)
                    .filter(path -> path.getFileName().toString().startsWith(miniAppName))  // moduleName과 일치하는 파일 이름 검색
                    .filter(path -> !Files.isDirectory(path))  // 디렉토리 제외
                    .findFirst();  // 첫 번째 매칭 파일 반환

            if (filePath.isPresent()) {
                return new UrlResource(filePath.get().toUri());
            } else {
                throw new RuntimeException("파일을 찾을 수 없습니다.");
            }
        } catch (IOException e) {
            throw new RuntimeException("파일 검색 중 오류가 발생했습니다.", e);
        }
    }


    public int saveMiniAppFile(MultipartFile file, String miniAppName) {
        return saveFile(file, miniAppName, MINI_APP_UPLOAD_DIR);
    }


    public Resource fetchMiniAppFile(String id){
        return fetchFile(id, MINI_APP_UPLOAD_DIR);
    }
}
