package classes;

import classes.statesOfAutoAGV.HybridState;
import classes.statesOfAutoAGV.RunningState;
import kernel.constant.Constant;
import kernel.utilities.GameController;
import javafx.geometry.Rectangle2D;
import scenes.MainScene;
import socket.Message;
import socket.Path;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AutoAgv extends Actor implements Controllable{

    public Graph graph;
    public ArrayList<Node2D> path;
    public Node2D curNode;
    public Node2D endNode;
    public int cur;
    public double waitT;
    public int movingSteps;
    public double travelTime;
    public double time;
    public HybridState hybridState;
    private int endX;
    private int endY;
    public Text firstText;
    private int startX;
    private int startY;
    public List<Controllable> collidedAGVs;
    public Controllable stuckAGV = null;

    public AutoAgv(MainScene scene,
                   int x, int y,
                   int endX,
                   int endY,
                   Graph graph) {
        super(scene, x * 32, y * 32, "agv");
        this.startX = x * 32;
        this.startY = y * 32;
        this.endX = endX * 32;
        this.endY = endY * 32;
        int stX = 4, stY;
        boolean upOrDown = Math.random() > 0.5;
        if(upOrDown) {
            // up
            stY = 12;
        } else {
            stY = 15;
        }
        this.graph = graph;
        this.cur = 0;
        this.waitT = 0;
        this.curNode = this.graph.nodes[stX][stY];
        this.curNode.setState(StateOfNode2D.BUSY);
        this.endNode = this.graph.nodes[endX][endY];
        this.firstText = new Text(
                this.scene, // getter
                endX,
                endY,
                -1, -15,
                "DES",
                "-fx-font-family: \"Courier New\";" +
                        "-fx-font-weight: 900;" +
                        " -fx-fill: red;" +
                        " -fx-stroke: black;" +
                        " -fx-stroke-width: 1;" +
                        " -fx-font-size: 22px");

        this.path = this.calPathAStar(this.curNode, this.endNode);
        this.movingSteps = 0;
        this.travelTime = GameController.now();
        this.time = GameController.now();
        this.estimateArrivalTime(x * 32, y * 32, endX * 32, endY * 32);
        this.hybridState = new RunningState(false);
        if(this.path == null) {
            this.eliminate();
            return;
        }
        if(!upOrDown) {
            this.path.add(0, this.graph.nodes[4][14]);
        }
        this.path.add(0, this.graph.nodes[4][13]);
        this.path.add(0, this.graph.nodes[x][y]);
        collidedAGVs = new ArrayList<>(20);
    }

    public void preUpdate() {
        if(this.hybridState == null) return;
        this.hybridState.move(this);
        this.collidedActors.clear();
        this.collidedAGVs.clear();
    }

    /**
     * Find path using AStar algorithm
     * */
    public ArrayList<Node2D> calPathAStar(Node2D start, Node2D end)  {
        Position startPos = new Position(start.x, start.y);
        Position endPos = new Position(end.x, end.y);
        ArrayList<Node2D> result = new ArrayList<>();
        try {
            scene.controller.oos.flush();
            scene.controller.oos.writeObject(new Message(4, startPos, endPos));
            ArrayList<Position> pp = ((Path) scene.controller.ois.readObject()).path;
            if(pp == null) return null;
            for(int k = 0; k < pp.size(); k++) {
                result.add(graph.nodes[pp.get(k).dx][pp.get(k).dy]);
            }
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
        return result;

    }
    /**
     * Find path to the Gate
     * */
    public void changeTarget() {
        MainScene scene = this.scene; //getter;
        int[] agvsToGate1 = scene.mapOfExits.get("Gate1");
        int[] agvsToGate2 = scene.mapOfExits.get("Gate2");
        String choosenGate = agvsToGate1[2] < agvsToGate2[2] ? "Gate1" : "Gate2";
        int[] newArray = scene.mapOfExits.get(choosenGate);
        newArray[2]++;
        scene.mapOfExits.put(choosenGate, newArray);

        this.startX = this.endX;
        this.startY = this.endY;

        int xEnd = newArray[0];
        int yEnd = newArray[1];
        this.endX = xEnd * 32;
        this.endY = yEnd * 32;

        int finalAGVs = scene.mapOfExits.get(choosenGate)[2];

        this.endNode = this.graph.nodes[xEnd][yEnd];
        this.firstText = new Text(
                this.scene, // getter
                xEnd,
                yEnd,
                -5,
                -15,
                "DES_" + finalAGVs,
                "-fx-font-family: \"Courier New\";" +
                        "-fx-font-weight: 900;" +
                        " -fx-fill: red;" +
                        " -fx-stroke: black;" +
                        " -fx-stroke-width: 1;" +
                        " -fx-font-size: 18px"
        );

//        this.path = this.calPathAStar(this.curNode, this.endNode);
        this.findPath();
        this.cur = 0;
        this.movingSteps = 0;
        this.travelTime = GameController.now();
        this.estimateArrivalTime(
                32 * this.startX,
                32 * this.startY,
                this.endX * 32,
                this.endY * 32);
    }

    public void findPath() {
        this.path = this.calPathAStar(this.curNode, this.endNode);
        if(this.path == null) {
            if(Constant.norm1(curNode, 47, 7) < Constant.norm1(curNode, 47, 18)) {
                this.path = this.calPathAStar(this.curNode, this.graph.nodes[47][7]);
            } else {
                this.path = this.calPathAStar(this.curNode, this.graph.nodes[47][18]);
            }
            this.path.add(this.graph.nodes[47][this.endNode.y]);
            this.path.add(endNode);
        }
    }

    public void eliminate() {
        this.firstText.destroy();
        this.scene.autoAgvs.remove(this);
        this.destroy();
    }

    @Override
    public Rectangle2D getFuture() {
        if(this.cur + 1 >= this.path.size()) {
            return this.getBoundary(30, 30);
        }
        Node2D temp = this.path.get(this.cur + 1);
        double dx = temp.x * 32 - this.getTranslateX();
        double dy = temp.y * 32 - this.getTranslateY();
        double d = Double.max(Math.abs(dx), Math.abs(dy));
        double new_x = this.getTranslateX() + dx/d + 1;
        double new_y = this.getTranslateY() + dy/d + 1;
        return new Rectangle2D(new_x, new_y, 30, 30);
    }
}
