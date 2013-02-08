package externalContract;

import static de.vksi.c4j.Condition.ignored;
import static de.vksi.c4j.Condition.postCondition;
import static de.vksi.c4j.Condition.result;
import de.vksi.c4j.Contract;
import de.vksi.c4j.PureTarget;
import de.vksi.c4j.Target;
import externalContract.RicherTimeOfDaySpec;

@Contract
public class RicherTimeOfDaySpecContract extends TimeOfDaySpecContract
         implements RicherTimeOfDaySpec {

   @Target
   private RicherTimeOfDaySpec target;

   @Override
   @PureTarget
   public int nearestHour() {
      if (postCondition()) {
         int result = result();
         assert result >= HOUR_MIN : "result >= HOUR_MIN";
         assert result <= HOUR_MAX : "result >= HOUR_MAX";
         if (target.getMinute() < 30) {
            assert result == target.getHour() : "if minute < 30 then result == hour";
         } else {
            if (target.getHour() < 23) {
               assert result == target.getHour() + 1 : "if minute >= 30 and hour < 23 then result == hour + 1";
            } else {
               assert result == 0 : "if minute >= 30 and hour == 23 then result == 0";
            }
         }
      }
      return ignored();
   }

}
