package azure.service.impl;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.UserDelegationKey;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;

import java.time.OffsetDateTime;

public class UserSasServiceImpl {
    public String getSasToken(BlobServiceClient blobServiceClient, String blobContainer, String blob){
        // Request a user delegation key (you'll need to authenticate with your Microsoft Entra credentials)
        UserDelegationKey userDelegationKey = blobServiceClient.getUserDelegationKey(
                OffsetDateTime.now(),
                OffsetDateTime.now().plusMinutes(5)
        );

        BlobContainerClient blobContainerClient = blobServiceClient.getBlobContainerClient(blobContainer);
        BlobClient blobClient = blobContainerClient.getBlobClient(blob);

        BlobSasPermission blobSasPermission = new BlobSasPermission();
        blobSasPermission.setAddPermission(true);

        // Create SAS signature values
        BlobServiceSasSignatureValues sasValues = new BlobServiceSasSignatureValues(OffsetDateTime.now().plusMinutes(5), blobSasPermission);

        // Generate the user delegation SAS
        return blobClient.generateUserDelegationSas(sasValues, userDelegationKey);
    }
}
