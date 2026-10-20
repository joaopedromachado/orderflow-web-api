package br.com.orderflow.domain.order;

public enum OrderStatus {
    RECEIVED,
    PROCESSING,
    PROCESSED,
    PROCESSING_FAILED,
    CANCELLED;
}
