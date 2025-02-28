package com.anam145.anamwallet.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class UploadFileController {

    @Value("${file.provide.dir}")
    private String UPLOAD_DIR;

    @GetMapping("/upload")
    public String showUploadForm(Model model) {
        return "upload";
    }


    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, Model model) {
        if (file.isEmpty()) {
            model.addAttribute("message", "파일을 선택하세요.");
            return "upload";
        }

        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!uploadPath.toFile().exists()) {
                uploadPath.toFile().mkdirs();
            }

            String fileName = file.getOriginalFilename();
            File destFile = new File(uploadPath.toFile(), fileName);
            file.transferTo(destFile);

            model.addAttribute("message", "파일 업로드 성공: " + fileName);
        } catch (IOException e) {
            model.addAttribute("message", "파일 업로드 실패: " + e.getMessage());
        }
        return "upload";
    }
}
