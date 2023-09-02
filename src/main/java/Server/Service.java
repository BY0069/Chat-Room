package Server;

import Entity.Message;
import Entity.User;
import Utils.Poster;
import com.alibaba.fastjson2.JSON;

import java.io.IOException;
import java.lang.invoke.SwitchPoint;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Service implements Runnable {

    private final Socket socket;

    private static boolean isReceived = false;

    private static Message info;

    private static final Object lock = new Object();

    public Service(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        ExecutorService pool = Executors.newFixedThreadPool(2);
        try {
            pool.execute(new Sender());
            pool.execute(new Receiver());
        } catch (Exception e) {
            throw new RuntimeException();
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
                            String line = Poster.receiveMessage(socket);
                            info = JSON.parseObject(line, Message.class);
                            Integer type = info.getType();
                            switch (type) {
                                case 1: {
                                    String[] infos = info.getContent().split("\\|");
                                    String username = infos[0];
                                    String password = infos[1];

                                    // TODO: 数据库登录校验
                                }

                                case 2: {
                                    String[] infos = info.getContent().split("\\|");
                                    String username = infos[0];
                                    String password = infos[1];
                                    String nickname = infos[2];

                                    User user = new User();
                                    user.setId(UUID.randomUUID().toString());
                                    user.setUsername(username);
                                    user.setPassword(password);
                                    user.setNickname(nickname);

                                    System.out.println(user);

                                    //TODO: 写入数据库

                                    break;
                                }
                            }
                        }
                        isReceived = true;
                        lock.wait();
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException();
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
                            lock.wait();
                        }
                        lock.notify();
                        isReceived = false;
                        switch (info.getType()) {
                            case 1: {

                            }
                            case 2: {
                                Message res = new Message();
                                res.setType(5);
                                res.setContent("Register Success!");
                                Poster.sendMessage(socket, JSON.toJSONString(res));
                            }
                        }

                    }
                }
            } catch (Exception e) {
                throw new RuntimeException();
            }
        }
    }
}
