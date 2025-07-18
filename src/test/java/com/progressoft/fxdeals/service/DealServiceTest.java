package com.progressoft.fxdeals.service;

import com.progressoft.fxdeals.exception.DealValidationException;
import com.progressoft.fxdeals.exception.DuplicateDealException;
import com.progressoft.fxdeals.model.dto.DealRequestDTO;
import com.progressoft.fxdeals.model.dto.DealResponseDTO;
import com.progressoft.fxdeals.model.entity.Deal;
import com.progressoft.fxdeals.repository.DealRepository;
import com.progressoft.fxdeals.service.impl.DealServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DealServiceTest {

    @Mock
    private DealRepository dealRepository;

    @InjectMocks
    private DealServiceImpl dealService;

    private DealRequestDTO validDealRequest;
    private Deal savedDeal;

    @BeforeEach
    void setUp() {
        validDealRequest = new DealRequestDTO(
                "DEAL-001",
                "USD",
                "EUR", 
                LocalDateTime.of(2024, 1, 15, 10, 30),
                new BigDecimal("1000.50")
        );

        savedDeal = new Deal(
                "DEAL-001",
                "USD", 
                "EUR",
                LocalDateTime.of(2024, 1, 15, 10, 30),
                new BigDecimal("1000.50")
        );
        savedDeal.setId(1L);
        savedDeal.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void shouldSubmitDealSuccessfully() {
        // Given
        when(dealRepository.existsByDealUniqueId("DEAL-001")).thenReturn(false);
        when(dealRepository.save(any(Deal.class))).thenReturn(savedDeal);

        // When
        DealResponseDTO result = dealService.submitDeal(validDealRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getDealUniqueId()).isEqualTo("DEAL-001");
        assertThat(result.getFromCurrency()).isEqualTo("USD");
        assertThat(result.getToCurrency()).isEqualTo("EUR");
        assertThat(result.getDealAmount()).isEqualTo(new BigDecimal("1000.50"));

        verify(dealRepository).existsByDealUniqueId("DEAL-001");
        verify(dealRepository).save(any(Deal.class));
    }

    @Test
    void shouldThrowDuplicateDealExceptionWhenDealExists() {
        // Given
        when(dealRepository.existsByDealUniqueId("DEAL-001")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> dealService.submitDeal(validDealRequest))
                .isInstanceOf(DuplicateDealException.class)
                .hasMessageContaining("DEAL-001");

        verify(dealRepository).existsByDealUniqueId("DEAL-001");
        verify(dealRepository, never()).save(any(Deal.class));
    }

    @Test
    void shouldThrowValidationExceptionForInvalidFromCurrency() {
        // Given
        DealRequestDTO invalidRequest = new DealRequestDTO(
                "DEAL-002",
                "XX", // Invalid currency
                "EUR",
                LocalDateTime.now(),
                new BigDecimal("1000.00")
        );
        when(dealRepository.existsByDealUniqueId("DEAL-002")).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> dealService.submitDeal(invalidRequest))
                .isInstanceOf(DealValidationException.class)
                .hasMessageContaining("Currency code must be exactly 3 characters");
    }

    @Test
    void shouldThrowValidationExceptionForInvalidToCurrency() {
        // Given
        DealRequestDTO invalidRequest = new DealRequestDTO(
                "DEAL-003",
                "USD",
                "INVALID", // Invalid currency
                LocalDateTime.now(),
                new BigDecimal("1000.00")
        );
        when(dealRepository.existsByDealUniqueId("DEAL-003")).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> dealService.submitDeal(invalidRequest))
                .isInstanceOf(DealValidationException.class)
                .hasMessageContaining("Currency code must be exactly 3 characters");
    }

    @Test
    void shouldThrowValidationExceptionForSameCurrencies() {
        // Given
        DealRequestDTO invalidRequest = new DealRequestDTO(
                "DEAL-004",
                "USD",
                "USD", // Same currency
                LocalDateTime.now(),
                new BigDecimal("1000.00")
        );
        when(dealRepository.existsByDealUniqueId("DEAL-004")).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> dealService.submitDeal(invalidRequest))
                .isInstanceOf(DealValidationException.class)
                .hasMessageContaining("From currency and to currency cannot be the same");
    }

    @Test
    void shouldThrowValidationExceptionForNegativeAmount() {
        // Given
        DealRequestDTO invalidRequest = new DealRequestDTO(
                "DEAL-005",
                "USD",
                "EUR",
                LocalDateTime.now(),
                new BigDecimal("-100.00") // Negative amount
        );
        when(dealRepository.existsByDealUniqueId("DEAL-005")).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> dealService.submitDeal(invalidRequest))
                .isInstanceOf(DealValidationException.class)
                .hasMessageContaining("Deal amount must be positive");
    }

    @Test
    void shouldThrowValidationExceptionForZeroAmount() {
        // Given
        DealRequestDTO invalidRequest = new DealRequestDTO(
                "DEAL-006",
                "USD",
                "EUR",
                LocalDateTime.now(),
                BigDecimal.ZERO // Zero amount
        );
        when(dealRepository.existsByDealUniqueId("DEAL-006")).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> dealService.submitDeal(invalidRequest))
                .isInstanceOf(DealValidationException.class)
                .hasMessageContaining("Deal amount must be positive");
    }

    @Test
    void shouldAcceptValidCurrencyCodes() {
        // Given - Test various valid currency codes
        String[] validCurrencies = {"USD", "EUR", "GBP", "JPY", "CHF", "CAD", "AUD"};
        
        for (String currency : validCurrencies) {
            DealRequestDTO request = new DealRequestDTO(
                    "DEAL-" + currency,
                    currency,
                    "USD".equals(currency) ? "EUR" : "USD",
                    LocalDateTime.now(),
                    new BigDecimal("100.00")
            );
            
            when(dealRepository.existsByDealUniqueId("DEAL-" + currency)).thenReturn(false);
            when(dealRepository.save(any(Deal.class))).thenReturn(savedDeal);

            // When & Then - Should not throw exception
            assertThatCode(() -> dealService.submitDeal(request))
                    .doesNotThrowAnyException();
        }
    }
} 