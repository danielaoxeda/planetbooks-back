package com.rodrigomv.planetbooksback.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesSummaryDTO {
    private Long totalOrders;
    private BigDecimal totalRevenue;
    private Map<String, Long> ordersByStatus;
}
