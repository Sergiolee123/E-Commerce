package com.ecom.thirdParty.service;

import com.azure.storage.blob.BlobServiceClient;

public interface UserSasService {

    String getSasToken(BlobServiceClient blobServiceClient, String blobContainer, String blob);
}
