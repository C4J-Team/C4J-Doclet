package externalContract;

public interface TimeOfDaySpec {

   int HOUR_MIN = 0;
   int HOUR_MAX = 23;
   int MINUTE_MIN = 0;
   int MINUTE_MAX = 59;
   int SECOND_MIN = 0;
   int SECOND_MAX = 59;

   // commands

   void setHour(int hour);

   void setMinute(int minute);

   void setSecond(int second);

   // basic queries

   int getHour();

   int getMinute();

   int getSecond();

   // derived queries

   boolean isBefore(TimeOfDaySpec other);
}
