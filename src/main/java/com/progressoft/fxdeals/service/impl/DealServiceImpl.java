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
        
        validateDealRequest(dealRequest);
        
        if (dealRepository.existsByDealUniqueId(dealRequest.getDealUniqueId())) {
            throw new DuplicateDealException(dealRequest.getDealUniqueId());
        }
        
        Deal deal = convertToEntity(dealRequest);
        Deal savedDeal = dealRepository.save(deal);
        
        logger.info("Successfully saved deal with ID: {} and database ID: {}", 
                   savedDeal.getDealUniqueId(), savedDeal.getId());
        
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
    public Optional<Deal> getDealEntityByUniqueId(String dealUniqueId) {
        return dealRepository.findByDealUniqueId(dealUniqueId);
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
    public Optional<DealResponseDTO> getDealById(Long id) {
        logger.debug("Fetching deal with ID: {}", id);
        
        return dealRepository.findById(id)
                .map(this::convertToResponseDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<DealResponseDTO> getDealsInTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
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
        
        return dealRepository.findDealsByCurrencyPair(
                fromCurrency.toUpperCase(), toCurrency.toUpperCase()).stream()
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
        logger.debug("Getting total count of deals");
        return dealRepository.count();
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean dealExists(String dealUniqueId) {
        logger.debug("Checking if deal exists with ID: {}", dealUniqueId);
        return dealRepository.existsByDealUniqueId(dealUniqueId);
    }
    
    private void validateDealRequest(DealRequestDTO dealRequest) {
        if (dealRequest == null) {
            throw new DealValidationException("Deal request cannot be null");
        }
        
        if (dealRequest.getDealUniqueId() == null || dealRequest.getDealUniqueId().trim().isEmpty()) {
            throw new DealValidationException("Deal unique ID is required");
        }
        
        if (dealRequest.getFromCurrency() == null || dealRequest.getFromCurrency().trim().isEmpty()) {
            throw new DealValidationException("From currency is required");
        }
        
        if (dealRequest.getToCurrency() == null || dealRequest.getToCurrency().trim().isEmpty()) {
            throw new DealValidationException("To currency is required");
        }
        
        if (dealRequest.getDealTimestamp() == null) {
            throw new DealValidationException("Deal timestamp is required");
        }
        
        if (dealRequest.getDealAmount() == null) {
            throw new DealValidationException("Deal amount is required");
        }
        
        validateCurrency(dealRequest.getFromCurrency());
        validateCurrency(dealRequest.getToCurrency());
        
        if (dealRequest.getFromCurrency().equalsIgnoreCase(dealRequest.getToCurrency())) {
            throw new DealValidationException("From currency and to currency cannot be the same");
        }
        
        if (dealRequest.getDealAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new DealValidationException("Deal amount must be greater than zero");
        }
        
        if (dealRequest.getDealTimestamp().isAfter(LocalDateTime.now())) {
            throw new DealValidationException("Deal timestamp cannot be in the future");
        }
    }
    
    private void validateCurrency(String currencyCode) {
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
    
    private Deal convertToEntity(DealRequestDTO dto) {
        return new Deal(
                dto.getDealUniqueId(),
                dto.getFromCurrency(),
                dto.getToCurrency(),
                dto.getDealTimestamp(),
                dto.getDealAmount()
        );
    }
    
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