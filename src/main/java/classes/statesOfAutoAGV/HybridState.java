package classes.statesOfAutoAGV;

import classes.AutoAgv;

public abstract class HybridState {
    public HybridState() {}

    public abstract void move(AutoAgv agv);
}