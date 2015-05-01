package waker.controllers;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javafx.event.ActionEvent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import waker.Log;
import waker.Waker;
import waker.models.WakerAlarm;
import waker.models.WakerAlarmListener;

/**
 * Created by mail on 01.05.2015.
 */
public class AlarmController implements WakerAlarmListener {

  public Label soundLabel;
  public Label countdownLabel;
  public ComboBox<String> hoursCombobox;
  public ComboBox<String> minutesCombobox;
  public GridPane alarmView;
  private WakerAlarm alarm;
  public ToggleButton alarmToggle;
  public DatePicker datePicker;
  private MediaPlayer player;
  private WakerController controller;

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

  public void setController(WakerController controller) {
    this.controller = controller;
    alarm = new WakerAlarm(this);
    hoursCombobox.getItems().addAll(IntStream.range(0, 24).mapToObj(String::valueOf).collect(Collectors.toList()));
    minutesCombobox.getItems().addAll(IntStream.range(0, 60).mapToObj(String::valueOf).collect(Collectors.toList()));
    hoursCombobox.setValue(String.valueOf(LocalTime.now().getHour()));
    minutesCombobox.setValue(String.valueOf(LocalTime.now().getMinute()));
    datePicker.setValue(LocalDate.now());

    initContextMenu();

    onHoursChanged(null);
    onMinutesChanged(null);
    updateSettings(null);
  }

  private void initContextMenu() {
    final ContextMenu contextMenu = new ContextMenu();
    MenuItem delete = new MenuItem("Delete");
    contextMenu.getItems().addAll(delete);
    delete.setOnAction(event -> {
      alarm.setEnabled(false);
      controller.remove(alarmView);
    });

    alarmView.setOnMousePressed(event -> {
      if (event.isSecondaryButtonDown()) {
        contextMenu.show(alarmView, event.getScreenX(), event.getScreenY());
      }
    });
  }

  @Override
  public void startAlarm(WakerAlarm wakerAlarm) {
    if (player != null) {
      player.stop();
    }
    try {
      player = new MediaPlayer(wakerAlarm.getSound());
    } catch (Exception e) {
      Log.v("Failed to play sound!");
      return;
    }
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
    } else {
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

  public void updateSettings(ActionEvent actionEvent) {
    alarm.setDate(datePicker.getValue());
    alarmToggle.setText(alarmToggle.isSelected() ? "On" : "Off");
    alarm.setEnabled(alarmToggle.isSelected());
  }

  public void onMinutesChanged(ActionEvent actionEvent) {
    try {
      int minute = Integer.parseInt(minutesCombobox.getSelectionModel().getSelectedItem());
      minute = minute < 0 ? 0 : minute;
      minute = minute > 59 ? 59 : minute;
      alarm.setMinute(minute);
      minutesCombobox.setValue(String.valueOf(minute));
    } catch (Exception e) {
      Log.v("Invalid value!");
    }
  }

  public void onHoursChanged(ActionEvent actionEvent) {
    try {
      int hour = Integer.parseInt(hoursCombobox.getSelectionModel().getSelectedItem());
      alarm.setHour(hour);
      hour = hour < 0 ? 0 : hour;
      hour = hour > 23 ? 23 : hour;
      alarm.setMinute(hour);
      hoursCombobox.setValue(String.valueOf(hour));
    } catch (Exception e) {
      Log.v("Invalid value!");
    }
  }
}
