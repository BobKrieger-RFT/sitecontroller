package com.sitecontroller.sitecontroller.service;

import com.sitecontroller.sitecontroller.model.response.AssetDetailResponse;

import java.util.List;

public interface IAssetService {

    List<AssetDetailResponse> getAllAssets();
    
    AssetDetailResponse createAsset(String name, String macAddress);
    AssetDetailResponse updateAsset(Long assetId, String name, String macAddress);
    AssetDetailResponse getAssetById(Long assetId);

    //void deleteAsset(Long assetId);
    //List<AssetDetailResponse> searchAssetsByName(String name);
}