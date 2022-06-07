package game.classes;

import game.entities.GameObject;
import scenes.MainScene;

public class Text extends javafx.scene.text.Text implements GameObject {
    public int i;
    public int j;
    private double x;
    private double y;
    private double xIntercept;
    private double yIntercept;
    public MainScene scene;

    public Text(MainScene scene, int i, int j, double xIntecept, double yIntecept, String text, String style) {
        super(text);
        this.setStyle(style);
        this.scene = scene;
        this.i = i;
        this.j = j;
        this.x = 32 * i + xIntecept;
        this.y = 32 * j + yIntecept;
        this.xIntercept = xIntecept;
        this.yIntercept = yIntecept;
        scene.displayList.add(this);
    }

    public void set_X(double value) {
        this.x = value + this.xIntercept;
    }

    public void set_Y(double value) {
        this.y = value + this.yIntercept;
    }

    @Override
    public void setVelocity(double vecX, double vecY) {

    }

    @Override
    public void moveX() {
        this.setTranslateX(this.x);
    }

    @Override
    public void moveY() {
        this.setTranslateY(this.y);
    }

    @Override
    public void destroy() {
        this.scene.displayList.destroy(this);
    }
}
