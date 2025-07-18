package com.progressoft.fxdeals.service.impl;

import com.progressoft.fxdeals.exception.DealValidationException;
import com.progressoft.fxdeals.exception.DuplicateDealException;
import com.progressoft.fxdeals.model.dto.DealRequestDTO;
import com.progressoft.fxdeals.model.dto.DealResponseDTO;
import com.progressoft.fxdeals.model.entity.Deal;
import com.progressoft.fxdeals.repository.DealRepository;
import com.progressoft.fxdeals.service.DealService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of Deal service with business logic
 */
@Service
@Transactional
public class DealServiceImpl implements DealService {
    
    private static final Logger logger = LoggerFactory.getLogger(DealServiceImpl.class);
    
    private final DealRepository dealRepository;
    
    @Autowired
    public DealServiceImpl(DealRepository dealRepository) {
        this.dealRepository = dealRepository;
    }
    
    @Override
    public DealResponseDTO submitDeal(DealRequestDTO dealRequest) {
        logger.info("Submitting new deal with ID: {}", dealRequest.getDealUniqueId());
        
        // Validate the deal request
        validateDealRequest(dealRequest);
        
        // Check for duplicates
        if (dealRepository.existsByDealUniqueId(dealRequest.getDealUniqueId())) {
            throw new DuplicateDealException(dealRequest.getDealUniqueId());
        }
        
        // Convert DTO to Entity
        Deal deal = convertToEntity(dealRequest);
        
        // Save the deal
        Deal savedDeal = dealRepository.save(deal);
        
        logger.info("Successfully saved deal with ID: {} and database ID: {}", 
                   savedDeal.getDealUniqueId(), savedDeal.getId());
        
        // Convert back to DTO and return
        return convertToResponseDTO(savedDeal);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<DealResponseDTO> getDealByUniqueId(String dealUniqueId) {
        logger.debug("Fetching deal with unique ID: {}", dealUniqueId);
        
        return dealRepository.findByDealUniqueId(dealUniqueId)
                .map(this::convertToResponseDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<DealResponseDTO> getAllDeals() {
        logger.debug("Fetching all deals");
        
        return dealRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<DealResponseDTO> getDealsByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        logger.debug("Fetching deals between {} and {}", startTime, endTime);
        
        if (startTime.isAfter(endTime)) {
            throw new DealValidationException("Start time cannot be after end time");
        }
        
        return dealRepository.findDealsByTimestampRange(startTime, endTime).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<DealResponseDTO> getDealsByCurrencyPair(String fromCurrency, String toCurrency) {
        logger.debug("Fetching deals for currency pair: {} -> {}", fromCurrency, toCurrency);
        
        String fromCurrencyUpper = fromCurrency.toUpperCase();
        String toCurrencyUpper = toCurrency.toUpperCase();
        
        validateCurrencyCode(fromCurrencyUpper);
        validateCurrencyCode(toCurrencyUpper);
        
        return dealRepository.findDealsByCurrencyPair(fromCurrencyUpper, toCurrencyUpper).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<DealResponseDTO> getRecentDeals(int limit) {
        logger.debug("Fetching {} most recent deals", limit);
        
        if (limit <= 0) {
            throw new DealValidationException("Limit must be greater than 0");
        }
        
        return dealRepository.findRecentDeals(limit).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getTotalDealsCount() {
        return dealRepository.countTotalDeals();
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean dealExists(String dealUniqueId) {
        return dealRepository.existsByDealUniqueId(dealUniqueId);
    }
    
    /**
     * Validate deal request data
     */
    private void validateDealRequest(DealRequestDTO dealRequest) {
        // Validate currency codes
        validateCurrencyCode(dealRequest.getFromCurrency());
        validateCurrencyCode(dealRequest.getToCurrency());
        
        // Validate that currencies are different
        if (dealRequest.getFromCurrency().equalsIgnoreCase(dealRequest.getToCurrency())) {
            throw new DealValidationException("From currency and to currency cannot be the same");
        }
        
        // Validate timestamp is not in the future
        if (dealRequest.getDealTimestamp().isAfter(LocalDateTime.now())) {
            throw new DealValidationException("Deal timestamp cannot be in the future");
        }
        
        // Additional business validations can be added here
        logger.debug("Deal request validation passed for deal ID: {}", dealRequest.getDealUniqueId());
    }
    
    /**
     * Validate currency code format and existence
     */
    private void validateCurrencyCode(String currencyCode) {
        if (currencyCode == null || currencyCode.trim().isEmpty()) {
            throw new DealValidationException("Currency code cannot be null or empty");
        }
        
        String upperCaseCode = currencyCode.toUpperCase();
        
        if (upperCaseCode.length() != 3) {
            throw new DealValidationException("Currency code must be exactly 3 characters: " + currencyCode);
        }
        
        try {
            Currency.getInstance(upperCaseCode);
        } catch (IllegalArgumentException e) {
            throw new DealValidationException("Invalid currency code: " + currencyCode);
        }
    }
    
    /**
     * Convert DealRequestDTO to Deal entity
     */
    private Deal convertToEntity(DealRequestDTO dto) {
        return new Deal(
            dto.getDealUniqueId(),
            dto.getFromCurrency().toUpperCase(),
            dto.getToCurrency().toUpperCase(),
            dto.getDealTimestamp(),
            dto.getDealAmount()
        );
    }
    
    /**
     * Convert Deal entity to DealResponseDTO
     */
    private DealResponseDTO convertToResponseDTO(Deal deal) {
        return new DealResponseDTO(
            deal.getId(),
            deal.getDealUniqueId(),
            deal.getFromCurrency(),
            deal.getToCurrency(),
            deal.getDealTimestamp(),
            deal.getDealAmount(),
            deal.getCreatedAt()
        );
    }
} 