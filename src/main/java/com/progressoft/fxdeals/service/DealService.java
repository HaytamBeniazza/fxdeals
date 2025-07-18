package com.progressoft.fxdeals.service;

import com.progressoft.fxdeals.model.dto.DealRequestDTO;
import com.progressoft.fxdeals.model.dto.DealResponseDTO;
import com.progressoft.fxdeals.model.entity.Deal;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DealService {
    
    DealResponseDTO submitDeal(DealRequestDTO dealRequest);
    
    Optional<DealResponseDTO> getDealByUniqueId(String dealUniqueId);
    
    Optional<Deal> getDealEntityByUniqueId(String dealUniqueId);
    
    List<DealResponseDTO> getAllDeals();
    
    Optional<DealResponseDTO> getDealById(Long id);
    
    List<DealResponseDTO> getDealsByCurrencyPair(String fromCurrency, String toCurrency);
    
    List<DealResponseDTO> getDealsInTimeRange(LocalDateTime startTime, LocalDateTime endTime);
    
    List<DealResponseDTO> getRecentDeals(int limit);
    
    long getTotalDealsCount();
    
    boolean dealExists(String dealUniqueId);
} 