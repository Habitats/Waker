package waker;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

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

  private static final String VOLUME     = "volume";
  private static final String INCREASING = "increasing_volume";
  private static final String REPEAT     = "repeat_song";
  private WakerController       controller;
  //  public static File defaultSound;
  private Stage                 primaryStage;
  private List<AlarmController> alarms;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    alarms = new ArrayList<>();
    this.primaryStage = primaryStage;
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("gui/waker.fxml"));
    primaryStage.setTitle("Waker");
    GridPane root = fxmlLoader.load();
    controller = fxmlLoader.getController();

    primaryStage.setScene(new Scene(root));
    primaryStage.show();
    primaryStage.setOnCloseRequest(e -> {
      saveState();
      Platform.exit();
      System.exit(0);
    });

    loadState();

    setup();
  }

  private void setup() throws Exception {
    controller.setApplication(this);
    controller.volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> onVolumeChanged(controller.getVolume()));
    updateTitle(controller, controller.getVolume());
//    defaultSound = new File(getClass().getResource("still_blastin.mp3").getFile());
  }

  private void loadState() {
    try {
      Log.v("Loading previous state ...");
      Properties      props = new Properties();
      FileInputStream in    = new FileInputStream("waker.properties");
      props.load(in);
      in.close();

      props.keySet().stream() //
          .map(String::valueOf).filter(k -> k.startsWith(WakerAlarm.ID))//
          .sorted(Comparator.naturalOrder()) //
          .forEach(id -> addAlarm(props.getProperty(id), props));

      primaryStage.sizeToScene();

      double  volume   = Double.parseDouble(props.getProperty(VOLUME));
      boolean repeat   = Boolean.parseBoolean(props.getProperty(REPEAT));
      boolean increase = Boolean.parseBoolean(props.getProperty(INCREASING));
      controller.setVolume(volume);
      controller.setRepeat(repeat);
      controller.setShouldIncrease(increase);

      Log.v("Loading previous state complete!");
    } catch (Exception e) {
      Log.v("Loading previous state failed!");
    }
  }

  private void saveState() {
    try {
      Log.v("Saving state ...");
      Properties props = new Properties();
      alarms.forEach(a -> a.saveState(props));
      props.setProperty(REPEAT, String.valueOf(controller.shouldRepat()));
      props.setProperty(INCREASING, String.valueOf(controller.isIncreasingVolume()));
      props.setProperty(VOLUME, String.valueOf(controller.getVolume()));
      props.store(new FileOutputStream("waker.properties"), null);
    } catch (Exception e) {
      Log.v("Saving state failed!");
      e.printStackTrace();
    }
  }

  public void addAlarm() {
    AlarmController alarmController = createAndAddController();
    alarmController.setController(controller);
    alarmController.setAlarm();
    alarms.add(alarmController);
    primaryStage.sizeToScene();
  }

  private void addAlarm(String id, Properties props) {
    AlarmController alarmController = createAndAddController();
    WakerAlarm      alarm           = WakerAlarm.fromProperties(id, props, alarmController);
    alarmController.setController(controller);
    alarmController.setAlarm(alarm);
    alarms.add(alarmController);
  }

  private AlarmController createAndAddController() {
    try {
      FXMLLoader alarmFxml = new FXMLLoader(getClass().getResource("gui/alarm.fxml"));
      GridPane   alarmView = alarmFxml.load();
      controller.alarmListView.getChildren().add(alarmView);
      return alarmFxml.getController();
    } catch (IOException e) {
      throw new IllegalStateException("Unable to create controller!");
    }
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

  private void onVolumeChanged(double volume) {
    updateTitle(controller, volume);
    alarms.forEach(a -> a.onVolumeChanged(volume));
  }

  public void remove(AlarmController alarmController, GridPane alarmView) {
    controller.alarmListView.getChildren().remove(alarmView);
    alarms.remove(alarmController);
    primaryStage.sizeToScene();
  }
}
