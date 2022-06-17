package scenes;

import game.utilities.DisplayList;
import game.utilities.Physics;
import game.controller.GameController;
import game.utilities.loadcomponent.LoaderPlugin;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;

public class GameScene extends Scene {


    protected ScrollPane scrollPane;
    protected GridPane gridPane;
    protected StackPane stackPane;
    protected VBox vBox;
    protected Pane pane;

    public final LoaderPlugin load = LoaderPlugin.getInstance();
    public final DisplayList displayList = new DisplayList();
    public final Physics physics= new Physics((MainScene) this);
    public GameController controller;
    public GameScene(Parent root, double width, double height, boolean depthBuffer) {
        super(root, width, height, depthBuffer);
        displayList.setScene((MainScene)this);
    }

    public VBox getVBox() {
        return this.vBox;
    }

    public Pane getPane() {
        return this.pane;
    }

    public StackPane getStackPane() {
        return this.stackPane;
    }

    public ScrollPane getScrollPane() {
        return this.scrollPane;
    }

    public GridPane getGridPane() {
        return this.gridPane;
    }

    public void removeNode(Node node) {
        this.gridPane.getChildren().remove(node);
    }
}
