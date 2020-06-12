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
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/username")
public class UsernameServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html");
    UserService userService = UserServiceFactory.getUserService();
    PrintWriter out = response.getWriter();

    if (userService.isUserLoggedIn()) {
      // Display form to set username when user is logged in.
      String username = getUsername(userService.getCurrentUser().getUserId());
      String redirectUrlLogout = "/contact_me.html";
      String logoutUrl = userService.createLogoutURL(redirectUrlLogout);

      out.append("<h5>Set User</h5>")
          .append("<form method=\"POST\" action=\"/username\">")
          .append("<div class=\"form-group\">")
          .append("<label for=\"comment-text-input\">Set your username here:</label>")
          .format("<input type=\"text\" class=\"form-control\" name=\"username\" value=\"%s\" />",
              username)
          .append("</div>")
          .append("<button type=\"submit\" class=\"btn btn-primary\">Submit</button>")
          .append("</form>")
          .format("<p><br>Logout <a href=\"%s\">here</a></p>", logoutUrl);
    } else {
      // Prompt user to login then reroute to contact me page if not logged in.
      String loginUrl = userService.createLoginURL("/contact_me.html");
      out.format("<p>Login <a href=\"%s\">here</a>.</p>", loginUrl);
    }
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    if (!userService.isUserLoggedIn()) {
      response.sendRedirect("/contact_me.html");
      return;
    }

    // Add username to entity.
    String username = request.getParameter("username");
    String id = userService.getCurrentUser().getUserId();

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Entity entity = new Entity("UserInfo", id);
    entity.setProperty("id", id);
    entity.setProperty("username", username);
    datastore.put(entity);

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
    PreparedQuery results = datastore.prepare(query);
    Entity entity = results.asSingleEntity();

    return (entity == null) ? null : (String) entity.getProperty("username");
  }
}
