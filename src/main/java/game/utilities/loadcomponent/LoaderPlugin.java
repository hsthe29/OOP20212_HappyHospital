package game.utilities.loadcomponent;

import com.google.gson.Gson;
import javafx.scene.image.Image;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class LoaderPlugin {
    private final HashMap<String, Image> images;
    private final HashMap<String, Object> loadedJSON = new HashMap<>();
    private ArrayList<String> imgNames;
    public String baseURL;
    public String baseJsonURL;

    private static LoaderPlugin loader = new LoaderPlugin();
    private LoaderPlugin() {
        images = new HashMap<>();
    }

    public static LoaderPlugin getInstance() {
        if(loader == null)
            loader = new LoaderPlugin();
        return loader;
    }

    public void tilemapTiledJSON(String key, String url) throws IOException {

        BufferedReader bw = new BufferedReader(new FileReader(this.baseJsonURL + url));
        Gson gson = new Gson();
        LayerLoader tiledJSON = gson.fromJson(bw, (Type) LayerLoader.class);
        loadedJSON.put(key, tiledJSON);
        bw.close();
    }

    public Object getTiles(String tilesetName) {
        return this.loadedJSON.get(tilesetName);
    }

    public void image(String key, String url) {
        try {
            images.put(key, new Image(this.baseURL + url));
        } catch (Exception e) {
            System.out.println("error with " + url);
            e.printStackTrace();
        }
    }

    public void image(String url) throws IOException {
        BufferedReader bw = new BufferedReader(new FileReader(this.baseJsonURL + url));
        Gson gson = new Gson();
        AssetsLoader assets = gson.fromJson(bw, (Type) AssetsLoader.class);
        imgNames = assets.getNames();
        String URL = assets.getURL();
        String extension = assets.getExtension();
        int n = imgNames.size();
        for(int i = 0; i < n; ++i) {
            images.put(String.valueOf(i), new Image(this.baseURL + URL + imgNames.get(i) + extension));
        }
        bw.close();
    }

    public Image getImage(String key) {
        return images.get(key);
    }

    public ArrayList<String> getNames() {
        return this.imgNames;
    }
}
