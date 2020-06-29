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


/**
 * Fetches a random quote from the server and adds it to the DOM.
 */
function getRandomQuote() {
  fetch('/data')
    .then(response => response.json())
    .then((comment) => {
      document.getElementById('quote-container').innerText = comment.message;
      console.log(comment.message);
    });
}

