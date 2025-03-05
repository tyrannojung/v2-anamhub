package com.anam145.anamwallet.repository;


import com.anam145.anamwallet.dto.ModuleDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ModuleRepository extends JpaRepository<ModuleDto, String> {
    ModuleDto findByModuleName(String name);
    List<ModuleDto> findByModuleNameContaining(String searchKeyword, Pageable paging);
}
