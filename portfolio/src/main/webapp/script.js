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

// Creates a map with markers
function createMap() {
  const map = new google.maps.Map(document.getElementById('map'), {center: {lat: 40.7128, lng: -74.0060}, zoom: 3});
  var greenPin = "https://maps.google.com/mapfiles/ms/icons/green-dot.png";
  var purplePin = "https://maps.google.com/mapfiles/ms/icons/purple-dot.png";
  var markers = [];
  var markerCoords = [
    {position: {lat: 40.7128, lng: -74.0060}, map: map, icon: greenPin},
    {position: {lat: 28.5383, lng: -81.3792}, map: map, icon: greenPin},
    {position: {lat: 29.4241, lng: -98.4936}, map: map, icon: greenPin},
    {position: {lat: 34.0522, lng: -118.2437}, map: map, icon: greenPin},
    {position: {lat: 37.7749, lng: -122.4194}, map: map, icon: greenPin},
    {position: {lat: 40.4406, lng: -79.9959}, map: map, icon: greenPin},
    {position: {lat: 44.4280, lng: -110.5885}, map: map, icon: greenPin},
    {position: {lat: 36.0544, lng: -112.1401}, map: map, icon: greenPin},
    {position: {lat: 39.7392, lng: -104.9903}, map: map, icon: greenPin},
    {position: {lat: 43.6532, lng: -79.3832}, map: map, icon: greenPin},
    {position: {lat: 52.1332, lng: -106.6700}, map: map, icon: greenPin},
    {position: {lat: 51.1784, lng: -115.5708}, map: map, icon: greenPin},
    {position: {lat: 31.7683, lng: 35.2137}, map: map, icon: greenPin},
    {position: {lat: 32.9646, lng: 35.4960}, map: map, icon: greenPin},
    {position: {lat: 48.4647, lng: 35.0462}, map: map, icon: greenPin},
    {position: {lat: 50.4501, lng: 30.5234}, map: map, icon: greenPin},
    {position: {lat: 64.9631, lng: -19.0208}, map: map, icon: purplePin},
    {position: {lat: -40.9006, lng: 174.8860}, map: map, icon: purplePin},
    {position: {lat: -30.5595, lng: 22.9375}, map: map, icon: purplePin},
    {position: {lat: -25.2744, lng: 133.7751}, map: map, icon: purplePin},
    {position: {lat: 39.0742, lng: 21.8243}, map: map, icon: purplePin},
    {position: {lat: 41.8719, lng: 12.5674}, map: map, icon: purplePin},
    {position: {lat: 48.8566, lng: 2.3522}, map: map, icon: purplePin},
    {position: {lat: 51.5074, lng: -0.1278}, map: map, icon: purplePin},  
    {position: {lat: 19.8968, lng: -155.5828}, map: map, icon: purplePin},  
    {position: {lat: 35.6762, lng: 139.6503}, map: map, icon: purplePin}
  ];
  for (mc of markerCoords) {
    marker = new google.maps.Marker(mc);
    markers.push(marker);
  }
}

// Global values that aren't all overwritten when getComment() is called
var g_quantity = 1;
var g_value = "POSTTIME_DESC";
const values = new Map([
  ["POSTTIME_DESC", {"SORT_BY": "postTime", "SORT_DIRECTION": "descending"}], 
  ["POSTTIME_ASC", {"SORT_BY": "postTime", "SORT_DIRECTION": "ascending"}]
]);

// Fetches adds comments to the DOM.
function getComments(quantity=g_quantity, value=g_value) {
  // Update global values
  g_quantity = quantity;
  g_value = value;
  // Create variables to create the URL
  let sortBy = values.get(value)["SORT_BY"];
  let sortDirection = values.get(value)["SORT_DIRECTION"];
  console.log(g_quantity, g_value, values.get(value), sortBy, sortDirection);
  console.log('/data?quantity=' + quantity + '&sortBy=' + sortBy + '&sortDirection=' + sortDirection);
  fetch('/data?quantity=' + quantity + '&sortBy=' + sortBy + '&sortDirection=' + sortDirection)
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
  let iElement = document.createElement('i');
  // Fill elements
  h3Element.innerText = comment.title;
  let dateTime = comment.postTime.split(" ");
  let date = dateTime.slice(0, 3).join(" ");
  let time = dateTime.slice(3, ).join(" ");
  h5Element.innerText = "Posted on " + date + " at " + time;
  h5Element.innerText += "\nPosted by " + nickname;
  pElement.innerText = comment.message;
  iElement.setAttribute("onclick", "deleteComment('" + comment.postTime + "')");
  iElement.setAttribute("class", "fa fa-trash-o fa-2x");
  iElement.setAttribute("aria-hidden", "true");
  // Add elements to the li element
  liElement.appendChild(h3Element);
  liElement.appendChild(h5Element);
  liElement.appendChild(pElement);
  liElement.appendChild(iElement);
  return liElement;
}

function deleteComment(postTime) {
  console.log("Clicked delete");
  console.log('/delete-data?postTime=' + postTime);
  fetch('/delete-data?postTime=' + postTime);
};

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
          let isAdmin = loginStatus.isAdmin;
          if (isAdmin === "true") {
            let deleteAllCommentsButton = document.getElementById('delete-all-comments-button');
            deleteAllCommentsButton.innerHTML = '<button onclick="deleteAllComments()">Delete All Comments</button>';
          }
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

function initFeedback(quantity) {
  verifyLogin();
  getComments(quantity);
}

function initHobbies() {
  makeCollapsible(); 
  createMap();
}