package com.rodrigomv.planetbooksback.service;

import com.rodrigomv.planetbooksback.model.dto.OrderDTO;
import com.rodrigomv.planetbooksback.model.dto.OrderItemDTO;
import com.rodrigomv.planetbooksback.model.entity.Order;
import com.rodrigomv.planetbooksback.model.entity.OrderItem;
import com.rodrigomv.planetbooksback.model.entity.User;
import com.rodrigomv.planetbooksback.model.entity.OrderStatus;
import com.rodrigomv.planetbooksback.repository.OrderRepository;
import com.rodrigomv.planetbooksback.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public OrderDTO getById(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Order no encontrado"));
        return convertToDTO(order);
    }

    public List<OrderDTO> getByUserId(Long userId) {
        return orderRepository.findByUserId(userId)
            .stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Transactional
    public OrderDTO create(OrderDTO dto) {
        User user = userRepository.findById(dto.getUserId())
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Order order = Order.builder()
            .user(user)
            .status(dto.getStatus() != null ? OrderStatus.valueOf(dto.getStatus()) : OrderStatus.PENDING)
            .totalAmount(BigDecimal.ZERO)
            .items(List.of())
            .build();

        BigDecimal total = BigDecimal.ZERO;
        if (dto.getItems() != null) {
            for (OrderItemDTO itemDto : dto.getItems()) {
                OrderItem it = OrderItem.builder()
                    .order(order)
                    .productId(itemDto.getProductId())
                    .productTitle(itemDto.getProductTitle())
                    .productImage(itemDto.getProductImage())
                    .itemKey(itemDto.getItemKey())
                    .itemTitle(itemDto.getItemTitle())
                    .itemDescription(itemDto.getItemDescription())
                    .itemPrice(itemDto.getItemPrice())
                    .quantity(itemDto.getQuantity())
                    .build();

                total = total.add(it.getItemPrice().multiply(BigDecimal.valueOf(it.getQuantity())));
                order.getItems().add(it);
            }
        }

        order.setTotalAmount(total);
        Order saved = orderRepository.save(order);
        return convertToDTO(saved);
    }

    @Transactional
    public OrderDTO updateStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Order no encontrado"));

        order.setStatus(OrderStatus.valueOf(status));
        Order saved = orderRepository.save(order);
        return convertToDTO(saved);
    }

    @Transactional
    public void delete(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new IllegalArgumentException("Order no encontrado");
        }
        orderRepository.deleteById(id);
    }

    private OrderDTO convertToDTO(Order order) {
        List<OrderItemDTO> items = order.getItems().stream().map(it -> OrderItemDTO.builder()
            .id(it.getId())
            .productId(it.getProductId())
            .productTitle(it.getProductTitle())
            .productImage(it.getProductImage())
            .itemKey(it.getItemKey())
            .itemTitle(it.getItemTitle())
            .itemDescription(it.getItemDescription())
            .itemPrice(it.getItemPrice())
            .quantity(it.getQuantity())
            .subtotal(it.getItemPrice().multiply(BigDecimal.valueOf(it.getQuantity())))
            .build()).collect(Collectors.toList());

        return OrderDTO.builder()
            .id(order.getId())
            .userId(order.getUser() != null ? order.getUser().getId() : null)
            .createdAt(order.getCreatedAt())
            .status(order.getStatus().name())
            .totalAmount(order.getTotalAmount())
            .items(items)
            .build();
    }
}
