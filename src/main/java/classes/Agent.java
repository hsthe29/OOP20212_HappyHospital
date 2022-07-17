package classes;

import kernel.algorithm.AStar;
import kernel.constant.Constant;
import javafx.geometry.Rectangle2D;
import scenes.MainScene;
import socket.Message;
import socket.Path;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

public class Agent extends Actor{
    private Position startPos;
    private Position endPos;
    private ArrayList<Position> groundPos;
    private ArrayList<Position> path;
    private ArrayList<Position> vertices;
    private Text endText;
    private Text agentText;
    private AStar astar;
    public Stack<Position> tempPos = new Stack<>();
    private int next = 1;
    private int id;
    public double speed = 0.8;

    public Agent(
            MainScene scene,
            Position startPos,
            Position endPos,
            ArrayList<Position> groundPos,
            int id
    ) throws IOException, ClassNotFoundException {
        super(scene, startPos.x * 32, startPos.y * 32, "agent");
        this.startPos = startPos;
        this.endPos = endPos;
        this.groundPos = groundPos;
        this.path = new ArrayList<>();
        this.vertices = new ArrayList<>();
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
        scene.controller.oos.flush();
        scene.controller.oos.writeObject(new Message(3, startPos, endPos));
        this.path = ((Path) scene.controller.ois.readObject()).path;
        if(this.path == null) this.eliminate();

        this.initVertexs();
    }

    public void preUpdate() {
        this.goToDestinationByVertexs();
        this.collidedActors.clear();
    }

    public void goToDestinationByVertexs() {
        if(this.next == this.vertices.size()) {
            this.setVelocity(0);
            this.eliminate();
            return;
        }

        if(!this.tempPos.empty()) {
            this.scene.physics.moveAgent(
                    this,
                    this.tempPos.peek().x,
                    this.tempPos.peek().y,
                    this.speed
            );

            if(Math.abs(this.getTranslateX() - this.tempPos.peek().x) < 1 && Math.abs(this.getTranslateY() - this.tempPos.peek().y) < 1) {
                this.tempPos.pop();
            }
            this.agentText.setX_(this.getTranslateX());
            this.agentText.setY_(this.getTranslateY());
            return;
        }

        if ((Math.abs(this.vertices.get(this.next).x * 32 - this.getTranslateX()) > 1 ||
                Math.abs(this.vertices.get(this.next).y * 32 - this.getTranslateY()) > 1))
        {
            this.scene.physics.moveAgent(
                    this,
                    this.vertices.get(this.next).x * 32,
                    this.vertices.get(this.next).y * 32,
                    this.speed
            );
            this.agentText.setX_(this.getTranslateX());
            this.agentText.setY_(this.getTranslateY());
        } else {
            this.next++;
            if(this.next == this.vertices.size()) this.eliminate();
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
                    this.vertices.add(rV);
                    break;
                }
            }
        }
    }

    public void eliminate() {
        this.endText.destroy();
        this.agentText.destroy();
        this.destroy();
        this.scene.agents.remove(this);
    }

    public void initVertexs() {
        if(this.path != null) {
            this.vertices.add(this.path.get(0));
            for(int cur = 2; cur < this.path.size(); cur++) {
                if (
                        (this.path.get(cur).dx == this.path.get(cur - 1).dx &&
                                this.path.get(cur).dx == this.path.get(cur - 2).dx) ||
                                (this.path.get(cur).dy == this.path.get(cur - 1).dy &&
                                        this.path.get(cur).dy == this.path.get(cur - 2).dy)
                ) {
                    continue;
                }

                Position curV = this.vertices.get(this.vertices.size() - 1);
                Position nextV = this.path.get(cur - 1);
                this.addRandomVertexs(curV, nextV);
                this.vertices.add(nextV);
            }
            this.addRandomVertexs(this.vertices.get(this.vertices.size() - 1),
                    this.path.get(this.path.size() - 1));
            this.vertices.add(this.path.get(this.path.size() - 1));
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

    @Override
    public Rectangle2D getFuture() {
        double trueX, trueY;
        if(this.tempPos.empty()) {
            trueX = this.vertices.get(this.next).x * 32;
            trueY = this.vertices.get(this.next).y * 32;
        } else {
            trueX = this.tempPos.peek().x;
            trueY = this.tempPos.peek().y;
        }
        double dx = trueX - this.getTranslateX();
        double dy = trueY - this.getTranslateY();
        double d = Math.sqrt(dx * dx + dy * dy);
        double vx = speed * dx / d;
        double vy = speed * dy / d;
        return new Rectangle2D(
                this.getTranslateX() + vx,
                this.getTranslateY() + vy,
                32, 32);
    }
}

