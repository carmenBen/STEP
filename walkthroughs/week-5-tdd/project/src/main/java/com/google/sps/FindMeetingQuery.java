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
    ArrayList<Event> relevantEvents = new ArrayList();
    for (Event event : events) {
      for (String attendee : request.getAttendees()) {
        if (event.getAttendees().contains(attendee)) {
          relevantEvents.add(event);
          // Prevents duplicate event entry if multiple requested attendees attending same meeting.
          break;
        }
      }
    }

    Collections.sort(relevantEvents, Event.ORDER_BY_START);
    Collection<TimeRange> possibleTimes = new ArrayList();

    // Returns entire day as available if the requested participants have no existing meetings.
    if (relevantEvents.size() == 0) {
      return Arrays.asList(TimeRange.WHOLE_DAY);
    }

    for (int i = 0; i <= relevantEvents.size(); i++) {
      TimeRange availableTime;

      if (i == 0) {
        availableTime = TimeRange.fromStartEnd(TimeRange.START_OF_DAY,
            getTime(relevantEvents, 0).start(), false);
      } else if (i == relevantEvents.size()){
        availableTime = TimeRange.fromStartEnd(getTime(relevantEvents, i - 1).end(),
            TimeRange.END_OF_DAY, true);
      } else {
        availableTime = TimeRange.fromStartEnd(getTime(relevantEvents, i - 1).end(), 
            getTime(relevantEvents, i).start(), false);
      }

      // Adds current time window to possible times if duration is long enough for event.
      if (availableTime.duration() >= request.getDuration()) {
        possibleTimes.add(availableTime);
      }

      // Removes next event if it is entirely contained within current event.
      if (i+1 < relevantEvents.size() && getTime(relevantEvents, i)
          .contains(getTime(relevantEvents, i + 1))) {
        relevantEvents.remove(relevantEvents.get(i + 1));
      }
    }

    return possibleTimes;
  }

  /** Returns TimeRange of event duration. */
  private TimeRange getTime(ArrayList<Event> events, int index) {
    return events.get(index).getWhen();
  }
}
