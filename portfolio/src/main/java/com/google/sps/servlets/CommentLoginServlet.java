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
public class CommentLoginServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html");
    UserService userService = UserServiceFactory.getUserService();
    PrintWriter out = response.getWriter();

    // Show link to login page if user is not logged in.
    if (!userService.isUserLoggedIn()) {
      String redirectUrlLogin = "/contact_me.html";
      String loginUrl = userService.createLoginURL(redirectUrlLogin);

      out.append("<p>Login to post a comment</p>")
          .format("<p>Login <a href=\"%s\">here</a>.</p>", loginUrl);
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
    String redirectUrlLogout = "/contact_me.html";
    String logoutUrl = userService.createLogoutURL(redirectUrlLogout);

    out.append("<p><h4>Submit your own comment:</h4></p>")
        .append("<form action=\"/comments\" method=\"POST\" id=\"comments-form\">")
        .append("<div class=\"form-group\">")
        .append("<label for=\"comment-text-input\">Comment:</label>")
        .append("<textarea class=\"form-control\" name=\"comment-text-input\"")
        .append("rows=\"3\"></textarea>")
        .append("</div>")
        .append("<button type=\"submit\" class=\"btn btn-primary\">Submit</button>")
        .append("</form>")
        .format("<p><br>Logout <a href=\"%s\">here</a></p>", logoutUrl)
        .format("<p>Commenting as %s.<button type=\"button\" class=\"btn btn-link\"", username)
        .append("onclick=\"changeUsername()\">Change Username</button></p>");
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
