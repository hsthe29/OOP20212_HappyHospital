package classes;

import kernel.constant.Constant;
import kernel.constant.ModeOfDirection;
import tilemaps.Tile;
import tilemaps.TilemapLayer;
import javafx.animation.PauseTransition;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.util.Duration;
import scenes.MainScene;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Agv extends Actor implements Controllable{

    private PauseTransition hideToastOverLay = new PauseTransition(Duration.seconds(1));
    private PauseTransition hideToastInvalid = new PauseTransition(Duration.seconds(1));
    private Label overlayToast;
    private Label invalidToast;

    private boolean isDisable = false;
    private Text text;
    private TilemapLayer pathLayer;
    private int desX;
    private int desY;
    private Text desText;
    public List<Controllable> collidedAGVs;
    public Controllable stuckAGV = null;

    public Agv(MainScene scene, int x, int y, int desX, int desY, TilemapLayer pathLayer) {
        super(scene, x, y, "agv");
        this.desX = desX;
        this.desY = desY;

        this.pathLayer = pathLayer;

        this.estimateArrivalTime(x, y, desX * 32, desY * 32);

        this.text = new Text(
                this.scene,
                this.x,
                this.y,
                0, -35,
                "AGV",
                "-fx-font-family: \"Courier New\";" +
                        "-fx-font-weight: 900;" +
                        " -fx-fill: green;" +
                        " -fx-stroke: black;" +
                        " -fx-stroke-width: 1.5;" +
                        " -fx-font-size: 18px"
        );

        this.desText = new Text(
                this.scene, //
                this.desX,
                this.desY,
                0, -15,
                "DES",
                "-fx-font-family: \"Courier New\";" +
                        "-fx-font-weight: 900;" +
                        " -fx-fill: green;" +
                        " -fx-stroke: black;" +
                        " -fx-stroke-width: 1.5;" +
                        " -fx-font-size: 22px");
        makeToast();
        collidedAGVs = new ArrayList<>(20);
    }

    private void makeToast() {
        String style = "-fx-label-padding: 10 30 10 30;" +
                "-fx-border-color:transparent;" +
                " -fx-background-color: white;" +
                "-fx-text-fill: black;" +
                "-fx-border-radius: 30 30 30 30;" +
                "-fx-background-radius: 30 30 30 30;" +
                "-fx-font-size: 18;" +
                "-fx-font-weight: 900";

        overlayToast = new Label("AGV va chạm với Agent!");
        overlayToast.setStyle(style);
        overlayToast.setVisible(false);
        hideToastOverLay.setOnFinished(e -> overlayToast.setVisible(false));
        this.scene.getStackPane().getChildren().add(overlayToast);
        overlayToast.setTranslateY(300);

        invalidToast = new Label("Di chuyển không hợp lệ!");
        invalidToast.setStyle(style);
        invalidToast.setVisible(false);
        hideToastInvalid.setOnFinished(e -> invalidToast.setVisible(false));
        this.scene.getStackPane().getChildren().add(invalidToast);
        invalidToast.setTranslateY(300);
    }

    public void ToastInvalidMove() {
        invalidToast.setVisible(true);
        hideToastInvalid.play();
    }
    public void ToastOverLay() {
        overlayToast.setVisible(true);
        hideToastOverLay.play();
    }

    public void update() {
        this.setVelocity(0);
        if(isDisable) return;
        if(!this.collidedActors.isEmpty()) return;

        boolean t = true, b = true, l = true, r = true;

        Iterator<Agent> agentIterator = scene.agents.iterator();
        while(agentIterator.hasNext()){
            Agent agent = agentIterator.next();
            if(scene.physics.collider(scene.getAgv(),agent)){
                ToastOverLay();
                return;
            }
        }

        ArrayList<Tile> tiles = this.getTilesWithin();
        for (Tile tile: tiles) {
            if(tile.getDirection() == ModeOfDirection.TOP) {
                b = false;
                if(this.scene.controller.keyS)
                    ToastInvalidMove();
            } else if (tile.getDirection() == ModeOfDirection.BOTTOM) {
                t = false;
                if(this.scene.controller.keyW)
                    ToastInvalidMove();
            } else if (tile.getDirection() == ModeOfDirection.LEFT) {
                r = false;
                if(this.scene.controller.keyD)
                    ToastInvalidMove();
            } else if (tile.getDirection() == ModeOfDirection.RIGHT) {
                l = false;
                if(this.scene.controller.keyA)
                    ToastInvalidMove();
            }
        }

        int _x = (int) ((this.getTranslateX() + 16) / 32);
        int _y = (int) ((this.getTranslateY() + 16) / 32);
        if(this.scene.controller.keyW) {
            if(t) {
                if(this.scene.physics.collision(_x, _y, 'W')) {
                    this.setTranslateY((int) ((this.getTranslateY() + 16) / 32) * 32);
                }
                else this.velocityY.set(-Constant.vec);
            }
        }
        if(this.scene.controller.keyS) {
            if(b) {
                if(this.scene.physics.collision(_x, _y, 'S')) {
                    this.setTranslateY((int) ((this.getTranslateY() + 16) / 32) * 32);
                }
                else this.velocityY.set(Constant.vec);
            }
        }
        if(!this.collidedAGVs.isEmpty()) {
            Rectangle2D nextPlace = new Rectangle2D(this.getTranslateX() + this.velocityX.get() + 1,
                    this.getTranslateY() + this.velocityY.get() + 1, 30, 30);

            for(Controllable c: this.collidedAGVs) {
                if(nextPlace.intersects(((AutoAgv)c).getBoundary(30, 30))) {
                    if(!this.equals(((AutoAgv) c).stuckAGV)) {
                        this.stuckAGV = c;
                        setVelocity(0, 0);
                    }
                    break;
                }
            }
        }
        moveY();
        setVelocity(0, 0);
        if(this.scene.controller.keyA) {
            if(l) {
                if(this.scene.physics.collision(_x, _y, 'A')) {
                    this.setTranslateX((int) ((this.getTranslateX() + 16) / 32) * 32);
                }
                else
                    this.velocityX.set(-Constant.vec);
            }
        }
        if(this.scene.controller.keyD) {
            if(r ) {
                if(this.scene.physics.collision(_x, _y, 'D')) {
                    this.setTranslateX((int) ((this.getTranslateX() + 16) / 32) * 32);
                }
                else this.velocityX.set(Constant.vec);
            }
        }
        if(!this.collidedAGVs.isEmpty()) {
            Rectangle2D nextPlace = new Rectangle2D(this.getTranslateX() + this.velocityX.get() + 1,
                    this.getTranslateY() + this.velocityY.get() + 1, 30, 30);

            for(Controllable c: this.collidedAGVs) {
                if(nextPlace.intersects(((AutoAgv)c).getBoundary(30, 30))) {
                    if(!this.equals(((AutoAgv) c).stuckAGV)) {
                        this.stuckAGV = c;
                        setVelocity(0, 0);
                    }
                    break;
                }
            }
        }
        moveX();
        this.text.setX_(this.getTranslateX());
        this.text.setY_(this.getTranslateY());
    }

    private ArrayList<Tile> getTilesWithin() {
        return this.pathLayer.getTilesWithinXY(this.getTranslateX(), this.getTranslateY(), 30, 30);
    }

    @Override
    public Rectangle2D getFuture() {
        return this.getBoundary(30, 30);
    }
}
