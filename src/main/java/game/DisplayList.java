package game;

import game.classes.Agent;
import game.classes.AutoAgv;
import game.classes.Text;
import game.entities.Sprite;
import javafx.scene.layout.GridPane;
import scenes.MainScene;

import java.util.ArrayList;

public class DisplayList {

    private MainScene scene;


    public void setScene(MainScene scene) {
        this.scene = scene;
    }
    public void add(Sprite sprite){
        GridPane gridPane = this.scene.getGridPane();
        gridPane.add(sprite, 0, 0);
        sprite.setTranslateX(32 * sprite.x);
        sprite.setTranslateY(32 * sprite.y);
    }

    public void add(Text text){
        GridPane gridPane = this.scene.getGridPane();
        gridPane.add(text, 0, 0, 3, 2);
        text.moveX();
        text.moveY();
    }

    public void destroy(Sprite sprite) {
        this.scene.getGridPane().getChildren().remove(sprite);
        if(sprite instanceof AutoAgv) {
            this.scene.autoAgvs.remove(sprite);
        } else if(sprite instanceof Agent) {
            this.scene.agents.remove(sprite);
        }
    }

    public void destroy(Text text) {
        this.scene.getGridPane().getChildren().remove(text);
    }
}
