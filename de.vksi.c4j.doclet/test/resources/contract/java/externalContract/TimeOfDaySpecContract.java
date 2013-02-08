package externalContract;

import static de.vksi.c4j.Condition.ignored;
import static de.vksi.c4j.Condition.postCondition;
import static de.vksi.c4j.Condition.preCondition;
import static de.vksi.c4j.Condition.result;
import static de.vksi.c4j.Condition.unchanged;
import de.vksi.c4j.ClassInvariant;
import de.vksi.c4j.Contract;
import de.vksi.c4j.PureTarget;
import de.vksi.c4j.Target;
import externalContract.TimeOfDaySpec;

@Contract
public class TimeOfDaySpecContract implements TimeOfDaySpec {

   @Target
   private TimeOfDaySpec target;

   @ClassInvariant
   public void classInvariant() {
      assert target.getHour() >= HOUR_MIN : "hour >= HOUR_MIN";
      assert target.getHour() <= HOUR_MAX : "hour <= HOUR_MAX";
      assert target.getMinute() >= MINUTE_MIN : "minute >= MINUTE_MIN";
      assert target.getMinute() <= MINUTE_MAX : "minute <= MINUTE_MAX";
      assert target.getSecond() >= SECOND_MIN : "second >= SECOND_MIN";
      assert target.getSecond() <= SECOND_MAX : "second <= SECOND_MAX";
   }

   @Override
   public void setHour(int hour) {
      if (preCondition()) {
         assert hour >= HOUR_MIN : "hour >= HOUR_MIN";
         assert hour <= HOUR_MAX : "hour <= HOUR_MAX";
      }
      if (postCondition()) {
         assert target.getHour() == hour : "hour set";
         assert unchanged(target.getMinute()) : "minute unchanged";
         assert unchanged(target.getSecond()) : "second unchanged";
      }
   }

   @Override
   public void setMinute(int minute) {
      if (preCondition()) {
         assert minute >= MINUTE_MIN : "minute >= MINUTE_MIN";
         assert minute <= MINUTE_MAX : "minute <= MINUTE_MAX";
      }
      if (postCondition()) {
         assert unchanged(target.getHour()) : "hour unchanged";
         assert target.getMinute() == minute : "minute set";
         assert unchanged(target.getSecond()) : "second unchanged";
      }
   }

   @Override
   public void setSecond(int second) {
      if (preCondition()) {
         assert second >= SECOND_MIN : "second >= SECOND_MIN";
         assert second <= SECOND_MAX : "second <= SECOND_MAX";
      }
      if (postCondition()) {
         assert unchanged(target.getHour()) : "hour unchanged";
         assert unchanged(target.getMinute()) : "minute unchanged";
         assert target.getSecond() == second : "second set";
      }
   }

   @Override
   @PureTarget
   public int getHour() {
      return ignored();
   }

   @Override
   @PureTarget
   public int getMinute() {
      return ignored();
   }

   @Override
   @PureTarget
   public int getSecond() {
      return ignored();
   }

   @Override
   @PureTarget
   public boolean isBefore(TimeOfDaySpec other) {
      if (preCondition()) {
         assert other != null : "other != null";
      }
      if (postCondition()) {
         boolean result = result();
         if (result) {
            assert !other.isBefore(target) : "if isBefore then not other isBefore";
         }
      }
      return ignored();
   }

}
