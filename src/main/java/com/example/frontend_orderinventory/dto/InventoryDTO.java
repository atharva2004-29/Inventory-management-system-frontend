package com.example.frontend_orderinventory.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InventoryDTO {
    private Integer inventoryId;
    private ProductDTO product;
    private StoreDTO store;
    private Integer productInventory;

    public InventoryDTO() {}

    public Integer getInventoryId() { return inventoryId; }
    public void setInventoryId(Integer inventoryId) { this.inventoryId = inventoryId; }

    public ProductDTO getProduct() { return product; }
    public void setProduct(ProductDTO product) { this.product = product; }

    public StoreDTO getStore() { return store; }
    public void setStore(StoreDTO store) { this.store = store; }

    public Integer getProductInventory() { return productInventory; }
    public void setProductInventory(Integer productInventory) { this.productInventory = productInventory; }
}
