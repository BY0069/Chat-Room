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
            group.groupMessage("welcome from server");
            String message;
            while ((message = Poster.receiveMessage(socket)) != null) {
                if (message.equals("exit")) {
                    socket.shutdownOutput();
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
