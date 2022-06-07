package game.tilemaps;

import game.constant.ModeOfDirection;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class Tile extends ImageView {
    public String name;
    public int xPlace = 0;
    public int yPlace = 0;
    public double trueX;
    public double trueY;
    private ModeOfDirection direction = ModeOfDirection.NO_DIRECTION;

    public Tile(Image img, String name) {
        super(img);
        this.name = name;
    }

    public void setPosition(int x, int y) {
        this.xPlace = x;
        this.yPlace = y;
        this.trueX = x * 32.0;
        this.trueY = y * 32.0;
    }

    public void place(GridPane gridPane) {
        gridPane.add(this, xPlace, yPlace, 1, 1);
    }

    public void setDirection(ModeOfDirection direction) {
        this.direction = direction;
    }

    public int X() {
        return this.xPlace;
    }

    public int Y() {
        return this.yPlace;
    }

    public ModeOfDirection getDirection() {
        return this.direction;
    }

    @Override
    public String toString() {
        return "Tile{" +
                "name='" + name + '\'' +
                ", yPlace=" + yPlace +
                ", xPlace=" + xPlace +
                ", trueX=" + trueX +
                ", trueY=" + trueY +
                ", direction=" + direction +
                '}';
    }
}
