package com.example.frontend_orderinventory.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderDTO {
    private Integer orderId;
    private CustomerDTO customer;
    private StoreDTO store;
    private LocalDateTime orderTms;
    private String orderStatus;

    public OrderDTO() {}

    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }

    public CustomerDTO getCustomer() { return customer; }
    public void setCustomer(CustomerDTO customer) { this.customer = customer; }

    public StoreDTO getStore() { return store; }
    public void setStore(StoreDTO store) { this.store = store; }

    public LocalDateTime getOrderTms() { return orderTms; }
    public void setOrderTms(LocalDateTime orderTms) { this.orderTms = orderTms; }

    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }
}