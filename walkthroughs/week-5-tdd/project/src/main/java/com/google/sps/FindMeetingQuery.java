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
import java.util.HashSet;
import java.util.Set;

/** Given a collection of events and a meeting request, returns a collection of Time Ranges. The
 *  Time Ranges are all the possible meeting options for required and optional attendees. If all
 *  attendees(required and optional) can attend one or more meeting times, return those, otherwise
 *  return all times that all required attendees can attend.
 */
public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    // No meetings possible for duration greater than a day.
    if (request.getDuration() > TimeRange.WHOLE_DAY.duration()) {
      return Arrays.asList();
    }

    ArrayList<Event> requiredEvents = new ArrayList();
    ArrayList<Event> optionalEvents = new ArrayList();
    for (Event event : events) {
      // Create a list of events that required attendees are already attending. 
      for (String attendee : request.getAttendees()) {
        if (event.getAttendees().contains(attendee)) {
          requiredEvents.add(event);
          // Prevent duplicate event entry if multiple required attendees attending same meeting.
          break;
        }
      }
      // Create a list of events that optional attendees are already attending. 
      for (String attendee : request.getOptionalAttendees()) {
        if (event.getAttendees().contains(attendee)) {
          optionalEvents.add(event);
          // Prevent duplicate event entry if multiple optional attendees attending same meeting.
          break;
        }
      }
    }

    // Create a list of events that optional and required guests are both attending, with no 
    // duplicates.
    Set<Event> setAllEvents = new HashSet<Event>();
    setAllEvents.addAll(requiredEvents);
    setAllEvents.addAll(optionalEvents);
    ArrayList<Event> allEvents = new ArrayList<>(setAllEvents);

    // Find all available times that optional and required guests can attend.
    Collection<TimeRange> allAttendeeTimes = getAvailableTimes(allEvents, request.getDuration());
    if(allAttendeeTimes.size() > 0) {
      return allAttendeeTimes;
    }

    // No meetings possible when there are no required attendees and no common time for optional
    // attendees.
    if(request.getAttendees().size() == 0){
      return Arrays.asList(); 
    }

    // Find available times for only required attendees(when optional attendees cannot attend).
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

    // Entire day as available when the requested participants have no existing meetings.
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

      // Add current time window to possible times if duration is long enough for event.
      if (availableTime.duration() >= meetingDuration) {
        possibleTimes.add(availableTime);
      }

      // Remove next event(s) if it is entirely contained within current event.
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
