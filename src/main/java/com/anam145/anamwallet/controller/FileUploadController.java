package com.anam145.anamwallet.controller;

import com.anam145.anamwallet.domain.MiniAppEntity;
import com.anam145.anamwallet.domain.MiniAppType;
import com.anam145.anamwallet.service.FileService;
import com.anam145.anamwallet.service.MiniAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class FileUploadController {

    @Autowired
    private FileService fileService;
    @Autowired
    private MiniAppService miniAppService;


    @GetMapping("/upload")
    public String showUploadForm(Model model) {
        return "upload";
    }


    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("miniAppFile") MultipartFile file,
                                   @RequestParam("type") MiniAppType type,
                                   Model model) {

        if (file.isEmpty()) {
            model.addAttribute("message", "모든 파일을 입력하세요");
            return "upload";
        }

        miniAppService.save(new MiniAppEntity(
                null,
                file.getOriginalFilename(),
                type,
                null,
                null
        ));
        fileService.saveMiniAppFile(file, file.getOriginalFilename());

        return "upload";
    }
}
