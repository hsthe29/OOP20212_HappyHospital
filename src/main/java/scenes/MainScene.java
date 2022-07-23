package scenes;

import classes.*;
import com.google.gson.Gson;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import kernel.algorithm.RandomDistribution;
import classes.statistic.Forcasting;
import kernel.constant.Constant;
import kernel.constant.ModeOfDirection;
import kernel.utilities.GameController;
import tilemaps.Tile;
import tilemaps.Tilemap;
import tilemaps.TilemapLayer;
import kernel.utilities.save.*;
import javafx.scene.Parent;
import javafx.beans.NamedArg;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import socket.Message;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class MainScene extends GameScene {

    public List<AutoAgv> autoAgvs;
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
    private double _harmfulness = 0;
    private Graph spaceGraph;
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
    private Image[] imgs = new Image[2];
    private Image[] skins = new Image[3];
    public final FileChooser fileChooser = new FileChooser();

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

        this.controller = GameController.getInstance();
        this.controller.setScene(this);
        this.agents = new ArrayList<>();
        this.groundPos = new ArrayList<>();
        this.pathPos = new ArrayList<>();
        this.adjacencyList = new ArrayList<>();
        this.doorPos = new ArrayList<>();
        this.autoAgvs = new ArrayList<>();
        this.forcasting = new Forcasting();

        for (int i = 0; i < 52; ++i) {
            this.adjacencyList.add(new ArrayList<>());
            for (int j = 0; j < 28; j++)
                this.adjacencyList.get(i).add(new ArrayList<>());
            for (int j = 0; j < 28; j++) {
                this.adjacencyList.get(i).set(j, new ArrayList<>());
            }
        }
        vBox.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case W -> this.controller.keyW = true;
                case S -> this.controller.keyS = true;
                case A -> this.controller.keyA = true;
                case D -> this.controller.keyD = true;
            }
        });
        vBox.setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case W -> this.controller.keyW = false;
                case S -> this.controller.keyS = false;
                case A -> this.controller.keyA = false;
                case D -> this.controller.keyD = false;
            }
        });
        map.setScene(this);
        createScene();
    }
    /** Getter - Setter segment */
    public Agv getAgv() {
        return this.agv;
    }

    public Graph getGraph() {
        return this.spaceGraph;
    }

    public double getHarmfullness() {
        return this._harmfulness;
    }

    public void setHarmfullness(double value) {
        this._harmfulness = value;
        this.controller.harmfulness.set(String.format("H.ness: %,.3f", this._harmfulness));
    }

    public void setMaxAgents(int num) {
        this.MAX_AGENT = num;
    }
    /** End of Getter - Setter segment */

    private void createScene() throws IOException {
        preLoad();
        create();
    }

    /**
     * Chuẩn bị các tài nguyên để hiện thị giao diện trò chơi
     * */
    private void preLoad() throws IOException {
        this.load.baseURL = "file:///" + Constant.rootPath + "assets/";
        this.load.baseJsonURL = Constant.rootPath + "assets/tilemaps/json/";
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

    /**
     * Hiện thị UI
     * */
    private void create() throws IOException {
        this.initMap();
        this.createAdjacencyList();
        this.initGraph();
        this.addButton();
        this.controller.oos.flush();
        this.controller.oos.writeObject(new Message(1, this.groundPos));
        this.controller.oos.flush();
        this.controller.oos.writeObject(new Message(2, this.adjacencyList, this.pathPos));
    }

    /**
     * Khi nhấn nút, các Actors được tạo và game đã bắt đầu
     * */
    private void loadRestPart() throws IOException, ClassNotFoundException {
        int r = (int) (Math.floor(Math.random() * this.pathPos.size()));
        while (!Constant.validDestination((int) this.pathPos.get(r).x, (int) this.pathPos.get(r).y, 1, 14)) {
            r = (int) Math.floor(Math.random() * this.pathPos.size());
        }
        this.agv = new Agv(this, 32, 14 * 32, this.pathPos.get(r).dx, this.pathPos.get(r).dy, this.pathLayer);

        this.agv.writeDeadline(this.desTable);

        this.createRandomAutoAgv();
        this.createAgents(10, 1500);

        chooseSkin();
    }

    /**
     * Thêm các nút và bảng hiện thị
     * */
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

        this.saveButton.setOnAction(e -> handleClickSaveButton());
        this.loadButton.setOnAction(e -> handleClickLoadButton());

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

        ImageView instructionButton = new ImageView(new Image(this.load.baseURL + "sprites/instruction.png"));
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
                    try {
                        this.loadRestPart();
                        controller.startGameLoop();
                    } catch (IOException | ClassNotFoundException ex) {
                        ex.printStackTrace();
                    }
                    controller.gameLoaded = true;
                } else {
                    controller.startGameLoop();
                }
                pauseButton.setImage(imgs[0]);
            }
        });
        pauseButton.setTranslateX(700);
        pauseButton.setTranslateY(30);

        Button rsButton = new Button("New Game");
        rsButton.setStyle("-fx-background-color: \"#eee\";" +
                "-fx-padding: 5 10 5 10;" +
                "-fx-font-size: 20;" +
                "-fx-font-weight: bold;" +
                "-fx-fill: \"#000\";" +
                "-fx-font-family: \"Comic Sans MS\"");

        rsButton.setTranslateX(1380);
        rsButton.setTranslateY(55);
        rsButton.setOnAction(e -> handleClickNewGameButton());

        this.pane.getChildren().addAll(innerVbox, innerVbox2, pauseButton, txt, innerScr3, rsButton);

    }

    public void createAGV() {
        int r = (int) (Math.floor(Math.random() * this.pathPos.size()));
        while (!Constant.validDestination((int) this.pathPos.get(r).x, (int) this.pathPos.get(r).y, 1, 14)) {
            r = (int) Math.floor(Math.random() * this.pathPos.size());
        }
        this.agv = new Agv(this, 32, 14 * 32, this.pathPos.get(r).dx, this.pathPos.get(r).dy, this.pathLayer);
    }

    public void update() {
        this.getGraph().updateState();
        if(this.agv.isDone)
            this.createAGV();
        this.agv.stuckAGV = null;
        this.agv.update();
        for(int i = 0; i < autoAgvs.size(); ++i) {
            autoAgvs.get(i).stuckAGV = null;
            autoAgvs.get(i).preUpdate();
        }
        for(int i = 0; i < agents.size(); ++i)
            agents.get(i).preUpdate();
        this.forcasting.calculate();
        this.agv.collidedActors.clear();
        this.agv.collidedAGVs.clear();
    }

    private void handleClickSaveButton() {
        if(!controller.gameLoaded) return;

        boolean isRunning = controller.isGameRunning;
        if(isRunning)
            controller.pauseGameLoop();
        SaveAgv saveAgv = new SaveAgv(agv.getTranslateX(), agv.getTranslateY());

        int n = agents.size();

        SavePos[] startPos = new SavePos[n];
        SavePos[] endPos = new SavePos[n];
        int[] id = new int[n];
        int i = 0;
        for(Agent a: this.agents) {
            startPos[i] = new SavePos(a.getTranslateX(), a.getTranslateY());
            endPos[i] = new SavePos(a.getEndPos().dx, a.getEndPos().dy);
            id[i] = a.get_Id();
            i++;
        }

        SaveAgent saveAgents = new SaveAgent(startPos, endPos, id);

        SaveMap saveMap = new SaveMap(saveAgv, saveAgents);
        Gson gson = new Gson();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(System.getProperty("user.home") + "\\Downloads\\save.json"))) {
            String saveString = gson.toJson(saveMap, SaveMap.class);
            bw.write(saveString);
            bw.flush();
        }
        catch (IOException e){
            e.printStackTrace();
        }

        if(isRunning)
            controller.startGameLoop();
    }

    private void handleClickLoadButton() {
        if(!controller.gameLoaded) return;

        boolean isRunning = controller.isGameRunning;
        if(isRunning)
            controller.pauseGameLoop();

        openFileChooser(fileChooser);
        File file = fileChooser.showOpenDialog(this.getWindow());

        Gson gson = new Gson();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            SaveMap map = gson.fromJson(reader, SaveMap.class);

            agv.setX_(map.agv.x);
            agv.setY_(map.agv.y);

            SaveAgent mapAgents = map.agents;
            while(!agents.isEmpty()) {
                agents.get(0).eliminate();
            }
            int n = mapAgents.id.length;
            for(int i = 0; i < n; i++) {
                if(mapAgents.startPos[i] != null) {
                    this.agents.add(new Agent(this, new Position(mapAgents.startPos[i].x / 32,
                            mapAgents.startPos[i].y / 32), new Position(mapAgents.endPos[i].x, mapAgents.endPos[i].y),
                            this.groundPos, mapAgents.id[i]));
                }
            }
            reader.close();
        }
        catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR, "No file chosen or file content is not correct!", ButtonType.CANCEL);
            alert.showAndWait();
        }
        if(isRunning)
            controller.startGameLoop();
    }

    private void handleClickNewGameButton() {
        if(!controller.gameLoaded) return;

        boolean isRunning = controller.isGameRunning;
        if(isRunning)
            controller.pauseGameLoop();
        this.agv.eliminate();
        int n = this.autoAgvs.size();
        for(int i = n - 1; i > -1; i--) {
            this.autoAgvs.get(i).eliminate();
        }
        n = this.agents.size();
        for(int i = n - 1; i > -1; i--) {
            this.agents.get(i).eliminate();
        }
        this._harmfulness = 0;
        this.desTable.getChildren().clear();
        Actor._id = 0;
        controller.restate();
        try {
            loadRestPart();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Couldn't load new game!");
        }

        if(isRunning)
            controller.startGameLoop();
    }

    private void openFileChooser(final FileChooser fileChooser) {
        fileChooser.setTitle("Select json file");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home") + "\\Downloads")
        );
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("JSON", "*.json")
        );
    }

    private void createRandomAutoAgv() {
        int r = (int) Math.floor(Math.random() * this.pathPos.size());
        while (!Constant.validDestination(this.pathPos.get(r).dx, this.pathPos.get(r).dy, 1, 13)) {
            r = (int) Math.floor(Math.random() * this.pathPos.size());
        }
        if (this.getGraph() != null) {
            AutoAgv tempAgv = new AutoAgv(this, 1, 13, this.pathPos.get(r).dx, this.pathPos.get(r).dy, this.getGraph());
            tempAgv.writeDeadline(this.desTable);
            this.autoAgvs.add(tempAgv);
        }
    }

    private void openLinkInstruction() {
        boolean isRunning = controller.isGameRunning;
        if(isRunning)
            controller.pauseGameLoop();

        try(FileInputStream fis = new FileInputStream(Constant.rootPath + "instruction.txt");
            InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr)
        ) {
            Dialog<String> dialog = new Dialog<>();
            dialog.setHeight(700);
            dialog.setResizable(true);
            //Setting the title
            dialog.setTitle("Instruction");
            ButtonType type = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);

            dialog.setContentText(br.lines().collect(Collectors.joining("\n")));
            dialog.getDialogPane().getButtonTypes().add(type);
            dialog.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(isRunning)
            controller.startGameLoop();
    }

    private void chooseSkin() {
        Stage stage = new Stage();
        VBox outterBox = new VBox();
        HBox box = new HBox();
        box.setPadding(new Insets(10));
        box.setSpacing(120);
        outterBox.setSpacing(90);

        outterBox.setAlignment(Pos.CENTER);
        box.setAlignment(Pos.CENTER);

        ImageView im_1 = new ImageView(skins[0]);
        ImageView im_2 = new ImageView(skins[1]);
        ImageView im_3 = new ImageView(skins[2]);

        im_1.setOnMouseClicked(e -> this.agv.setImage(skins[0]));
        im_2.setOnMouseClicked(e -> this.agv.setImage(skins[1]));
        im_3.setOnMouseClicked(e -> this.agv.setImage(skins[2]));

        im_1.setScaleX(5);
        im_1.setScaleY(5);
        im_2.setScaleX(5);
        im_2.setScaleY(5);
        im_3.setScaleX(5);
        im_3.setScaleY(5);

        Button confirm = new Button("Confirm");
        confirm.setOnAction(e -> stage.close());

        box.getChildren().addAll(im_1, im_2, im_3);

        outterBox.getChildren().addAll(box, confirm);

        Scene scene = new Scene(outterBox, 500, 300);
        stage.setScene(scene);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(this.getWindow());
        stage.toFront();
        stage.setTitle("Choose your AGV's skin");
        stage.showAndWait();
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

        imgs[0] = new Image(this.load.baseURL + "icons/pause.png");
        imgs[1] = new Image(this.load.baseURL + "icons/play.png");
        skins[0] = new Image(this.load.baseURL + "sprites/agv_violet.png");
        skins[1] = new Image(this.load.baseURL + "sprites/agv_blue.png");
        skins[2] = new Image(this.load.baseURL + "sprites/agv_green.png");

        this.groundLayer.getTiles().forEach(e -> {
            Position pos = new Position(e.x, e.y);
            this.groundPos.add(pos);
        });
        this.pathLayer.getTiles().forEach(e -> {
            Position pos = new Position(e.x, e.y);
            this.pathPos.add(pos);
        });
        this.doorLayer.getTiles().forEach(e -> {
            Position pos = new Position(e.x, e.y);
            this.doorPos.add(pos);
        });
        this.gateLayer.getTiles().forEach(e -> {
            Position pos = new Position(e.x, e.y);
            this.doorPos.add(pos);
        });
    }

    public void createAgents(int numAgentInit, int time) throws IOException, ClassNotFoundException {
        ArrayList<Integer> randoms = new ArrayList<>();
        while (randoms.size() < (numAgentInit << 1)) {
            int r = (int) (Math.floor(Math.random() * this.doorPos.size()));
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

            this.agents.add(agent);

        }
        if (this.getGraph() != null) {
            this.getGraph().setAgents(this.agents);
        }

        this.controller.setAgentTime(time);
    }

    public void addAgent() throws IOException, ClassNotFoundException {
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

        this.agents.add(agent);

        for(AutoAgv aagv: this.autoAgvs) {
            if(aagv.intersects(agent)) {
                agent.eliminate();
                return;
            }
        }
        if (this.getGraph() != null)
            this.getGraph().setAgents(this.agents);
        this.count++;
        if (this.count == 3) {
            this.createRandomAutoAgv();
            this.count = 0;
        }
    }

    public void initGraph() {
        this.spaceGraph = new Graph(52, 28, this.adjacencyList, this.pathPos);
    }

    private boolean checkTilesUndirection(Tile tileA, Tile tileB) {
        if (tileA.x == tileB.x && tileA.y == tileB.y + 1) {
            if (tileB.getDirection() == ModeOfDirection.TOP) return true;
        }
        if (tileA.x + 1 == tileB.x && tileA.y == tileB.y) {
            if (tileB.getDirection() == ModeOfDirection.RIGHT) return true;
        }
        if (tileA.x == tileB.x && tileA.y + 1 == tileB.y) {
            if (tileB.getDirection() == ModeOfDirection.BOTTOM) return true;
        }
        if (tileA.x == tileB.x + 1 && tileA.y == tileB.y) {
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
                if (tileA.x == tileB.x && tileA.y == tileB.y + 1) return true;
            }
            if (tileA.getDirection() == ModeOfDirection.RIGHT) {
                if (tileA.x + 1 == tileB.x && tileA.y == tileB.y) return true;
            }
            if (tileA.getDirection() == ModeOfDirection.BOTTOM) {
                if (tileA.x == tileB.x && tileA.y + 1 == tileB.y) return true;
            }
            if (tileA.getDirection() == ModeOfDirection.LEFT) {
                if (tileA.x == tileB.x + 1 && tileA.y == tileB.y) return true;
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
                        this.adjacencyList.get(tiles.get(i).x).get(tiles.get(i).y).add(new Position(tiles.get(j).x, tiles.get(j).y));
                    }
                }
            }
        }
    }
}
