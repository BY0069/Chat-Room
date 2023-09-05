package Client.GUI;

import Entity.Message;
import Utils.Poster;
import com.alibaba.fastjson2.JSON;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Chatroom extends JFrame {
    private final Socket server;
    ExecutorService pool = Executors.newSingleThreadExecutor();
    private JTextField inputField;
    private JButton sendButton;
    private JTextArea chatArea;
    private JPanel panel;
    private JList<String> onlineList;
    private JButton privateChatButton;
    private PrivateChat privateChatWindow;
    private String curUser;

    public Chatroom(Socket socket) {
        chatArea.setEditable(false);

        server = socket;

        pool.execute(new receiver());

        // 获取当前登录用户
        try {
            Message req = new Message();
            req.setType(3);
            Poster.sendMessage(server, JSON.toJSONString(req));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 发送按钮的监听器，实现信息发送
        sendButton.addActionListener(e -> {
            String text = inputField.getText();
            Message message = new Message();
            message.setType(8);
            message.setFrom(curUser);
            message.setContent(text);
            try {
                Poster.sendMessage(server, JSON.toJSONString(message));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            inputField.setText("");
        });

        // 私聊按钮监听器，实现私聊功能
        privateChatButton.addActionListener(e -> {
            String selectedUser = onlineList.getSelectedValue();
            if (selectedUser == null) {
                JOptionPane.showMessageDialog(null, "请选择私聊对象", "ERROR",
                        JOptionPane.ERROR_MESSAGE);
            } else if (selectedUser.equals(curUser)) {
                JOptionPane.showMessageDialog(null, "不能与自己私聊", "ERROR",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                privateChatWindow = new PrivateChat(curUser, selectedUser, server);
            }
        });

        // 获取在线用户列表
        try {
            Message req = new Message();
            req.setType(7);
            Poster.sendMessage(server, JSON.toJSONString(req));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 输入框监听，实现回车键一键发送功能
        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    sendButton.doClick();
                }
            }
        });

        // 窗口渲染
        setTitle("当前用户：" + curUser);
        this.setContentPane(panel);
        this.setSize(800, 600);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    /**
     *  消息接受处理线程
     */
    private class receiver implements Runnable {
        @Override
        public void run() {
            try {
                while (!server.isClosed()) {
                    String receiveMessage = Poster.receiveMessage(server);
                    Message message = JSON.parseObject(receiveMessage, Message.class);
                    switch (message.getType()) {
                        // 获取当前用户信息
                        case 3: {
                            curUser = message.getContent();
                            break;
                        }

                        // 获取在线用户列表
                        case 7: {
                            onlineList.setListData(message.getContent().split(","));
                            break;
                        }

                        // 群发消息处理
                        case 8: {
                            // 将接收到的消息放入消息框
                            chatArea.append(message.getFrom() + ":" + message.getContent() + '\n');
                            break;
                        }

                        // 私聊消息处理
                        case 9: {
                            // 获取私聊消息框
                            JTextArea textArea = privateChatWindow.getChatArea();
                            // 接受消息放入私聊框中
                            textArea.append(message.getFrom() + ":" + message.getContent() + '\n');
                            break;
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
