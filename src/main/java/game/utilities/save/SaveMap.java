package game.utilities.save;

public class SaveMap {
    public SaveAgv agv;
    public SaveAgent agents;

    public SaveMap(SaveAgv agv, SaveAgent agents){
        this.agv = agv;
        this.agents = agents;
    }
}
