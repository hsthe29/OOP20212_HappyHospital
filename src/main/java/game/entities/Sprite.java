package game.entities;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.CacheHint;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import scenes.MainScene;
import game.controller.GameController;

public class Sprite extends ImageView implements GameObject {
    /**
     * Creates a new view that represents an IMG element.
     *
     * @param elem the element to create a view for
     */
    public MainScene scene;
    public SimpleDoubleProperty velocityX = new SimpleDoubleProperty(0);
    public SimpleDoubleProperty velocityY = new SimpleDoubleProperty(0);
    public int x;
    public int y;
    public boolean active;

    public Sprite(MainScene scene, int x, int y, String type) {
        super(scene.load.getImage(type));
        this.setCache(true);
        this.setCacheHint(CacheHint.SPEED);
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
