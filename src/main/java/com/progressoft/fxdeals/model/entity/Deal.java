package com.progressoft.fxdeals.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "deals", indexes = {
    @Index(name = "idx_deal_unique_id", columnList = "dealUniqueId", unique = true),
    @Index(name = "idx_deal_timestamp", columnList = "dealTimestamp")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "dealUniqueId")
public class Deal {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "deal_unique_id", nullable = false, unique = true, length = 100)
    @NotBlank(message = "Deal unique ID is required")
    @Size(min = 1, max = 100, message = "Deal unique ID must be between 1 and 100 characters")
    private String dealUniqueId;
    
    @Column(name = "from_currency", nullable = false, length = 3)
    @NotBlank(message = "From currency (ordering currency) is required")
    @Pattern(regexp = "^[A-Z]{3}$", message = "From currency must be a valid 3-letter ISO currency code")
    @Setter(AccessLevel.NONE)
    private String fromCurrency;
    
    @Column(name = "to_currency", nullable = false, length = 3)
    @NotBlank(message = "To currency is required")
    @Pattern(regexp = "^[A-Z]{3}$", message = "To currency must be a valid 3-letter ISO currency code")
    @Setter(AccessLevel.NONE)
    private String toCurrency;
    
    @Column(name = "deal_timestamp", nullable = false)
    @NotNull(message = "Deal timestamp is required")
    private LocalDateTime dealTimestamp;
    
    @Column(name = "deal_amount", nullable = false, precision = 19, scale = 4)
    @NotNull(message = "Deal amount is required")
    @DecimalMin(value = "0.0001", message = "Deal amount must be greater than 0")
    @Digits(integer = 15, fraction = 4, message = "Deal amount must have at most 15 integer digits and 4 decimal places")
    private BigDecimal dealAmount;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    public void setFromCurrency(String fromCurrency) {
        this.fromCurrency = fromCurrency != null ? fromCurrency.toUpperCase() : null;
    }
    
    public void setToCurrency(String toCurrency) {
        this.toCurrency = toCurrency != null ? toCurrency.toUpperCase() : null;
    }
    
    public Deal(String dealUniqueId, String fromCurrency, String toCurrency, 
                LocalDateTime dealTimestamp, BigDecimal dealAmount) {
        this.dealUniqueId = dealUniqueId;
        setFromCurrency(fromCurrency);
        setToCurrency(toCurrency);
        this.dealTimestamp = dealTimestamp;
        this.dealAmount = dealAmount;
    }
} 