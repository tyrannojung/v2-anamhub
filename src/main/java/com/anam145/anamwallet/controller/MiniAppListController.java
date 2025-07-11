package com.anam145.anamwallet.controller;

import com.anam145.anamwallet.domain.MiniAppEntity;
import com.anam145.anamwallet.dto.ApiResponse;
import com.anam145.anamwallet.dto.MiniAppResponse;
import com.anam145.anamwallet.service.MiniAppService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/miniapps")
@RequiredArgsConstructor
public class MiniAppListController {

    private final MiniAppService miniAppService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<MiniAppResponse>>> getAvailableMiniApps() {
        List<MiniAppEntity> miniApps = miniAppService.listAll();
        List<MiniAppResponse> responses = miniApps.stream()
                .map(MiniAppResponse::from)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success(responses, 
                String.format("Found %d mini-apps", miniApps.size())));
    }

    @GetMapping("/{appId}")
    public ResponseEntity<ApiResponse<MiniAppResponse>> getMiniApp(@PathVariable String appId) {
        MiniAppEntity miniApp = miniAppService.get(appId);
        MiniAppResponse response = MiniAppResponse.from(miniApp);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
