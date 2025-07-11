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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
                model.addAttribute("message", "❌ One or more page files specified in manifest.json were not found. Ensure all HTML files exist at the specified paths.");
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
            
            // ZIP 파일명 생성
            String zipFileName = appId + ".zip";
            
            // ZIP 파일 수정 (app_id, type, version 추가)
            final File tempModifiedZipFile = zipModificationService.modifyAndRepackageZip(file, manifest, appId, type);
            modifiedZipFile = tempModifiedZipFile;
            
            // MiniApp 엔티티 생성
            MiniAppEntity miniApp = new MiniAppEntity();
            miniApp.setAppId(appId);
            miniApp.setManifestName(manifest.getName());
            miniApp.setManifestVersion("1.0.0"); // 고정값
            miniApp.setManifestIcon(manifest.getIcon());
            miniApp.setFileName(zipFileName);
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
            
            fileService.saveMiniAppFile(modifiedMultipartFile, zipFileName);
            
            // 아이콘 파일 추출 및 저장
            extractAndSaveIcon(file, manifest.getIcon(), appId);
            
            // 성공 메시지 추가
            model.addAttribute("message", "✅ Upload successful: " + manifest.getName() + " (v1.0.0)<br>app_id: " + appId);
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
    
    // 아이콘 파일 추출 및 저장 메소드
    private void extractAndSaveIcon(MultipartFile zipFile, String iconPath, String appId) {
        if (iconPath == null || iconPath.isEmpty()) {
            return;
        }
        
        try (InputStream is = zipFile.getInputStream();
             ZipInputStream zis = new ZipInputStream(is)) {
            
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (iconPath.equals(entry.getName())) {
                    // 아이콘 파일 읽기
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        baos.write(buffer, 0, len);
                    }
                    
                    // 파일 확장자 추출
                    String extension = "";
                    int lastDotIndex = iconPath.lastIndexOf('.');
                    if (lastDotIndex > 0) {
                        extension = iconPath.substring(lastDotIndex);
                    }
                    
                    // 아이콘 파일명: appId + 확장자
                    String iconFileName = appId + extension;
                    
                    // 아이콘 저장
                    fileService.saveIconFile(baos.toByteArray(), iconFileName);
                    
                    log.info("Icon extracted and saved: {}", iconFileName);
                    break;
                }
            }
        } catch (IOException e) {
            log.error("Failed to extract icon from ZIP: {}", e.getMessage());
            // 아이콘 추출 실패는 전체 업로드 실패로 처리하지 않음
        }
    }
}
