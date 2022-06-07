package game.classes.statesOfAutoAGV;

import game.classes.AutoAgv;

public abstract class HybridState {
    public HybridState() {}

    public abstract void move(AutoAgv agv);
}