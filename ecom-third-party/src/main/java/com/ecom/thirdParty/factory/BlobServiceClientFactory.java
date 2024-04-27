package com.ecom.thirdParty.factory;

import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;

public enum BlobServiceClientFactory {
    INSTANT;

    private final BlobServiceClient blobServiceClient;

    BlobServiceClientFactory(){
        blobServiceClient = new BlobServiceClientBuilder()
                .endpoint("https://ecomoss.blob.core.windows.net/")
                .credential(DefaultCertFactory.INSTANT.getIntelliJCredential())
                .buildClient();

    }

    public BlobServiceClient getClient(){
        return blobServiceClient;
    }
}
