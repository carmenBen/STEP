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
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.sps.data.Comment;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns comments and adds comments.*/
@WebServlet("/comments")
public class CommentServlet extends HttpServlet {
    static final String NAME = "name";
    static final String TIMESTAMP = "timestamp";
    static final String USERNAME = "username";
    static final String EMAIL = "email";
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    // Obtain requested comments from datastore.
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
        query.addSort(TIMESTAMP, SortDirection.ASCENDING);
        break;
      case "aToZ":
        query.addSort(NAME, SortDirection.ASCENDING);
        break;
      case "zToA":
        query.addSort(NAME, SortDirection.DESCENDING);
        break;
      default:
        query.addSort(TIMESTAMP, SortDirection.DESCENDING);
    }

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    // Create a list of comments from the data received from the Datastore.
    List<Comment> comments = new ArrayList<>();
    for (Entity entity : results.asList(FetchOptions.Builder.withLimit(maxInt))) {
      long id = entity.getKey().getId();
      String username = (String) entity.getProperty(USERNAME);
      String email = (String) entity.getProperty(EMAIL);
      String commentText = (String) entity.getProperty("commentText");
      long timestamp = (long) entity.getProperty(TIMESTAMP);

      Comment comment = new Comment(id, username, email, commentText, timestamp);
      comments.add(comment);
    }

    UserService userService = UserServiceFactory.getUserService();
    String email = userService.getCurrentUser().getEmail();

    Gson gson = new Gson();
    response.setContentType("application/json;");
    String json = "{ \"comments\": " + gson.toJson(comments) + ", \"email\": \"" + email + "\" }";
    response.getWriter().println(json);
  }
  
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    
    // Obtain comment information from form.
    String username = getUsername(userService.getCurrentUser().getUserId());
    String email = userService.getCurrentUser().getEmail();
    String commentText = request.getParameter("comment-text-input");
    long timestamp = System.currentTimeMillis();

    // Create entity for comment.
    Entity commentEntity = new Entity("Comment");
    commentEntity.setProperty(USERNAME, username);
    commentEntity.setProperty(EMAIL, email);
    commentEntity.setProperty("commentText", commentText);
    commentEntity.setProperty(TIMESTAMP, timestamp);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);

    // Redirect back to the contact page.
    response.sendRedirect("/contact_me.html");
  }

  /** Retrieves username from entity based on id. 
   * @returns the username of the user with id, or null if the user has not set a username. 
  */
  private String getUsername(String id) {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query =
        new Query("UserInfo")
            .setFilter(new Query.FilterPredicate("id", Query.FilterOperator.EQUAL, id));
    PreparedQuery preparedQuery = datastore.prepare(query);
    Entity entity = preparedQuery.asSingleEntity();
    
    return (entity == null) ? null : (String) entity.getProperty("username");
  }
}
