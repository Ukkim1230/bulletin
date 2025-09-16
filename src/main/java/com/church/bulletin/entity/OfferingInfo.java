package com.church.bulletin.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "offering_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OfferingInfo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "service_date", nullable = false)
    private LocalDate serviceDate;
    
    @Column(name = "offering_type", nullable = false, length = 50)
    private String offeringType; // 십일조, 감사헌금, 특별헌금 등
    
    @Column(name = "purpose", length = 200)
    private String purpose; // 헌금 목적
    
    @Column(name = "account_info", length = 200)
    private String accountInfo; // 계좌 정보
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
