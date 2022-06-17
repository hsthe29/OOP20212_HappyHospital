package game.utilities.loadcomponent;

import java.util.ArrayList;

public class AssetsLoader {
    private String name;
    private ArrayList<String> listName;
    private String URL;
    private String extension;

    public AssetsLoader(String name,
                      ArrayList<String> listName,
                      String URL,
                      String extension) {
        this.name = name;
        this.listName = listName;
        this.URL = URL;
        this.extension = extension;
    }

    public String getName() {
        return this.name;
    }

    public void display() {
        System.out.println(this.listName);
        System.out.println(this.URL);
    }

    public ArrayList<String> getNames() {
        return this.listName;
    }

    public String getExtension() {
        return this.extension;
    }

    public String getURL() {
        return this.URL;
    }
}
