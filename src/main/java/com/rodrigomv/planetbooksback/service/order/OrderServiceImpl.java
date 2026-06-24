package com.rodrigomv.planetbooksback.service.order;

import com.rodrigomv.planetbooksback.model.dto.OrderDTO;
import com.rodrigomv.planetbooksback.model.dto.OrderItemDTO;
import com.rodrigomv.planetbooksback.model.dto.SalesSummaryDTO;
import com.rodrigomv.planetbooksback.model.entity.Cart;
import com.rodrigomv.planetbooksback.model.entity.CartItem;
import com.rodrigomv.planetbooksback.exception.BadRequestException;
import com.rodrigomv.planetbooksback.exception.ResourceNotFoundException;
import com.rodrigomv.planetbooksback.model.entity.Order;
import com.rodrigomv.planetbooksback.model.entity.OrderItem;
import com.rodrigomv.planetbooksback.model.entity.ProductItem;
import com.rodrigomv.planetbooksback.model.enums.OrderStatus;
import com.rodrigomv.planetbooksback.repository.CartRepository;
import com.rodrigomv.planetbooksback.repository.OrderRepository;
import com.rodrigomv.planetbooksback.repository.ProductItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductItemRepository productItemRepository;

    @Override
    public OrderDTO createOrder(Long userId) {
        if (userId == null) {
            throw new BadRequestException("Se requiere userId para crear la orden");
        }

        Cart cart = cartRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado"));
        if (cart.getItems().isEmpty()) {
            throw new BadRequestException("El carrito está vacío");
        }

        Order order = Order.builder()
            .user(cart.getUser())
            .status(OrderStatus.PAID)
            .totalAmount(BigDecimal.ZERO)
            .build();

        List<OrderItem> orderItems = cart.getItems().stream()
            .map(cartItem -> buildOrderItem(order, cartItem))
            .collect(Collectors.toList());

        BigDecimal totalAmount = orderItems.stream()
            .map(item -> item.getItemPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setItems(orderItems);
        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);

        cart.getItems().clear();
        cartRepository.save(cart);

        return convertToDTO(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersForUser(Long userId) {
        return orderRepository.findByUserId(userId).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDTO getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
            .map(this::convertToDTO)
            .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SalesSummaryDTO exportSalesSummary() {
        List<Order> orders = orderRepository.findAll();
        BigDecimal totalRevenue = orders.stream()
            .map(Order::getTotalAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        Map<String, Long> ordersByStatus = orders.stream()
            .collect(Collectors.groupingBy(order -> order.getStatus().name(), Collectors.counting()));

        return SalesSummaryDTO.builder()
            .totalOrders((long) orders.size())
            .totalRevenue(totalRevenue)
            .ordersByStatus(ordersByStatus)
            .build();
    }

    private OrderItem buildOrderItem(Order order, CartItem cartItem) {
        ProductItem productItem = productItemRepository.findByProductIdAndKey(cartItem.getProductId(), cartItem.getItemKey())
            .orElseThrow(() -> new ResourceNotFoundException("ProductItem no encontrado"));

        if (productItem.getStock() < cartItem.getQuantity()) {
            throw new BadRequestException("Stock insuficiente para la orden");
        }
        productItem.setStock(productItem.getStock() - cartItem.getQuantity());
        productItemRepository.save(productItem);

        return OrderItem.builder()
            .order(order)
            .productId(cartItem.getProductId())
            .productTitle(cartItem.getProductTitle())
            .productImage(cartItem.getProductImage())
            .itemKey(cartItem.getItemKey())
            .itemTitle(cartItem.getItemTitle())
            .itemDescription(cartItem.getItemDescription())
            .itemPrice(cartItem.getItemPrice())
            .quantity(cartItem.getQuantity())
            .build();
    }

    private OrderDTO convertToDTO(Order order) {
        return OrderDTO.builder()
            .id(order.getId())
            .userId(order.getUser().getId())
            .createdAt(order.getCreatedAt())
            .status(order.getStatus().name())
            .totalAmount(order.getTotalAmount())
            .items(order.getItems().stream().map(this::convertOrderItemToDTO).collect(Collectors.toList()))
            .build();
    }

    private OrderItemDTO convertOrderItemToDTO(com.rodrigomv.planetbooksback.model.entity.OrderItem item) {
        return OrderItemDTO.builder()
            .id(item.getId())
            .orderId(item.getOrder().getId())
            .productId(item.getProductId())
            .productTitle(item.getProductTitle())
            .productImage(item.getProductImage())
            .itemKey(item.getItemKey())
            .itemTitle(item.getItemTitle())
            .itemDescription(item.getItemDescription())
            .itemPrice(item.getItemPrice())
            .quantity(item.getQuantity())
            .subtotal(item.getItemPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
            .build();
    }
}
