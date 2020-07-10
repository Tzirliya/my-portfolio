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

package com.google.sps.data;

import java.util.Date;

/** Class containing comments. */
public final class Comment {

  private final long id;
  private final String message;
  private final String userId;
  private final String title;
  private final Date postTime;

  
  public Comment(long id, String message, String userId, String title, Date postTime) {
    this.id = id;
    this.message = message;
    this.userId = userId;
    this.title = title;
    this.postTime = postTime;
  }

  public long getId() {
    return id;
  }

  public String getMessage() {
    return message;
  }

  public String getUserId() {
    return userId;
  }

  public String getTitle() {
    return title;
  }

  public Date getPostTime() {
    return postTime;
  }

}
