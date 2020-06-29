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

import java.util.ArrayList;
import com.google.sps.data.Comment;
import com.google.gson.Gson;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  private final ArrayList<Comment> comments= new ArrayList<Comment>();
  
  //Comment comment = new Comment("Hello Tzirliya!");


  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // comments.clear();
    // for (int i = 0; i < 5; i++){
    //   Comment comment = new Comment("Comment " + Integer.toString(i));
    //   comments.add(comment);
    // }
    String json = convertToJsonUsingGson(comments);
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String message = request.getParameter("comment");
    if (!message.isEmpty()){
      Comment comment = new Comment(message);
      comments.add(comment);
    }

    // Redirect back to the HTML page.
    response.sendRedirect("/feedback.html");
  }
  
  /**
   * Converts a ServerStats instance into a JSON string using the Gson library. Note: We first added
   * the Gson library dependency to pom.xml.
   */
  private String convertToJsonUsingGson(ArrayList<Comment> comments) {
    Gson gson = new Gson();
    String json = gson.toJson(comments);
    return json;
  }
}
