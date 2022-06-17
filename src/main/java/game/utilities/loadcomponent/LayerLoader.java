package game.utilities.loadcomponent;

import java.util.ArrayList;

public class LayerLoader {
    private String name;
    private ArrayList<LayerItem> listItem;

    public LayerLoader(String name, ArrayList<LayerItem> listItem) {
        this.name = name;
        this.listItem = listItem;
    }

    public void display() {
        System.out.println(this.listItem);
    }

    public ArrayList<LayerItem> getItems() {
        return this.listItem;
    }
}
