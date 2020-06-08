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

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that verifies login status and shows comment form based on result. */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html");
    UserService userService = UserServiceFactory.getUserService();
    PrintWriter out = response.getWriter();

    // Show link to login page if user is not logged in.
    if (!userService.isUserLoggedIn()) {
      String urlToRedirectToAfterUserLogsIn = "/contact_me.html";
      String loginUrl = userService.createLoginURL(urlToRedirectToAfterUserLogsIn);
      out.println("<p>Login to post a comment</p>");
      out.println("<p>Login <a href=\"" + loginUrl + "\">here</a>.</p>");
      return;
    } 

    // Show link to username page if user is logged in but has no username.
    String username = getUsername(userService.getCurrentUser().getUserId());
    if (username == null) {
      response.sendRedirect("/username");
      return;
    }

    // Shows comment form when user is logged in with username set.
    String userEmail = userService.getCurrentUser().getEmail();
    String urlToRedirectToAfterUserLogsOut = "/contact_me.html";
    String logoutUrl = userService.createLogoutURL(urlToRedirectToAfterUserLogsOut);

    out.println("<p><h4>Submit your own comment:</h4></p>");
    out.println("<form action=\"/comments\" method=\"POST\" id=\"comments-form\">");
    out.println("<div class=\"form-group\">");
    out.println("<label for=\"comment-text-input\">Comment:</label>");
    out.println("<textarea class=\"form-control\" name=\"comment-text-input\""
        + "rows=\"3\"></textarea>");
    out.println("</div>");
    out.println("<button type=\"submit\" class=\"btn btn-primary\">Submit</button>");
    out.println("</form>");          
    out.println("<p><br>Logout <a href=\"" + logoutUrl + "\">here</a></p>");
    out.println("<p>Commenting as " + username
        + ".<button type=\"button\" class=\"btn btn-link\""
        + "onclick=\"changeUsername()\">Change Username</button></p>");
  } 


  /** Returns the username of the user with id, or null if the user has not set a username. */
  private String getUsername(String id) {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query =
        new Query("UserInfo")
            .setFilter(new Query.FilterPredicate("id", Query.FilterOperator.EQUAL, id));
    PreparedQuery results = datastore.prepare(query);
    Entity entity = results.asSingleEntity();
    if (entity == null) {
      return null;
    }
    String username = (String) entity.getProperty("username");
    return username;
  }
}
