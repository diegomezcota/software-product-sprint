package com.google.sps.servlets;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Shows all of the images uploaded to Cloud Storage. */
@WebServlet("/list-images")
public class ListImagesServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // List all of the uploaded files.
    String projectId = "dgomezcota-sps-spring21";
    String bucketName = "dgomezcota-sps-spring21.appspot.com";
    Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
    Bucket bucket = storage.get(bucketName);
    Page<Blob> blobs = bucket.list();
    // Prepare image links array
    ArrayList<String> sImageLinks = new ArrayList<String>();
    for (Blob blob : blobs.iterateAll()) {
        sImageLinks.add(blob.getMediaLink());
    }
    // Convert link to json
    Gson gson = new Gson();
    String json = gson.toJson(sImageLinks);
    // Return server response as JSON
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }
}