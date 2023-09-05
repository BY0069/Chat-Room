package Server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 服务器类，维护一个线程池，用于执行服务类
 */
public class Server {
    static int port = 10899;

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("Waiting...");
            while (true) {
                Socket socket = server.accept();
                executorService.execute(new Service(socket));
            }
        } catch (Exception e) {
            throw new RuntimeException();
        } finally {
            executorService.shutdown();
        }
    }
}
