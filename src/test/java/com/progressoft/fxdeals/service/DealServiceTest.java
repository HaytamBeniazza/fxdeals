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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
            LocalDateTime.now().minusHours(1),
            new BigDecimal("1000.50")
        );
        
        savedDeal = new Deal(
            "DEAL-001",
            "USD",
            "EUR",
            LocalDateTime.now().minusHours(1),
            new BigDecimal("1000.50")
        );
        savedDeal.setId(1L);
        savedDeal.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void shouldSubmitDealSuccessfully() {
        // Given
        when(dealRepository.existsByDealUniqueId(anyString())).thenReturn(false);
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
        when(dealRepository.existsByDealUniqueId(anyString())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> dealService.submitDeal(validDealRequest))
            .isInstanceOf(DuplicateDealException.class)
            .hasMessageContaining("DEAL-001");
        
        verify(dealRepository).existsByDealUniqueId("DEAL-001");
        verify(dealRepository, never()).save(any(Deal.class));
    }

    @Test
    void shouldThrowValidationExceptionForInvalidCurrencyCode() {
        // Given
        DealRequestDTO invalidRequest = new DealRequestDTO(
            "DEAL-002",
            "INVALID",
            "EUR",
            LocalDateTime.now().minusHours(1),
            new BigDecimal("1000.00")
        );

        // When & Then
        assertThatThrownBy(() -> dealService.submitDeal(invalidRequest))
            .isInstanceOf(DealValidationException.class)
            .hasMessageContaining("Currency code must be exactly 3 characters");
    }

    @Test
    void shouldThrowValidationExceptionForSameCurrencies() {
        // Given
        DealRequestDTO invalidRequest = new DealRequestDTO(
            "DEAL-003",
            "USD",
            "USD",
            LocalDateTime.now().minusHours(1),
            new BigDecimal("1000.00")
        );

        // When & Then
        assertThatThrownBy(() -> dealService.submitDeal(invalidRequest))
            .isInstanceOf(DealValidationException.class)
            .hasMessageContaining("From currency and to currency cannot be the same");
    }

    @Test
    void shouldThrowValidationExceptionForFutureTimestamp() {
        // Given
        DealRequestDTO invalidRequest = new DealRequestDTO(
            "DEAL-004",
            "USD",
            "EUR",
            LocalDateTime.now().plusHours(1), // Future timestamp
            new BigDecimal("1000.00")
        );

        // When & Then
        assertThatThrownBy(() -> dealService.submitDeal(invalidRequest))
            .isInstanceOf(DealValidationException.class)
            .hasMessageContaining("Deal timestamp cannot be in the future");
    }

    @Test
    void shouldGetDealByUniqueIdSuccessfully() {
        // Given
        when(dealRepository.findByDealUniqueId(anyString())).thenReturn(Optional.of(savedDeal));

        // When
        Optional<DealResponseDTO> result = dealService.getDealByUniqueId("DEAL-001");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getDealUniqueId()).isEqualTo("DEAL-001");
        
        verify(dealRepository).findByDealUniqueId("DEAL-001");
    }

    @Test
    void shouldReturnEmptyWhenDealNotFound() {
        // Given
        when(dealRepository.findByDealUniqueId(anyString())).thenReturn(Optional.empty());

        // When
        Optional<DealResponseDTO> result = dealService.getDealByUniqueId("NON-EXISTENT");

        // Then
        assertThat(result).isEmpty();
        
        verify(dealRepository).findByDealUniqueId("NON-EXISTENT");
    }

    @Test
    void shouldGetAllDealsSuccessfully() {
        // Given
        List<Deal> deals = List.of(savedDeal);
        when(dealRepository.findAll()).thenReturn(deals);

        // When
        List<DealResponseDTO> result = dealService.getAllDeals();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDealUniqueId()).isEqualTo("DEAL-001");
        
        verify(dealRepository).findAll();
    }

    @Test
    void shouldGetDealsByTimeRangeSuccessfully() {
        // Given
        LocalDateTime startTime = LocalDateTime.now().minusHours(2);
        LocalDateTime endTime = LocalDateTime.now();
        List<Deal> deals = List.of(savedDeal);
        when(dealRepository.findDealsByTimestampRange(any(), any())).thenReturn(deals);

        // When
        List<DealResponseDTO> result = dealService.getDealsInTimeRange(startTime, endTime);

        // Then
        assertThat(result).hasSize(1);
        
        verify(dealRepository).findDealsByTimestampRange(startTime, endTime);
    }

    @Test
    void shouldThrowValidationExceptionForInvalidTimeRange() {
        // Given
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = LocalDateTime.now().minusHours(1); // End before start

        // When & Then
        assertThatThrownBy(() -> dealService.getDealsInTimeRange(startTime, endTime))
            .isInstanceOf(DealValidationException.class)
            .hasMessageContaining("Start time cannot be after end time");
    }

    @Test
    void shouldGetDealsByCurrencyPairSuccessfully() {
        // Given
        List<Deal> deals = List.of(savedDeal);
        when(dealRepository.findDealsByCurrencyPair(anyString(), anyString())).thenReturn(deals);

        // When
        List<DealResponseDTO> result = dealService.getDealsByCurrencyPair("USD", "EUR");

        // Then
        assertThat(result).hasSize(1);
        
        verify(dealRepository).findDealsByCurrencyPair("USD", "EUR");
    }

    @Test
    void shouldGetRecentDealsSuccessfully() {
        // Given
        List<Deal> deals = List.of(savedDeal);
        when(dealRepository.findRecentDeals(anyInt())).thenReturn(deals);

        // When
        List<DealResponseDTO> result = dealService.getRecentDeals(10);

        // Then
        assertThat(result).hasSize(1);
        
        verify(dealRepository).findRecentDeals(10);
    }

    @Test
    void shouldThrowValidationExceptionForInvalidLimit() {
        // When & Then
        assertThatThrownBy(() -> dealService.getRecentDeals(0))
            .isInstanceOf(DealValidationException.class)
            .hasMessageContaining("Limit must be greater than 0");
    }

    @Test
    void shouldGetTotalDealsCountSuccessfully() {
        // Given
        when(dealRepository.count()).thenReturn(5L);

        // When
        long result = dealService.getTotalDealsCount();

        // Then
        assertThat(result).isEqualTo(5L);
        
        verify(dealRepository).count();
    }

    @Test
    void shouldCheckDealExistsSuccessfully() {
        // Given
        when(dealRepository.existsByDealUniqueId(anyString())).thenReturn(true);

        // When
        boolean result = dealService.dealExists("DEAL-001");

        // Then
        assertThat(result).isTrue();
        
        verify(dealRepository).existsByDealUniqueId("DEAL-001");
    }

    @Test
    void shouldNormalizeCurrencyCodeToUpperCase() {
        // Given
        DealRequestDTO requestWithLowerCase = new DealRequestDTO(
            "DEAL-005",
            "usd", // lowercase
            "eur", // lowercase
            LocalDateTime.now().minusHours(1),
            new BigDecimal("1000.00")
        );
        
        when(dealRepository.existsByDealUniqueId(anyString())).thenReturn(false);
        when(dealRepository.save(any(Deal.class))).thenReturn(savedDeal);

        // When
        DealResponseDTO result = dealService.submitDeal(requestWithLowerCase);

        // Then
        assertThat(result).isNotNull();
        
        // Verify that the saved deal has uppercase currency codes
        verify(dealRepository).save(argThat(deal -> 
            deal.getFromCurrency().equals("USD") && 
            deal.getToCurrency().equals("EUR")
        ));
    }

    @Test
    void shouldValidateThreeLetterCurrencyCode() {
        // Given
        DealRequestDTO invalidRequest = new DealRequestDTO(
            "DEAL-006",
            "US", // Only 2 letters
            "EUR",
            LocalDateTime.now().minusHours(1),
            new BigDecimal("1000.00")
        );

        // When & Then
        assertThatThrownBy(() -> dealService.submitDeal(invalidRequest))
            .isInstanceOf(DealValidationException.class)
            .hasMessageContaining("Currency code must be exactly 3 characters");
    }
} 