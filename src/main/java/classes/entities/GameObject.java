package classes.entities;

public interface GameObject {
    void setVelocity(double vecX, double vecY);
    void moveX();
    void moveY();
    void setX_(double value);
    void setY_(double value);
    void destroy();
}
