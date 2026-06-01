package com.rodrigomv.planetbooksback.model.enums;

/**
 * Enum que define los posibles estados de una orden.
 * PENDING: orden creada pero no confirmada
 * PAID: orden pagada
 * CANCELLED: orden cancelada
 * SHIPPED: orden enviada
 * COMPLETED: orden completada
 */
public enum OrderStatus {
    PENDING,
    PAID,
    CANCELLED,
    SHIPPED,
    COMPLETED
}

