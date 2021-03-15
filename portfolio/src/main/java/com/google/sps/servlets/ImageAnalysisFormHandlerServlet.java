// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.FullEntity;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.protobuf.ByteString;

/**
 * Takes the image uploaded by the user, stores it in Cloud Storage, and then extracts labels using
 * Cloud Vision.
 */
@MultipartConfig
@WebServlet("/upload-to-analyze")
public class ImageAnalysisFormHandlerServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    // Get the file chosen by the user.
    Part filePart = request.getPart("image");
    String fileName = filePart.getSubmittedFileName();
    InputStream fileInputStream = filePart.getInputStream();
    byte[] imageBytes = fileInputStream.readAllBytes();

    // Upload the file to Cloud Storage and get its URL.
    String imageUrl = uploadToCloudStorage(fileName, imageBytes);

    // Get the labels of the image that the user uploaded.
    List<EntityAnnotation> imageLabels = getImageLabels(imageBytes);

    Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    KeyFactory keyFactory = datastore.newKeyFactory().setKind("Label");

    long timestamp = System.currentTimeMillis();

    // Store labels and image in Datastore
    for (EntityAnnotation label : imageLabels) {
        // agregarla a la lista de labels si es que no existe
        String description = label.getDescription();
        FullEntity labelEntity =
            Entity.newBuilder(keyFactory.newKey())
                .set("description", description)
                .set("timestamp", timestamp)
                .build();
        datastore.put(labelEntity);
        // agregar la imagen a su Kind
        KeyFactory keyFactoryLabel = datastore.newKeyFactory().setKind(description);
        FullEntity imageEntity = 
            Entity.newBuilder(keyFactoryLabel.newKey())
                .set("imageUrl", imageUrl)
                .set("timestamp", timestamp)
                .build();
        datastore.put(imageEntity);
     }
     response.sendRedirect("/analyzedGallery.html");
   }

  /**
   * Uses the Google Cloud Vision API to generate a list of labels that apply to the image
   * represented by the binary data stored in imgBytes.
   */
  private List<EntityAnnotation> getImageLabels(byte[] imageBytes) throws IOException {
    ByteString byteString = ByteString.copyFrom(imageBytes);
    Image image = Image.newBuilder().setContent(byteString).build();

    Feature feature = Feature.newBuilder().setType(Feature.Type.LABEL_DETECTION).build();
    AnnotateImageRequest request =
        AnnotateImageRequest.newBuilder().addFeatures(feature).setImage(image).build();
    List<AnnotateImageRequest> requests = new ArrayList<>();
    requests.add(request);

    ImageAnnotatorClient client = ImageAnnotatorClient.create();
    BatchAnnotateImagesResponse batchResponse = client.batchAnnotateImages(requests);
    client.close();
    List<AnnotateImageResponse> imageResponses = batchResponse.getResponsesList();
    AnnotateImageResponse imageResponse = imageResponses.get(0);

    if (imageResponse.hasError()) {
      System.err.println("Error getting image labels: " + imageResponse.getError().getMessage());
      return null;
    }

    return imageResponse.getLabelAnnotationsList();
  }

  /** Uploads a file to Cloud Storage and returns the uploaded file's URL. */
  private static String uploadToCloudStorage(String fileName, byte[] fileBytes) {
    String projectId = "dgomezcota-sps-spring21";
    String bucketName = "dgomezcota-sps-spring21.appspot.com";
    Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
    BlobId blobId = BlobId.of(bucketName, fileName);
    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
    Blob blob = storage.create(blobInfo, fileBytes);
    return blob.getMediaLink();
  }
}
