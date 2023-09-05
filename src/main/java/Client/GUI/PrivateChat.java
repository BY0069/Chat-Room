package Client.GUI;

import Entity.Message;
import Utils.Poster;
import com.alibaba.fastjson2.JSON;

import javax.swing.*;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PrivateChat extends JFrame {
    private final Socket server;
    private JPanel panel;
    private JTextField inputField;
    private JButton submit;
    private JTextArea chatArea;
    public PrivateChat(String from, String to, Socket socket) {
        this.server = socket;
        setTitle("与" + to + "私聊中");
        chatArea.setEditable(false);

        // 单线程池负责管理消息接收线程
        ExecutorService receiver = Executors.newSingleThreadExecutor();
        receiver.execute(new receiver());

        // 消息发送功能
        submit.addActionListener(e -> {
            String text = inputField.getText();
            Message message = new Message();
            message.setType(9);
            message.setFrom(from);
            message.setTo(to);
            message.setContent(text);
            try {
                Poster.sendMessage(server, JSON.toJSONString(message));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            inputField.setText("");
        });

        // 窗口渲染
        this.setContentPane(panel);
        this.setSize(800, 600);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public JTextArea getChatArea() {
        return chatArea;
    }

    /**
     * 消息接收线程
     */
    private class receiver implements Runnable {
        @Override
        public void run() {
            try {
                String receiveMessage = Poster.receiveMessage(server);
                Message message = JSON.parseObject(receiveMessage, Message.class);
                if (message.getType().equals(9)) {
                    chatArea.append(message.getContent() + '\n');
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
