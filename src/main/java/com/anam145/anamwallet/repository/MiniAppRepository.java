package com.anam145.anamwallet.repository;


import com.anam145.anamwallet.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;

public interface MiniAppRepository extends JpaRepository<MiniAppEntity, String> {
    MiniAppEntity findByAppId(String appId);
    Page<MiniAppEntity> findByAppId(String appId, Pageable paging);
    Page<MiniAppEntity> findByFileName(String fileName, Pageable paging);
    Page<MiniAppEntity> findByTypeAndFileName(MiniAppType type, String fileName, Pageable pageable);
}
