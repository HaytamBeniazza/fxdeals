package com.progressoft.fxdeals.controller;

import com.progressoft.fxdeals.model.dto.DealRequestDTO;
import com.progressoft.fxdeals.model.dto.DealResponseDTO;
import com.progressoft.fxdeals.service.DealService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/deals")
public class DealController {
    
    private static final Logger logger = LoggerFactory.getLogger(DealController.class);
    
    private final DealService dealService;
    
    @Autowired
    public DealController(DealService dealService) {
        this.dealService = dealService;
    }
    
    @PostMapping
    public ResponseEntity<DealResponseDTO> submitDeal(@Valid @RequestBody DealRequestDTO dealRequest) {
        logger.info("Received deal submission request for deal ID: {}", dealRequest.getDealUniqueId());
        
        DealResponseDTO savedDeal = dealService.submitDeal(dealRequest);
        
        logger.info("Deal successfully submitted with ID: {}", savedDeal.getDealUniqueId());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDeal);
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "FX Deals API"
        ));
    }
} 