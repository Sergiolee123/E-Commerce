package com.ecom.product.azure;

import azure.BlobClientFactory;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobItem;
import org.junit.jupiter.api.Test;

public class BlobTest {
    @Test
    public void testList(){
        BlobServiceClient client = BlobClientFactory.INSTANT.getClient();
        BlobContainerClient ecom = client.getBlobContainerClient("ecom");
        for (BlobItem blobItem: ecom.listBlobs()){
            System.out.println(blobItem.getName());
        }
    }

    public static void main(String[] args) {
        BlobServiceClient client = BlobClientFactory.INSTANT.getClient();
        BlobContainerClient ecom = client.getBlobContainerClient("ecom");
        for (BlobItem blobItem: ecom.listBlobs()){
            System.out.println(blobItem.getName());
        }
    }
}
