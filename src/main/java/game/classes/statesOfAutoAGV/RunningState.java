package game.classes.statesOfAutoAGV;

import game.classes.Agent;
import game.classes.AutoAgv;
import game.classes.Node2D;
import game.classes.StateOfNode2D;
import game.classes.statistic.WaitingDuration;
import game.constant.Constant;
import game.controller.GameController;
import scenes.MainScene;

import java.util.Iterator;

public class RunningState extends HybridState{
    public boolean _isLastMoving;
    private boolean _agvIsDestroyed;

    public RunningState(boolean isLastMoving) {
        super();
        this._isLastMoving = isLastMoving;
        this._agvIsDestroyed = false;
    }

    @Override
    public void move(AutoAgv agv) {

        if(this._agvIsDestroyed) //|| this._isEliminated)
            return;
        //
        if (agv.path == null) {
            return;
        }
        // nếu đã đến đích thì không làm gì
        if (agv.cur == agv.path.size() - 1) {
            agv.setVelocity(0, 0);
            if(this._isLastMoving){
                MainScene mainScene = agv.scene;
                mainScene.autoAgvs.remove(agv);

                mainScene.forcasting.rememberDoneAutoAgv(agv.getAgvID());
                this._agvIsDestroyed = true;
                agv.destroy();
                agv.firstText.destroy();
                return;
            } else {
                agv.hybridState = new IdleState(GameController.now());
            }
            return;
        }
        // nodeNext: nút tiếp theo cần đến
        if(agv.cur + 1 >= agv.path.size()) {
            System.out.println("Loi roi do: "+ (agv.cur + 1));
        }
        Node2D nodeNext = agv.graph.nodes[agv.path.get(agv.cur + 1).x][agv.path.get(agv.cur + 1).y];
        //Khoảng cách của autoAgv với các actors khác đã va chạm
        double shortestDistance = Constant.minDistance(agv, agv.collidedActors);

        if (nodeNext.state == StateOfNode2D.BUSY || shortestDistance < Constant.SAFE_DISTANCE) {
            agv.setVelocity(0, 0);
            if (agv.waitT != 0) return;
            agv.waitT = GameController.now();
            agv.scene.forcasting.
                    addDuration(agv.getAgvID(), new WaitingDuration((int) Math.floor(agv.waitT / 1000), -1, 0));
        } else {
            if(shortestDistance >= Constant.SAFE_DISTANCE) {
                agv.collidedActors.clear();
            }

            if (agv.waitT != 0) {
                agv.curNode.setU((GameController.now() - agv.waitT) / 1000);
                agv.scene.forcasting.
                        updateDuration(agv.getAgvID(), (int) Math.floor(agv.waitT / 1000), (int) Math.floor(GameController.now() / 1000));
                agv.waitT = 0;
            }
            // di chuyển đến nút tiếp theo
            /* Nếu AutoAgv có va chạm với một agent nào đó thì dừng lại
               chờ agent qua mới đi tiếp*/
            boolean collided = false;
            Iterator<Agent> agentIterator = agv.scene.agents.iterator();
            while(agentIterator.hasNext()){
                Agent agent = agentIterator.next();
                if(agv.scene.physics.collider(agv, agent)){
                    collided = true;
                    break;
                }
            }
            if (Math.abs(agv.getTranslateX() - nodeNext.x * 32) > 1 || Math.abs(agv.getTranslateY() - nodeNext.y * 32) > 1) {
                if(!collided)
                    agv.scene.physics.moveTo(agv, nodeNext.x * 32, nodeNext.y * 32, 1);
                else
                    agv.setVelocity(0);
            } else {
                /**
                 * Khi đã đến nút tiếp theo thì cập nhật trạng thái
                 * cho nút trước đó, nút hiện tại và Agv
                 */
                agv.curNode.setState(StateOfNode2D.EMPTY);
                agv.curNode = nodeNext;
                agv.curNode.setState(StateOfNode2D.BUSY);
                agv.cur++;
                agv.setX(agv.curNode.x * 32);
                agv.setY(agv.curNode.y * 32);
                agv.setVelocity(0, 0);
                agv.sobuocdichuyen++;
//                 cap nhat lai duong di Agv moi 10 buoc di chuyen;
//                 hoac sau 10s di chuyen
                if (agv.sobuocdichuyen % 10 == 0 || GameController.now() - agv.thoigiandichuyen > 10000) {
                    agv.thoigiandichuyen = GameController.now();
                    agv.cur = 0;
                    agv.path = agv.calPathAStar(agv.curNode, agv.endNode);
                }
            }
        }
    }
}
