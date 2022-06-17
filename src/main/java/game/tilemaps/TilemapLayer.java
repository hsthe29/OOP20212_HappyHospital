package game.tilemaps;

import scenes.MainScene;

import java.util.ArrayList;

public class TilemapLayer {
    public Tile[][] tiles2D;
    private ArrayList<Tile> tiles;

    public TilemapLayer(MainScene scene, ArrayList<Tile> tiles, String type) {

        this.tiles2D = new Tile[52][28];
        for (Tile tile : tiles) {
            this.tiles2D[tile.x][tile.y] = tile;
        }
        this.tiles = tiles;
    }

    public ArrayList<Tile> getTiles() {
        return this.tiles;
    }

    public ArrayList<Tile> getTilesWithinXY(double x, double y, double rangeX, double rangeY) {

        ArrayList<Tile> ans = new ArrayList<>();
        int indexX = (int) ((x + 16) / 32);
        int indexY = (int) ((y + 16) / 32);

//        Tile cur = this.tiles2D[indexX][indexY];

        double startX = x + 1.0;
        double endX = x + 31 ;
        double startY = y + 1;
        double endY = y + 31;

        for(int i = -1; i < 2; i++) {
            for(int j = -1; j < 2; j++) {
                Tile temp = this.tiles2D[indexX + i][indexY + j];
                if(temp != null) {
                    if(((temp.trueX <= startX && temp.trueX + 32 >= startX) || (temp.trueX <= endX && temp.trueX + 32 >= endX))
                            && ((temp.trueY <= startY && temp.trueY + 32 >= startY) || (temp.trueY <= endY && temp.trueY + 32 >= endY))) {
                        ans.add(temp);
                    }
                }
            }
        }
        return ans;
    }
}
