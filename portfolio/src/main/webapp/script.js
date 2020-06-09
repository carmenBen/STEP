// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the 'License');
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an 'AS IS' BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/**
 * Adds a random activity card to the page. These are descriptions and images of random quarantine activities.
 */
function addActivity() {
  const activityTitles = [
    'Bake Banana Bread!',
    'Listen to an Album!',
    'Watch a RomCom!',
    'Take a Nap!',
    'Read a Book!'
  ];

  const activityDescriptions = [
    'You\'ve seen it all over instagram, now is the time to learn to bake banana bread! I recommend Bon Appetits recipe(linked below), which I have been guilty of making multiple times in a week(enjoy the picture of my latest loaf above!).',
    'Listen to an entire album. Yes, I mean start to finish! When will you have the chance to after this? Some recommendations include \'Fine Line\' by Harry Styles, \'After Hours\' by The Weeknd, and \'Melodrama\' by Lorde.',
    'A personal favorite choice, now is the time to start a group call with friends and watch (or rewatch!) a romcom. Some recommendations include \'The Half of It,\' \'Someone Great,\' \'Clueless,\' and \'10 Things I Hate About You.\'',
    'zzz',
    'Yes, a real book, doesn\'t matter if it\'s paperback, hardcover, or an ebook. When was the last time you read one? I\'m personally taking the time to reread the Harry Potter seies.',
  ];

  const activityImageFilenames = [
    'images/banana.JPG',
    'images/music.JPG',
    'images/movie.JPG',
    'images/nap.JPG',
    'images/reading.JPG',
  ];

  // Pick an activity to display.
  const index = Math.floor(Math.random() * activityTitles.length);
  const activityTitle = activityTitles[index];
  const activityDescription = activityDescriptions[index];
  const activityImageFilename = activityImageFilenames[index];
  
  // Add the chosen activity, its description, and its photo to the page.
  const activityTitleContainer = 
      document.getElementById('activity-title-container');
  activityTitleContainer.innerText = activityTitle;
  const activityDescriptionContainer = 
      document.getElementById('activity-description-container');
  activityDescriptionContainer.innerText = activityDescription;
  const activityImageFilenameItem = document.getElementById('activity-image');
  activityImageFilenameItem.setAttribute('src', activityImageFilename);
}

/**
 * Gets previous comments and loads them, formatted, to page.
 * @param {number=} maxComments an optional number specifying how many comments
 *     to load.
 * @param {string=} sort direction by string.
 */
function getComments(maxComments = 5, sortDirection = 'latest') {
  fetch('/comments?max='+maxComments+'&sort='+sortDirection).then(
        (response) => response.json()).then((commentsData) => {
    const commentsContainer = 
        document.getElementById('comments-list-container');
    commentsContainer.innerHTML = '';

    if (commentsData.length === 0) {
      commentsContainer.innerHTML = 'No comments currently. Comment now!';
    } else {
      let commentsText = '';
      for (let i = 0; i < commentsData.length; i++) {
        const commentEl = 
            createCommentElement(commentsData[i].id, commentsData[i].username,
                commentsData[i].commentText);
        commentsContainer.appendChild(commentEl);
      }
    }
  });
}

/**
 * Changes number of comments displayed on page.
 */
function changeCommentNumber() {
  getComments(document.getElementById('comment-number').value,
      document.getElementById('sort-direction').value);
}

/**
 * Changes sort order of comments displayed on page.
 */
function changeSort() {
  getComments(document.getElementById('comment-number').value,
      document.getElementById('sort-direction').value);
}

/**
 * Deletes all comments from datastore by calling DeleteDataServlet post.
 */
function deleteComments() {
  fetch('/delete-data', {method: 'POST'}).then(window.location.reload(true));
  document.getElementById('comment-lists-container').remove();
}

/**
 * Deletes a given comment from datastore by calling DeleteDataServlet post.
 * @param {number} id of comment to be deleted.
 */
function deleteComment(id) {
  fetch('/delete-data?id='+id, {method: 'POST'}).then(
      window.location.reload(true));
}

/**
 * Creates formatted media list element for comment, from Bootstrap formatting.
 * @param {number} id of comment.
 * @param {string} username on comment.
 * @param {string} comment text.
 * @return {Element} formatted element created from name and commentText.
 */
function createCommentElement(id, username, commentText) {
  const commentContainer = document.createElement('li');
  commentContainer.setAttribute('class','media mt-3');

  const imgElement = document.createElement('img');
  imgElement.setAttribute('class', 'mr-3');
  imgElement.setAttribute('height', '64');
  imgElement.setAttribute('width', '64');
  imgElement.src ='images/profile.png';
  commentContainer.appendChild(imgElement);  

  const divElement = document.createElement('div'); 
  divElement.setAttribute('class','media-body');
  commentContainer.appendChild(divElement);  

  const headerElement = document.createElement('h5'); 
  headerElement.setAttribute('class','mt-0 mb-1');
  divElement.appendChild(headerElement); 

  const deleteButtonElement = document.createElement('button');
  deleteButtonElement.setAttribute('class', 'btn btn-light');
  deleteButtonElement.setAttribute('type', 'button');
  deleteButtonElement.innerText = 'Delete Comment';
  deleteButtonElement.addEventListener('click', () => {
    deleteComment(id);
  });
  commentContainer.appendChild(deleteButtonElement);

  usernameNode = document.createTextNode(username); 
  headerElement.appendChild(usernameNode);  
  commentTextNode = document.createTextNode(commentText); 
  divElement.appendChild(commentTextNode);  

  return commentContainer;
}

/**
 * Shows comment form or login message based on whether or not user is logged
 *     in.
 */
function displayCommentsForm() {
  fetch('/login').then(response => response.text()).then((loginResponse) => {
    document.getElementById('submit-comment-container').innerHTML =
        loginResponse;
  });
}

/**
 * Displays comments and comments form or login info when contact me page
 *     loaded.
 */
function setUpContactPage() {
    getComments();
    displayCommentsForm();
}

/**
 * Displays change username form.
 */
function changeUsername() {
  fetch('/username').then(response => response.text()).then((usernameResponse) => {
    document.getElementById('submit-comment-container').innerHTML = usernameResponse;
  });
}

/** Creates a map and adds it to the page. */
function createMap() {
    console.log("test");
  const map = new google.maps.Map(
      document.getElementById('map'),
      {center: {lat: 37.422, lng: -122.084}, zoom: 16});
}
