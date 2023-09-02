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

public class Login extends JFrame{
    private JPanel panel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel registerField;

    public Login() {
        registerField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                registerField.setForeground(Color.red);
            }
        });

        registerField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                registerField.setForeground(Color.getColor("#BBBBBB"));
            }
        });

        registerField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new Register().open();
                dispose();
            }
        });

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                if (username.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "用户名不能为空", "ERROR", JOptionPane.ERROR_MESSAGE);
                }

                String password = new String(passwordField.getPassword());
                if (password.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "密码不能为空", "ERROR", JOptionPane.ERROR_MESSAGE);
                }

                Message message = new Message();
                message.setType(1);
                message.setContent(username + "|" + password);
                String line = JSON.toJSONString(message);

                try {
                    Socket login = new Socket("127.0.0.1", 10899);
                    Poster.sendMessage(login, line);
                    String res = Poster.receiveMessage(login);
                    JOptionPane.showMessageDialog(null, res, "", JOptionPane.PLAIN_MESSAGE);
                    login.close();
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
