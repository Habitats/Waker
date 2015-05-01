package waker;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import waker.controllers.AlarmController;
import waker.controllers.WakerController;
import waker.models.WakerAlarm;

public class Waker extends Application {

  private WakerController controller;
  public static File defaultSound;
  private Stage primaryState;
  private List<AlarmController> alarms;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    alarms = new ArrayList<>();
    this.primaryState = primaryStage;
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("gui/waker.fxml"));
    primaryStage.setTitle("Waker");
    GridPane root = fxmlLoader.load();
    controller = fxmlLoader.getController();

    primaryStage.setScene(new Scene(root));
    primaryStage.show();
    primaryStage.setOnCloseRequest(e -> {
      Platform.exit();
      System.exit(0);
    });

    setup();
  }

  private void setup() throws Exception {
    controller.setApplication(this);
    controller.volumeSlider.valueProperty()
        .addListener((observable, oldValue, newValue) -> onVolumeChanged(getVolume()));
    updateTitle(controller, getVolume());
    defaultSound = new File(getClass().getResource("still_blastin.mp3").getFile());

    addAlarm();
  }

  public void addAlarm() {
    try {
      FXMLLoader alarmFxml = new FXMLLoader(getClass().getResource("gui/alarm.fxml"));
      GridPane alarmView = alarmFxml.load();
      AlarmController alarmController = alarmFxml.getController();
      alarmController.setController(controller);
      WakerAlarm alarm = new WakerAlarm(alarmController);
      alarms.add(alarmController);
      alarmController.setAlarm(alarm);
      controller.alarmListView.getChildren().add(alarmView);
      primaryState.sizeToScene();
    } catch (IOException e) {
    }
  }

  public double getVolume() {
    return controller.volumeSlider.getValue() / 100.;
  }

  private void updateTitle(WakerController controller, double choke) {
    final InnerShadow innerShadow = new InnerShadow();
    innerShadow.setRadius(5d);
    innerShadow.setOffsetX(1);
    innerShadow.setOffsetY(1);
    innerShadow.setChoke(choke);
    innerShadow.setColor(Color.BLACK);
    controller.titleLabel.setEffect(innerShadow);
  }

  public void onVolumeChanged(double volume) {
    updateTitle(controller, volume);
    alarms.forEach(a -> a.onVolumeChanged(volume));
  }
}
