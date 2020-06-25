var mapStyle = [{
  'stylers': [{'visibility': 'off'}]
}, {
  'featureType': 'landscape',
  'elementType': 'geometry',
  'stylers': [{'visibility': 'on'}, {'color': '#fcfcfc'}]
}, {
  'featureType': 'water',
  'elementType': 'geometry',
  'stylers': [{'visibility': 'on'}, {'color': '#bfd4ff'}]
}];
var map;
var dataMin = Number.MAX_VALUE, dataMax = -Number.MAX_VALUE;

function initMap() {
  // load the map
  map = new google.maps.Map(document.getElementById('map'), {
    center: {lat: 29.246630, lng: 29.678410},
    zoom: 3,
    styles: mapStyle
  });


  // set up the style rules and events for google.maps.Data
  map.data.setStyle(styleFeature);
  map.data.addListener('mouseover', mouseInToRegion);
  map.data.addListener('mouseout', mouseOutOfRegion);

  // country polygons only need to be loaded once, do them now
  loadMapShapes();
  //console.log(map.data.contains(map.data.getFeatureById()))
}

/** Loads the country boundary polygons from a GeoJSON source. */
function loadMapShapes() {
  // load US country outline polygons from a GeoJson file
  map.data.loadGeoJson('countries.geojson');

  // wait for the request to complete by listening for the first feature to be
  // added
  google.maps.event.addListener(map.data, 'addfeature', function() {
    loadCountryData();
  });
}

/**
  * Loads the country data from a simulated API call to the US Census API.
  *
  * @param {string} variable
  */
function loadCountryData() {
  map.data.forEach(function(row) {
    const dataVariable = Math.floor(Math.random() * Math.floor(100));
    // keep track of min and max values
    if (dataVariable < dataMin) {
      dataMin = dataVariable;
    }
    if (dataVariable > dataMax) {
      dataMax = dataVariable;
    }
    row.setProperty('country_data', dataVariable);
  });
}

/**
  * Applies a gradient style based on the 'country_data' column.
  * This is the callback passed to data.setStyle() and is called for each row in
  * the data set.  Check out the docs for Data.StylingFunction.
  *
  * @param {google.maps.Data.Feature} feature
  */
function styleFeature(feature) {
  var low = [5, 69, 54];  // color of smallest datum
  var high = [151, 83, 34];   // color of largest datum

  // delta represents where the value sits between the min and max
  var delta = (feature.getProperty('country_data') - dataMin) /
      (dataMax - dataMin);

  var color = [];
  for (var i = 0; i < 3; i++) {
    // calculate an integer color based on the delta
    color[i] = (high[i] - low[i]) * delta + low[i];
  }

  // determine whether to show this shape or not
  var showRow = true;
  if (feature.getProperty('country_data') == null ||
      isNaN(feature.getProperty('country_data'))) {
    showRow = false;
  }

  var outlineWeight = 0.5, zIndex = 1;
  if (feature.getProperty('country') === 'hover') {
    outlineWeight = zIndex = 2;
  }

  return {
    strokeWeight: outlineWeight,
    strokeColor: '#fff',
    zIndex: zIndex,
    fillColor: 'hsl(' + color[0] + ',' + color[1] + '%,' + color[2] + '%)',
    fillOpacity: 0.75,
    visible: showRow
  };
}

/**
  * Responds to the mouse-in event on a map shape (country).
  *
  * @param {?google.maps.MouseEvent} e
  */
function mouseInToRegion(e) {
  // set the hover country so the setStyle function can change the border
  e.feature.setProperty('country', 'hover');

  var percent = (e.feature.getProperty('country_data') - dataMin) /
      (dataMax - dataMin) * 100;

  // update the label
  document.getElementById('data-label').textContent =
      e.feature.getProperty('name');
  document.getElementById('data-value').textContent =
      e.feature.getProperty('country_data').toLocaleString();
  document.getElementById('data-box').style.display = 'block';
}

/**
  * Responds to the mouse-out event on a map shape (country).
  *
  * @param {?google.maps.MouseEvent} e
  */
function mouseOutOfRegion(e) {
  // reset the hover country, returning the border to normal
  e.feature.setProperty('country', 'normal');
}
