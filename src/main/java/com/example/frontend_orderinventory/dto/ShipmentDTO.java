package com.example.frontend_orderinventory.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ShipmentDTO {
    private Integer shipmentId;
    private StoreDTO store;
    private CustomerDTO customer;
    private String deliveryAddress;
    private String shipmentStatus;

    public ShipmentDTO() {}

    public Integer getShipmentId() { return shipmentId; }
    public void setShipmentId(Integer shipmentId) { this.shipmentId = shipmentId; }

    public StoreDTO getStore() { return store; }
    public void setStore(StoreDTO store) { this.store = store; }

    public CustomerDTO getCustomer() { return customer; }
    public void setCustomer(CustomerDTO customer) { this.customer = customer; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public String getShipmentStatus() { return shipmentStatus; }
    public void setShipmentStatus(String shipmentStatus) { this.shipmentStatus = shipmentStatus; }
}