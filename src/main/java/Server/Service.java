package Server;

import Utils.Poster;

import java.net.Socket;

public class Service implements Runnable {

    Socket socket;

    private final Group group = Group.getGroup();

    public Service(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            group.addClient(socket);
            group.groupMessage("New user joined!");
            group.groupMessage("Online users:" + group.length());
            String message;
            while ((message = Poster.receiveMessage(socket)) != null) {
                if (message.equals("exit")) {
                    socket.close();
                    group.removeClient(socket);
                    break;
                }
                System.out.println("Receive:" + message);
                group.groupMessage(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
