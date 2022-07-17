package tilemaps;

import kernel.constant.ModeOfDirection;
import kernel.utilities.loadcomponent.LayerItem;
import kernel.utilities.loadcomponent.LayerLoader;
import kernel.utilities.loadcomponent.LoaderPlugin;
import scenes.MainScene;

import java.util.ArrayList;
import java.util.HashMap;

public class Tilemap {
    public MainScene scene;

    public Tilemap() {
    }

    public void setScene(MainScene scene) {
        this.scene = scene;
    }

    public HashMap<String, ArrayList<Tile>> addTilesetImage(String tilesetName) {
        HashMap<String, ArrayList<Tile>> tileSet = new HashMap<>();
        LoaderPlugin lp = LoaderPlugin.getInstance();
        LayerLoader ld = (LayerLoader) lp.getTiles(tilesetName);
        ArrayList<LayerItem> listLi = ld.getItems();

        for(LayerItem li: listLi) {
            ArrayList<Tile> imgv = new ArrayList<>();
            ArrayList<Integer> dataX = li.getDataX();
            ArrayList<Integer> dataY = li.getDataY();
            ArrayList<Integer> id = li.getID();
            ArrayList<ModeOfDirection> dir = li.getDirections();
            String name = li.getName();
            int n = id.size();
            for(int i = 0; i < n; ++i) {
                Tile spr = new Tile(lp.getImage(String.valueOf(id.get(i))), name);
                spr.setDirection(dir.get(i));
                spr.setPosition(dataX.get(i), dataY.get(i));
                imgv.add(spr);
            }
            tileSet.put(name, imgv);
        }

        return tileSet;
    }

    public TilemapLayer createLayer(String key, HashMap<String, ArrayList<Tile>> tileSet, double x, double y) {
        ArrayList<Tile> ts = tileSet.get(key);

        TilemapLayer tml = new TilemapLayer(this.scene, ts, key);
        for(Tile iv: ts) {
            iv.place(scene.getGridPane());
        }
        return tml;
    }
}
