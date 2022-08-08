package eu.zinovi.receipts.util.impl;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import com.google.cloud.vision.v1.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.zinovi.receipts.domain.exception.ReceiptProcessException;
import eu.zinovi.receipts.domain.model.service.ReceiptPolyJsonServiceModel;
import eu.zinovi.receipts.util.ReceiptProcessApi;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

public class GoogleReceiptProcessApi implements ReceiptProcessApi {

    private final Storage storage;
    private final ImageAnnotatorClient imageAnnotatorClient;
    private final String bucket;
    private UUID receiptId;
    private boolean isUploaded;

    private final Gson gson;


    public GoogleReceiptProcessApi(String googleCreds, String bucket) {
        JSONObject jsonObject = new JSONObject(new String(Base64.getDecoder().decode(googleCreds)));
        InputStream stream = new ByteArrayInputStream(jsonObject.toString().getBytes());

        try {


            Credentials credentials = GoogleCredentials.fromStream(stream);

            storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();

            ImageAnnotatorSettings imageAnnotatorSettings =
                    ImageAnnotatorSettings.newBuilder()
                            .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                            .build();

            imageAnnotatorClient = ImageAnnotatorClient.create(imageAnnotatorSettings);
        } catch (IOException e) {
            throw new ReceiptProcessException("Грешка при зареждане на ключовете за достъп до Google Cloud Platform");
        }
        this.bucket = bucket;

        gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
    }

    @Override
    public void setReceiptId(UUID receiptId) {
        this.receiptId = receiptId;
    }

    @Override
    public String uploadReceipt(InputStream imageStream) {

        try {
            GCSUpload(imageStream, "receipts", "jpg", true);
        } catch (IOException e) {
            throw new ReceiptProcessException("Грешка при качване на касовата бележка");
        }
        isUploaded = true;

        return "https://" + bucket + ".storage.googleapis.com/receipts/" + receiptId + ".jpg";
    }

    @Override
    public void deleteReceipt(String receiptId) {
        try {
            GCSDelete("receipts", receiptId + ".jpg");
            GCSDelete("json/poly", receiptId + ".json");
        } catch (IOException e) {
            throw new ReceiptProcessException("Грешка при изтриване на касовата бележка");
        }
    }

    @Override
    public String doOCR() {

        if (isUploaded) {
            List<AnnotateImageRequest> requests = new ArrayList<>();

            ImageSource imgSource = ImageSource.newBuilder().setGcsImageUri(
                    "gs://" + bucket + "/receipts/" + receiptId + ".jpg").build();
            Image img = Image.newBuilder().setSource(imgSource).build();
            Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();

            AnnotateImageRequest request =
                    AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
            requests.add(request);

            BatchAnnotateImagesResponse response = imageAnnotatorClient.batchAnnotateImages(requests);

            if (response.getResponsesList().size() == 0) {
                throw new ReceiptProcessException("Неуспешен анализ.");
            }

            String polygonsJson = processMLToJson(response);

            try {
                GCSUpload(new ByteArrayInputStream(polygonsJson.getBytes()),
                        "json/poly", "json", true);
            } catch (IOException e) {
                throw new ReceiptProcessException("Грешка при качване на полигоните");
            }
            return polygonsJson;
        }
        return null;
    }

    @Override
    public void close() {
        imageAnnotatorClient.close();
    }

    private void GCSDelete(String path, String fileName) throws IOException {
        storage.delete(bucket, path + "/" + fileName);
    }

    private void GCSUpload(
            InputStream imageStream, String path,
            String fileExtension, boolean isPublic) throws IOException {

        BlobId blobId = BlobId.of(bucket, path + "/" + receiptId + "." + fileExtension);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();

        storage.createFrom(blobInfo, imageStream);

        if (isPublic) {
            storage.createAcl(blobId, Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));
        }
    }

    private String processMLToJson(BatchAnnotateImagesResponse response) {
        List<ReceiptPolyJsonServiceModel> polygons = new ArrayList<>();

        for (AnnotateImageResponse imageResponse : response.getResponsesList()) {
            for (EntityAnnotation entityAnnotation : imageResponse.getTextAnnotationsList()) {
                ReceiptPolyJsonServiceModel receiptPolyModelView = ReceiptPolyJsonServiceModel.builder()
                        .x1(entityAnnotation.getBoundingPoly().getVertices(0).getX())
                        .y1(entityAnnotation.getBoundingPoly().getVertices(0).getY())
                        .x2(entityAnnotation.getBoundingPoly().getVertices(1).getX())
                        .y2(entityAnnotation.getBoundingPoly().getVertices(1).getY())
                        .x3(entityAnnotation.getBoundingPoly().getVertices(2).getX())
                        .y3(entityAnnotation.getBoundingPoly().getVertices(2).getY())
                        .x4(entityAnnotation.getBoundingPoly().getVertices(3).getX())
                        .y4(entityAnnotation.getBoundingPoly().getVertices(3).getY())
                        .text(entityAnnotation.getDescription())
                        .build();
                polygons.add(receiptPolyModelView);
            }
        }
        return gson.toJson(polygons);
    }
}
