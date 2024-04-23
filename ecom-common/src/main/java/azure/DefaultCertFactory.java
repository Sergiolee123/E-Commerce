package azure;

import com.azure.identity.IntelliJCredential;
import com.azure.identity.IntelliJCredentialBuilder;

public enum DefaultCertFactory {
    INSTANT;

    private final IntelliJCredential intelliJCredential;

    DefaultCertFactory(){
        intelliJCredential = new IntelliJCredentialBuilder()
                // KeePass configuration isrequired only for Windows. No configuration needed for Linux / Mac.
                .keePassDatabasePath("C:\\Users\\user\\AppData\\Roaming\\JetBrains\\IntelliJIdea2024.1\\c.kdbx")
                .build();
    }

    public IntelliJCredential getIntelliJCredential() {
        return intelliJCredential;
    }
}
