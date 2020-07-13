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

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import com.google.sps.data.Comment;
import com.google.sps.data.User;
import com.google.gson.Gson;
import java.util.Date;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    // Get comments
    String sortBy = (String) request.getParameter("sortBy");
    String sortDirection = (String) request.getParameter("sortDirection");
    Query query;
    if (sortDirection.equals("descending")){
      query = new Query("Comment").addSort(sortBy, SortDirection.DESCENDING);
    } else {
      query = new Query("Comment").addSort(sortBy, SortDirection.ASCENDING);
    }
    PreparedQuery results = datastore.prepare(query);

    List<Comment> comments = new ArrayList<>();
    int count = 0;
    int quantity = Integer.parseInt(request.getParameter("quantity"));
    for (Entity entity : results.asIterable()) {
      if (count >= quantity) {
        break;
      }
      long id = (long) entity.getKey().getId();
      String message = (String) entity.getProperty("message");
      String userId = (String) entity.getProperty("userId");
      String title = (String) entity.getProperty("title");
      Date postTime = (Date) entity.getProperty("postTime");
      Comment comment = new Comment(id, message, userId, title, postTime);
      comments.add(comment);
      count++;
    }

    // Get users
    Query query2 = new Query("User");
    PreparedQuery results2 = datastore.prepare(query2);
    
    List<User> users = new ArrayList<>();
    for (Entity entity : results2.asIterable()) {
      String id = (String) entity.getProperty("id");
      String email = (String) entity.getProperty("email");
      String nickname = (String) entity.getProperty("nickname");
      User user = new User(id, email, nickname);
      users.add(user);
    }

    // Convert lists to JSON
    Gson gson = new Gson();
    response.setContentType("application/json;");
    HashMap<String, List> data = new HashMap<>();
    data.put("comments", comments);
    data.put("users", users);
    response.getWriter().println(gson.toJson(data));

  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String message = request.getParameter("comment").trim();
    UserService userService = UserServiceFactory.getUserService();
    String userId = userService.getCurrentUser().getUserId();
    String title = request.getParameter("title").trim();
    Date postTime = new Date();
    if (!message.isEmpty() && !title.isEmpty()){
      Entity commentEntity = new Entity("Comment");
      commentEntity.setProperty("message", message);
      commentEntity.setProperty("userId", userId);
      commentEntity.setProperty("title", title);
      commentEntity.setProperty("postTime", postTime);
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      datastore.put(commentEntity);
    }
    // Redirect back to the HTML page.
    response.sendRedirect("/feedback.html");
  }
  
}
