package socket;

import kernel.GameUI;
import kernel.utilities.GameController;

import java.io.IOException;
import java.net.Socket;

public class MyClient {
    private Socket socket;

    public MyClient() {}

    public void connect() throws IOException {
        socket = new Socket("localhost", 4000);
        GameController.getInstance().setSocket(this.socket);
        new GameUI().run();
    }
}
