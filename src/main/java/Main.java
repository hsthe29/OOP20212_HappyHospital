import socket.MyClient;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            new MyClient().connect();
        } catch (IOException e) {
            System.out.println("Couldn't connect to server side!");
            System.out.println("Unable to run application!");
        }
    }
}
