package Server;

import Utils.Poster;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Group {
    private final List<Socket> clients;

    private static final Group group = new Group();

    private Group() {
        clients = new ArrayList<>(8);
    }

    public static Group getGroup() {
        return group;
    }

    public void addClient(Socket socket) {
        clients.add(socket);
    }

    public void removeClient(Socket socket) {
        if (clients.contains(socket)) {
            clients.remove(socket);
        } else {
            throw new NullPointerException();
        }
    }

    public int length() {
        return clients.size();
    }

    public void groupMessage(String msg) throws IOException {
        for (Socket client : clients) {
            Poster.sendMessage(client, msg);
        }
    }
}
