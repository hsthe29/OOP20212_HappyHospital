package kernel.utilities;

import classes.*;
import kernel.constant.Constant;
import classes.entities.Sprite;
import tilemaps.Tile;
import javafx.geometry.Rectangle2D;
import scenes.MainScene;
import java.util.List;

public class Physics {
    public MainScene scene;

    public Physics(MainScene scene) {
        this.scene = scene;
    }

    public void update() {
        List<AutoAgv> autoAgvs = this.scene.autoAgvs;
        Agv agv = scene.getAgv();
        Rectangle2D agvFuture = agv.getFuture();

        for(int i = 0; i < scene.agents.size(); i++) {
            Rectangle2D agentFuture = this.scene.agents.get(i).getFuture();
            for(int j = 0; j < autoAgvs.size(); j++) {
                if(autoAgvs.get(j).path ==null) continue;
                if(agentFuture.intersects(autoAgvs.get(j).getFuture())) {
                    scene.agents.get(i).collidedActors.add(autoAgvs.get(j));
                    autoAgvs.get(j).collidedActors.add(scene.agents.get(i));
                }
            }
            if(agentFuture.intersects(scene.getAgv().getFuture())) {
                agv.collidedActors.add(scene.agents.get(i));
                scene.agents.get(i).collidedActors.add(agv);
            }
        }

        for(int i = 0; i < autoAgvs.size(); i++) {
            if(autoAgvs.get(i).path == null) continue;
            Rectangle2D temp = autoAgvs.get(i).getFuture();
            for(int j = i + 1; j < autoAgvs.size(); j++) {
                if(autoAgvs.get(j).path == null) continue;
                if(temp.intersects(autoAgvs.get(j).getFuture())) {
                    autoAgvs.get(i).collidedAGVs.add(autoAgvs.get(j));
                    autoAgvs.get(j).collidedAGVs.add(autoAgvs.get(i));
                }
            }
            if(temp.intersects(agvFuture)) {
                agv.collidedAGVs.add(autoAgvs.get(i));
                autoAgvs.get(i).collidedAGVs.add(agv);
            }
        }
    }

    public boolean collider(Sprite sprite_a, Sprite sprite_b) {
        if(sprite_a.intersects(sprite_b))
            return true;
        return false;
    }

    public void moveAutoAGV(AutoAgv agv, double trueX, double trueY) {
        agv.setVelocity(0, 0);
        double dx = trueX - agv.getTranslateX();
        double dy = trueY - agv.getTranslateY();
        double d = Double.max(Math.abs(dx), Math.abs(dy));
        if(agv.collidedAGVs.isEmpty())
            agv.setVelocity(dx / d, dy / d);
        else {
            Rectangle2D nextPlace = new Rectangle2D(agv.getTranslateX() + dx/d + 1, agv.getTranslateY() + dy/d + 1, 30, 30);
            boolean free = true;
            for(Controllable c: agv.collidedAGVs) {
                if(c instanceof Agv) {
                    if(nextPlace.intersects(((Agv)c).getBoundary(30, 30))) {
                        if(agv.equals(((Agv) c).stuckAGV)) {
                            agv.setVelocity(dx / d, dy / d);
                        } else agv.stuckAGV = c;
                        free = false;
                        break;
                    }
                } else {
                    if(nextPlace.intersects(((AutoAgv)c).getBoundary(30, 30))) {
                        if(agv.equals(((AutoAgv) c).stuckAGV)) {
                            agv.setVelocity(dx / d, dy / d);
                        } else agv.stuckAGV = c;
                        free = false;
                        break;
                    }
                }
            }
            if (free) agv.setVelocity(dx / d, dy / d);
        }
        agv.moveX();
        agv.moveY();
    }

