package com.progressoft.fxdeals.model.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DealResponseDTO {
    
    private Long id;
    private String dealUniqueId;
    private String fromCurrency;
    private String toCurrency;
    private LocalDateTime dealTimestamp;
    private BigDecimal dealAmount;
    private LocalDateTime createdAt;
} 