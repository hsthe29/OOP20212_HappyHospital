package game.entities;

public interface GameObject {
    void setVelocity(double vecX, double vecY);
    void moveX();
    void moveY();
    void destroy();
}
