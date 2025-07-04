package com.anam145.anamwallet.service;


import com.anam145.anamwallet.domain.MiniAppEntity;
import com.anam145.anamwallet.repository.MiniAppRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class MiniAppService {
    @Autowired
    private MiniAppRepository repo;

    public MiniAppEntity get(String appId){
        return repo.findById(appId)
                .orElseThrow(() -> new NoSuchElementException("MiniApp not found with id: " + appId));
    }

    public List<MiniAppEntity> listAll() {
        return repo.findAll();
    }

    public void save(MiniAppEntity miniAppEntity) {
        repo.save(miniAppEntity);
    }

    public void delete(String appId) {
        repo.deleteById(appId);
    }
}
