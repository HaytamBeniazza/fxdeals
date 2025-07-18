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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for FX Deal operations
 * Simplified version without OpenAPI annotations for testing
 */
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
    public ResponseEntity<DealResponseDTO> submitDeal(
            @Valid @RequestBody DealRequestDTO dealRequest) {
        
        logger.info("Received deal submission request for deal ID: {}", dealRequest.getDealUniqueId());
        
        DealResponseDTO response = dealService.submitDeal(dealRequest);
        
        logger.info("Deal successfully submitted with ID: {}", response.getDealUniqueId());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{dealUniqueId}")
    public ResponseEntity<DealResponseDTO> getDeal(@PathVariable String dealUniqueId) {
        
        logger.debug("Fetching deal with unique ID: {}", dealUniqueId);
        
        return dealService.getDealByUniqueId(dealUniqueId)
                .map(deal -> ResponseEntity.ok(deal))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping
    public ResponseEntity<List<DealResponseDTO>> getAllDeals() {
        logger.debug("Fetching all deals");
        
        List<DealResponseDTO> deals = dealService.getAllDeals();
        return ResponseEntity.ok(deals);
    }
    
    @GetMapping("/search/currency-pair")
    public ResponseEntity<List<DealResponseDTO>> getDealsByCurrencyPair(
            @RequestParam String fromCurrency,
            @RequestParam String toCurrency) {
        
        logger.debug("Fetching deals for currency pair: {} -> {}", fromCurrency, toCurrency);
        
        List<DealResponseDTO> deals = dealService.getDealsByCurrencyPair(fromCurrency, toCurrency);
        return ResponseEntity.ok(deals);
    }
    
    @GetMapping("/recent")
    public ResponseEntity<List<DealResponseDTO>> getRecentDeals(
            @RequestParam(defaultValue = "10") int limit) {
        
        logger.debug("Fetching {} most recent deals", limit);
        
        List<DealResponseDTO> deals = dealService.getRecentDeals(limit);
        return ResponseEntity.ok(deals);
    }
    
    @GetMapping("/stats/count")
    public ResponseEntity<Map<String, Long>> getTotalDealsCount() {
        logger.debug("Fetching total deals count");
        
        long count = dealService.getTotalDealsCount();
        return ResponseEntity.ok(Map.of("totalDeals", count));
    }
    
    @GetMapping("/exists/{dealUniqueId}")
    public ResponseEntity<Map<String, Boolean>> checkDealExists(@PathVariable String dealUniqueId) {
        
        logger.debug("Checking existence of deal with unique ID: {}", dealUniqueId);
        
        boolean exists = dealService.dealExists(dealUniqueId);
        return ResponseEntity.ok(Map.of("exists", exists));
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "FX Deals Data Warehouse",
            "timestamp", LocalDateTime.now().toString()
        ));
    }
} 