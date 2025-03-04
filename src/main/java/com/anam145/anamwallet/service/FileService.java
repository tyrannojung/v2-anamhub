package com.anam145.anamwallet.service;

import com.anam145.anamwallet.dao.ModuleDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileService {

    @Value("${file.provide.dir}")
    private String UPLOAD_DIR;

    @Autowired
    private ModuleDao apkMetaDao;

    public int saveFile(MultipartFile file, String moduleName){
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!uploadPath.toFile().exists()) {
                uploadPath.toFile().mkdirs();
            }

            String fileName = file.getOriginalFilename();
            File destFile = new File(uploadPath.toFile(), fileName);
            file.transferTo(destFile);

            return 0;
        } catch (IOException e) {
            System.err.print(e.toString());
            return 1;
        }


    }

    public Resource fetchFile(String moduleName){
//        try {
//            Path filePath = Paths.get(UPLOAD_DIR).resolve(fileName).normalize();
//            Resource resource = null;
//            resource = new UrlResource(filePath.toUri());
//
//            if (!resource.exists()) {
//                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "파일을 찾을 수 없습니다.");
//            }
//            return resource;
//
//        } catch (MalformedURLException e) {
//            throw new RuntimeException(e);
//        }
        return null;
    }
}
