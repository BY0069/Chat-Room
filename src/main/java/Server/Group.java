package Server;

import Entity.Message;
import Utils.Poster;
import com.alibaba.fastjson.JSON;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 群组类，用于维护当前在线用户组，采用单例模式防止出现数据重复读现象及节约空间
 * @author 25083
 */
public class Group {

    /**
     * 构造函数设为private，防止重复调用
     */
    private Group() {
        clients = new HashMap<>();
    }

    // 初始化唯一实例
    private static final Group group = new Group();

    // 使用Map维护一个用户组
    private final Map<Socket, String> clients;

    /**
     * 获得已初始化的用户组实例
     * @return 用户组
     */
    public static Group getGroup() {
        return group;
    }

    /**
     * 添加用户
     * @param socket 用户所属的socket
     * @param username 用户的用户名
     */
    public void addClient(Socket socket, String username) {
        clients.put(socket, username);
    }

    /**
     * 通过Socket删除用户组中的用户
     * @param socket 目标用户所属socket
     */
    public void removeClient(Socket socket) {
        for (Map.Entry<Socket, String> entry : clients.entrySet()) {
            if (socket == entry.getKey()) {
                clients.remove(socket);
            }
        }
    }

    /**
     * 群发消息函数
     * @param msg 群发消息
     * @throws IOException 抛出IO错误信息
     */
    public void groupMessage(Message msg) throws IOException {
        Set<Socket> socketSet = clients.keySet();

        for (Socket socket : socketSet) {
            Poster.sendMessage(socket, JSON.toJSONString(msg));
        }
    }

    /**
     * 查找函数，通过用户名来查找该用户所属socket
     * @param username 用户名
     * @return 该用户所属socket
     */
    public Socket getSocketByUsername(String username) {
        for (Map.Entry<Socket, String> entry : clients.entrySet()) {
            if (entry.getValue().equals(username)) {
                return entry.getKey();
            }
        }
        return null;
    }
}
