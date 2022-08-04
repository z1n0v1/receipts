package eu.zinovi.receipts.util.impl;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import eu.zinovi.receipts.domain.exception.ReceiptProcessException;
import eu.zinovi.receipts.util.CloudStorage;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

public class GoogleCloudStorage implements CloudStorage {

    private final Storage storage;
    private final String bucket;

    public GoogleCloudStorage(String googleCreds, String bucket){

        JSONObject jsonObject = new JSONObject(new String(Base64.getDecoder().decode(googleCreds)));
        InputStream stream = new ByteArrayInputStream(jsonObject.toString().getBytes());

        try {
            Credentials credentials = GoogleCredentials.fromStream(stream);
            storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
        } catch (IOException e) {
            throw new ReceiptProcessException("Грешка при инициализиране на Google Cloud Storage", e);
        }

        this.bucket = bucket;
    }

    @Override
    public String uploadFile(InputStream imageStream,
                             String path,
                             String fileName,
                             boolean isPublic) throws IOException {

        BlobId blobId;
        if (path == null) {
            blobId = BlobId.of(bucket, fileName);
        } else {
            blobId = BlobId.of(bucket, path + "/" + fileName);
        }
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();

        storage.createFrom(blobInfo, imageStream);

        if (isPublic) {
            storage.createAcl(blobId, Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));
        }

        return path != null ? "https://" + bucket + ".storage.googleapis.com/" + path + "/" + fileName :
                "https://" + bucket + ".storage.googleapis.com/" + fileName;
    }

}

