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
import java.util.List;
import java.util.HashSet;
import java.util.Iterator;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    // TODO: Maybe use binary tree or another data structure for availableTimes 
    // because branches split in two?
    Collection<TimeRange> availableTimes = new ArrayList<>();
    Collection<TimeRange> availableTimesWithOptional = new ArrayList<>();
    Collection<TimeRange> availableTimesWithoutOptional;
    Collection<String> attendees = request.getAttendees();
    Collection<String> optionalAttendees = request.getOptionalAttendees();
    long duration = request.getDuration();
    if (duration > TimeRange.WHOLE_DAY.duration()){
      return availableTimes;
    }
    availableTimes.add(TimeRange.WHOLE_DAY);
    getAvailableTimes(availableTimes, events, attendees, duration);
    if (!availableTimes.isEmpty()){
      availableTimesWithoutOptional = new ArrayList<>(availableTimes); // Save availableTimes without optional
      getAvailableTimes(availableTimes, events, optionalAttendees, duration); // include optional in availableTimes
      if (!availableTimes.isEmpty()){
        return availableTimes;
      } else {
        return availableTimesWithoutOptional;
      }
    } else {
      return availableTimes;
    }
  }

  private void getAvailableTimes(Collection<TimeRange> availableTimes, Collection<Event> events, Collection<String> attendees, long duration){
    Collection<TimeRange> pendingTimes = new ArrayList<>();
    if (attendees.isEmpty()){
      return;
    }
    for (Event event: events){
      for (String attendee: attendees){
        if (event.getAttendees().contains(attendee)){
          pendingTimes.clear();
          Iterator<TimeRange> iterator = availableTimes.iterator();
          while (iterator.hasNext()){
            TimeRange timeRange = iterator.next();
            if (timeRange.overlaps(event.getWhen())){
              iterator.remove();
              int beforeEventDuration = event.getWhen().start() - timeRange.start();
              if (beforeEventDuration >= duration){
                TimeRange before = TimeRange.fromStartDuration(timeRange.start(), beforeEventDuration);
                pendingTimes.add(before);
              }
              int afterEventDuration = timeRange.end() - event.getWhen().end();
              if (afterEventDuration >= duration){
                TimeRange after = TimeRange.fromStartDuration(event.getWhen().end(), afterEventDuration);
                pendingTimes.add(after);
              }
            }
          }
          availableTimes.addAll(pendingTimes);
        }
      }
    }
  }

}
