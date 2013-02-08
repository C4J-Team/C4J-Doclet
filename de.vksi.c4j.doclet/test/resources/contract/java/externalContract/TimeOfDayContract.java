package externalContract;

import static de.vksi.c4j.Condition.postCondition;
import static de.vksi.c4j.Condition.preCondition;
import de.vksi.c4j.Contract;
import de.vksi.c4j.Target;
import externalContract.TimeOfDay;

@Contract
public class TimeOfDayContract extends TimeOfDay {

   @Target
   private TimeOfDay target;

   public TimeOfDayContract(int hour, int minute, int second) {
      super(hour, minute, second);
      if (preCondition()) {
         assert hour >= HOUR_MIN : "hour >= HOUR_MIN";
         assert hour <= HOUR_MAX : "hour <= HOUR_MAX";
         assert minute >= MINUTE_MIN : "minute >= MINUTE_MIN";
         assert minute <= MINUTE_MAX : "minute <= MINUTE_MAX";
         assert second >= SECOND_MIN : "second >= SECOND_MIN";
         assert second <= SECOND_MAX : "second <= SECOND_MAX";
      }
      if (postCondition()) {
         assert target.getHour() == hour : "hour set";
         assert target.getMinute() == minute : "minute set";
         assert target.getSecond() == second : "second set";
      }
   }
}
