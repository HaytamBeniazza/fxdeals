package com.progressoft.fxdeals.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.progressoft.fxdeals.model.dto.DealRequestDTO;
import com.progressoft.fxdeals.model.dto.DealResponseDTO;
import com.progressoft.fxdeals.service.DealService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DealController.class)
class DealControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DealService dealService;

    @Autowired
    private ObjectMapper objectMapper;

    private DealRequestDTO validDealRequest;
    private DealResponseDTO dealResponse;

    @BeforeEach
    void setUp() {
        validDealRequest = new DealRequestDTO(
                "DEAL-001",
                "USD",
                "EUR",
                LocalDateTime.of(2024, 1, 15, 10, 30, 0),
                new BigDecimal("1000.50")
        );

        dealResponse = new DealResponseDTO(
                1L,
                "DEAL-001",
                "USD",
                "EUR",
                LocalDateTime.of(2024, 1, 15, 10, 30, 0),
                new BigDecimal("1000.50"),
                LocalDateTime.now()
        );
    }

    @Test
    void shouldSubmitDealSuccessfully() throws Exception {
        // Given
        when(dealService.submitDeal(any(DealRequestDTO.class))).thenReturn(dealResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/deals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validDealRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.dealUniqueId").value("DEAL-001"))
                .andExpect(jsonPath("$.fromCurrency").value("USD"))
                .andExpect(jsonPath("$.toCurrency").value("EUR"))
                .andExpect(jsonPath("$.dealAmount").value(1000.50));
    }

    @Test
    void shouldReturnBadRequestForInvalidDealData() throws Exception {
        // Given
        DealRequestDTO invalidRequest = new DealRequestDTO();

        // When & Then
        mockMvc.perform(post("/api/v1/deals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnHealthCheckSuccessfully() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/deals/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("FX Deals API"));
    }
} 