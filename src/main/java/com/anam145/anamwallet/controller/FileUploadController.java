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


    @GetMapping("/")
    public String showUploadForm(Model model) {
        return "upload";
    }


    @PostMapping("/")
    public String handleFileUpload(@RequestParam("miniAppFile") MultipartFile file,
                                   @RequestParam("type") MiniAppType type,
                                   Model model) {

        if (file.isEmpty()) {
            model.addAttribute("message", "모든 파일을 입력하세요");
            model.addAttribute("success", false);
            return "upload";
        }

        // ZIP 파일 확장자 검증
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".zip")) {
            model.addAttribute("message", "❌ ZIP 파일만 업로드 가능합니다");
            model.addAttribute("success", false);
            return "upload";
        }

        MiniAppEntity miniApp = new MiniAppEntity();
        miniApp.setFileName(file.getOriginalFilename());
        miniApp.setType(type);
        miniAppService.save(miniApp);
        fileService.saveMiniAppFile(file, file.getOriginalFilename());
        
        // 성공 메시지 추가
        model.addAttribute("message", "✅ 파일이 성공적으로 업로드되었습니다: " + file.getOriginalFilename());
        model.addAttribute("success", true);

        return "upload";
    }
}
