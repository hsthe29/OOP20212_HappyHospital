package classes.statesOfAutoAGV;

import classes.AutoAgv;
import kernel.constant.Constant;
import kernel.utilities.GameController;
import scenes.MainScene;

public class IdleState extends HybridState {
    private double _start;
    private double _init;
    private boolean _calculated;

    public IdleState(double start, double initTime) {
        super();
        this._start = start;
        this._init = initTime;
        this._calculated = false;
    }

    @Override
    public void move(AutoAgv agv) {
        if(GameController.now() - this._start < Constant.DURATION * 1000) {
            if(!this._calculated) {
                this._calculated = true;
                double finish = (this._start - this._init)/1000;
                MainScene mainScene = agv.scene;
                int expectedTime = agv.getExpectedTime();
                if(finish >= expectedTime - Constant.DURATION
                        && finish <= expectedTime + Constant.DURATION){
                    return;
                } else {
                    double diff = Math.max(expectedTime - Constant.DURATION - finish,
                            finish - expectedTime - Constant.DURATION);
                    double lateness = Constant.getLateness(diff);
                    mainScene.setHarmfullness(Math.max(mainScene.getHarmfullness() + lateness, 0));
                }
            }
        } else {
            if(agv != null) {
                agv.firstText.destroy();
                agv.eraseDeadline(agv.scene.desTable);
                agv.hybridState = new RunningState(true);
                agv.changeTarget();
            }
        }
    }
}


