package sample;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.media.Media;

/**
 * Created by mail on 01.05.2015.
 */
public class WakerAlarm {

  private final WakerController wakerController;
  private boolean enabled = false;
  private LocalDate date = LocalDate.now();
  private int minute;
  private int hour;
  private LocalDateTime fireDate;
  private File soundFile;

  public WakerAlarm(WakerController wakerController) {
    this.wakerController = wakerController;
    initAlarmWatcher(wakerController);
  }

  private void initAlarmWatcher(WakerController wakerController) {
    new Thread(() -> {
      while (true) {
        update();
        if (enabled) {
          if (LocalDateTime.now().isAfter(fireDate)) {
            wakerController.fireAlarm(this);
            enabled = false;
          }
        }
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
      }
    }).start();
  }

  public void setEnabled(final boolean enabled) {
    this.enabled = enabled;
    if (!enabled) {
      wakerController.stopAlarm();
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
    System.out.println("update!");
    if (getDescriptionLabel() != null) {
      Platform.runLater(() -> getDescriptionLabel().setText(toString()));
    }
//    System.out.println(String.format("New time: %s", fireDate.toString()));
  }

  private Label getDescriptionLabel() {
    return wakerController.alarmDescriptionLabel;
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

  @Override
  public String toString() {
    return String.format("%s - %s - %s", LocalDateTime.now().until(getFireDate(), ChronoUnit.SECONDS), getFireDate(),
                         getSoundFileName());
  }

  private String getSoundFileName() {
    return soundFile == null ? "No sound! " : soundFile.getName();
  }
}
