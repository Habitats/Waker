package waker.models;

/**
 * Created by mail on 01.05.2015.
 */
public interface WakerAlarmListener {

  void startAlarm(WakerAlarm wakerAlarm);

  void stopAlarm(WakerAlarm wakerAlarm);

  void setCountdown(String formattedUntil);
}
