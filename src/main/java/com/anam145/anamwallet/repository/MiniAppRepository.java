package com.anam145.anamwallet.repository;


import com.anam145.anamwallet.domain.MiniAppEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MiniAppRepository extends JpaRepository<MiniAppEntity, Long> {
    MiniAppEntity findByAppId(String appId);
    boolean existsByAppId(String appId);
}
