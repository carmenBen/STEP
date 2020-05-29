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
