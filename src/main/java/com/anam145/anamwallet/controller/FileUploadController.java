package com.anam145.anamwallet.controller;

import com.anam145.anamwallet.dto.ModuleDto;
import com.anam145.anamwallet.service.FileService;
import com.anam145.anamwallet.service.ModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.util.Locale;

@Controller
public class FileUploadController {

    @Autowired
    private FileService fileService;
    @Autowired
    private ModuleService moduleService;



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

        moduleName = moduleName.toLowerCase();
        int apkReturnCode = fileService.saveApkFile(apkFile, moduleName);
        int imgReturnCode = fileService.saveImgFile(imgFile, moduleName);

        moduleService.save(new ModuleDto(moduleName, moduleEntryClass));

        return "upload";
    }

//    @ExceptionHandler({IOException.class})
//    public ResponseEntity<String> handle(Exception ex) {
//
//    }

}
