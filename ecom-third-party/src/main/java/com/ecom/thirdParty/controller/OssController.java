package com.ecom.thirdParty.controller;

import com.azure.storage.blob.BlobServiceClient;
import com.ecom.thirdParty.factory.BlobServiceClientFactory;
import com.ecom.thirdParty.service.UserSasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("thirdparty/oss")
public class OssController {

    private UserSasService userSasService;

    private BlobServiceClient blobServiceClient = BlobServiceClientFactory.INSTANT.getClient();

    @Autowired
    public OssController(UserSasService userSasService) {
        this.userSasService = userSasService;
    }

    @RequestMapping("/policy/{blob}")
    public String policy(@PathVariable("blob") String blob){
        return userSasService.getSasToken(blobServiceClient, "ecom", blob);
    }
}
