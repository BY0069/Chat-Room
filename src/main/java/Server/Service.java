package Server;

import Entity.Message;
import Entity.Status;
import Entity.User;
import Entity.UserDAO;
import Utils.Poster;
import com.alibaba.fastjson2.JSON;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 服务线程
 */
public class Service implements Runnable {

    //该服务类维护的客户端
    private final Socket client;

    //在线用户群组
    private final Group group = Group.getGroup();

    //当前用户实例对象
    private User self;

    public Service(Socket socket) {
        this.client = socket;
    }

    /**
     * 主线程维护一个单例线程池进行消息的收发
     */
    @Override
    public void run() {
        ExecutorService pool = Executors.newSingleThreadExecutor();
        try {
            pool.execute(new Controller());
        } catch (Exception e) {
            throw new RuntimeException();
        } finally {
            pool.shutdown();
        }
    }

    /**
     * 服务线程中的消息处理线程
     */
    private class Controller implements Runnable {
        UserDAO userDAO;

        @Override
        public void run() {
            SqlSession session; // sqlSession对象，用于数据库操作
            try {
                session = new SqlSessionFactoryBuilder().build(
                        Resources.getResourceAsStream("MybatisConfig.xml")
                ).openSession(true);
                userDAO = session.getMapper(UserDAO.class); // 获得映射对象

                // 服务开始
                while (!client.isClosed()) {
                    String req = Poster.receiveMessage(client);     // 对请求进行接受
                    Message info = JSON.parseObject(req, Message.class);    // 对请求进行包装处理
                    User user;
                    switch (info.getType()) {

                        // 登录请求处理
                        case 1: {
                            // 获得登录信息
                            String[] infos = info.getContent().split("\\|");
                            String username = infos[0];
                            String password = infos[1];
                            //查找数据库
                            user = userDAO.getUserByName(username);
                            Message res = new Message();

                            // 若数据库中存在用户
                            if (user != null) {
                                if (password.equals(user.getPassword())) {
                                    // 构建响应
                                    res.setType(5);
                                    res.setTo(username);
                                    res.setContent("Login Success!");
                                    Poster.sendMessage(client, JSON.toJSONString(res));

                                    // 数据库中该用户状态变更
                                    userDAO.setStatus(username, Status.online);
                                    group.addClient(client, username);
                                    // 获得当前用户实例
                                    self = user;

                                    // 通知所有客户端更新在线用户列表
                                    Message response = getOnlineUsers();
                                    group.groupMessage(response);
                                } else {    // 若信息对比错误
                                    res.setType(-1);
                                    res.setContent("密码错误");
                                    Poster.sendMessage(client, JSON.toJSONString(res));
                                }
                            } else {    //若用户不存在
                                res.setType(-1);
                                res.setContent("未知用户");
                                Poster.sendMessage(client, JSON.toJSONString(res));
                            }
                            break;
                        }
                        // 注册请求处理
                        case 2: {
                            // 获取客户端信息
                            String[] infos = info.getContent().split("\\|");
                            String username = infos[0];
                            String password = infos[1];
                            String nickname = infos[2];
                            // 查询数据库信息
                            userDAO = session.getMapper(UserDAO.class);
                            user = userDAO.getUserByName(username);
                            // 若请求用户不存在，则进行注册操作
                            if (user == null) {
                                // 实例化用户对象
                                user = new User();
                                user.setId(UUID.randomUUID().toString());   // 生成UUID
                                user.setUsername(username);
                                user.setNickname(nickname);
                                user.setPassword(password);
                                user.setStatus(Status.offline);
                                // 存入数据库
                                userDAO.saveUser(user);
                                // 构建响应
                                Message ret = new Message();
                                ret.setType(2);
                                ret.setContent("注册成功！");
                                Poster.sendMessage(client, JSON.toJSONString(ret));
                            } else {    // 若请求用户存在，返回错误信息
                                Message ret = new Message();
                                ret.setType(2);
                                ret.setContent("该用户名已存在！");
                                Poster.sendMessage(client, JSON.toJSONString(ret));
                            }
                            break;
                        }

                        // 获取当前用户信息
                        case 3: {
                            Message response = new Message();
                            response.setType(3);
                            response.setContent(self.getUsername());
                            Poster.sendMessage(client, JSON.toJSONString(response));
                            break;
                        }

                        // 获取在线用户列表
                        case 7: {
                            userDAO = session.getMapper(UserDAO.class);
                            Message response = getOnlineUsers();
                            Poster.sendMessage(client, JSON.toJSONString(response));
                            break;
                        }

                        // 群聊请求处理
                        case 8: {
                            Message message = new Message();
                            message.setType(8);
                            message.setFrom(info.getFrom());
                            message.setContent(info.getContent());
                            group.groupMessage(message);
                            break;
                        }

                        // 私聊请求处理
                        case 9: {
                            // 获取发信者与收信者所属socket
                            Socket sender = group.getSocketByUsername(info.getFrom());
                            Socket receiver = group.getSocketByUsername(info.getTo());

                            // 构建响应
                            Message message = new Message();
                            message.setType(9);
                            message.setFrom(info.getFrom());
                            message.setTo(info.getTo());
                            message.setContent(info.getContent());

                            // 分别发送信息给发信者与收信者
                            Poster.sendMessage(sender, JSON.toJSONString(message));
                            Poster.sendMessage(receiver, JSON.toJSONString(message));
                            break;
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                // 下线及断线处理
                group.removeClient(client);
                try {
                    session = new SqlSessionFactoryBuilder().build(
                            Resources.getResourceAsStream("MybatisConfig.xml")).openSession(true);
                    userDAO = session.getMapper(UserDAO.class);
                    // 设置数据库中用户状态
                    userDAO.setStatus(self.getUsername(), Status.offline);
                    session.close();
                } catch (IOException exception) {
                    throw new RuntimeException(exception);
                }
            }
        }

        /**
         * 获取当前在线用户列表
         * @return 已构建的请求
         */
        private Message getOnlineUsers() {
            Message response = new Message();
            response.setType(7);
            List<User> allOnlineUsers = userDAO.getAllOnlineUsers();
            List<String> allOnlineUsernames = new ArrayList<>();
            for (User onlineUser : allOnlineUsers) {
                allOnlineUsernames.add(onlineUser.getUsername());
            }
            String usernames = String.join(",", allOnlineUsernames);
            response.setContent(usernames);
            return response;
        }
    }
}