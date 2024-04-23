package azure.service;

import com.azure.storage.blob.BlobServiceClient;

public interface UserSasService {

    String getSasToken(BlobServiceClient blobServiceClient, String blobContainer, String blob);
}
