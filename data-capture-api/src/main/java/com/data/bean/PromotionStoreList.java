package com.data.bean;

public class PromotionStoreList {
    private Integer promotionDetailId;

    private String storeCode;

    public Integer getPromotionDetailId() {
        return promotionDetailId;
    }

    public void setPromotionDetailId(Integer promotionDetailId) {
        this.promotionDetailId = promotionDetailId;
    }

    public String getStoreCode() {
        return storeCode;
    }

    public void setStoreCode(String storeCode) {
        this.storeCode = storeCode == null ? null : storeCode.trim();
    }
}