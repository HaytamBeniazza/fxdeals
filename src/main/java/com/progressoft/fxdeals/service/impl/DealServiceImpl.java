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

import java.util.Currency;

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
        
        if (dealRepository.existsByDealUniqueId(dealRequest.getDealUniqueId())) {
            throw new DuplicateDealException(dealRequest.getDealUniqueId());
        }
        
        validateDeal(dealRequest);
        
        Deal deal = convertToEntity(dealRequest);
        Deal savedDeal = dealRepository.save(deal);
        
        logger.info("Successfully saved deal with ID: {} and database ID: {}", 
                    savedDeal.getDealUniqueId(), savedDeal.getId());
        
        return convertToResponseDTO(savedDeal);
    }
    
    private void validateDeal(DealRequestDTO dealRequest) {
        validateCurrency(dealRequest.getFromCurrency());
        validateCurrency(dealRequest.getToCurrency());
        
        if (dealRequest.getFromCurrency().equalsIgnoreCase(dealRequest.getToCurrency())) {
            throw new DealValidationException("From currency and to currency cannot be the same");
        }
        
        if (dealRequest.getDealAmount().signum() <= 0) {
            throw new DealValidationException("Deal amount must be positive");
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