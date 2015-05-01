package waker.controllers;

import java.io.File;
import java.util.stream.IntStream;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import waker.Waker;
import waker.models.WakerAlarm;
import waker.models.WakerAlarmListener;

/**
 * Created by mail on 01.05.2015.
 */
public class AlarmController implements WakerAlarmListener {

  public Label soundLabel;
  public Label countdownLabel;
  private WakerAlarm alarm;
  public ToggleButton alarmToggle;
  public DatePicker datePicker;
  public TextField minuteInput;
  public TextField hourInput;
  private MediaPlayer player;
  private WakerController controller;

  public void onToggle(ActionEvent actionEvent) {
    alarmToggle.setText(alarmToggle.isSelected() ? "On" : "Off");
    alarm.setEnabled(alarmToggle.isSelected());
  }

  public void onLoad(ActionEvent actionEvent) {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Open Resource File");
    fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.aac"));
    File selectedFile = fileChooser.showOpenDialog(null);
    if (selectedFile != null && selectedFile.exists()) {
      soundLabel.setText(selectedFile.getName());
      alarm.setSound(selectedFile);
    }
  }

  public void onLoadDefaultSound(ActionEvent actionEvent) {
    File defaultSound = Waker.defaultSound;
    soundLabel.setText(defaultSound.getName());
    alarm.setSound(defaultSound);
  }

  public void onDateSelected(ActionEvent actionEvent) {
    alarm.setDate(datePicker.getValue());
  }

  public void onHourChanged(Event event) {
    alarm.setHour(Integer.parseInt(hourInput.getText()));
  }

  public void onMinuteChanged(Event event) {
    alarm.setMinute(Integer.parseInt(minuteInput.getText()));
  }

  public void setAlarm(WakerAlarm alarm) {
    this.alarm = alarm;
  }


  public void setController(WakerController controller) {
    this.controller = controller;
  }

  @Override
  public void startAlarm(WakerAlarm wakerAlarm) {
    if (player != null) {
      player.stop();
    }
    player = new MediaPlayer(wakerAlarm.getSound());
    player.play();
    if (controller.increasingVolume) {
      IntStream.range(0, 100).forEach(i -> {
        if (wakerAlarm.isEnabled()) {
          controller.volumeSlider.setValue(i);
          player.setVolume(i / 100.);
          try {
            Thread.sleep(1000);
          } catch (InterruptedException e) {
          }
        }
      });
    }
    else {
      player.setVolume(controller.getVolume());
    }
  }

  @Override
  public void stopAlarm(WakerAlarm wakerAlarm) {
    if (player != null) {
      player.stop();
      player = null;
    }
  }

  @Override
  public void setCountdown(String countdown) {
    countdownLabel.setText(countdown);
  }

  public void onVolumeChanged(double volume) {
    if (player != null) {
      player.setVolume(volume);
    }
//    Log.v(volume);
  }
}
