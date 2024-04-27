package azure;


import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobItem;
import com.ecom.thirdParty.factory.BlobServiceClientFactory;
import org.junit.jupiter.api.Test;

public class BlobTest {
    @Test
    public void testList(){
        BlobServiceClient client = BlobServiceClientFactory.INSTANT.getClient();
        BlobContainerClient ecom = client.getBlobContainerClient("ecom");
        for (BlobItem blobItem: ecom.listBlobs()){
            System.out.println(blobItem.getName());
        }
    }

}
