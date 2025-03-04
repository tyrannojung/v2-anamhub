package com.anam145.anamwallet.controller;

import com.anam145.anamwallet.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private FileService fileService;

    @GetMapping("/upload")
    public String showUploadForm(Model model) {
        return "upload";
    }


    @PostMapping("/upload")
    public String handleFileUpload(
            @RequestParam("apkFile") MultipartFile apkFile,
            @RequestParam("moduleEntryClass") String moduleEntryClass,
            @RequestParam("imgFile") MultipartFile imgFile,
            @RequestParam("moduleName") String moduleName,
            Model model) {

        if (apkFile.isEmpty() || imgFile.isEmpty()) {
            model.addAttribute("message", "모든 파일을 입력하세요");
            return "upload";
        }

        int apkReturnCode = fileService.saveFile(apkFile, moduleName);
        int imgReturnCode = fileService.saveFile(imgFile, moduleName);

        return "upload";
    }
}
