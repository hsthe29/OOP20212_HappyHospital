package init;

import javafx.application.Application;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import scenes.GameScene;
import scenes.MainScene;

import java.io.IOException;

public class GameUI extends Application {

    private static final int ROWS = 28;
    private static final int COLS = 52;

    @Override
    public void start(Stage primaryStage) throws IOException {
        GameScene scene = this.loadScene();
        primaryStage.setScene(scene);

        primaryStage.setTitle("Happy Hospital");
        primaryStage.setMaximized(true);
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(600);
        primaryStage.setScene(scene);

        primaryStage.show();
    }

    private MainScene loadScene() throws IOException {
        GridPane gridPane = new GridPane();
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
        launch("launching");
    }
}

