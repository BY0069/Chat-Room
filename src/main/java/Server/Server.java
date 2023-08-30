package Server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    static int port = 10899;

    public static void main(String[] args) {
        Socket socket;
        ExecutorService executorService = Executors.newCachedThreadPool();
        try(ServerSocket server = new ServerSocket(port)) {
            System.out.println("Waiting...");
            while(true) {
                socket = server.accept();
                executorService.execute(new Service(socket));
                System.out.println(executorService);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }
    }
}
