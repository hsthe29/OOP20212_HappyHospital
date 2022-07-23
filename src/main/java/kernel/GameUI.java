package kernel;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.CacheHint;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import kernel.constant.Constant;
import scenes.MainScene;
import socket.Message;

import java.io.File;
import java.io.IOException;

public class GameUI extends Application {

    private static final int ROWS = 28;
    private static final int COLS = 52;

    @Override
    public void start(Stage primaryStage) throws IOException {
        MainScene scene = this.loadScene();
        primaryStage.setScene(scene);
        primaryStage.setTitle("Happy Hospital");
        primaryStage.setMaximized(true);
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(600);
        primaryStage.setScene(scene);

        primaryStage.setOnCloseRequest(event -> {
            try {
                scene.controller.oos.flush();
                scene.controller.oos.writeObject(new Message(0));
            } catch (IOException e) {
                e.printStackTrace();
            }
            scene.controller.disconnect();
            Platform.exit();
            System.exit(0);
        });
        primaryStage.show();
    }

    private MainScene loadScene() throws IOException {
        GridPane gridPane = new GridPane();
        gridPane.setCache(true);
        gridPane.setCacheHint(CacheHint.SPEED);
        ScrollPane scrollPane = new ScrollPane(gridPane);
        StackPane stackPane = new StackPane(scrollPane);
        Pane pane = new Pane();
        VBox vBox = new VBox(pane, stackPane);

        for (int x = 0 ; x < COLS; x++) {
            ColumnConstraints cc = new ColumnConstraints();
            gridPane.getColumnConstraints().add(cc);
        }

        for (int y = 0 ; y < ROWS ; y++) {
            RowConstraints rc = new RowConstraints();
            gridPane.getRowConstraints().add(rc);
        }

        return new MainScene(vBox, 1000, 600, true);
    }

    public void run() {
        try {
            File directory = new File("./");
            Constant.rootPath = directory.getCanonicalPath() + "/src/main/resources/";
        } catch (IOException e) {
            System.out.println("Cant find path");
        }
        launch("launching");
    }
}

