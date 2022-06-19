package game.classes;

import game.algorithm.AStar;
import game.constant.Constant;
import javafx.scene.text.Font;
import scenes.MainScene;

import java.util.ArrayList;

public class Agent extends Actor{
    private Position startPos;
    private Position endPos;
    private ArrayList<Position> groundPos;
    private ArrayList<Position> path;
    private ArrayList<Position> vertexs;
    private Text endText;
    private Text agentText;
    private AStar astar;
    private int next = 1;
    private int id;
    public boolean isOverlap = false;
    public double speed = 0.8;

    public Agent(
            MainScene scene,
            Position startPos,
            Position endPos,
            ArrayList<Position> groundPos,
            int id
    ) {
        super(scene, startPos.x * 32, startPos.y * 32, "agent");
        this.startPos = startPos;
        this.endPos = endPos;
        this.groundPos = groundPos;
        this.path = new ArrayList<>();
        this.vertexs = new ArrayList<>();
        this.id = id;
        this.speed = Constant.randomSpeed.nextDouble(0.4, 1.0);

        this.endText = new Text(
                this.scene,
                endPos.dx,
                endPos.dy,
                0,
                -15,
                String.valueOf(id),
                "-fx-font-family: \"Courier New\";" +
                        "-fx-font-weight: 900;" +
                        " -fx-fill: white;" +
                        " -fx-stroke: black;" +
                        " -fx-stroke-width: 1;" +
                        " -fx-font-size: 28px");

        this.agentText = new Text(
                this.scene,
                startPos.dx,
                startPos.dy,
                6,
                -36,
                String.valueOf(id),
                "-fx-font-family: \"Courier New\";" +
                        "-fx-font-weight: 900;" +
                        " -fx-fill: white;" +
                        " -fx-stroke: black;" +
                        " -fx-stroke-width: 1;" +
                        " -fx-font-size: 18px"
        );

        this.astar = new AStar(52, 28, startPos, endPos, groundPos);
        this.path = this.astar.cal();
        if(this.path == null) this.eliminate();

        this.initVertexs();
    }

    public void preUpdate() {
        this.goToDestinationByVertexs();
    }

    public void goToDestinationByVertexs() {
        if(this.next == this.vertexs.size()) {
//            this.agentText.setText("DONE");
//            this.agentText.setFont(new Font(12));
//            this.agentText.setX_(this.getTranslateX() - 1);
//            this.x = this.vertexs.get(this.vertexs.size() - 1).dx * 32;
//            this.y = this.vertexs.get(this.vertexs.size() - 1).dy * 32;
            this.setVelocity(0);
            this.eliminate();
            return;
        }
        if ((Math.abs(this.vertexs.get(this.next).x * 32 - this.getTranslateX()) > 1 ||
                Math.abs(this.vertexs.get(this.next).y * 32 - this.getTranslateY()) > 1))
        {
//            System.out.println("\n x = " + this.next);
//            System.out.println("update: " + this.translateX + ", " + this.translateY + ", " + this.vertexs.get(this.next).x * 32 + ",  " + this.vertexs.get(this.next).y * 32);
            this.scene.physics.moveTo(
                    this,
                    this.vertexs.get(this.next).x * 32,
                    this.vertexs.get(this.next).y * 32,
                    this.speed
            );
           this.agentText.setX_(this.getTranslateX());
            this.agentText.setY_(this.getTranslateY());
        } else {
            this.next++;
        }
    }

    public void addRandomVertexs(Position start, Position end) {
        double dis = Math.sqrt((start.x - end.x) * (start.x - end.x) + (start.y - end.y) * (start.y - end.y));
        int num = (int)Math.ceil((dis * 32) / 50);
        for(int i = 1; i < num; i++) {
            while (true) {
                Position rV = new Position(((end.x - start.x) / num) * i + start.x + (Math.random() - 0.5),
                        ((end.y - start.y) / num) * i + start.y + (Math.random() - 0.5));
                Position _1, _2, _3, _4;
                boolean b_1 = false, b_2 = false, b_3 = false, b_4 = false;

                _1 = new Position(rV.x, rV.y);
                _2 = new Position(rV.x + 1, rV.y);
                _3 = new Position(rV.x + 1, rV.y + 1);
                _4 = new Position(rV.x, rV.y + 1);

                for (int j = 0; j < this.groundPos.size(); j++) {
                    Position p = this.groundPos.get(j);
                    if (_1.x < p.x + 1 && _1.y < p.y + 1 && _1.x >= p.x && _1.y >= p.y) {
                        b_1 = true;
                    }
                    if (_2.x < p.x + 1 && _2.y < p.y + 1 && _2.x >= p.x && _2.y >= p.y) {
                        b_2 = true;
                    }
                    if (_3.x < p.x + 1 && _3.y < p.y + 1 && _3.x >= p.x && _3.y >= p.y) {
                        b_3 = true;
                    }
                    if (_4.x < p.x + 1 && _4.y < p.y + 1 && _4.x >= p.x && _4.y >= p.y) {
                        b_4 = true;
                    }
                }
                if (b_1 && b_2 && b_3 && b_4) {
                    this.vertexs.add(rV);
                    break;
                }
            }
        }
    }

    public void eliminate() {
        this.endText.destroy();
        this.agentText.destroy();
        this.destroy();
    }

    public void initVertexs() {
        if(this.path != null) {
            this.vertexs.add(this.path.get(0));
            for(int cur = 2; cur < this.path.size(); cur++) {
                if (
                        (this.path.get(cur).dx == this.path.get(cur - 1).dx &&
                                this.path.get(cur).dx == this.path.get(cur - 2).dx) ||
                                (this.path.get(cur).dy == this.path.get(cur - 1).dy &&
                                        this.path.get(cur).dy == this.path.get(cur - 2).dy)
                ) {
                    continue;
                }

                Position curV = this.vertexs.get(this.vertexs.size() - 1);
                Position nextV = this.path.get(cur - 1);
                this.addRandomVertexs(curV, nextV);
                this.vertexs.add(nextV);
            }
            this.addRandomVertexs(this.vertexs.get(this.vertexs.size() - 1),
                    this.path.get(this.path.size() - 1));
            this.vertexs.add(this.path.get(this.path.size() - 1));
        }
    }

    public Position getStartPos() {
        return this.startPos;
    }
    public Position getEndPos() {
        return this.endPos;
    }

    public int get_Id() {
        return this.id;
    }

//    public void pause() {
//        this.setVelocity(0, 0);
//        this.setActive(false);
//    }
//    public void restart() {
//        this.setActive(true);
//    }
//
//    public void waitOverlap() {
//        if (this.isOverlap) return;
//        this.isOverlap = true;
////        setTimeout(() => {
////                this.isOverlap = false;
////    }, 4000);
//        double r = Math.random();
//        if (r < 0.5) {
//        } else {
//            this.setVelocity(0, 0);
//            this.setActive(false);
////            setTimeout(() => {
////                    this.setActive(true);
////      }, 2000);
//        }
//    }
}

