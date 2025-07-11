package com.anam145.anamwallet.service;


import com.anam145.anamwallet.domain.MiniAppEntity;
import com.anam145.anamwallet.exception.MiniAppNotFoundException;
import com.anam145.anamwallet.repository.MiniAppRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MiniAppService {
    
    private final MiniAppRepository repo;

    public MiniAppEntity get(String appId) {
        MiniAppEntity entity = repo.findByAppId(appId);
        if (entity == null) {
            throw new MiniAppNotFoundException(appId);
        }
        return entity;
    }
    
    public boolean existsByAppId(String appId) {
        return repo.existsByAppId(appId);
    }

    public List<MiniAppEntity> listAll() {
        return repo.findAll();
    }

    @Transactional
    public void save(MiniAppEntity miniAppEntity) {
        repo.save(miniAppEntity);
    }

    @Transactional
    public void delete(String appId) {
        MiniAppEntity entity = get(appId);
        repo.delete(entity);
    }
}
