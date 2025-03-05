package com.anam145.anamwallet.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ModuleDto {
    @Id
    private String moduleName;
    private String moduleEntryClass;
}
