package com.ecom.thirdParty.service.impl;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.UserDelegationKey;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import com.ecom.thirdParty.service.UserSasService;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class UserSasServiceImpl implements UserSasService {
    public String getSasToken(BlobServiceClient blobServiceClient, String blobContainer, String name){
        // Request a user delegation key (you'll need to authenticate with your Microsoft Entra credentials)
        UserDelegationKey userDelegationKey = blobServiceClient.getUserDelegationKey(
                OffsetDateTime.now(),
                OffsetDateTime.now().plusMinutes(1)
        );

        BlobContainerClient blobContainerClient = blobServiceClient.getBlobContainerClient(blobContainer);
        BlobClient blobClient = blobContainerClient.getBlobClient(name);

        BlobSasPermission blobSasPermission = new BlobSasPermission();
        blobSasPermission.setCreatePermission(true);
        blobSasPermission.setAddPermission(true);
        blobSasPermission.setWritePermission(true);

        // Create SAS signature values
        BlobServiceSasSignatureValues sasValues = new BlobServiceSasSignatureValues(OffsetDateTime.now().plusMinutes(1), blobSasPermission);

        // Generate the user delegation SAS
        return blobClient.generateUserDelegationSas(sasValues, userDelegationKey);
    }
}
