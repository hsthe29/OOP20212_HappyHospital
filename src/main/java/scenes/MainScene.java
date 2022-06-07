package scenes;

import game.algorithm.RandomDistribution;
import game.classes.*;
import game.classes.statistic.Forcasting;
import game.constant.Constant;
import game.constant.ModeOfDirection;
import game.constant.ModeOfPathPlanning;
import game.controller.GameController;
import game.tilemaps.Tile;
import game.tilemaps.Tilemap;
import game.tilemaps.TilemapLayer;
import javafx.scene.Parent;
import javafx.beans.NamedArg;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.*;

public class MainScene extends GameScene {

    public Set<AutoAgv> autoAgvs;
    private Agv agv;
    public ArrayList<Agent> agents;
    private Tilemap map = new Tilemap();
    private HashMap<String, ArrayList<Tile>> tileSet;
    private TilemapLayer groundLayer;
    private TilemapLayer elevatorLayer;
    private TilemapLayer roomLayer;
    private TilemapLayer gateLayer;
    private TilemapLayer wallLayer;
    private TilemapLayer doorLayer;
    private TilemapLayer pathLayer;
    public TilemapLayer noPathLayer;
    private TilemapLayer bedLayer;
    private Button saveButton;
    private Button loadButton;
    private ArrayList<Position> groundPos;
    private ArrayList<Position> pathPos;
    private ArrayList<Position> doorPos;
    private int MAX_AGENT = 10;
    private int count = 0;
//    public int sec = 0;

    private SimpleIntegerProperty spI = new SimpleIntegerProperty(1);

    private double _harmfulness = 0;

    private Graph spaceGraph;
    private EmergencyGraph emergencyGraph;
    private ArrayList<ArrayList<ArrayList<Position>>> adjacencyList;
    public Forcasting forcasting;

    public HashMap<String, int[]> mapOfExits = new HashMap<>() {{
        put("Gate1", new int[]{50, 13, 0});
        put("Gate2", new int[]{50, 14, 0});
    }};
    private TextField setAgentInput;
    private Button setAgentButton;
    private Label timeText;
    private Label harmfulTable;
    public VBox desTable;

    private Image imgs[] = new Image[2];


