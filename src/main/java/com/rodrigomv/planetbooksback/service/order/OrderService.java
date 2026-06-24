package com.rodrigomv.planetbooksback.service.order;

import com.rodrigomv.planetbooksback.model.dto.OrderDTO;
import com.rodrigomv.planetbooksback.model.dto.SalesSummaryDTO;

import java.util.List;

public interface OrderService {
    OrderDTO createOrder(Long userId);
    List<OrderDTO> getOrdersForUser(Long userId);
    OrderDTO getOrderById(Long orderId);
    List<OrderDTO> getAllOrders();
    SalesSummaryDTO exportSalesSummary();
}
