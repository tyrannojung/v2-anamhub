package com.anam145.anamwallet.controller;

import com.anam145.anamwallet.domain.MiniAppEntity;
import com.anam145.anamwallet.dto.ApiResponse;
import com.anam145.anamwallet.service.MiniAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MiniAppListController {

    @Autowired
    private MiniAppService miniAppService;


    @GetMapping("/miniapps")
    public ResponseEntity<ApiResponse<List<MiniAppEntity>>> getAvailableMiniApps(){
        List<MiniAppEntity> miniApps = miniAppService.listAll();
        return ResponseEntity.ok(ApiResponse.success(miniApps, 
                "Found " + miniApps.size() + " mini-apps"));
    }


    @GetMapping("/miniapps/{appId}")
    public ResponseEntity<ApiResponse<MiniAppEntity>> getMiniApp(@PathVariable String appId){
        MiniAppEntity miniApp = miniAppService.get(appId);
        return ResponseEntity.ok(ApiResponse.success(miniApp));
    }


}
