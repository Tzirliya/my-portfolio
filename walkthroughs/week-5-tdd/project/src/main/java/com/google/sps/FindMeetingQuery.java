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
    Collection<TimeRange> times = new ArrayList<>();
    Collection<String> attendees = request.getAttendees();
    Collection<String> optionalAttendees = request.getOptionalAttendees();
    Collection<String> allAttendees = new ArrayList<>(request.getAttendees());
    allAttendees.addAll(optionalAttendees);
    long duration = (long) request.getDuration();
    if (duration > TimeRange.WHOLE_DAY.duration()){
      return times;
    }
    times.add(TimeRange.WHOLE_DAY);
    if (allAttendees.isEmpty()){
      return times;
    }
    Collection<TimeRange> tempTimes;
    for (Event event: events){
      for (String attendee: allAttendees){
        if (event.getAttendees().contains(attendee)){
          tempTimes = new ArrayList<>();
          for (Iterator<TimeRange> iterator = times.iterator(); iterator.hasNext();){
            TimeRange timeRange = iterator.next();
            if (timeRange.overlaps(event.getWhen())){
              iterator.remove();
              int startDuration = event.getWhen().start() - timeRange.start();
              if (startDuration >= duration){
                TimeRange before = TimeRange.fromStartDuration(timeRange.start(), startDuration);
                tempTimes.add(before);
              }
              int endDuration = timeRange.end() - event.getWhen().end();
              if (endDuration >= duration){
                TimeRange after = TimeRange.fromStartDuration(event.getWhen().end(), endDuration);
                tempTimes.add(after);
              }
            }
          }
          times.addAll(tempTimes);
        }
      }
    }
    if (times.isEmpty()){
      times.add(TimeRange.WHOLE_DAY);
      if (attendees.isEmpty()){
        return times;
      }
      for (Event event: events){
        for (String attendee: attendees){
          if (event.getAttendees().contains(attendee)){
            tempTimes = new ArrayList<>();
            for (Iterator<TimeRange> iterator = times.iterator(); iterator.hasNext();){
              TimeRange timeRange = iterator.next();
              if (timeRange.overlaps(event.getWhen())){
                iterator.remove();
                int startDuration = event.getWhen().start() - timeRange.start();
                if (startDuration >= duration){
                  TimeRange before = TimeRange.fromStartDuration(timeRange.start(), startDuration);
                  tempTimes.add(before);
                }
                int endDuration = timeRange.end() - event.getWhen().end();
                if (endDuration >= duration){
                  TimeRange after = TimeRange.fromStartDuration(event.getWhen().end(), endDuration);
                  tempTimes.add(after);
                }
              }
            }
            times.addAll(tempTimes);
          }
        }
      }
    }
    return times;
  }
}
