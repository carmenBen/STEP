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

/*
 * Adds a random activity car to the page.
 */
function addRandomActivity() {
  const activityTitles = [
    "Bake Banana Bread!",
    "Listen to an Album!",
    "Watch a RomCom!",
    "Take a Nap!",
    "Read a Book!"
  ];

  const activityDescriptions = [
    "You've seen it all over instagram, now is the time to learn to bake banana bread! I recommend Bon Appetit's recipe(linked below), which I have been guilty of making multiple times in a week(enjoy the picture of my latest loaf below!).",
    "Listen to an entire album. Yes, I mean start to finish! When will you have the chance to after this? Some recommendations include 'Fine Line' by Harry Styles, 'After Hours' by The Weeknd, and 'Melodrama' by Lorde.",
    "A personal favorite choice, now is the time to start a group call with friends and watch (or rewatch!) a romcom. Some recommendations include 'The Half of It,' 'Someone Great,' 'Clueless,' and '10 Things I Hate About You.'",
    "zzz",
    "Yes, a real book, doesn't matter if it's paperback, hardcover, or an ebook. When was the last time you read one? I'm personally taking the time to reread the Harry Potter series.",
  ];

  const activityPhotos = [
    "images/banana.JPG",
    "images/music.JPG",
    "images/movie.JPG",
    "images/nap.JPG",
    "images/reading.JPG",
  ];

  // Pick a random activity.
  const index = Math.floor(Math.random() * activityTitles.length);
  const activityTitle = activityTitles[index];
  const activityDescription = activityDescriptions[index];
  const activityPhoto = activityPhotos[index];
  
  // Add it to the page.
  const activityTitleContainer = document.getElementById("activity-title-container");
  activityTitleContainer.innerText = activityTitle;
  const activityDescriptionContainer = document.getElementById("activity-description-container");
  activityDescriptionContainer.innerText = activityDescription;
  const activityPhotoItem = document.getElementById("activity-photo");
  activityPhotoItem.setAttribute("src",activityPhoto);
}

/**
 * Gets previous comments and loads them, formatted, to page
 * @param {number=} maxComments an optional number specifying how many comemnts
 *     to load
 */
function getComments(maxComments = 5) {
  fetch('/comments?max='+maxComments).then((response) => response.json()).then(
        (commentsData) => {
    const commentsContainer = document.getElementById('comments-list-container');
    commentsContainer.innerHTML = '';

    if (commentsData.length === 0) {
      commentsContainer.innerHTML = 'No comments currently. Comment now!';
    } else {
      let i;
      let commentsText = '';
      for (i = 0; i < commentsData.length; i++) {
        const commentEl = 
            createCommentElement(commentsData[i].name,
                commentsData[i].commentText);
        commentsContainer.appendChild(commentEl);
      }
    }
  });
}

/**
 * Changes number of comments displayed on page
 */
function changeCommentNumber() {
  getComments(document.getElementById('comment-number').value);
}

/**
 * Deletes all comments from datastore by calling DeleteDataServlet post
 */
function deleteComments() {
  fetch('/delete-data', {method: 'POST'}).then(window.location.reload(true));
}

/**
 * Creates formatted media list element for comment, from Bootstrap formatting
 * @param {string} name on comment
 * @param {string}
 * @return {Element} formatted element created from name and commentText
 */
function createCommentElement(name, commentText) {
  const liElement = document.createElement('li');
  liElement.setAttribute('class','media mt-3');

  const imgElement = document.createElement('img');
  imgElement.setAttribute('class', 'mr-3');
  imgElement.setAttribute('height', '64');
  imgElement.setAttribute('width', '64');
  imgElement.src ='images/profile.png';
  liElement.appendChild(imgElement);  

  const divElement = document.createElement('div'); 
  divElement.setAttribute('class','media-body');
  liElement.appendChild(divElement);  

  const headerElement = document.createElement('h5'); 
  headerElement.setAttribute('class','mt-0 mb-1');
  divElement.appendChild(headerElement);  

  nameNode = document.createTextNode(name); 
  headerElement.appendChild(nameNode);  
  commentTextNode = document.createTextNode(commentText); 
  divElement.appendChild(commentTextNode);  

  return liElement;
}