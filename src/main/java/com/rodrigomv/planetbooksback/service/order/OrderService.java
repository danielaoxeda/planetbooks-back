package com.rodrigomv.planetbooksback.service.order;

import com.rodrigomv.planetbooksback.model.dto.OrderDTO;
import com.rodrigomv.planetbooksback.model.dto.SalesSummaryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderDTO createOrder(Long userId);
    Page<OrderDTO> getOrdersForUser(Long userId, Pageable pageable);
    OrderDTO getOrderById(Long orderId);
    Page<OrderDTO> getAllOrders(Pageable pageable);
    SalesSummaryDTO exportSalesSummary();
}
