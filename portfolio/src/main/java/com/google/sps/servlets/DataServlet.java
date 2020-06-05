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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import com.google.sps.data.Comment;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/** Servlet that returns comments and adds comments.*/
@WebServlet("/comments")
public class DataServlet extends HttpServlet {
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Obtain requested comments from datastore
    String maxString = request.getParameter("max");
    String sortDirection = request.getParameter("sort");
    Integer maxInt;
    try {
      maxInt = Integer.parseInt(maxString);
    } catch (NumberFormatException ok) {
      maxInt = 5;
    }

    Query query = new Query("Comment");
    switch (sortDirection) {
      case "earliest":
        query.addSort("timestamp", SortDirection.ASCENDING);
        break;
      case "aToZ":
        query.addSort("name", SortDirection.ASCENDING);
        break;
      case "zToA":
        query.addSort("name", SortDirection.DESCENDING);
        break;
      default:
        query.addSort("timestamp", SortDirection.DESCENDING);
    }

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    // Loop through comments and then send as json to page
    List<Comment> comments = new ArrayList<>();
    for (Entity entity : results.asList(FetchOptions.Builder.withLimit(maxInt))) {
      long id = entity.getKey().getId();
      String name = (String) entity.getProperty("name");
      String commentText = (String) entity.getProperty("commentText");
      long timestamp = (long) entity.getProperty("timestamp");

      Comment comment = new Comment(id, name, commentText, timestamp);
      comments.add(comment);
    }

    Gson gson = new Gson();
    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(comments));
  }
  
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Obtain comment information from form
    String name = request.getParameter("name-input");
    String commentText = request.getParameter("comment-text-input");
    long timestamp = System.currentTimeMillis();

    // Create entity for comment
    Entity commentEntity = new Entity("Comment");
    commentEntity.setProperty("name", name);
    commentEntity.setProperty("commentText", commentText);
    commentEntity.setProperty("timestamp", timestamp);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);

    // Redirect back to the HTML page.
    response.sendRedirect("/contact_me.html");
  }
}
