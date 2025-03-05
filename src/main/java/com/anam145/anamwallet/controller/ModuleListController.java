package com.anam145.anamwallet.controller;

import com.anam145.anamwallet.dto.ModuleDto;
import com.anam145.anamwallet.service.ModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ModuleListController {

    @Autowired
    private ModuleService moduleService;


    @GetMapping("/list")
    public List<ModuleDto> getAvailableModules(){
        return moduleService.listAll();
    }


    @GetMapping("/list/{moduleName}")
    public ModuleDto getModule(@PathVariable String moduleName){
        return moduleService.get(moduleName);
    }


}
