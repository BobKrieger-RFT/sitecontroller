package com.sitecontroller.sitecontroller.model.response;

import com.sitecontroller.sitecontroller.entity.AssetEntity;


import java.io.Serializable;

public record AssetDetailResponse(
        Long id,
        String name,
        String macAddress) implements Serializable {

    //public static AssetDetailResponse fromEntity(AssetEntity assetEntity) {
        /* 
        return new AssetDetailResponse(
                assetEntity.getId(),
                assetEntity.getName(),
                assetEntity.getMacAddress());
                */
    //}
}