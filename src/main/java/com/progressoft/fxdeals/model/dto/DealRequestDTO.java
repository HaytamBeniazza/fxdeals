package com.progressoft.fxdeals.model.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DealRequestDTO {
    
    @NotBlank(message = "Deal unique ID is required")
    @Size(min = 1, max = 100, message = "Deal unique ID must be between 1 and 100 characters")
    private String dealUniqueId;
    
    @NotBlank(message = "From currency (ordering currency) is required")
    @Pattern(regexp = "^[A-Za-z]{3}$", message = "From currency must be a valid 3-letter ISO currency code")
    private String fromCurrency;
    
    @NotBlank(message = "To currency is required")
    @Pattern(regexp = "^[A-Za-z]{3}$", message = "To currency must be a valid 3-letter ISO currency code")
    private String toCurrency;
    
    @NotNull(message = "Deal timestamp is required")
    private LocalDateTime dealTimestamp;
    
    @NotNull(message = "Deal amount is required")
    @DecimalMin(value = "0.0001", message = "Deal amount must be greater than 0")
    @Digits(integer = 15, fraction = 4, message = "Deal amount must have at most 15 integer digits and 4 decimal places")
    private BigDecimal dealAmount;
} 