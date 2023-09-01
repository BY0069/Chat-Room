package Server;

import Utils.Poster;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Service implements Runnable {

    private final Socket socket;

    private static volatile String message;

    private static volatile boolean isReceived = false;

    private static final Object lock = new Object();

    private final Group group = Group.getGroup();

    public Service(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        ExecutorService pool = Executors.newFixedThreadPool(2);
        try {
            group.addClient(socket);
            group.groupMessage("New user joined!");
            group.groupMessage("Online users:" + group.length());
            pool.execute(new Sender());
            pool.execute(new Receiver());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.shutdown();
        }
    }

    private class Receiver implements Runnable {

        @Override
        public void run() {
            try {
                while (!socket.isClosed()) {
                    synchronized (lock) {
                        lock.notify();
                        if (!isReceived) {
                            message = Poster.receiveMessage(socket);
                            if (message != null) {
                                if (message.equals("exit")) {
                                    socket.close();
                                    group.removeClient(socket);
                                }
                                System.out.println("Receive:" + message);
                            }
                        }
                        isReceived = true;
                        lock.wait();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class Sender implements Runnable {
        @Override
        public void run() {
            try {
                while (!socket.isClosed()) {
                    synchronized (lock) {
                        while (!isReceived) {
                            System.out.println("Sender waiting...");
                            lock.wait();
                        }
                        lock.notify();
                        isReceived = false;
                        group.groupMessage(message);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
