package com.progressoft.fxdeals.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for deal submission requests
 */
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
    
    // Default constructor
    public DealRequestDTO() {}
    
    // Constructor
    public DealRequestDTO(String dealUniqueId, String fromCurrency, String toCurrency,
                          LocalDateTime dealTimestamp, BigDecimal dealAmount) {
        this.dealUniqueId = dealUniqueId;
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.dealTimestamp = dealTimestamp;
        this.dealAmount = dealAmount;
    }
    
    // Getters and Setters
    public String getDealUniqueId() {
        return dealUniqueId;
    }
    
    public void setDealUniqueId(String dealUniqueId) {
        this.dealUniqueId = dealUniqueId;
    }
    
    public String getFromCurrency() {
        return fromCurrency;
    }
    
    public void setFromCurrency(String fromCurrency) {
        this.fromCurrency = fromCurrency;
    }
    
    public String getToCurrency() {
        return toCurrency;
    }
    
    public void setToCurrency(String toCurrency) {
        this.toCurrency = toCurrency;
    }
    
    public LocalDateTime getDealTimestamp() {
        return dealTimestamp;
    }
    
    public void setDealTimestamp(LocalDateTime dealTimestamp) {
        this.dealTimestamp = dealTimestamp;
    }
    
    public BigDecimal getDealAmount() {
        return dealAmount;
    }
    
    public void setDealAmount(BigDecimal dealAmount) {
        this.dealAmount = dealAmount;
    }
    
    @Override
    public String toString() {
        return "DealRequestDTO{" +
                "dealUniqueId='" + dealUniqueId + '\'' +
                ", fromCurrency='" + fromCurrency + '\'' +
                ", toCurrency='" + toCurrency + '\'' +
                ", dealTimestamp=" + dealTimestamp +
                ", dealAmount=" + dealAmount +
                '}';
    }
} 