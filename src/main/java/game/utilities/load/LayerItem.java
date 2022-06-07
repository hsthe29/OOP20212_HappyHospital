package game.utilities.load;

import game.constant.ModeOfDirection;

import java.util.ArrayList;

public class LayerItem {
    private ArrayList<Integer> dataX;
    private ArrayList<Integer> dataY;
    private ArrayList<Integer> id;
    private ArrayList<ModeOfDirection> direction;
    private String name;
    private boolean visible;
    private String type;

    public ArrayList<Integer> getDataX() {
        return this.dataX;
    }

    public ArrayList<Integer> getDataY() {
        return this.dataY;
    }

    public ArrayList<Integer> getID() {
        return this.id;
    }

    public ArrayList<ModeOfDirection> getDirections() {
        return this.direction;
    }

    public String getName() {
        return this.name;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public String getType() {
        return this.type;
    }

    public LayerItem(
            ArrayList<Integer> dataX,
            ArrayList<Integer> dataY,
            ArrayList<Integer> id,
            ArrayList<ModeOfDirection> direction,
            String name,
            boolean visible,
            String type
    ) {
        this.dataX = dataX;
        this.dataY = dataY;
        this.id = id;
        this.direction = direction;
        this.name = name;
        this.visible = visible;
        this.type = type;
    }
}
