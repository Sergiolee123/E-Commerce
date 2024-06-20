package com.ecom.thirdParty.factory;

import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.UserDelegationKey;

import java.time.OffsetDateTime;
import java.util.concurrent.ConcurrentHashMap;

public enum UserDelegationKeyFactory {
    INSTANT;

    private final ConcurrentHashMap<String, UserDelegationKey> userDelegationKeyMap = new ConcurrentHashMap<>();

    public UserDelegationKey getDelegationKey(BlobServiceClient blobServiceClient){
        String accountName = blobServiceClient.getAccountName();
        UserDelegationKey delegationKey;
        delegationKey = userDelegationKeyMap.computeIfAbsent(accountName, key -> blobServiceClient.getUserDelegationKey(
                OffsetDateTime.now(),
                OffsetDateTime.now().plusDays(1)
        ));

        if (OffsetDateTime.now().isAfter(userDelegationKeyMap.get(accountName).getSignedExpiry())) {
            delegationKey = userDelegationKeyMap.computeIfPresent(accountName, (key, value) -> blobServiceClient.getUserDelegationKey(
                    OffsetDateTime.now(),
                    OffsetDateTime.now().plusDays(1)
            ));
        }
        return delegationKey;
    }
}
