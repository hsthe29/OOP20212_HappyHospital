package game.entities;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.image.ImageView;
import scenes.MainScene;

public class Sprite extends ImageView implements GameObject {
    /**
     * Creates a new view that represents an IMG element.
     *
     */
    public MainScene scene;
    public SimpleDoubleProperty velocityX = new SimpleDoubleProperty(0);
    public SimpleDoubleProperty velocityY = new SimpleDoubleProperty(0);
    public double x;
    public double y;
    public boolean active;

    public Sprite(MainScene scene, double x, double y, String type) {
        super(scene.load.getImage(type));
        this.scene = scene;
        this.x = x;
        this.y = y;
    }

    public void setVelocity(double velocity) { // abstract
        this.velocityX.set(velocity);
        this.velocityY.set(velocity);
    }

    @Override
    public void setVelocity(double vecX, double vecY) {
        this.velocityX.set(vecX);
        this.velocityY.set(vecY);
    }

    public void setX_(double value) {
        this.setTranslateX(value);
    }

    public void setY_(double value) {
        this.setTranslateY(value);
    }

    @Override
    public void moveX() {
        this.setTranslateX(this.getTranslateX() + velocityX.get());
    }

    @Override
    public void moveY() {
        this.setTranslateY(this.getTranslateY() + velocityY.get());
    }

    @Override
    public void destroy() {
        this.scene.displayList.destroy(this);
    }
}
