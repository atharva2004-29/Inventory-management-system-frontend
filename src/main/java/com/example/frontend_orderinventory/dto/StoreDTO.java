package com.example.frontend_orderinventory.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StoreDTO {
    private Integer storeId;
    private String storeName;
    private String webAddress;
    private String physicalAddress;
    private Double latitude;
    private Double longitude;

    public StoreDTO() {}

    public Integer getStoreId() { return storeId; }
    public void setStoreId(Integer storeId) { this.storeId = storeId; }

    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }

    public String getWebAddress() { return webAddress; }
    public void setWebAddress(String webAddress) { this.webAddress = webAddress; }

    public String getPhysicalAddress() { return physicalAddress; }
    public void setPhysicalAddress(String physicalAddress) { this.physicalAddress = physicalAddress; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
}
