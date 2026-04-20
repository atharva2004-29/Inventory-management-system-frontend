package com.example.frontend_orderinventory.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderItemDTO {
    private Integer id;
    private OrderDTO order;
    private ProductDTO product;
    private Integer quantity;
    private BigDecimal unitPrice;

    public OrderItemDTO() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public OrderDTO getOrder() { return order; }
    public void setOrder(OrderDTO order) { this.order = order; }

    public ProductDTO getProduct() { return product; }
    public void setProduct(ProductDTO product) { this.product = product; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
}
