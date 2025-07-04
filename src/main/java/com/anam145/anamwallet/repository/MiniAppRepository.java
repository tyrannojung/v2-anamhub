package com.anam145.anamwallet.repository;


import com.anam145.anamwallet.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;

public interface MiniAppRepository extends JpaRepository<MiniAppEntity, String> {
    MiniAppEntity findByAppId(String appId);
    Page<MiniAppEntity> findByAppId(String appId, Pageable paging);
    Page<MiniAppEntity> findByName(String name, Pageable paging);
    Page<MiniAppEntity> findByTypeAndName(MiniAppType type, String name, Pageable pageable);
}
