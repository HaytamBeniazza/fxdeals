package com.progressoft.fxdeals.service;

import com.progressoft.fxdeals.model.dto.DealRequestDTO;
import com.progressoft.fxdeals.model.dto.DealResponseDTO;

public interface DealService {
    
    DealResponseDTO submitDeal(DealRequestDTO dealRequest);
} 