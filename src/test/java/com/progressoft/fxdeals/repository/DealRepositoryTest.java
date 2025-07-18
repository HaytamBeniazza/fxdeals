package com.progressoft.fxdeals.repository;

import com.progressoft.fxdeals.model.entity.Deal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class DealRepositoryTest {

    @Autowired
    private DealRepository dealRepository;

    private Deal testDeal1;
    private Deal testDeal2;
    private Deal testDeal3;

    @BeforeEach
    void setUp() {
        // Clean up before each test
        dealRepository.deleteAll();
        
        testDeal1 = new Deal(
            "DEAL-001",
            "USD",
            "EUR",
            LocalDateTime.now().minusHours(2),
            new BigDecimal("1000.50")
        );
        
        testDeal2 = new Deal(
            "DEAL-002",
            "GBP",
            "USD",
            LocalDateTime.now().minusHours(1),
            new BigDecimal("2500.75")
        );
        
        testDeal3 = new Deal(
            "DEAL-003",
            "USD",
            "EUR",
            LocalDateTime.now().minusMinutes(30),
            new BigDecimal("500.25")
        );
    }

    @Test
    void shouldSaveDeal() {
        // When
        Deal savedDeal = dealRepository.save(testDeal1);
        
        // Then
        assertThat(savedDeal.getId()).isNotNull();
        assertThat(savedDeal.getDealUniqueId()).isEqualTo("DEAL-001");
        assertThat(savedDeal.getFromCurrency()).isEqualTo("USD");
        assertThat(savedDeal.getToCurrency()).isEqualTo("EUR");
        assertThat(savedDeal.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldFindByDealUniqueId() {
        // Given
        dealRepository.save(testDeal1);
        
        // When
        Optional<Deal> found = dealRepository.findByDealUniqueId("DEAL-001");
        
        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getDealUniqueId()).isEqualTo("DEAL-001");
    }

    @Test
    void shouldReturnEmptyWhenDealNotFound() {
        // When
        Optional<Deal> found = dealRepository.findByDealUniqueId("NON-EXISTENT");
        
        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void shouldCheckIfDealExists() {
        // Given
        dealRepository.save(testDeal1);
        
        // When & Then
        assertThat(dealRepository.existsByDealUniqueId("DEAL-001")).isTrue();
        assertThat(dealRepository.existsByDealUniqueId("NON-EXISTENT")).isFalse();
    }

    @Test
    void shouldFindDealsByCurrencyPair() {
        // Given
        dealRepository.saveAll(List.of(testDeal1, testDeal2, testDeal3));
        
        // When
        List<Deal> usdEurDeals = dealRepository.findDealsByCurrencyPair("USD", "EUR");
        
        // Then
        assertThat(usdEurDeals).hasSize(2);
        assertThat(usdEurDeals).extracting(Deal::getDealUniqueId)
                .containsExactlyInAnyOrder("DEAL-001", "DEAL-003");
    }

    @Test
    void shouldFindDealsByTimestampRange() {
        // Given
        dealRepository.saveAll(List.of(testDeal1, testDeal2, testDeal3));
        LocalDateTime startTime = LocalDateTime.now().minusHours(3);
        LocalDateTime endTime = LocalDateTime.now().minusMinutes(45);
        
        // When
        List<Deal> dealsInRange = dealRepository.findDealsByTimestampRange(startTime, endTime);
        
        // Then
        assertThat(dealsInRange).hasSize(2);
        assertThat(dealsInRange).extracting(Deal::getDealUniqueId)
                .containsExactlyInAnyOrder("DEAL-001", "DEAL-002");
    }

    @Test
    void shouldCountTotalDeals() {
        // Given
        dealRepository.saveAll(List.of(testDeal1, testDeal2, testDeal3));
        
        // When
        Long count = dealRepository.count();
        
        // Then
        assertThat(count).isEqualTo(3);
    }

    @Test
    void shouldFindRecentDeals() {
        // Given
        dealRepository.saveAll(List.of(testDeal1, testDeal2, testDeal3));
        
        // When
        List<Deal> recentDeals = dealRepository.findRecentDeals(2);
        
        // Then
        assertThat(recentDeals).hasSize(2);
        // Should be ordered by creation time (most recent first)
        assertThat(recentDeals.get(0).getCreatedAt())
                .isAfterOrEqualTo(recentDeals.get(1).getCreatedAt());
    }

    @Test
    void shouldEnforceUniqueConstraintOnDealUniqueId() {
        // Given
        dealRepository.save(testDeal1);
        
        Deal duplicateDeal = new Deal(
            "DEAL-001", // Same unique ID
            "GBP",
            "JPY",
            LocalDateTime.now(),
            new BigDecimal("100.00")
        );
        
        // When & Then
        try {
            dealRepository.save(duplicateDeal);
            dealRepository.flush(); // Force the constraint check
            
            // Should not reach here
            assertThat(false).as("Expected constraint violation").isTrue();
        } catch (Exception e) {
            // Expected behavior - constraint violation
            assertThat(e.getMessage()).contains("constraint");
        }
    }

    @Test
    void shouldHandleEmptyRepository() {
        // When
        List<Deal> allDeals = dealRepository.findAll();
        Long count = dealRepository.count();
        List<Deal> recentDeals = dealRepository.findRecentDeals(10);
        
        // Then
        assertThat(allDeals).isEmpty();
        assertThat(count).isEqualTo(0);
        assertThat(recentDeals).isEmpty();
    }
} 