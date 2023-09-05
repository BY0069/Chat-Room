package Client;

import Entity.User;
import Utils.Poster;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {

    private static User user;

    private static Socket client;

    public Client(String host, int port) throws IOException {
        client = new Socket(host, port);
    }

    public static Socket getClient() {
        return client;
    }

    public void start() {
        ExecutorService pool = Executors.newFixedThreadPool(2);
        try {
            Poster.sendMessage(client, user.getNickname());
            pool.execute(new receiver(client));
            pool.execute(new sender(client));
            if (client.isInputShutdown() && client.isOutputShutdown()) {
                client.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.shutdown();
        }
    }

    private static class receiver implements Runnable {
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
                        socket.shutdownInput();
                        break;
                    } else {
                        System.out.println(message);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static class sender implements Runnable {

        Socket socket;

        public sender(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    String message = new Scanner(System.in).nextLine();
                    Poster.sendMessage(socket, message);
                    if (message.equals("exit")) {
                        socket.shutdownOutput();
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException();
            }
        }
    }
}

