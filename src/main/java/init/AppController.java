package init;


import java.io.IOException;

public class AppController {
    private final GameUI game = new GameUI();

    public void launchApp() {

        game.run();

    }
}
