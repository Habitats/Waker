package waker;

import java.io.File;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class WakerController {

  public ToggleButton alarmToggle;
  public DatePicker datePicker;
  public TextField minuteInput;
  public TextField hourInput;
  public Label soundLabel;
  public Slider volumeSlider;
  public CheckBox increasingCheckbox;
  public Label countdownLabel;
  public Label titleLabel;
  private WakerAlarm alarm;
  public boolean increasingVolume;
  private Waker application;

  public WakerController() {
    alarm = new WakerAlarm(this);
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
    application.fireAlarm(wakerAlarm);

  }

  public void onIncreasing(ActionEvent actionEvent) {
    increasingVolume = increasingCheckbox.isSelected();
  }

  public void stopAlarm() {
    application.stopAlarm();

  }

  public void setApplication(Waker application) {
    this.application = application;
  }

  public void onLoadDefaultSound(ActionEvent actionEvent) {
    File defaultSound = application.getDefaultSound();
    soundLabel.setText(defaultSound.getName());
    alarm.setSound(defaultSound);
  }
}
