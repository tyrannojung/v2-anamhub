package com.anam145.anamwallet.controller;

import com.anam145.anamwallet.domain.MiniAppEntity;
import com.anam145.anamwallet.domain.MiniAppType;
import com.anam145.anamwallet.dto.MiniAppManifest;
import com.anam145.anamwallet.service.FileService;
import com.anam145.anamwallet.service.MiniAppService;
import com.anam145.anamwallet.service.ManifestValidationService;
import com.anam145.anamwallet.service.ZipModificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Controller
public class FileUploadController {

    @Autowired
    private FileService fileService;
    @Autowired
    private MiniAppService miniAppService;
    @Autowired
    private ManifestValidationService manifestValidationService;
    @Autowired
    private ZipModificationService zipModificationService;


    @GetMapping("/")
    public String showUploadForm(Model model) {
        return "upload";
    }


    @PostMapping("/")
    public String handleFileUpload(@RequestParam("miniAppFile") MultipartFile file,
                                   @RequestParam("type") MiniAppType type,
                                   Model model) {

        if (file.isEmpty()) {
            model.addAttribute("message", "❌ Please select a ZIP file to upload");
            model.addAttribute("success", false);
            return "upload";
        }

        // ZIP 파일 확장자 검증
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".zip")) {
            model.addAttribute("message", "❌ Only ZIP files are allowed");
            model.addAttribute("success", false);
            return "upload";
        }

        File modifiedZipFile = null;
        try {
            // manifest.json 파싱 및 검증 (name만 필수)
            MiniAppManifest manifest = manifestValidationService.validateAndParseManifest(file);
            
            // icon 파일 존재 여부 확인 (필수)
            boolean iconExists = zipModificationService.validateIconPath(file, manifest.getIcon());
            if (!iconExists) {
                model.addAttribute("message", "❌ Icon file not found in ZIP. Path: " + manifest.getIcon() + " (Please ensure the file exists at the path specified in manifest.json)");
                model.addAttribute("success", false);
                return "upload";
            }
            
            // pages 파일들 존재 여부 확인 (필수)
            boolean pagesExist = zipModificationService.validatePagesPath(file, manifest.getPages());
            if (!pagesExist) {
                // 누락된 파일 목록 계산
                Set<String> missingFiles = new HashSet<>();
                for (String page : manifest.getPages()) {
                    String htmlPath = page + ".html";
                    // ZIP 파일에서 해당 파일 확인
                    boolean fileExists = false;
                    try (InputStream is = file.getInputStream();
                         ZipInputStream zis = new ZipInputStream(is)) {
                        ZipEntry entry;
                        while ((entry = zis.getNextEntry()) != null) {
                            if (htmlPath.equals(entry.getName())) {
                                fileExists = true;
                                break;
                            }
                        }
                    }
                    if (!fileExists) {
                        missingFiles.add(htmlPath);
                    }
                }
                
                // 특별히 index 페이지가 누락된 경우
                if (missingFiles.contains("pages/index/index.html")) {
                    model.addAttribute("message", "❌ Required page 'pages/index/index.html' not found in ZIP file. This file must be included.");
                } else {
                    String missingStr = String.join(", ", missingFiles);
                    model.addAttribute("message", "❌ The following HTML files were not found in ZIP: " + missingStr + " (All files specified in manifest.json pages array must exist)");
                }
                model.addAttribute("success", false);
                return "upload";
            }
            
            // 고유한 app_id 생성 (항상 자동 생성)
            String appId = manifestValidationService.generateAppId(null);
            
            // 중복 체크
            if (miniAppService.existsByAppId(appId)) {
                model.addAttribute("message", "❌ App ID already exists: " + appId);
                model.addAttribute("success", false);
                return "upload";
            }
            
            // ZIP 파일 수정 (app_id, type, version 추가)
            final File tempModifiedZipFile = zipModificationService.modifyAndRepackageZip(file, manifest, appId, type);
            modifiedZipFile = tempModifiedZipFile;
            
            // MiniApp 엔티티 생성
            MiniAppEntity miniApp = new MiniAppEntity();
            miniApp.setAppId(appId);
            miniApp.setManifestName(manifest.getName());
            miniApp.setManifestVersion("1.0.0"); // 고정값
            miniApp.setManifestIcon(manifest.getIcon());
            miniApp.setFileName(file.getOriginalFilename());
            miniApp.setType(type);
            
            // DB 저장
            miniAppService.save(miniApp);
            
            // 수정된 ZIP 파일 저장
            MultipartFile modifiedMultipartFile = new MultipartFile() {
                @Override
                public String getName() { return file.getName(); }
                @Override
                public String getOriginalFilename() { return file.getOriginalFilename(); }
                @Override
                public String getContentType() { return file.getContentType(); }
                @Override
                public boolean isEmpty() { return false; }
                @Override
                public long getSize() { return tempModifiedZipFile.length(); }
                @Override
                public byte[] getBytes() throws IOException { return Files.readAllBytes(tempModifiedZipFile.toPath()); }
                @Override
                public InputStream getInputStream() throws IOException { return Files.newInputStream(tempModifiedZipFile.toPath()); }
                @Override
                public void transferTo(File dest) throws IOException { Files.copy(tempModifiedZipFile.toPath(), dest.toPath()); }
            };
            
            fileService.saveMiniAppFile(modifiedMultipartFile, file.getOriginalFilename());
            
            // 성공 메시지 추가
            model.addAttribute("message", "✅ Upload successful: " + manifest.getName() + " (v1.0.0)");
            model.addAttribute("success", true);
            
        } catch (Exception e) {
            model.addAttribute("message", "❌ Upload failed: " + e.getMessage());
            model.addAttribute("success", false);
        } finally {
            // 임시 파일 정리
            if (modifiedZipFile != null && modifiedZipFile.exists()) {
                modifiedZipFile.delete();
            }
        }

        return "upload";
    }
}
