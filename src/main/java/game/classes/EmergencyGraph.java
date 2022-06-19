package game.classes;

import game.constant.Constant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EmergencyGraph extends Graph {

    public Node2D[][] virtualNodes;

    public EmergencyGraph(
            int width,
            int height,
            ArrayList<ArrayList<ArrayList<Position>>> adjacencyList,
            ArrayList<Position> pathPos
    ) {
        super(width, height, adjacencyList, pathPos);
        this.virtualNodes = new Node2D[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                this.virtualNodes[i][j] = new Node2D(i, j, true, StateOfNode2D.NOT_ALLOW, 0.05, 2000, 3000); //new VirtualNode(i, j, true);
            }
        }
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                for (int k = 0; k < adjacencyList.get(i).get(j).size(); k++) {
                    Position adjacencyNode = adjacencyList.get(i).get(j).get(k);
                    this.nodes[i][j].setNeighbor(this.virtualNodes[adjacencyNode.dx][adjacencyNode.dy]);
                    this.virtualNodes[i][j].setNeighbor(this.virtualNodes[adjacencyNode.dx][adjacencyNode.dy]);
                    this.virtualNodes[i][j].setNeighbor(this.nodes[adjacencyNode.dx][adjacencyNode.dy]);
                }
            }
        }
        for (Position p: pathPos) {
            this.virtualNodes[p.dx][p.dy].setState(StateOfNode2D.EMPTY);
        }
    }

    public void updateState() {
        super.updateState();
        for(int j = 0; j < this.width; j++) {
            for(int k = 0; k < this.height; k++) {
                int x = this.nodes[j][k].x;
                int y = this.nodes[j][k].y;
                this.nodes[j][k].setWeight(0);
                this.virtualNodes[j][k].setWeight(0);

                // Cần cập nhật lại
                for(int i = 0; i < this.agents.size(); i++) {
                    double dist = Math.sqrt((x - this.agents.get(i).x) * (x - this.agents.get(i).x) + (y - this.agents.get(i).y) * (y - this.agents.get(i).y));
                    if(dist / this.agents.get(i).speed < Constant.DELTA_T) {
                        this.nodes[j][k].setWeight(this.nodes[j][k].getWeight() + 1);
                    }
                }
                if(this.getAutoAgvs() != null) {
                    for(AutoAgv item: this.getAutoAgvs()) {
                        if (item.path != null) {
                            for (int i = 0; i < item.path.size(); i++) {
                                if (item.path.get(i).isVirtualNode) {
                                    if (item.path.get(i).x == this.virtualNodes[j][k].x
                                            && item.path.get(i).y == this.virtualNodes[j][k].y) {
                                        this.virtualNodes[j][k].setWeight(this.virtualNodes[j][k].getWeight() + 1);
                                    }
                                } else {
                                    if (item.path.get(i).equal(this.nodes[j][k])) {
                                        this.nodes[j][k].setWeight(this.nodes[j][k].getWeight() + 1);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public ArrayList<Node2D> calPathAStar(Node2D start, Node2D end) {
        /**
         * Khoi tao cac bien trong A*
         */
        ArrayList<Node2D> openSet = new ArrayList<>();
        ArrayList<Node2D> closeSet = new ArrayList<>();
        ArrayList<Node2D> path = new ArrayList<>();
        double[][] astar_f = new double[this.width][this.height];
        double[][] astar_g = new double[this.width][this.height];
        int[][] astar_h = new int[this.width][this.height];
        Node2D[][] previous = new Node2D[this.width][this.height];

        for (int i = 0; i < this.width; i++) {
            for (int j = 0; j < this.height; j++) {
                astar_f[i][j] = 0;
                astar_g[i][j] = 0;
                astar_h[i][j] = 0;
            }
        }
        int lengthOfPath = 1;
        /**
         * Thuat toan
         */
        openSet.add(this.nodes[start.x][start.y]);
        while (openSet.size() > 0) {
            int winner = 0;
            for (int i = 0; i < openSet.size(); i++) {
                if (
                        astar_f[openSet.get(i).x][openSet.get(i).y] <
                                astar_f[openSet.get(winner).x][openSet.get(winner).y]
                ) {
                    winner = i;
                }
            }
            Node2D current = openSet.get(winner);
            if (openSet.get(winner).equal(end)) {
                Node2D cur = this.nodes[end.x][end.y];
                path.add(cur);
                while (previous[cur.x][cur.y] != null) {
                    path.add(previous[cur.x][cur.y]);
                    cur = previous[cur.x][cur.y];
                }
                Collections.reverse(path);
                //console.assert(lengthOfPath == path.length, "path has length: " + path.length + " instead of " + lengthOfPath);
                return path;
            }
            openSet.remove(winner);
            closeSet.add(current);
            ArrayList<Node2D> neighbors = new ArrayList<>(List.of(current.nodeN, current.nodeE, current.nodeS, current.nodeW,
                    current.nodeVN, current.nodeVE, current.nodeVS, current.nodeVW));

            for (int i = 0; i < neighbors.size(); i++) {
                Node2D neighbor = neighbors.get(i);
                if (neighbor != null) {
                    if (!this.isInclude(neighbor, closeSet)) {
                        int timexoay = 0;
                        if (
                                previous[current.x][current.y] != null &&
                                        neighbor.x != previous[current.x][current.y].x &&
                                        neighbor.y != previous[current.x][current.y].y
                        ) {
                            timexoay = 1;
                        }
                        double tempG =
                                astar_g[current.x][current.y] + 1 + current.getW() + timexoay;
                        if (super.isInclude(neighbor, openSet)) {
                            if (tempG < astar_g[neighbor.x][neighbor.y]) {
                                astar_g[neighbor.x][neighbor.y] = tempG;
                            }
                        } else {
                            astar_g[neighbor.x][neighbor.y] = tempG;
                            openSet.add(neighbor);
                            lengthOfPath++;
                        }
                        astar_h[neighbor.x][neighbor.y] = this.heuristic(neighbor, end);
                        astar_f[neighbor.x][neighbor.y] = astar_h[neighbor.x][neighbor.y] + astar_g[neighbor.x][neighbor.y];
                        previous[neighbor.x][neighbor.y] = current;
                    }//end of if (!this.isInclude(neighbor, closeSet)) {
                }
            }
        }//end of while (openSet.length > 0)
        System.out.println("Path not found!");
        return null;
    }
}

