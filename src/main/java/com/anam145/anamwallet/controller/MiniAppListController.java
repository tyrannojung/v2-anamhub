package com.anam145.anamwallet.controller;

import com.anam145.anamwallet.domain.MiniAppEntity;
import com.anam145.anamwallet.service.MiniAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MiniAppListController {

    @Autowired
    private MiniAppService miniAppService;


    @GetMapping("/miniapps")
    public List<MiniAppEntity> getAvailableMiniApps(){
        return miniAppService.listAll();
    }


    @GetMapping("/miniapps/{appId}")
    public MiniAppEntity getMiniApp(@PathVariable String appId){
        return miniAppService.get(appId);
    }


}
