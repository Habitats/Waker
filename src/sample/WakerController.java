package sample;

import java.io.File;
import java.util.stream.IntStream;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class WakerController {

  public Label alarmDescriptionLabel;
  public ToggleButton alarmToggle;
  public DatePicker datePicker;
  public TextField minuteInput;
  public TextField hourInput;
  public Label soundLabel;
  public Slider volumeSlider;
  public CheckBox increasingCheckbox;
  public Label countdownLabel;
  private WakerAlarm alarm = new WakerAlarm(this);
  private MediaPlayer player;
  private boolean increasingVolume;

  public void addAlarm(ActionEvent actionEvent) {
    alarmDescriptionLabel.setText(actionEvent.toString());
  }

  public void onToggle(ActionEvent actionEvent) {
    alarmToggle.setText(alarmToggle.isSelected() ? "On" : "Off");
    alarm.setEnabled(alarmToggle.isSelected());
  }

  public void onDateSelected(ActionEvent actionEvent) {
    alarm.setDate(datePicker.getValue());
  }


  public void onLoad(ActionEvent actionEvent) {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Open Resource File");
    fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.aac"));
    File selectedFile = fileChooser.showOpenDialog(null);
    if (selectedFile != null && selectedFile.exists()) {
      soundLabel.setText(selectedFile.getAbsolutePath());
      alarm.setSound(selectedFile);
    }
  }

  public void onHourChanged(Event event) {
    alarm.setHour(Integer.parseInt(hourInput.getText()));
  }

  public void onMinuteChanged(Event event) {
    alarm.setMinute(Integer.parseInt(minuteInput.getText()));
  }

  public synchronized void fireAlarm(WakerAlarm wakerAlarm) {
    player = new MediaPlayer(wakerAlarm.getSound());
    player.play();
    if (increasingVolume) {
      IntStream.range(0, 100).forEach(i -> {
        if (alarm.isEnabled()) {
          volumeSlider.setValue(i);
          player.setVolume(i / 100.);
          try {
            Thread.sleep(1000);
          } catch (InterruptedException e) {
          }
        }
      });
    }
  }

  public void onVolumeChanged(Event event) {
    if (player != null) {
      player.setVolume(volumeSlider.getValue() / 100.);
    }
  }

  public void onIncreasing(ActionEvent actionEvent) {
    increasingVolume = increasingCheckbox.isSelected();
  }

  public void stopAlarm() {
    if (player != null) {
      player.stop();
      player = null;
    }
  }
}
