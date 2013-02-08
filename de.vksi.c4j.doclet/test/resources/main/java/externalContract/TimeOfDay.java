package externalContract;

import de.vksi.c4j.Pure;

public class TimeOfDay implements TimeOfDaySpec {

   private int hour;
   private int minute;
   private int second;

   public TimeOfDay(int hour, int minute, int second) {
      setHour(hour);
      setMinute(minute);
      setSecond(second);
   }

   @Override
   public void setHour(int hour) {
      this.hour = hour;
   }

   @Override
   public void setMinute(int minute) {
      this.minute = minute;
   }

   @Override
   public void setSecond(int second) {
      this.second = second;
   }

   @Override
   public int getHour() {
      return hour;
   }

   @Override
   public int getMinute() {
      return minute;
   }

   @Override
   public int getSecond() {
      return second;
   }

   @Override
   public boolean isBefore(TimeOfDaySpec other) {
      boolean result;
      int seconds = 0;
      int otherSeconds = 0;
      seconds = getSeconds(this);
      otherSeconds = getSeconds(other);
      result = seconds < otherSeconds;
      return result;
   }

   @Pure
   private int getSeconds(TimeOfDaySpec tod) {
      int result = 0;
      result = tod.getSecond() + tod.getMinute() * 60 + tod.getHour() * 3600;
      return result;
   }

}
