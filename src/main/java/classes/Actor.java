package classes;

import kernel.constant.Constant;
import classes.entities.Sprite;
import javafx.geometry.Rectangle2D;
import scenes.MainScene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import java.util.ArrayList;
import java.util.Objects;

public abstract class Actor extends Sprite {
    private static int _id = 0;
    private int agvID;
    private int expectedTime;
    public ArrayList<Actor> collidedActors;

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
        this.collidedActors = new ArrayList<>();
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

    public abstract Rectangle2D getFuture();
}
