package Client.GUI;

import Entity.Message;
import Utils.Poster;
import com.alibaba.fastjson.JSON;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.Socket;

public class Register extends JFrame {

    private final JLabel title;//标题文本
    private final JLabel username_label;//用户名文本
    private final JLabel password_label;//密码文本
    private final JLabel re_password_label;//重复密码
    private final JTextField username_text;//用户名输入框
    private final JPasswordField password_text;//密码输入框
    private final JPasswordField re_password_text;//重复密码输入框
    private final JButton submit;//提交按钮
    private final JLabel login_label;//登录提示语

    private final JLabel realname_label;
    private final JTextField realname_text;
    private final Socket server;

    public Register(Socket server) {
        title = new JLabel("欢迎注册XXX聊天室");
        username_label = new JLabel("用户名");
        password_label = new JLabel("密  码");
        re_password_label = new JLabel("重复密码");
        username_text = new JTextField();
        password_text = new JPasswordField();
        re_password_text = new JPasswordField();
        login_label = new JLabel("已有账号？点我登录！");
        submit = new JButton("注册");

        realname_label = new JLabel("昵  称");
        realname_text = new JTextField();
        this.server = server;

        showPanel();
    }

    public void showPanel() {
        /* 设置窗口的宽高 */
        this.setSize(600, 510);
        /* 获取使用设备的屏幕宽高 */
        double width = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        double height = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
        /* 创建一块画布 */
        MyJPanel jPanel = new MyJPanel();
        //布局组件
        title.setBounds(180, 15, 300, 60);
        title.setFont(new Font("宋体", Font.PLAIN, 25));
        title.setForeground(new Color(0x000000));
        //将组件添加到画布上
        jPanel.add(title);

        username_label.setBounds(50, 100, 300, 40);
        username_label.setFont(new Font("宋体", Font.PLAIN, 30));
        //usernameLabel其他设置...
        jPanel.add(username_label);

        username_text.setBounds(200, 100, 300, 40);
        username_text.setFont(new Font("宋体", Font.PLAIN, 25));
        //...
        jPanel.add(username_text);

        password_label.setBounds(50, 160, 300, 40);
        password_label.setFont(new Font("宋体", Font.PLAIN, 30));
        //...
        jPanel.add(password_label);

        re_password_label.setBounds(50, 220, 300, 40);
        re_password_label.setFont(new Font("宋体", Font.PLAIN, 30));
        //...
        jPanel.add(re_password_label);

        password_text.setBounds(200, 160, 300, 40);
        password_text.setFont(new Font("宋体", Font.PLAIN, 25));
        //...
        jPanel.add(password_text);

        re_password_text.setBounds(200, 220, 300, 40);
        re_password_text.setFont(new Font("宋体", Font.PLAIN, 25));
        //...
        jPanel.add(re_password_text);

        realname_label.setBounds(50, 280, 300, 40);
        realname_label.setFont(new Font("宋体", Font.PLAIN, 30));
        jPanel.add(realname_label);

        realname_text.setBounds(200, 280, 300, 40);
        realname_text.setFont(new Font("宋体", Font.PLAIN, 25));
        jPanel.add(realname_text);

        login_label.setBounds(380, 330, 200, 30);
        login_label.setFont(new Font("宋体", Font.ITALIC, 15));
        login_label.setForeground(Color.BLUE);
        jPanel.add(login_label);

        JFrame that = this;
        login_label.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                that.dispose();
                Login loginFrame = new Login(server);
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                login_label.setForeground(Color.gray);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                login_label.setForeground(Color.blue);
            }
        });

        submit.setBounds(250, 380, 85, 50);
        submit.setFont(new Font("宋体", Font.PLAIN, 20));
        submit.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = username_text.getText();
                String password = new String(password_text.getPassword());
                String re_password = new String(re_password_text.getPassword());
                String nickname = realname_text.getText();
                if (username == null || username.length() == 0) {
                    /* 提示 */
                    JOptionPane.showMessageDialog(null, "用户名不能为空", "错误信息", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (password == null || password.length() == 0) {
                    /* 提示 */
                    JOptionPane.showMessageDialog(null, "密码不能为空", "错误信息", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!password.equals(re_password)) {
                    /* 提示 */
                    JOptionPane.showMessageDialog(null, "两次密码不一致", "错误信息", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (nickname == null || nickname.length() == 0) {
                    /* 提示 */
                    JOptionPane.showMessageDialog(null, "昵称不能为空", "错误信息", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Message message = new Message();
                message.setType(2);
                message.setContent(username + "|" + password + "|" + nickname);
                String line = JSON.toJSONString(message);

                try {
                    Poster.sendMessage(server, line);
                    String res = Poster.receiveMessage(server);
                    Message rec = JSON.parseObject(res, Message.class);
                    if (rec.getType().equals(2)) {
                        JOptionPane.showMessageDialog(null, rec.getContent(), "", JOptionPane.PLAIN_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "未知错误", "ERROR", JOptionPane.ERROR_MESSAGE);
                    }
                    Message exitMsg = new Message();
                    exitMsg.setType(0);
                    Poster.sendMessage(server, JSON.toJSONString(exitMsg));
                    dispose();
                    new Login(server);
                } catch (IOException ex) {
                    throw new RuntimeException();
                }
            }
        });

        jPanel.add(submit);
        /* 将组件添加到画布上 */
        jPanel.add(title);
        /* 设置布局方式 */
        jPanel.setLayout(null);
        jPanel.setBackground(new Color(255, 255, 255));
        /* 将画布添加到窗体上 */
        this.setIconImage(new ImageIcon("src\\qq.jpg").getImage().getScaledInstance(80, 80, Image.SCALE_DEFAULT));


        this.setContentPane(jPanel);

        /* 设置窗体的标题 */
        this.setTitle("XXX聊天室");
        /* 设置布局方式 */
        //this.setLayout(null);
        /* 使得窗口居中 */
        this.setLocation((int) (width / 2 - this.getWidth() / 2), (int) (height / 2 - this.getHeight() / 2));
        /* 显示窗口 */
        this.setVisible(true);
        /* 设置关闭窗口即退出程序 */
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public class MyJPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            ImageIcon img = new ImageIcon("src\\background.jpg");
            img.paintIcon(this, g, 0, 0);
        }
    }
}

