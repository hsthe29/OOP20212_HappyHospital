package classes.entities;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;
import scenes.MainScene;

public abstract class Sprite extends ImageView implements GameObject {
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

    public void setVelocity(double velocity) {
        this.velocityX.set(velocity);
        this.velocityY.set(velocity);
    }

    public Rectangle2D getBoundary(){
        return new Rectangle2D(this.getTranslateX(),this.getTranslateY(),32,32);
    }

    public Rectangle2D getBoundary(double width, double height){
        double dw = (width - 32) / 2;
        double dh = (height - 32) / 2;
        return new Rectangle2D(this.getTranslateX() - dw,this.getTranslateY() - dh,width,height);
    }

    public boolean intersects(Sprite sprite){
        return sprite.getBoundary(30, 30).intersects(this.getBoundary());
    }

    public boolean intersects(Rectangle2D rect1, Rectangle2D rect2) {
        return rect1.intersects(rect2);
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
