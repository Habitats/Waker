package waker;

import java.io.File;
import java.net.URISyntaxException;
import java.util.stream.IntStream;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Waker extends Application {

  private WakerController controller;
  private MediaPlayer player;
  private File defaultSound;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("waker.fxml"));
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

  private void setup() throws URISyntaxException {
    controller.setApplication(this);
    controller.volumeSlider.valueProperty()
        .addListener((observable, oldValue, newValue) -> onVolumeChanged(getVolume()));
    updateTitle(controller, getVolume());
    defaultSound = new File(getClass().getResource("still_blastin.mp3").getFile());
  }

  private double getVolume() {
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
  }

  public void fireAlarm(WakerAlarm wakerAlarm) {
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
  }

  public void stopAlarm() {
    if (player != null) {
      player.stop();
      player = null;
    }
  }

  public File getDefaultSound() {
    return defaultSound;
  }
}
