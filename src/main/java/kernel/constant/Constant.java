package kernel.constant;


import classes.Actor;
import classes.Node2D;
import javafx.geometry.Rectangle2D;

import java.util.ArrayList;
import java.util.Random;

public class Constant {
    public static final double vec = 1.0;
    public static final double lambda = 0.4;
    public static final int DURATION = 4;
    public static double getLateness(double x) {
        return 5 * x;
    }
    public static final int SAFE_DISTANCE = 46;
    public static final int DELTA_T = 10;
    public static final ModeOfPathPlanning MODE = ModeOfPathPlanning.FRANSEN;
    public static final boolean[][] pathMap = new boolean[52][28];
    public static final Random randomSpeed = new Random();
    public static double rng01() {
        return Math.random();
    }
    public static Rectangle2D[] roomArea = new Rectangle2D[]{
            new Rectangle2D(160, 96, 384, 128),
            new Rectangle2D(640, 96, 384, 128),
            new Rectangle2D(1120, 96, 384, 128),
            new Rectangle2D(11 * 32, 32 * 10, 384, 256),
            new Rectangle2D(26 * 32, 32 * 10, 384, 256),
            new Rectangle2D(41 * 32, 32 * 10, 192, 256),
            new Rectangle2D(160, 672, 384, 128),
            new Rectangle2D(640, 672, 384, 128),
            new Rectangle2D(1120, 672, 384, 128),
            new Rectangle2D(32, 13 * 32, 32, 64),
            new Rectangle2D(50 * 32, 13 * 32, 32, 64)
    };

    public static double rng11() {
        return (Math.floor(Math.random() * 0x100000000L)) / 0x100000000L * 2;
    }


    public static String secondsToHMS(int second) {
        int h = second % (1600 * 24) / 3600;
        int m = second % 3600 / 60;
        int s = second % 60;

        return String.format("%02d:%02d:%02d", h, m , s);
    }

    public static boolean validDestination(int destX, int destY, int x, int y) {
        if((destY == 14 || destY == 13) && (destX >= 0 && destX <= 5) ||
                (destX >= 45 && destX <= 50)) {
            return false;
        }
        double d = Math.sqrt((x - destX)) * (x - destX) + (y - destX) * (y - destY);
        return !(d * 32 < 10);
    }

    public static double minDistance(Actor actor, ArrayList<Actor> otherActors) {
        double dist = Double.POSITIVE_INFINITY;
        for(Actor element: otherActors) {
            double dx = element.getTranslateX() - actor.getTranslateX();
            double dy = element.getTranslateY() - actor.getTranslateY();
            double smaller = Math.sqrt(dx * dx + dy * dy);
            if(dist > smaller) {
                dist=smaller;
            }
        }
        return dist;
    }

    public static int numberOfEdges(int width, int height, Node2D[][] nodes) {
        int count = 0;
        for(int i = 0; i < width; i++) {
            for(int j = 0; j < height; j++) {
                count += (nodes[i][j].nodeE != null) ? 1 : 0;
                count += (nodes[i][j].nodeS != null) ? 1 : 0;
                count += (nodes[i][j].nodeW != null) ? 1 : 0;
                count += (nodes[i][j].nodeN != null) ? 1 : 0;
                count += (nodes[i][j].nodeVE != null) ? 1 : 0;
                count += (nodes[i][j].nodeVS != null) ? 1 : 0;
                count += (nodes[i][j].nodeVW != null) ? 1 : 0;
                count += (nodes[i][j].nodeVN != null) ? 1 : 0;
            }
        }
        return count;
    }

    public static boolean isSafe(double x, double y) {
        return !(x < 32 || x > 1600 || y < 32 || y > 832);
    }
}