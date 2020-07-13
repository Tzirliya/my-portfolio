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
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.util.ArrayList;
import java.util.List;
import com.google.sps.data.Comment;
import com.google.gson.Gson;
import java.util.Date;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.*;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/delete-data")
public class DeleteDataServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String deleteAll = (String) request.getParameter("deleteAll");
    if (deleteAll.equals("true")) {
      UserService userService = UserServiceFactory.getUserService();
      String isAdmin = String.valueOf(userService.isUserAdmin());
      if (isAdmin.equals("true")){
        Query query = new Query("Comment").setKeysOnly();
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery results = datastore.prepare(query);  
        List<Key> keys = new ArrayList<>();  
        for (Entity entity : results.asIterable()) {
        keys.add(entity.getKey());
        }
        datastore.delete(keys);
        System.out.println("Deleted all comments");
      }
    } else {
      Query query = new Query("Comment");
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      PreparedQuery results = datastore.prepare(query);
      UserService userService = UserServiceFactory.getUserService();
      String userId = userService.getCurrentUser().getUserId();
      String isAdmin = String.valueOf(userService.isUserAdmin());
      long id = Long.parseLong(request.getParameter("id"));
      for (Entity entity : results.asIterable()) {
        String e_userId = (String) entity.getProperty("userId");
        long e_id = (long) entity.getKey().getId();
        if (e_id == id && (e_userId.equals(userId) || isAdmin.equals("true"))){
          Key key = entity.getKey();
          datastore.delete(key);
          System.out.println("Deleted a comment");
          break;
        }
      }
    }
    // Redirect back to the HTML page.
    response.sendRedirect("/feedback.html");
  }
  
}
