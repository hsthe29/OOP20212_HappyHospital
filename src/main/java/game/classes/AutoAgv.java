package game.classes;

import game.classes.statesOfAutoAGV.HybridState;
import game.classes.statesOfAutoAGV.RunningState;
import game.controller.GameController;
import scenes.MainScene;

import java.util.ArrayList;

public class AutoAgv extends Actor {

    public Graph graph;
    public ArrayList<Node2D> path;
    public Node2D curNode;
    public Node2D endNode;
    public int cur;
    public double waitT;
    public int sobuocdichuyen;
    public double thoigiandichuyen;
    public HybridState hybridState;
    public int endX;
    public int endY;
    public Text firstText;
    public int startX;
    public int startY;

    public AutoAgv(MainScene scene,
                   int x, int y,
                   int endX,
                   int endY,
                   Graph graph) {
        super(scene, x, y, "agv");
        this.startX = x * 32;
        this.startY = y * 32;
        this.endX = endX * 32;
        this.endY = endY * 32;

        this.graph = graph;

        this.cur = 0;
        this.waitT = 0;
        this.curNode = this.graph.nodes[x][y];
        this.curNode.setState(StateOfNode2D.BUSY);
        this.endNode = this.graph.nodes[endX][endY];
        this.firstText = new Text(
                this.scene, // getter
                endX,
                endY,
                0, -15,
                "DES",
                "-fx-font-family: \"Courier New\";" +
                        "-fx-font-weight: 900;" +
                        " -fx-fill: red;" +
                        " -fx-stroke: black;" +
                        " -fx-stroke-width: 1.5;" +
                        " -fx-font-size: 22px");

        this.path = this.calPathAStar(this.curNode, this.endNode);
        this.sobuocdichuyen = 0;
        this.thoigiandichuyen = GameController.now();
        this.estimateArrivalTime(x * 32, y * 32, endX * 32, endY * 32);
        this.hybridState = new RunningState(false);
    }

    public void preUpdate(int time, int delta) {
        if(this.hybridState == null) return;
        this.hybridState.move(this);
    }

    public ArrayList<Node2D> calPathAStar(Node2D start, Node2D end) {
        return this.graph.calPathAStar(start, end);
    }

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
        this.path = this.calPathAStar(this.curNode, this.endNode);
        this.cur = 0;
        this.sobuocdichuyen = 0;
        this.thoigiandichuyen = GameController.now();
        this.estimateArrivalTime(
                32 * this.startX,
                32 * this.startY,
                this.endX * 32,
                this.endY * 32);
    }
}
