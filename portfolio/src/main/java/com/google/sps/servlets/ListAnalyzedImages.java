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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.OrderBy;
import com.google.gson.Gson;
import com.google.sps.data.Label;

/** Servlet responsible for listing tasks. */
@WebServlet("/list-analyzed-images")
public class ListAnalyzedImages extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
        Query<Entity> query = Query.newEntityQueryBuilder().setKind("Label").setOrderBy(OrderBy.desc("timestamp"))
                .build();
        QueryResults<Entity> labelResults = datastore.run(query);

        List<Label> labels = new ArrayList<>();
        HashSet<String> uniqueLabels = new HashSet<String>();
        while (labelResults.hasNext()) {
            Entity labelEntity = labelResults.next();
            // query labelID kind
            String description = labelEntity.getString("description");
            if (!uniqueLabels.contains(description)) {
                uniqueLabels.add(description);
                Datastore datastoreImages = DatastoreOptions.getDefaultInstance().getService();
                // query images
                Query<Entity> queryImage = Query.newEntityQueryBuilder().setKind(description)
                        .setOrderBy(OrderBy.desc("timestamp")).build();
                QueryResults<Entity> imageResults = datastoreImages.run(queryImage);
                ArrayList<String> imagesFromLabel = new ArrayList<String>();
                int numberOfEntities = 0;
                while (imageResults.hasNext()) {
                    numberOfEntities += 1;
                    Entity imageEntity = imageResults.next();
                    String imageUrl = imageEntity.getString("imageUrl");
                    imagesFromLabel.add(imageUrl);
                }
                if (numberOfEntities > 1) {
                    Label label = new Label(description, imagesFromLabel);
                    labels.add(label);
                }
            }
        }

        Gson gson = new Gson();
        response.setContentType("application/json;");
        response.getWriter().println(gson.toJson(labels));
  }
}