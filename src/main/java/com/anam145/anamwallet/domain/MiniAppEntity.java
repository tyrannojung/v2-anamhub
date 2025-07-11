package com.anam145.anamwallet.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "mini_app")
public class MiniAppEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "app_id", unique = true, nullable = false)
    private String appId;  // com.anam.{nanoId} 형식
    
    @Column(name = "manifest_name")
    private String manifestName;  // manifest.json의 name
    
    @Column(name = "manifest_version")
    private String manifestVersion;  // manifest.json의 version
    
    @Column(name = "manifest_icon")
    private String manifestIcon;  // manifest.json의 icon 경로
    
    @Column(name = "file_name", nullable = false)
    private String fileName;  // zip 파일명
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MiniAppType type;
    
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();
}

