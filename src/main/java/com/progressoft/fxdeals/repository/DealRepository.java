package com.progressoft.fxdeals.repository;

import com.progressoft.fxdeals.model.entity.Deal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DealRepository extends JpaRepository<Deal, Long> {
    
    boolean existsByDealUniqueId(String dealUniqueId);
    
    Optional<Deal> findByDealUniqueId(String dealUniqueId);
    
    @Query("SELECT d FROM Deal d WHERE d.dealTimestamp BETWEEN :startTime AND :endTime ORDER BY d.dealTimestamp DESC")
    List<Deal> findDealsByTimestampRange(@Param("startTime") LocalDateTime startTime, 
                                         @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT d FROM Deal d WHERE d.fromCurrency = :fromCurrency AND d.toCurrency = :toCurrency ORDER BY d.dealTimestamp DESC")
    List<Deal> findDealsByCurrencyPair(@Param("fromCurrency") String fromCurrency, 
                                       @Param("toCurrency") String toCurrency);
    
    @Query("SELECT d FROM Deal d ORDER BY d.createdAt DESC LIMIT :limit")
    List<Deal> findRecentDeals(@Param("limit") int limit);
} 