    public MainScene(
            @NamedArg("root") Parent root,
            @NamedArg("width") double width,
            @NamedArg("height") double height,
            @NamedArg("depthBuffer") boolean depthBuffer
    ) throws IOException {
        super(root, width, height, depthBuffer);

        vBox = (VBox) root;
        pane = (Pane) vBox.getChildren().get(0);
        stackPane = (StackPane) vBox.getChildren().get(1);
        scrollPane = (ScrollPane) stackPane.getChildren().get(0);
        gridPane = (GridPane) scrollPane.getContent();

        this.controller = new GameController(this);
        this.agents = new ArrayList<>();
        this.groundPos = new ArrayList<>();
        this.pathPos = new ArrayList<>();
        this.adjacencyList = new ArrayList<>();
        this.doorPos = new ArrayList<>();
        this.autoAgvs = new HashSet<>();
        this.forcasting = new Forcasting();

        for (int i = 0; i < 52; ++i) {
            this.adjacencyList.add(new ArrayList<>());
            for (int j = 0; j < 28; j++)
                this.adjacencyList.get(i).add(new ArrayList<>());
            for (int j = 0; j < 28; j++) {
                this.adjacencyList.get(i).set(j, new ArrayList<>());
            }
        }

        scrollPane.requestFocus();

        scrollPane.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case W -> this.controller.keyW.set(true);
                case S -> this.controller.keyS.set(true);
                case A -> this.controller.keyA.set(true);
                case D -> this.controller.keyD.set(true);
            }
        });

        scrollPane.setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case W -> this.controller.keyW.set(false);
                case S -> this.controller.keyS.set(false);
                case A -> this.controller.keyA.set(false);
                case D -> this.controller.keyD.set(false);
            }
        });

        map.setScene(this);
        createScene();
    }

    private void createScene() throws IOException {
        preLoad();
        create();
    }

    private void preLoad() throws IOException {
        this.load.baseURL = "assets/";
        this.load.baseJsonURL = "src/main/resources/assets/tilemaps/json/";
        this.load.image("assets.json");
        this.load.tilemapTiledJSON("hospital", "hospital.json");
        this.load.image("agv", "sprites/agv.png");
        this.load.image("instruction", "sprites/instruction.png");
        this.load.image("agent", "sprites/agent.png");

        this.pane.setStyle("-fx-background-color: \"#777\"");
        this.scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        this.scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        this.pane.setMinHeight(160);
        this.pane.setMaxHeight(160);
    }

    private void create() {
        this.initMap();
        this.createAdjacencyList();

        this.initGraph();
        this.spaceGraph = new Graph(52, 28, this.adjacencyList, this.pathPos);
        this.emergencyGraph = new EmergencyGraph(52, 28, this.adjacencyList, this.pathPos);

        this.addButton();



        // render
        /*
        var des = document.getElementById("des");
          if (des) {
            while (des.childNodes.length >= 1) {
              des.firstChild && des.removeChild(des.firstChild);
            }

            des.appendChild(des.ownerDocument.createTextNode(this.timeTable?.text || ""));
          }
        * */


    }

    private void loadRestPart() {
        int r = (int) (Math.floor(Math.random() * this.pathPos.size()));
        while (!Constant.validDestination((int) this.pathPos.get(r).x, (int) this.pathPos.get(r).y, 1, 14)) {
            r = (int) Math.floor(Math.random() * this.pathPos.size());
        }
        this.agv = new Agv(this, 1, 14, this.pathPos.get(r).dx, this.pathPos.get(r).dy, this.pathLayer);

        this.agv.writeDeadline(this.desTable);

        this.createRandomAutoAgv();
//        this.events.on("destroyAgent", this.destroyAgentHandler, this);
        this.createAgents(10, 1000);
        this.physics.collider(this.agv, this.noPathLayer);
//        this.openLinkInstruction();

        controller.startGameLoop();
    }

    public void setMaxAgents(int num) {
        this.MAX_AGENT = num;
    }

    private void addButton() {
        this.saveButton = new Button("Save data");
        this.saveButton.setTranslateX(40);
        this.saveButton.setStyle("-fx-background-color: \"#eee\";" +
                "-fx-padding: 5 10 5 10;" +
                "-fx-font-size: 20;" +
                "-fx-font-weight: bold;" +
                "-fx-fill: \"#000\";" +
                "-fx-font-family: \"Comic Sans MS\"");

        this.loadButton = new Button("Load data");
        this.loadButton.setTranslateX(40);
        this.loadButton.setStyle("-fx-background-color: \"#eee\";" +
                "-fx-padding: 5 10 5 10;" +
                "-fx-font-size: 20;" +
                "-fx-font-weight: bold;" +
                "-fx-fill: \"#000\";" +
                "-fx-font-family: \"Comic Sans MS\"");

        this.saveButton.setOnAction(e -> System.out.println("Save Clicked"));
        this.loadButton.setOnAction(e -> System.out.println("Load clicked"));

        this.setAgentInput = new TextField();
        this.setAgentInput.setPromptText("Number of Agents");
        this.setAgentButton = new Button("Apply");
        this.setAgentButton.setStyle("-fx-padding: 5 10 5 10;" +
                "-fx-font-weight: bold;" +
                "-fx-fill: \"#000\";");

        this.setAgentButton.setOnAction(e -> {
            String s = this.setAgentInput.getText().trim();
            try {
                int numAgent = Integer.parseInt(s);
                this.setMaxAgents(numAgent);
                this.setAgentInput.setText("");
            } catch (Exception ignored) {

            }
        });
        HBox innerHbox = new HBox(this.setAgentInput, this.setAgentButton);
        innerHbox.setSpacing(10);
        VBox innerVbox = new VBox(this.saveButton, this.loadButton, innerHbox);
        innerVbox.setSpacing(10);
        innerVbox.setPadding(new Insets(20, 10, 20, 30));

        this.timeText = new Label();
        this.timeText.textProperty().bind(controller.timeDisplay);
        this.timeText.setStyle("-fx-padding: 5 10 5 10;" +
                "-fx-font-size: 20;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #ff0000;" +
                "-fx-font-family: \"Comic Sans MS\"");
        this.timeText.setTranslateX(50);

        this.harmfulTable = new Label();
        this.harmfulTable.textProperty().bind(this.controller.harmfulness);

        this.harmfulTable.setStyle("-fx-padding: 5 10 5 10;" +
                "-fx-font-size: 20;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #ff0000;" +
                "-fx-font-family: \"Comic Sans MS\"");
        this.harmfulTable.setTranslateX(50);

        ImageView instructionButton = new ImageView(new Image("assets/sprites/instruction.png"));
        instructionButton.setOnMouseClicked(e -> openLinkInstruction());
        instructionButton.setTranslateX(60);

        VBox innerVbox2 = new VBox(this.timeText, this.harmfulTable, instructionButton);
        innerVbox2.setSpacing(10);
        innerVbox2.setTranslateX(300);

        desTable = new VBox();
        ScrollPane innerScr3 = new ScrollPane(desTable);
        innerScr3.setMinSize(250, 150);
        innerScr3.setMaxSize(250, 150);

        Text txt = new Text("AGV Deadlines");
        txt.setStyle("-fx-padding: 5 10 5 10;" +
                "-fx-font-size: 20;" +
                "-fx-font-weight: bold;" +
                "-fx-fill: #ff0000;" +
                "-fx-font-family: \"Comic Sans MS\"");

        txt.setTranslateX(920);
        txt.setTranslateY(80);

        innerScr3.setTranslateX(1100);
        innerScr3.setTranslateY(5);

        ImageView pauseButton = new ImageView(imgs[1]);
        pauseButton.setOnMouseClicked(e -> {
            if(controller.isGameRunning) {
                controller.pauseGameLoop();
                pauseButton.setImage(imgs[1]);
            } else {
                if(!controller.gameLoaded) {
                    this.loadRestPart();
                    controller.gameLoaded = true;
                } else {
                    controller.startGameLoop();
                }
                pauseButton.setImage(imgs[0]);
            }
        });
        pauseButton.setTranslateX(650);
        pauseButton.setTranslateY(30);
        this.pane.getChildren().addAll(innerVbox, innerVbox2, pauseButton, txt, innerScr3);
    }

    public void update() {
        this.getGraph().updateState();
        this.agv.update();
        this.forcasting.calculate();
    }

    private void createRandomAutoAgv() {
        int r = (int) Math.floor(Math.random() * this.pathPos.size());
        while (!Constant.validDestination((int) this.pathPos.get(r).x, (int) this.pathPos.get(r).y, 1, 13)) {
            r = (int) Math.floor(Math.random() * this.pathPos.size());
        }
        if (this.getGraph() != null) {
            AutoAgv tempAgv = new AutoAgv(this, 1, 13, this.pathPos.get(r).dx, this.pathPos.get(r).dy, this.getGraph());

            tempAgv.writeDeadline(this.desTable);
//            if(des) {
//                while (des.childNodes.length >= 1) {
//                    des.firstChild && des.removeChild(des.firstChild);
//                }
//
//                des.appendChild(des.ownerDocument.createTextNode(this.timeTable.text));
//
//            }
            this.autoAgvs.add(tempAgv);
        }
    }

    public Graph getGraph() {
        if (Constant.MODE == ModeOfPathPlanning.FRANSEN) {
            return this.spaceGraph;
        } else {
            return this.emergencyGraph;
        }
    }

    private void openLinkInstruction() {
        System.out.println("Clicked");
    }

    private void initMap() {
        this.tileSet = this.map.addTilesetImage("hospital");
        this.noPathLayer = this.map.createLayer("nopath", this.tileSet, 0, 0);
        this.groundLayer = this.map.createLayer("ground", this.tileSet, 0, 0);
        this.wallLayer = this.map.createLayer("wall", this.tileSet, 0, 0);
        this.roomLayer = this.map.createLayer("room", this.tileSet, 0, 0);
        this.pathLayer = this.map.createLayer("path", this.tileSet, 0, 0);
        this.doorLayer = this.map.createLayer("door", this.tileSet, 0, 0);
        this.elevatorLayer = this.map.createLayer("elevator", this.tileSet, 0, 0);
        this.gateLayer = this.map.createLayer("gate", this.tileSet, 0, 0);
        this.bedLayer = this.map.createLayer("bed", this.tileSet, 0, 0);

        imgs[0] = new Image("assets/icons/pause.png");
        imgs[1] = new Image("assets/icons/play.png");

        this.groundLayer.getTiles().forEach(e -> {
            Position pos = new Position(e.X(), e.Y());
            this.groundPos.add(pos);
        });

        this.pathLayer.getTiles().forEach(e -> {
            Position pos = new Position(e.X(), e.Y());
            this.pathPos.add(pos);
        });

        this.doorLayer.getTiles().forEach(e -> {
            Position pos = new Position(e.X(), e.Y());
            this.doorPos.add(pos);
        });
    }

    public double getHarmfullness() {
        return this._harmfulness;
    }

    public void setHarmfullness(double value) {
        this._harmfulness = value;
        this.controller.harmfulness.set(String.format("H.ness: %.3f", this._harmfulness));
    }

    public void createAgents(int numAgentInit, int time) {
        ArrayList<Integer> randoms = new ArrayList<>();
        while (randoms.size() < (numAgentInit << 1)) {
            int r = (int) Math.floor(Math.random() * this.doorPos.size());
            if (!randoms.contains(r)) randoms.add(r);
        }

        this.agents = new ArrayList<>();
        for (int i = 0; i < numAgentInit; i++) {
            Agent agent = new Agent(
                    this,
                    this.doorPos.get(randoms.get(i)),
                    this.doorPos.get(randoms.get(i + numAgentInit)),
                    this.groundPos,
                    (int) Math.floor((Math.random() * 100))
            );

            // set interval them ngau nhien agent vao
            this.agents.add(agent);

        }
        if (this.getGraph() != null) {
            this.getGraph().setAgents(this.agents);
        }

        this.controller.setAgentTime(time);
    }

    public void addAgent() {
        if (this.agents.size() >= this.MAX_AGENT) return;
        RandomDistribution rand = new RandomDistribution();
        double ran = rand.getProbability();
        if (ran > 1) System.out.println(rand.getName() + " " + ran);
        if (ran > 0.37) return;
        int r1 = (int) Math.floor(Math.random() * this.doorPos.size());
        int r2 = (int) Math.floor(Math.random() * this.doorPos.size());
        Agent agent = new Agent(
                this,
                this.doorPos.get(r1),
                this.doorPos.get(r2),
                this.groundPos,
                (int) Math.floor(Math.random() * 100)
        );

//        this.physics.add.overlap(this.agv, agent, () => {
//                agent.handleOverlap();
//        this.agv.handleOverlap();
//      });
//        this.autoAgvs.forEach(
//                (item) => {
//                item && this.physics.add.overlap(agent, item, () => {
//                        item.freeze(agent);
//          });
//        }
//      );
        this.agents.add(agent);
        if (this.getGraph() != null)
            this.getGraph().setAgents(this.agents);
        this.count++;
        if (this.count == 2) {
            this.createRandomAutoAgv();
            this.count = 0;
        }
    }

    private void destroyAgentHandler(Agent agent) {
        int index = 0;
        for (int i = 0; i < this.agents.size(); i++) {
            if (Objects.equals(this.agents.get(i).get_Id(), agent.get_Id())) index = i;
        }
        this.agents.remove(index);
        this.getGraph().removeAgent(agent);
        this.autoAgvs.forEach(e -> {
            e.collidedActors.remove(agent);
        });
    }

    public void initGraph() {
        if(Constant.MODE == ModeOfPathPlanning.FRANSEN) {
            this.spaceGraph = new Graph(52, 28, this.adjacencyList, this.pathPos);
        } else {
            this.emergencyGraph = new EmergencyGraph(52, 28, this.adjacencyList, this.pathPos);
        }
    }

    private boolean checkTilesUndirection(Tile tileA, Tile tileB) {
        if (tileA.X() == tileB.X() && tileA.Y() == tileB.Y() + 1) {
            if (tileB.getDirection() == ModeOfDirection.TOP) return true;
        }
        if (tileA.X() + 1 == tileB.X() && tileA.Y() == tileB.Y()) {
            if (tileB.getDirection() == ModeOfDirection.RIGHT) return true;
        }
        if (tileA.X() == tileB.X() && tileA.Y() + 1 == tileB.Y()) {
            if (tileB.getDirection() == ModeOfDirection.BOTTOM) return true;
        }
        if (tileA.X() == tileB.X() + 1 && tileA.Y() == tileB.Y()) {
            if (tileB.getDirection() == ModeOfDirection.LEFT) return true;
        }
        return false;
    }

    private boolean checkTilesNeighbor(Tile tileA, Tile tileB) {
        // neu o dang xet khong co huong
        if (tileA.getDirection() == ModeOfDirection.NO_DIRECTION) {
            if (this.checkTilesUndirection(tileA, tileB))
                return true;
        } else {
            // neu o dang xet co huong
            if (tileA.getDirection() == ModeOfDirection.TOP) {
                if (tileA.X() == tileB.X() && tileA.Y() == tileB.Y() + 1) return true;
            }
            if (tileA.getDirection() == ModeOfDirection.RIGHT) {
                if (tileA.X() + 1 == tileB.X() && tileA.Y() == tileB.Y()) return true;
            }
            if (tileA.getDirection() == ModeOfDirection.BOTTOM) {
                if (tileA.X() == tileB.X() && tileA.Y() + 1 == tileB.Y()) return true;
            }
            if (tileA.getDirection() == ModeOfDirection.LEFT) {
                if (tileA.X() == tileB.X() + 1 && tileA.Y() == tileB.Y()) return true;
            }
        }
        return false;
    }

    private void createAdjacencyList() {
        ArrayList<Tile> tiles = this.pathLayer.getTiles();
        for (int i = 0; i < tiles.size(); ++i) {
            for (int j = 0; j < tiles.size(); ++j) {
                if (i != j) {
                    if (this.checkTilesNeighbor(tiles.get(i), tiles.get(j))) {
                        this.adjacencyList.get(tiles.get(i).X()).get(tiles.get(i).Y()).add(new Position(tiles.get(j).X(), tiles.get(j).Y()));
                    }
                }
            }
        }
    }

    public Agv getAgv() {
        return this.agv;
    }
}