    public void moveAgent(Agent agent, double trueX, double trueY, double speed) {
        agent.setVelocity(0, 0);
        double dx = trueX - agent.getTranslateX();
        double dy = trueY - agent.getTranslateY();
        double d = Math.sqrt(dx * dx + dy * dy);
        double vx = speed * dx / d;
        double vy = speed * dy / d;
        Rectangle2D agentBoundary = new Rectangle2D(
                agent.getTranslateX() + vx,
                agent.getTranslateY() + vy,
                32, 32);

        if(agent.collidedActors.isEmpty()) {
                agent.setVelocity(vx, vy);
        }
        else {
            boolean moveLeft = true, moveRight = true, moveUp = true, moveDown = true;
            Rectangle2D recL = new Rectangle2D(agent.getTranslateX() - 32, agent.getTranslateY(), 32, 32);
            Rectangle2D recR = new Rectangle2D(agent.getTranslateX() + 32, agent.getTranslateY(), 32, 32);
            Rectangle2D recU = new Rectangle2D(agent.getTranslateX(), agent.getTranslateY() - 32, 32, 32);
            Rectangle2D recD = new Rectangle2D(agent.getTranslateX(), agent.getTranslateY() + 32, 32, 32);

            for(Actor actor: agent.collidedActors) {
                // change 32 -> 30 pixel edge of actor.getBoundary()
                if(agentBoundary.intersects(actor.getBoundary())) {
                    moveLeft &= !(recL.intersects(actor.getBoundary(30, 30)));
                    moveRight &= !(recR.intersects(actor.getBoundary(30, 30)));
                    moveUp &= !(recU.intersects(actor.getBoundary(30, 30)));
                    moveDown &= !(recD.intersects(actor.getBoundary(30, 30)));
                }
            }

            moveLeft &= Constant.isSafe(recL.getMinX(), recL.getMinY());
            moveRight &= Constant.isSafe(recR.getMinX(), recR.getMinY());
            moveUp &= Constant.isSafe(recU.getMinX(), recU.getMinY());
            moveDown &= Constant.isSafe(recD.getMinX(), recD.getMinY());

//            System.out.println("Agent id: " + agent.get_Id());
//            System.out.println(moveLeft + ", " + moveRight + ", " + moveUp + ", " + moveDown);

            boolean fixed = true;
            if(moveLeft) {
                if(feasiblePosition(recL) == null){
                    agent.tempPos.push(new Position(recL.getMinX(), recL.getMinY()));
                    agent.setVelocity(speed * (recL.getMinX() - agent.getTranslateX()) / 32, speed * (recL.getMinY() - agent.getTranslateY()) / 32);
                    fixed = false;
                }
            }
            if(moveDown & fixed) {
                if(feasiblePosition(recD) == null){
                    agent.tempPos.push(new Position(recD.getMinX(), recD.getMinY()));
                    agent.setVelocity(speed * (recD.getMinX() - agent.getTranslateX()) / 32, speed * (recD.getMinY() - agent.getTranslateY()) / 32);
                    fixed = false;
                }
            }
            if(moveUp & fixed) {
                if(feasiblePosition(recU) == null){
                    agent.tempPos.push(new Position(recU.getMinX(), recU.getMinY()));
                    agent.setVelocity(speed * (recU.getMinX() - agent.getTranslateX()) / 32, speed * (recU.getMinY() - agent.getTranslateY()) / 32);
                    fixed = false;
                }
            }
            if(moveRight & fixed) {
                if(feasiblePosition(recR) == null){
                    agent.tempPos.push(new Position(recR.getMinX(), recR.getMinY()));
                    agent.setVelocity(speed * (recR.getMinX() - agent.getTranslateX()) / 32, speed * (recR.getMinY() - agent.getTranslateY()) / 32);
                    fixed = false;
                }
            }

            if(fixed) {
                // stuck mode: force the agent to jump somewhere else
                Rectangle2D recLU = new Rectangle2D((int)((agent.getTranslateX() - 32)/32) * 32, (int)((agent.getTranslateY() - 32)/32) * 32, 32, 32);
                Rectangle2D recRD = new Rectangle2D((int)((agent.getTranslateX() + 32)/32)*32, (int)((agent.getTranslateY() + 32)/32)*32, 32, 32);
                Rectangle2D recRU = new Rectangle2D((int)((agent.getTranslateX() + 32)/32)*32, (int)((agent.getTranslateY() - 32)/32)*32, 32, 32);
                Rectangle2D recLD = new Rectangle2D((int)((agent.getTranslateX() - 32)/32) * 32, (int)((agent.getTranslateY() + 32)/32)*32, 32, 32);

                boolean lU = true, lD = true, rU = true, rD = true;
                for(Rectangle2D rect: Constant.roomArea) {
                    lU &= !recLU.intersects(rect);
                    rU &= !recRU.intersects(rect);
                    lD &= !recLD.intersects(rect);
                    rD &= !recRD.intersects(rect);
                }
                if(lU) {
                    agent.setX_(recLU.getMinX());
                    agent.setY_(recLU.getMinY());
                    return;
                }
                if(lD) {
                    agent.setX_(recLD.getMinX());
                    agent.setY_(recLD.getMinY());
                    return;
                }
                if(rU) {
                    agent.setX_(recRU.getMinX());
                    agent.setY_(recRU.getMinY());
                    return;
                }
                if(rD) {
                    agent.setX_(recRD.getMinX());
                    agent.setY_(recRD.getMinY());
                    return;
                }
            }
        }
        agent.moveX();
        agent.moveY();
    }

