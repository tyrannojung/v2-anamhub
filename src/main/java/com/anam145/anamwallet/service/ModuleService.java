package com.anam145.anamwallet.service;


import com.anam145.anamwallet.dto.ModuleDto;
import com.anam145.anamwallet.repository.ModuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class ModuleService {
    @Autowired
    private ModuleRepository repo;

    public ModuleDto get(String moduleName){
        return repo.findById(moduleName)
                .orElseThrow(() -> new NoSuchElementException("ModuleDto not found with id: " + moduleName));
    }

    public List<ModuleDto> listAll() {
        return repo.findAll();
    }

    public void save(ModuleDto moduleDto) {
        repo.save(moduleDto);
    }

    public void delete(String moduleName) {
        repo.deleteById(moduleName);
    }
}
