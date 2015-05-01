package waker.controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import waker.Waker;

public class WakerController {


  public Slider volumeSlider;
  public CheckBox increasingCheckbox;
  public Label titleLabel;
  public VBox alarmListView;
  public boolean increasingVolume;
  private Waker application;

  public void onIncreasing(ActionEvent actionEvent) {
    increasingVolume = increasingCheckbox.isSelected();
  }

  public void setApplication(Waker application) {
    this.application = application;
  }

  public void onAddAlarmClicked(ActionEvent actionEvent) {
    application.addAlarm();
  }

  public double getVolume() {
    return application.getVolume();
  }

  public void remove(AlarmController alarmController, GridPane alarmView) {
   application.remove(alarmController, alarmView);
  }
}
