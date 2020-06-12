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

    if (commentsData.comments.length === 0) {
      commentsContainer.innerHTML = 'No comments currently. Comment now!';
    } else {
      let commentsText = '';
      for (let i = 0; i < commentsData.comments.length; i++) {
        const currentComment = commentsData.comments[i];
        const showDelete = (commentsData.email === currentComment.email);
        const commentEl = 
            createCommentElement(currentComment.id, currentComment.username,
                currentComment.commentText, showDelete);
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
  fetch('/delete-comments', {method: 'POST'}).then(
      window.location.reload(true));
  document.getElementById('comment-lists-container').remove();
}

/**
 * Deletes a given comment from datastore by calling DeleteDataServlet post.
 * @param {number} id of comment to be deleted.
 */
function deleteComment(id) {
  fetch('/delete-comments?id='+id, {method: 'POST'}).then(
      window.location.reload(true));
}

/**
 * Creates formatted media list element for comment, from Bootstrap formatting.
 * @param {number} id of comment.
 * @param {string} name on comment.
 * @param {string} comment text.
 * @param {boolean} true if current user wrote comment, and indicating that the
 *     'Delete Comment' button should be shown.
 * @return {Element} formatted element created from name and commentText.
 */
function createCommentElement(id, name, commentText, showDelete) {
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

  if (showDelete) {
    const deleteButtonElement = document.createElement('button');
    deleteButtonElement.setAttribute('class', 'btn btn-light');
    deleteButtonElement.setAttribute('type', 'button');
    deleteButtonElement.innerText = 'Delete Comment';
    deleteButtonElement.addEventListener('click', () => {
      deleteComment(id);
    });
    commentContainer.appendChild(deleteButtonElement);
  }

  nameNode = document.createTextNode(name); 
  headerElement.appendChild(nameNode);  
  commentTextNode = document.createTextNode(commentText); 
  divElement.appendChild(commentTextNode);  

  return commentContainer;
}

/**
 * Shows form to comment or login message based on whether or not user is logged
 *     in.
 */
function displayCommentsForm() {
  fetch('/login').then(response => response.text()).then((loginResponse) => {
    document.getElementById('submit-comment-container').innerHTML =
        loginResponse;
  });
}

/**
 * Displays comments or form to comment or login when contact me page
 *     loaded.
 */
function setUpContactPage() {
    getComments();
    displayCommentsForm();
}

/**
 * Displays form to change username.
 */
function changeUsername() {
  fetch('/username').then(response => response.text()).then(
      (usernameResponse) => {
    document.getElementById('submit-comment-container').innerHTML =
        usernameResponse;
  });
}

/** Create world map with city markers. */
function createMap() {
  const map = new google.maps.Map(
      document.getElementById('map'),
      {center: {lat: 25.246630, lng: 29.678410}, zoom: 1});

  addCityMarker(
      map, 40.768093, -73.981904, 'New York City',
      'newyork');
      
  addCityMarker(
      map, -33.824032, 151.187674, 'Sydney',
      'sydney');

  addCityMarker(
      map, 14.563149, 121.036559, 'Manila',
      'manila');

  addCityMarker(
      map, 34.105419, -117.706635, 'Los Angeles',
      'losangeles');
}

/** Adds a city marker that shows info in side div when clicked. */
function addCityMarker(map, lat, lng, title, description) {
  const marker = new google.maps.Marker(
      {position: {lat: lat, lng: lng}, map: map, title: title});
  const infoDiv = document.getElementById(description);
  
  marker.addListener('click', () => {
    document.getElementById("marker-click-text").style.display = "none";
    document.getElementById("newyork-map").style.display = "none";
    document.getElementById("losangeles-map").style.display = "none";
    const markerDivs = document.getElementsByClassName("marker-info");
    for (let i = 0; i < markerDivs.length; i++) {
        markerDivs[i].style.display = "none";
    }
    infoDiv.style.display = "block";
  });
}

/** Creates New York map with place markers. */
function createNewyorkMap() {
  const map = new google.maps.Map(
      document.getElementById('newyork-map'),
      {center: {lat: 40.747345, lng: -73.988955}, zoom: 12});
  document.getElementById("newyork-map").style.display = "block";

  addLandmark(
      map, 40.717839, -74.013735, 'Stuyvesant High School',
      'I attended Stuyvesant High School from 2014-2018');

  addLandmark(
      map, 40.704545, -74.009393, 'Financial District',
      'I lived in the financial district for almost a year from 2014-2015');

  addLandmark(
      map, 40.767702, -73.984881, 'Hell\'s Kitchen',
      'I\'ve live in Hell\'s Kitchen since 2015');

  addLandmark(
      map, 40.718845, -74.034609, 'Tradeweb',
      'I interned at Tradeweb in the summer of 2019');

  addLandmark(
      map, 40.749886, -73.993602, 'Madison Square Garden',
      'The world famous Madison Square Garden, my favorite concert venue');

  addLandmark(
      map, 40.780943, -73.966083, 'The Great Lawn',
      'I love to hang out on the Great Lawn during the summer with friends');

  addLandmark(
      map, 40.741531, -73.988014, 'Shake Shack',
      'Shake Shack - my favorite burgers in the world!');

  addLandmark(
      map, 40.734906, -73.988437, 'Irving Plaza',
      'Irving plaza is my favorite small concert venue!');
}

/** Creates Los Angeles map with place markers. */
function createLAMap() {
  const map = new google.maps.Map(
      document.getElementById('losangeles-map'),
      {center: {lat: 33.956608, lng: -117.931550}, zoom: 9});
  document.getElementById("losangeles-map").style.display = "block";

  addLandmark(
      map, 34.105419, -117.706635, 'Harvey Mudd College',
      'I attend Harvey Mudd College currently.');

  addLandmark(
      map, 34.011472, -118.499552, 'Santa Monica',
      'The famous Santa Monica Pier is my favorite place for a beach trip!');

  addLandmark(
      map, 33.958745, -118.342610, 'The Forum',
      'The Forum is one of my favorite concert venues in LA!');

  addLandmark(
      map, 33.657268, -118.005065, 'Huntington Beach',
      'Huntington Beach is my favorite place to watch the sunrise!');

  addLandmark(
      map, 34.071896, -117.552049, 'Ontario Mills',
      'Ontario Mills is the outlet mall near my school with the best deals!');

  addLandmark(
      map, 33.811833, -117.919285, 'Disneyland',
      'Disneyland! What more is there to say.');
}

/** Adds a marker that shows an info window when clicked. */
function addLandmark(map, lat, lng, title, description) {
  const marker = new google.maps.Marker(
      {position: {lat: lat, lng: lng}, map: map, title: title});

  const infoWindow = new google.maps.InfoWindow({content: description});
  marker.addListener('click', () => {
    infoWindow.open(map, marker);
  });
}
