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


// Adds a random fact to the page
function addRandomFact() {
  const facts = [
    'I own over ten types of Rubik\'s Cubes',
    'I memorized one thousand digits of Pi in high school',
    'I\'m a huge fan of the Harry Potter series',
    'I love dogs, but I\'ve only ever owned a goldfish',
    'My useless talent is that I can whistle and hum at the same time',
    'I\'m the oldest of four siblings',
    'I\'m a shoe fanatic; I own more pairs of shoes than dresses'];

  // Pick a random fact
  const fact = facts[Math.floor(Math.random() * facts.length)];

  // Add it to the page
  const factContainer = document.getElementById('fact-container');
  factContainer.innerText = fact;
}

// Allows sections to collapse
function makeCollapsible() {
  var coll = document.getElementsByClassName("collapsible");
  for (var i = 0; i < coll.length; i++) {
    coll[i].addEventListener("click", function() {
      this.classList.toggle("active");
      var content = this.nextElementSibling;
      if (content.style.display === "block") {
        content.style.display = "none";
      } else {
        content.style.display = "block";
      }
    });
  }
}

// Fetches adds comments to the DOM.
function getComments(quantity) {
  console.log('/data?quantity=' + quantity);
  fetch('/data?quantity=' + quantity)
    .then(response => response.json())
    .then((response) => {
      let comments = response[0];
      let users = response[1];
      let comments_container = document.getElementById('comments-container');
      comments_container.innerHTML = "";
      for (let i = 0; i < comments.length; i++){
        let nickname = "Anonymous";
        for (let j = 0; j < users.length; j++){
          if (comments[i].userId === users[j].id){
            if (users[j].nickname !== ""){
              nickname = users[j].nickname;
            }
            break;
          }
        }
        comments_container.appendChild(createListElement(comments[i], nickname));
        console.log(comments[i]);
      }
    });
  var choices = document.getElementsByClassName("choice");
  for (var i = 0; i < choices.length; i++) {
    if (choices[i].classList.contains(quantity)){
      choices[i].classList.add("active");
    } else {
      choices[i].classList.remove("active");
    }
  }
}

// Returns an li element that contains the comment info to be displayed
function createListElement(comment, nickname) {
  // Create elements
  let liElement = document.createElement('li');
  let h3Element = document.createElement('h3');
  let h5Element = document.createElement('h5');
  let pElement = document.createElement('p');
  // Fill elements
  h3Element.innerText = comment.title;
  let dateTime = comment.postTime.split(" ");
  let date = dateTime.slice(0, 3).join(" ");
  let time = dateTime.slice(3, ).join(" ");
  h5Element.innerText = "Posted on " + date + " at " + time;
  h5Element.innerText += "\nPosted by " + nickname;
  pElement.innerText = comment.message;
  // Add elements to the li element
  liElement.appendChild(h3Element);
  liElement.appendChild(h5Element);
  liElement.appendChild(pElement);
  return liElement;
}

// Fetches to delete all comments
function deleteAllComments() {
  console.log('/delete-data');
  fetch('/delete-data');
  console.log("Deleted all comments");
}

// Ensures that user is logged-in in order to comment
function verifyLogin() {
  console.log('/login-status');
  fetch('/login-status')
    .then(loginStatus => loginStatus.json())
      .then((loginStatus) => {
        let isLoggedIn = loginStatus.isLoggedIn;
        let url = loginStatus.url;
        let nickname = loginStatus.nickname;
        let postCommentForm = document.getElementById('post-comment-form');
        let postComment = document.getElementById('post-comment');
        console.log(loginStatus);
        console.log(typeof(loginStatus));
        if (isLoggedIn === "true") {
          postComment.style.display = "block";
          if (!nickname){
            nickname = "Anonymous";
          }
          postCommentForm.innerHTML += '<p>"' + nickname + '" will be diplayed as your nickname. Click <a href=\'/nickname\';">here</a> to change your nickname.';
          postCommentForm.innerHTML += '<a href=\'' + url + '\';">Logout</a>';
        } else {
          postComment.style.display = "none";
          postCommentForm.innerHTML += '<a href=\'' + url + '\';">Login to comment</a>';
        }  
      });
}

function onload(quantity) {
  verifyLogin();
  getComments(quantity);
}