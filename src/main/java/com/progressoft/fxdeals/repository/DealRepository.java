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
    
    /**
     * Check if a deal with the given unique ID already exists
     * @param dealUniqueId the unique identifier for the deal
     * @return true if deal exists, false otherwise
     */
    boolean existsByDealUniqueId(String dealUniqueId);
    
    /**
     * Find a deal by its unique identifier
     * @param dealUniqueId the unique identifier for the deal
     * @return Optional containing the deal if found
     */
    Optional<Deal> findByDealUniqueId(String dealUniqueId);
    
    /**
     * Find deals within a specific time range
     * @param startTime the start of the time range
     * @param endTime the end of the time range
     * @return list of deals within the time range
     */
    @Query("SELECT d FROM Deal d WHERE d.dealTimestamp BETWEEN :startTime AND :endTime ORDER BY d.dealTimestamp DESC")
    List<Deal> findDealsByTimestampRange(@Param("startTime") LocalDateTime startTime, 
                                         @Param("endTime") LocalDateTime endTime);
    
    /**
     * Find deals by currency pair
     * @param fromCurrency the source currency
     * @param toCurrency the target currency
     * @return list of deals for the specified currency pair
     */
    @Query("SELECT d FROM Deal d WHERE d.fromCurrency = :fromCurrency AND d.toCurrency = :toCurrency ORDER BY d.dealTimestamp DESC")
    List<Deal> findDealsByCurrencyPair(@Param("fromCurrency") String fromCurrency, 
                                       @Param("toCurrency") String toCurrency);
    
    /**
     * Count total number of deals
     * @return total count of deals
     */
    @Query("SELECT COUNT(d) FROM Deal d")
    Long countTotalDeals();
    
    /**
     * Find the most recent deals (last N deals)
     * @param limit the maximum number of deals to return
     * @return list of most recent deals
     */
    @Query("SELECT d FROM Deal d ORDER BY d.createdAt DESC LIMIT :limit")
    List<Deal> findRecentDeals(@Param("limit") int limit);
} 