    private Rectangle2D feasiblePosition(Rectangle2D rectA) {
        for(Rectangle2D rectB: Constant.roomArea) {
            if(rectA.intersects(rectB)) return rectB;
        }
        return null;
    }

    private boolean intersect(double x1, double y1, double x2, double y2) {
        boolean f1 = (x1 >= x2) && (x1 <= x2 + 32);
        boolean f2 = (x2 >= x1) && (x2 <= x1 + 32);
        boolean f3 = (y1 >= y2) && (y1 <= y2 + 32);
        boolean f4 = (y2 >= y1) && (y2 <= y1 + 32);
        return (f1 && (f3 || f4)) || (f2 && (f3 || f4));
    }

    public boolean collision(int x, int y, char mode) {
        if(mode == 'W') {
            for (int i = -1; i <= 1; i++) {
                Tile cur = this.scene.noPathLayer.tiles2D[x + i][y - 1];
                if (cur != null) {
                    if (intersect(this.scene.getAgv().getTranslateX(), this.scene.getAgv().getTranslateY(),
                            cur.trueX, cur.trueY)) {
                        if (this.scene.getAgv().getTranslateX() == cur.trueX + 32
                                || this.scene.getAgv().getTranslateX() + 32 == cur.trueX) {
                            continue;
                        } else if (this.scene.getAgv().getTranslateY() <= cur.trueY + 32) {
                            return true;
                        }
                    }
                }
            }
        } else if(mode == 'S') {
            for (int i = -1; i <= 1; i++) {
                Tile cur = this.scene.noPathLayer.tiles2D[x + i][y + 1];
                if (cur != null) {
                    if (intersect(this.scene.getAgv().getTranslateX(), this.scene.getAgv().getTranslateY(),
                            cur.trueX, cur.trueY)) {
                        if (this.scene.getAgv().getTranslateX() == cur.trueX + 32
                                || this.scene.getAgv().getTranslateX() + 32 == cur.trueX) {
                            continue;
                        } else if (this.scene.getAgv().getTranslateY() + 32 >= cur.trueY) {
                            return true;
                        }
                    }
                }
            }
        } else if(mode == 'D') {
            for (int i = -1; i <= 1; i++) {
                Tile cur = this.scene.noPathLayer.tiles2D[x + 1][y + i];
                if (cur != null) {
                    if (intersect(this.scene.getAgv().getTranslateX(), this.scene.getAgv().getTranslateY(),
                            cur.trueX, cur.trueY)) {
                        if (this.scene.getAgv().getTranslateY() == cur.trueY + 32
                                || this.scene.getAgv().getTranslateY() + 32 == cur.trueY) {
                            continue;
                        } else if (this.scene.getAgv().getTranslateX() + 32 >= cur.trueX) {
                            return true;
                        }
                    }
                }
            }
        }
        else if(mode == 'A') {
            for (int i = -1; i <= 1; i++) {
                Tile cur = this.scene.noPathLayer.tiles2D[x - 1][y + i];
                if (cur != null) {
                    if (intersect(this.scene.getAgv().getTranslateX(), this.scene.getAgv().getTranslateY(),
                            cur.trueX, cur.trueY)) {
                        if (this.scene.getAgv().getTranslateY() == cur.trueY + 32
                                || this.scene.getAgv().getTranslateY() + 32 == cur.trueY) {
                            continue;
                        } else if (this.scene.getAgv().getTranslateX() <= cur.trueX + 32) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
