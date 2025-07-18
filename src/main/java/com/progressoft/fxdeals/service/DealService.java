package com.progressoft.fxdeals.service;

import com.progressoft.fxdeals.model.dto.DealRequestDTO;
import com.progressoft.fxdeals.model.dto.DealResponseDTO;
import com.progressoft.fxdeals.model.entity.Deal;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for Deal operations
 */
public interface DealService {
    
    /**
     * Submit a new deal
     * @param dealRequest the deal submission request
     * @return the created deal response
     * @throws com.progressoft.fxdeals.exception.DuplicateDealException if deal already exists
     * @throws com.progressoft.fxdeals.exception.DealValidationException if validation fails
     */
    DealResponseDTO submitDeal(DealRequestDTO dealRequest);
    
    /**
     * Get a deal by its unique ID
     * @param dealUniqueId the unique identifier
     * @return Optional containing the deal if found
     */
    Optional<DealResponseDTO> getDealByUniqueId(String dealUniqueId);
    
    /**
     * Get all deals
     * @return list of all deals
     */
    List<DealResponseDTO> getAllDeals();
    
    /**
     * Get deals within a time range
     * @param startTime start of the time range
     * @param endTime end of the time range
     * @return list of deals within the time range
     */
    List<DealResponseDTO> getDealsByTimeRange(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * Get deals by currency pair
     * @param fromCurrency source currency
     * @param toCurrency target currency
     * @return list of deals for the currency pair
     */
    List<DealResponseDTO> getDealsByCurrencyPair(String fromCurrency, String toCurrency);
    
    /**
     * Get recent deals (limited count)
     * @param limit maximum number of deals to return
     * @return list of recent deals
     */
    List<DealResponseDTO> getRecentDeals(int limit);
    
    /**
     * Get total count of deals
     * @return total number of deals in the system
     */
    long getTotalDealsCount();
    
    /**
     * Check if a deal exists by unique ID
     * @param dealUniqueId the unique identifier to check
     * @return true if deal exists, false otherwise
     */
    boolean dealExists(String dealUniqueId);
} 