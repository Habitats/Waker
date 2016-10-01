package waker.models;

public interface WakerAlarmListener {

  void startAlarm(WakerAlarm wakerAlarm);

  void stopAlarm(WakerAlarm wakerAlarm);

  void setCountdown(String formattedUntil);
}
