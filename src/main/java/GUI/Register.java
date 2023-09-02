package GUI;

import Entity.Message;
import Utils.Poster;
import com.alibaba.fastjson.JSON;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.Socket;

public class Register extends JFrame{
    private JButton registerButton;
    private JTextField setUsernameField;
    private JPasswordField setPasswordField;
    private JPasswordField confirmPasswordField;
    private JLabel registerLabel;
    private JPanel panel;
    private JLabel loginField;
    private JTextField nicknameField;

    public Register() {
        loginField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                loginField.setForeground(Color.red);
            }
        });

        loginField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                loginField.setForeground(Color.getColor("#BBBBBB"));
            }
        });

        loginField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new Login().open();
                dispose();
            }
        });
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = setUsernameField.getText();
                if (username.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "用户名不能为空", "ERROR", JOptionPane.ERROR_MESSAGE);
                }

                String password = new String(setPasswordField.getPassword());
                if (password.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "密码不能为空", "ERROR", JOptionPane.ERROR_MESSAGE);
                }

                String cfmPassword = new String(confirmPasswordField.getPassword());
                if (!(cfmPassword.equals(password))) {
                    JOptionPane.showMessageDialog(null, "两次密码不相同", "ERROR", JOptionPane.ERROR_MESSAGE);
                }
                if (cfmPassword.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "请再次确认密码", "ERROR", JOptionPane.ERROR_MESSAGE);
                }

                String nickname = nicknameField.getText();
                if (nickname.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "昵称不能为空", "ERROR", JOptionPane.ERROR_MESSAGE);
                }

                Message message = new Message();
                message.setType(2);
                message.setContent(username + "|" + password + "|" + nickname);
                String line = JSON.toJSONString(message);

                try {
                    Socket register = new Socket("127.0.0.1", 10899);
                    Poster.sendMessage(register, line);
                    String res = Poster.receiveMessage(register);
                    Message rec = JSON.parseObject(res, Message.class);
                    JOptionPane.showMessageDialog(null, rec.getContent(), "", JOptionPane.PLAIN_MESSAGE);
                    register.close();
                } catch (IOException ex) {
                    throw new RuntimeException();
                }
            }
        });
    }

    public void open() {
        this.setContentPane(panel);
        this.setSize(800, 600);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }
}
