package azure;

import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;

public enum BlobClientFactory {
    INSTANT;

    private final BlobServiceClient blobServiceClient;

    BlobClientFactory(){
        blobServiceClient = new BlobServiceClientBuilder()
                .endpoint("https://ecomoss.blob.core.windows.net/")
                .credential(DefaultCertFactory.INSTANT.getIntelliJCredential())
                .buildClient();

    }

    public BlobServiceClient getClient(){
        return blobServiceClient;
    }
}
