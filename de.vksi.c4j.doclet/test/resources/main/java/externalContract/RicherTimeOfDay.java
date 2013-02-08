package externalContract;

public class RicherTimeOfDay extends TimeOfDay implements RicherTimeOfDaySpec {

   public RicherTimeOfDay(int hour, int minute, int second) {
      super(hour, minute, second);
   }

   @Override
   public int nearestHour() {
      int result = 0;
      if (getMinute() < 30) {
         result = getHour();
      }
      if (getMinute() >= 30 && getHour() < 23) {
         result = getHour() + 1;
      }
      return result;
   }

}
