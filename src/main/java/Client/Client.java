package Client;

import Utils.Poster;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private final static String host = "10.8.0.3";
    private final static int port = 10899;

    public void start() {
        try (Socket client = new Socket(host, port)) {
            new Thread(new receiver(client)).start();
            while (true) {
                String message = new Scanner(System.in).nextLine();
                Poster.sandMessage(client, message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class receiver implements Runnable {
    Socket socket;

    public receiver(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            String message;
            while ((message = Poster.receiveMessage(socket)) != null) {
                if (message.equals("exit")) {
                    socket.close();
                    throw new ConnectException();
                } else {
                    System.out.println(message);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}