package classes;

import classes.entities.GameObject;
import scenes.MainScene;

public class Text extends javafx.scene.text.Text implements GameObject {
    public double x;
    public double y;
    public double xIntercept;
    public double yIntercept;
    public MainScene scene;

    public Text(MainScene scene, double x, double y, double xIntecept, double yIntecept, String text, String style) {
        super(text);
        this.setStyle(style);
        this.scene = scene;
        this.x = x;
        this.y = y;
        this.xIntercept = xIntecept;
        this.yIntercept = yIntecept;
        scene.displayList.add(this);
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
    public void setX_(double value) {
        this.setTranslateX(value + this.xIntercept);
    }

    @Override
    public void setY_(double value) {
        this.setTranslateY(value + this.yIntercept);
    }

    @Override
    public void destroy() {
        this.scene.displayList.destroy(this);
    }
}
