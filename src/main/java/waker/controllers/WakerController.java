package waker.controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import waker.Waker;

public class WakerController {

  public  Slider   volumeSlider;
  public  CheckBox increasingCheckbox;
  public  CheckBox repeatCheckbox;
  public  Label    titleLabel;
  public  VBox     alarmListView;
  private Waker    application;

  public void setApplication(Waker application) {
    this.application = application;
  }

  public void onAddAlarmClicked(ActionEvent actionEvent) {
    application.addAlarm();
  }

  public double getVolume() {
    return volumeSlider.getValue() / 100.;
  }

  public void remove(AlarmController alarmController, GridPane alarmView) {
    application.remove(alarmController, alarmView);
  }

  public boolean isIncreasingVolume() {
    return increasingCheckbox.isSelected();
  }

  public boolean shouldRepat() {
    return repeatCheckbox.isSelected();
  }

  public void setShouldIncrease(boolean increase) {
    increasingCheckbox.setSelected(increase);
  }

  public void setRepeat(boolean repeat) {
    repeatCheckbox.setSelected(repeat);
  }

  public void setVolume(double volume) {
    volumeSlider.setValue(volume * 100.);
  }
}
