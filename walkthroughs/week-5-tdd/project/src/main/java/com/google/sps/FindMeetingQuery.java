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

package com.google.sps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.HashSet;

/** Given a collection of events and a meeting request, returns a collection of Time Ranges for a
 *      possible meeting handling cases for required and optional attendees.
 */
public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    // Returns no times if given a duration greater than a day.
    if (request.getDuration() > TimeRange.WHOLE_DAY.duration()) {
      return Arrays.asList();
    }

    ArrayList<Event> requiredEvents = new ArrayList();
    ArrayList<Event> optionalEvents = new ArrayList();
    for (Event event : events) {
      // Creates a list of events that required attendees are already attending. 
      for (String attendee : request.getAttendees()) {
        if (event.getAttendees().contains(attendee)) {
          requiredEvents.add(event);
          // Prevents duplicate event entry if multiple required attendees attending same meeting.
          break;
        }
      }
      // Creates a list of events that optional attendees are already attending. 
      for (String attendee : request.getOptionalAttendees()) {
        if (event.getAttendees().contains(attendee)) {
          optionalEvents.add(event);
          // Prevents duplicate event entry if multiple optional attendees attending same meeting.
          break;
        }
      }
    }

    // Creates a list of events that optional and required guests are attending, no duplicates.
    Set<Event> setAllEvents = new HashSet<Event>();
    setAllEvents.addAll(requiredEvents);
    setAllEvents.addAll(optionalEvents);
    ArrayList<Event> allEvents = new ArrayList<>(setAllEvents);

    // Finds all available times that optional and required guests can attend, and returns this
    //     when there are available times.
    Collection<TimeRange> allAttendeeTimes = getAvailableTimes(allEvents, request.getDuration());
    if(allAttendeeTimes.size() > 0) {
      return allAttendeeTimes;
    }

    // Returns no times when there are no available times with optional attendees and there are no
    //     required attendees.
    if(request.getAttendees().size() == 0){
      return Arrays.asList(); 
    }

    // Finds and returns available times for only required attendees(when optional attendees cannot
    //     attend.
    return getAvailableTimes(requiredEvents, request.getDuration());
  }

  /** Returns TimeRange of event duration. */
  private TimeRange getTime(ArrayList<Event> events, int index) {
    return events.get(index).getWhen();
  }

  /** Returns available times given list of existing meetings and desired meeting duration. */
  private Collection<TimeRange> getAvailableTimes(ArrayList<Event> events, long meetingDuration) {
    Collections.sort(events, Event.ORDER_BY_START);
    Collection<TimeRange> possibleTimes = new ArrayList();

    // Returns entire day as available if the requested participants have no existing meetings.
    if (events.size() == 0) {
      return Arrays.asList(TimeRange.WHOLE_DAY);
    }

    for (int i = 0; i <= events.size(); i++) {
      TimeRange availableTime;

      if (i == 0) {
        availableTime = TimeRange.fromStartEnd(TimeRange.START_OF_DAY,
            getTime(events, 0).start(), false);
      } else if (i == events.size()){
        availableTime = TimeRange.fromStartEnd(getTime(events, i - 1).end(),
            TimeRange.END_OF_DAY, true);
      } else {
        availableTime = TimeRange.fromStartEnd(getTime(events, i - 1).end(), 
            getTime(events, i).start(), false);
      }

      // Adds current time window to possible times if duration is long enough for event.
      if (availableTime.duration() >= meetingDuration) {
        possibleTimes.add(availableTime);
      }

      // Removes next event(s) if it is entirely contained within current event.
      while(i+1 < events.size()) {
        if(getTime(events, i).contains(getTime(events, i + 1))) {
          events.remove(events.get(i + 1));
        } else {
          break;
        }
      }
    }

    return possibleTimes;
  }
}
