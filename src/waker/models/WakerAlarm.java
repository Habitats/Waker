package waker.models;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import javafx.application.Platform;
import javafx.scene.media.Media;
import waker.Log;

/**
 * Created by mail on 01.05.2015.
 */
public class WakerAlarm {

  private final WakerAlarmListener listener;
  private boolean enabled = false;
  private LocalDate date = LocalDate.now();
  private int minute;
  private int hour;
  private LocalDateTime fireDate;
  private File soundFile;

  public WakerAlarm(WakerAlarmListener listener) {
    this.listener = listener;
    initAlarmWatcher();
  }

  private void initAlarmWatcher() {
    new Thread(() -> {
      while (true) {
        update();
        if (shouldFire()) {
          listener.startAlarm(this);
          enabled = false;
        }
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          Log.v(e);
        }
      }
    }).start();
  }

  private boolean shouldFire() {
    return enabled && LocalDateTime.now().isAfter(fireDate);
  }

  public void setEnabled(final boolean enabled) {
    this.enabled = enabled;
    if (!enabled) {
      listener.stopAlarm(this);
    }
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setDate(LocalDate date) {
    this.date = date;
    update();
  }

  public void setMinute(int minute) {
    this.minute = minute;
    update();
  }


  public void setHour(int hour) {
    this.hour = hour;
    update();
  }

  private void update() {
    fireDate = getFireDate();
    Log.v("Updating! " + getFormattedUntil());
    Platform.runLater(() -> listener.setCountdown(getFormattedUntil()));
  }

  private String getFormattedFireDate() {
    return String.format("%4d-%2d-%2d:%2d:%2d", //
                         fireDate.getYear(), fireDate.getMonth().getValue(), fireDate.getDayOfMonth(),
                         fireDate.getHour(), fireDate.getMinute()).replaceAll(" ", "0");
  }


  private LocalDateTime getFireDate() {
    return LocalDateTime.of(date, LocalTime.of(hour, minute));
  }

  public void setSound(File soundFile) {
    this.soundFile = soundFile;
    update();
  }

  public Media getSound() {
    return new Media(soundFile.toURI().toString());
  }

  private String getFormattedUntil() {
    LocalDateTime fromDateTime = LocalDateTime.now();
    LocalDateTime toDateTime = getFireDate();
    if (fromDateTime.until(toDateTime, ChronoUnit.SECONDS) < 0) {
      return String.format("Went off %s ago!", getFormattedDifference(toDateTime, fromDateTime));
    } else {
      return String.format("Going off in %s", getFormattedDifference(fromDateTime, toDateTime));
    }
  }

  private String getFormattedDifference(LocalDateTime fromDateTime, LocalDateTime toDateTime) {
    LocalDateTime tempDateTime = LocalDateTime.from(fromDateTime);

    long years = tempDateTime.until(toDateTime, ChronoUnit.YEARS);
    tempDateTime = tempDateTime.plusYears(years);

    long months = tempDateTime.until(toDateTime, ChronoUnit.MONTHS);
    tempDateTime = tempDateTime.plusMonths(months);

    long days = tempDateTime.until(toDateTime, ChronoUnit.DAYS);
    tempDateTime = tempDateTime.plusDays(days);

    long hours = tempDateTime.until(toDateTime, ChronoUnit.HOURS);
    tempDateTime = tempDateTime.plusHours(hours);

    long minutes = tempDateTime.until(toDateTime, ChronoUnit.MINUTES);
    tempDateTime = tempDateTime.plusMinutes(minutes);

    long seconds = tempDateTime.until(toDateTime, ChronoUnit.SECONDS);

    String formatted = String.format("%s%s%s%s%s%s",  //
                                     years > 0 ? years + " y " : "",  //
                                     months > 0 ? months + " m " : "", //
                                     days > 0 ? days + " d " : "", //
                                     hours > 0 ? hours + " h " : "", //
                                     minutes > 0 ? minutes + " m " : "", //
                                     seconds > 0 ? seconds + " s " : "");
    return formatted;
  }

  private String getSoundFileName() {
    return soundFile == null ? "No sound! " : soundFile.getName();
  }

  @Override
  public String toString() {
    return String.format("%s - %s - %s", getFormattedUntil(), getFireDate(), getSoundFileName());
  }
}