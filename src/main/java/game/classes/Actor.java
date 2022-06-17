package game.classes;

import game.constant.Constant;
import game.entities.Sprite;
import scenes.MainScene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Actor extends Sprite {
    private static int _id = 0;
    private int agvID;
    private int expectedTime;
    public Set<Actor> collidedActors;

    public Actor(
            MainScene scene,
            double x,
            double y,
            String type
    ) {
        super(scene, x, y, type); // true x, y

        scene.displayList.add(this);

        if(Objects.equals(type, "agv")) {
            Actor._id++;
            this.agvID = Actor._id;
        } else {
            this.agvID = -1; // ám chỉ đây là agent
        }
        this.collidedActors = new HashSet<>();
    }

    public int getAgvID() {
        return this.agvID;
    }

    public int getExpectedTime() {
        return this.expectedTime;
    }

    public void estimateArrivalTime(int startX, int startY, int endX, int endY) {
        this.expectedTime = (int)Math.floor(Math.sqrt((endX - startX) * (endX - startX) + (endY - startY) * (endY - startY)) * 0.085);
    }

    public void writeDeadline(VBox desTable) {
        if(this.agvID != -1) {
            Label txt = new Label("   DES_" + this.agvID + ": " +
                    Constant.secondsToHMS(this.expectedTime) + " ± " + Constant.DURATION);
            txt.setStyle("-fx-font-size: 14");
            desTable.getChildren().add(0, txt);
        }
    }

    public void eraseDeadline(VBox desTable) {
        if(this.agvID != -1) {
            String erasedStr = "   DES_" + this.agvID + ": " +
                    Constant.secondsToHMS(this.expectedTime) + " ± " + Constant.DURATION;
            desTable.getChildren().removeIf(e -> ((Label)e).getText().equals(erasedStr));
        }
    }

    public void freeze(Actor actor) {
        if(this.collidedActors == null) {
            this.collidedActors = new HashSet<>();
        }
        //Thêm actor
        this.collidedActors.add(actor);
    }
}
