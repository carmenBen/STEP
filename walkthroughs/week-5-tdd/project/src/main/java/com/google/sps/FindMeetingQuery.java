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
 *      possible meeting.
 */
public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    // Returns no times if given a duration greater than a day.
    if (request.getDuration() > TimeRange.WHOLE_DAY.duration()) {
      return Arrays.asList();
    }

    // Creates a list of all events that requested attendees are already attending.
    ArrayList<Event> requiredEvents = new ArrayList();
    ArrayList<Event> optionalEvents = new ArrayList();
    for (Event event : events) {
      for (String attendee : request.getAttendees()) {
        if (event.getAttendees().contains(attendee)) {
          requiredEvents.add(event);
          // Prevents duplicate event entry if multiple required attendees attending same meeting.
          break;
        }
      }
      for (String attendee : request.getOptionalAttendees()) {
        if (event.getAttendees().contains(attendee)) {
          optionalEvents.add(event);
          // Prevents duplicate event entry if multiple optional attendees attending same meeting.
          break;
        }
      }
    }

    Set<Event> setAllEvents = new HashSet<Event>();
    setAllEvents.addAll(requiredEvents);
    setAllEvents.addAll(optionalEvents);
    ArrayList<Event> allEvents = new ArrayList<>(setAllEvents);

    Collections.sort(allEvents, Event.ORDER_BY_START);
    Collection<TimeRange> allAttendeeTimes = getAvailableTimes(allEvents, request.getDuration());
    if(allAttendeeTimes.size() > 0) {
      return allAttendeeTimes;
    }

    if(request.getAttendees().size() == 0){
      return Arrays.asList(); 
    }

    Collections.sort(requiredEvents, Event.ORDER_BY_START);
    return getAvailableTimes(requiredEvents, request.getDuration());
  }

  /** Returns TimeRange of event duration. */
  private TimeRange getTime(ArrayList<Event> events, int index) {
    return events.get(index).getWhen();
  }

  private Collection<TimeRange> getAvailableTimes(ArrayList<Event> events, long meetingDuration) {
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

      // Removes next event if it is entirely contained within current event.
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
