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
import java.util.Set;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    ArrayList<TimeRange> availableTimes = new ArrayList<>();
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
      return getAvailableTimesWithMaxOptional(availableTimes, events, optionalAttendees, duration);
    } else {
      return availableTimes;
    }
  }

  private void getAvailableTimes(Collection<TimeRange> availableTimes, Collection<Event> events, Collection<String> attendees, long duration){
    if (attendees.isEmpty()){
      return;
    }
    Collection<TimeRange> pendingTimes = new ArrayList<>();
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

  private Collection<TimeRange> getAvailableTimesWithMaxOptional(ArrayList<TimeRange> availableTimes, Collection<Event> events, Collection<String> attendees, long duration){
    if (attendees.isEmpty()){
      return availableTimes;
    }
    // Create Hashtable to hold every TimeRange and a Hashset of all attendees available at that time
    Hashtable<TimeRange, HashSet<String>> allTimeRangesAvailableAttendees = new Hashtable<>();
    for (TimeRange timeRange: availableTimes){
      allTimeRangesAvailableAttendees.put(timeRange, new HashSet<>(attendees));
    }
    HashSet<String> timeRangeAvailableAttendees = new HashSet<>();
    HashSet<String> eventAttendees = new HashSet<>();
    Hashtable<TimeRange, HashSet<String>> pendingTimeRangeAvailableAttendees = new Hashtable<>();
    for (Event event: events){
      pendingTimeRangeAvailableAttendees.clear();
      Set<TimeRange> timeRanges = allTimeRangesAvailableAttendees.keySet();
      for (TimeRange timeRange: timeRanges){
        if (timeRange.overlaps(event.getWhen())){
          timeRangeAvailableAttendees.addAll(allTimeRangesAvailableAttendees.get(timeRange));
          int beforeEventDuration = event.getWhen().start() - timeRange.start();
          if (beforeEventDuration >= duration){
            TimeRange before = TimeRange.fromStartDuration(timeRange.start(), beforeEventDuration);
            // if the new time slot isn't already saved, save it
            if (pendingTimeRangeAvailableAttendees.get(before) == null && allTimeRangesAvailableAttendees.get(before) == null){
              pendingTimeRangeAvailableAttendees.put(before, new HashSet<>(timeRangeAvailableAttendees));
            }
          }
          int afterEventDuration = timeRange.end() - event.getWhen().end();
          if (afterEventDuration >= duration){
            TimeRange after = TimeRange.fromStartDuration(event.getWhen().end(), afterEventDuration);
            // if the new time slot isn't already saved, save it
            if (pendingTimeRangeAvailableAttendees.get(after) == null && allTimeRangesAvailableAttendees.get(after) == null){
              pendingTimeRangeAvailableAttendees.put(after, new HashSet<>(timeRangeAvailableAttendees));
            }
          }
          allTimeRangesAvailableAttendees.get(timeRange).removeAll(event.getAttendees());
          timeRangeAvailableAttendees.clear();
        }
      }
      allTimeRangesAvailableAttendees.putAll(pendingTimeRangeAvailableAttendees);      
    }
    // Find and return the TimeRanges with the most attendees available
    Set<TimeRange> keys = allTimeRangesAvailableAttendees.keySet();
    ArrayList<TimeRange> maximizedAvailableTimes = new ArrayList<>();
    int highestAvailable = 0;
    int numAvailable;
    for (TimeRange timeRange: keys){
      numAvailable = allTimeRangesAvailableAttendees.get(timeRange).size();
      if (numAvailable > highestAvailable){
        maximizedAvailableTimes.clear();
        maximizedAvailableTimes.add(timeRange);
        highestAvailable = numAvailable;
      } else if (numAvailable == highestAvailable){
        maximizedAvailableTimes.add(timeRange);
      }
    }
    Collections.sort(maximizedAvailableTimes, TimeRange.ORDER_BY_START);
    return maximizedAvailableTimes;
  }
 
}
