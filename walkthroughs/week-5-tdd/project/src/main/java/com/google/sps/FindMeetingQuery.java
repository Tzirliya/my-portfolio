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
    Hashtable<TimeRange, HashSet<String>> timeRangeEventOverlap = new Hashtable<>();
    for (TimeRange timeRange: availableTimes){
      timeRangeEventOverlap.put(timeRange, new HashSet<>(attendees));
    }
    boolean overlapped;
    HashSet<String> timeRangeAvailableAttendees = new HashSet<>();
    HashSet<String> eventAttendees = new HashSet<>();
    Hashtable<TimeRange, HashSet<String>> pendingTimeRangeEventOverlap = new Hashtable<>();
    for (Event event: events){
      Collections.sort(availableTimes, TimeRange.ORDER_BY_START);
      overlapped = false;
      // Get all relevant attendees of event
      eventAttendees.addAll(event.getAttendees());
      for (String attendee: eventAttendees){
        if (!attendees.contains(attendee)){
          eventAttendees.remove(attendee);
        }
      }
      pendingTimeRangeEventOverlap.clear();
      Set<TimeRange> timeRanges = timeRangeEventOverlap.keySet();
      for (TimeRange timeRange: timeRanges){
        if (timeRange.overlaps(event.getWhen())){
          overlapped = true;
          timeRangeAvailableAttendees = new HashSet<>(timeRangeEventOverlap.get(timeRange));
          int beforeEventDuration = event.getWhen().start() - timeRange.start();
          if (beforeEventDuration >= duration){
            TimeRange before = TimeRange.fromStartDuration(timeRange.start(), beforeEventDuration);
            if (pendingTimeRangeEventOverlap.get(before) == null && timeRangeEventOverlap.get(before) == null){
              pendingTimeRangeEventOverlap.put(before, new HashSet<>(timeRangeAvailableAttendees));
            }
          }
          int afterEventDuration = timeRange.end() - event.getWhen().end();
          if (afterEventDuration >= duration){
            TimeRange after = TimeRange.fromStartDuration(event.getWhen().end(), afterEventDuration);
            if (pendingTimeRangeEventOverlap.get(after) == null && timeRangeEventOverlap.get(after) == null){
              pendingTimeRangeEventOverlap.put(after, new HashSet<>(timeRangeAvailableAttendees));
            }
          }
          timeRangeEventOverlap.get(timeRange).removeAll(eventAttendees);
        } else if (overlapped == true){
          break;
        }
      }
      timeRangeEventOverlap.putAll(pendingTimeRangeEventOverlap);
      eventAttendees.clear();
    }
    Set<TimeRange> keys = timeRangeEventOverlap.keySet();
    ArrayList<TimeRange> maximizedAvailableTimes = new ArrayList<>();
    int highestAvailable = 0;
    int numAvailable;
    for (TimeRange timeRange: keys){
      numAvailable = timeRangeEventOverlap.get(timeRange).size();
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

//   private Collection<TimeRange> getAvailableTimesWithMaxOptional(Collection<TimeRange> availableTimes, Collection<Event> events, Collection<String> attendees, long duration){
//     if (attendees.isEmpty()){
//       return availableTimes;
//     }
//     // Overview: There's an ArrayList containing HashSets. All TimeRanges start off in the 
//     // HashSet in the first index. For each event/attendee combination, if it doesn't conflict
//     // with a TimeRange, move that TimeRange up an index. If it partially conflicts, split
//     // the TimeRange and move up the non-conflicting TimeRange. 
//     int size = attendees.size()*events.size() + 1;
//     ArrayList<HashSet<TimeRange>> rankNumOptionalCanAttend = new ArrayList<>(size);
//     rankNumOptionalCanAttend.add(new HashSet<>(availableTimes));
//     for (int i = 1; i < size; i++){
//       rankNumOptionalCanAttend.add(new HashSet<>());
//     }
//     for (String attendee: attendees){
//       for (Event event: events){
//         if (event.getAttendees().contains(attendee)){
//           for (int i = rankNumOptionalCanAttend.size()-1; i >= 0; i--){
//             Iterator<TimeRange> iterator = rankNumOptionalCanAttend.get(i).iterator();
//             while (iterator.hasNext()){
//               TimeRange timeRange = iterator.next();
//               if (timeRange.overlaps(event.getWhen())){
//                 int beforeEventDuration = event.getWhen().start() - timeRange.start();
//                 if (beforeEventDuration >= duration){
//                   TimeRange before = TimeRange.fromStartDuration(timeRange.start(), beforeEventDuration);
//                   rankNumOptionalCanAttend.get(i+1).add(before);
//                 }
//                 int afterEventDuration = timeRange.end() - event.getWhen().end();
//                 if (afterEventDuration >= duration){
//                   TimeRange after = TimeRange.fromStartDuration(event.getWhen().end(), afterEventDuration);
//                   rankNumOptionalCanAttend.get(i+1).add(after);
//                 }
//               } else {
//                 iterator.remove();
//                 rankNumOptionalCanAttend.get(i+1).add(timeRange);
//               }
//             }
//           }
//         }
//       }
//     }
//     // Find the highest index containing TimeRanges
//     for (int i = rankNumOptionalCanAttend.size()-1; i >= 0; i--){
//       if (!rankNumOptionalCanAttend.get(i).isEmpty()){
//         ArrayList<TimeRange> ans = new ArrayList<>(rankNumOptionalCanAttend.get(i));
//         Collections.sort(ans, TimeRange.ORDER_BY_START);
//         return ans;
//       }
//     }
//     return availableTimes;
//   }
 
}
