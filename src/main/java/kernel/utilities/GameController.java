package kernel.utilities;

import kernel.constant.Constant;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.NamedArg;
import javafx.beans.property.SimpleStringProperty;
import javafx.util.Duration;
import scenes.MainScene;
import socket.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class GameController {

    public MainScene scene;
    // move controller
    public boolean keyW = false;
    public boolean keyA = false;
    public boolean keyS = false;
    public boolean keyD = false;

    public SimpleStringProperty harmfulness = new SimpleStringProperty("H.ness: 0");
    public SimpleStringProperty timeDisplay = new SimpleStringProperty("00:00:00");
    public int sec = 0;
    private static long _start;
    private static long breakDuration = -1;
    private long agentTime;
    private long lastAgent;

    public boolean isGameRunning = false;
    public boolean gameLoaded = false;

    private Timeline gameLoop;
    private Timeline clock;
    private Socket socket;
    public ObjectOutputStream oos;
    public ObjectInputStream ois;
    private static GameController instance = null;

    private GameController() {}

    public void setScene(MainScene scene) {
        this.scene = scene;

        Duration durationPerFrame = Duration.millis(35);
        final KeyFrame oneFrame = new KeyFrame(durationPerFrame,
                event -> {
                    scene.physics.update();
                    scene.update();
                    double checkAgent = (double) (System.currentTimeMillis() - lastAgent);
                    if(checkAgent >= agentTime) {
                        try {
                            scene.addAgent();
                        } catch (IOException | ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                });
        gameLoop = new Timeline(oneFrame);
        gameLoop.setCycleCount(Animation.INDEFINITE);

        Duration durationClock = Duration.millis(1000);
        final KeyFrame clockFrame = new KeyFrame(durationClock,
                event -> {
                    sec++;
                    timeDisplay.set(Constant.secondsToHMS(sec));
                    if(sec % 5 == 0) {
                        try {
                            if(this.scene.getHarmfullness() > 1500)
                                oos.writeObject(new Message(5, true));
                            else
                                oos.writeObject(new Message(5, false));

                        } catch (IOException e) {
                            System.out.println("Error!");
                        }
                    }
                });
        clock = new Timeline(clockFrame);
        clock.setCycleCount(Animation.INDEFINITE);
        _start = System.currentTimeMillis();
        breakDuration = System.currentTimeMillis();
    }

    public static GameController getInstance() {
        if(instance == null) instance = new GameController();
        return instance;
    }

    public void setSocket(Socket socket) throws IOException {
        this.socket = socket;
        oos = new ObjectOutputStream(socket.getOutputStream());
        ois = new ObjectInputStream(socket.getInputStream());
    }

    public void startGameLoop() {
        _start += System.currentTimeMillis() - breakDuration;
        lastAgent = breakDuration;
        isGameRunning = true;
        gameLoop.play();
        clock.play();
    }

    public void pauseGameLoop() {
        breakDuration = System.currentTimeMillis();
        isGameRunning = false;
        gameLoop.pause();
        clock.pause();
    }

    public void restate() {
        this.harmfulness.set("H.ness: 0");
        this.timeDisplay.set("00:00:00");
        this.sec = 0;
    }

    public static double now() {
        if(instance.isGameRunning)
            return System.currentTimeMillis() - _start;
        else return breakDuration - _start;
    }

    public void setAgentTime(@NamedArg("ms") int time) {
        this.agentTime = time;
    }

    public void disconnect() {
        System.out.println("Disconnecting ...");
        try {
            this.socket.close();
            System.out.println("Disconnected!");
        } catch (IOException e) {
            System.out.println("Socket error!");
        }
    }
}
