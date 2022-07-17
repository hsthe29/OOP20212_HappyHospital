package kernel.utilities.save;

public class SaveAgent {
    public SavePos[] startPos;
    public SavePos[] endPos;
    public int[] id;

    public SaveAgent (SavePos[] startPos, SavePos[] endPos, int[] id) {
        this.startPos = startPos;
        this.endPos = endPos;
        this.id = id;
    }
}
