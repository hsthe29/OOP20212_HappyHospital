package game.controller;

import game.classes.AutoAgv;
import game.constant.Constant;
import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.NamedArg;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;
import scenes.MainScene;

public class GameController {
    public MainScene scene;

    // move controller
    public SimpleBooleanProperty keyW = new SimpleBooleanProperty(false);
    public SimpleBooleanProperty keyA = new SimpleBooleanProperty(false);
    public SimpleBooleanProperty keyS = new SimpleBooleanProperty(false);
    public SimpleBooleanProperty keyD = new SimpleBooleanProperty(false);

    public SimpleStringProperty harmfulness = new SimpleStringProperty("H.ness: 0");
    public SimpleStringProperty timeDisplay = new SimpleStringProperty("00:00:00");
    public int sec = 0;

    private static long _start;
    private long agentTime;
    private long lastAgent;

    public boolean isGameRunning = false;
    public boolean gameLoaded = false;

    private final Timeline gameLoop;
    private final Timeline clock;

    public GameController(MainScene scene) {
        this.scene = scene;

        Duration durationPerFrame = Duration.millis(35);
        final KeyFrame oneFrame = new KeyFrame(durationPerFrame,
                event -> {
                    scene.update();
                    scene.physics.update();
                    double checkAgent = (double) (System.currentTimeMillis() - lastAgent);
                    if(checkAgent >= agentTime) {
                        scene.addAgent();
                    }
                });
        gameLoop = new Timeline(oneFrame);
        gameLoop.setCycleCount(Animation.INDEFINITE);

        Duration durationClock = Duration.millis(1000);
        final KeyFrame clockFrame = new KeyFrame(durationClock,
                event -> {
                    sec++;
                    timeDisplay.set(Constant.secondsToHMS(sec));
                });
        clock = new Timeline(clockFrame);
        clock.setCycleCount(Animation.INDEFINITE);
    }

    public void startGameLoop() {
        _start = System.currentTimeMillis();
        lastAgent = _start;
        isGameRunning = true;
        gameLoop.play();
        clock.play();
    }

    public void pauseGameLoop() {
        isGameRunning = false;
        gameLoop.pause();
        clock.pause();
    }

    public static double now() {
        return System.currentTimeMillis() - _start;
    }



    public void setAgentTime(@NamedArg("ms") int time) {
        this.agentTime = time;
    }
}
