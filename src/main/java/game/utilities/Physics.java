package game.utilities;

import game.classes.AutoAgv;
import game.entities.Sprite;
import game.tilemaps.Tile;
import game.tilemaps.TilemapLayer;
import scenes.MainScene;

import java.util.ArrayList;
import java.util.List;

public class Physics {
    public MainScene scene;

    public Physics(MainScene scene) {
        this.scene = scene;
    }

    public void update() {
        for(int i = 0; i < scene.agents.size(); ++i)
            this.scene.agents.get(i).preUpdate();

        List<AutoAgv> autoAgvs = new ArrayList<>(this.scene.autoAgvs);
        for(int i = 0; i < autoAgvs.size(); ++i) {
            autoAgvs.get(i).preUpdate(1000, 1000);
        }

    }

    public void collider(Sprite sprite, TilemapLayer objs) {

    }

    public void moveTo(Sprite sprite, double trueX, double trueY, double speed) {

        double dx = trueX - sprite.getTranslateX();
        double dy = trueY - sprite.getTranslateY();
        double d = Math.sqrt(dx * dx + dy * dy);
        if(d <= 1e-4)
            sprite.setVelocity(0, 0);
        else
            sprite.setVelocity(speed * dx / d, speed * dy / d);
        sprite.moveX();
        sprite.moveY();
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
