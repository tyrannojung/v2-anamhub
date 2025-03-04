package com.anam145.anamwallet.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class ModuleDto {
    @Id
    private String moduleName;
    private String moduleEntryClass;
    private String apkPath;
    private String imgPath;
}
