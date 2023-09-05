package Client.GUI;

import Entity.Message;
import Utils.Poster;
import com.alibaba.fastjson.JSON;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.Socket;

public class Login extends JFrame {

    private final JLabel title;//标题文本
    private final JLabel username_label;//用户名文本
    private final JLabel password_label;//密码文本
    private final JTextField username_text;//用户名输入框
    private final JPasswordField password_text;//密码输入框
    private final JButton submit;//提交按钮
    private final JLabel register_label;//注册提示语
    private final JLabel avatar;//头像
    private final ImageIcon imageIcon;
    private final Socket server;

    public Login(Socket socket) {
        server = socket;
        title = new JLabel("欢迎登录XXX聊天室");
        username_label = new JLabel("用户名");
        password_label = new JLabel("密  码");
        username_text = new JTextField();
        password_text = new JPasswordField();
        register_label = new JLabel("还没有账号？点我注册！");
        submit = new JButton("登录");
        imageIcon = new ImageIcon("src\\avatar.png");
        avatar = new JLabel(imageIcon);
        showPanel();
    }

    public void showPanel() {
        /* 设置窗口的宽高 */
        this.setSize(600, 450);
        /* 获取使用设备的屏幕宽高 */
        double width = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        double height = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
        /* 创建一块画布 */
        MyJPanel jPanel = new MyJPanel();

        title.setBounds(180, 15, 300, 60);
        title.setFont(new Font("宋体", Font.PLAIN, 25));
        title.setForeground(new Color(0x000609));
        //将组件添加到画布上
        jPanel.add(title);


        imageIcon.setImage(imageIcon.getImage().getScaledInstance(60, 60, Image.SCALE_DEFAULT));
        avatar.setBounds(270, 75, 60, 60);
        jPanel.add(avatar);

        username_label.setBounds(50, 150, 300, 40);
        username_label.setFont(new Font("宋体", Font.PLAIN, 30));
        //usernameLabel其他设置...
        jPanel.add(username_label);

        username_text.setBounds(170, 150, 300, 40);
        username_text.setFont(new Font("宋体", Font.PLAIN, 25));
        //...
        jPanel.add(username_text);

        password_label.setBounds(50, 220, 300, 40);
        password_label.setFont(new Font("宋体", Font.PLAIN, 30));
        //...
        jPanel.add(password_label);

        password_text.setBounds(170, 220, 300, 40);
        password_text.setFont(new Font("宋体", Font.PLAIN, 25));
        //...
        jPanel.add(password_text);

        register_label.setBounds(340, 270, 200, 30);
        register_label.setFont(new Font("宋体", Font.ITALIC, 15));
        register_label.setForeground(Color.BLUE);
        jPanel.add(register_label);
        JFrame that = this;
        register_label.addMouseListener(new MouseListener() {
            /* 鼠标点击事件 按下和释放 */
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("clicked");
                //隐藏当前界面
                that.dispose();
                //打开注册界面

            }

            /* 鼠标按下 */
            @Override
            public void mousePressed(MouseEvent e) {

            }

            /* 鼠标释放 */
            @Override
            public void mouseReleased(MouseEvent e) {
                that.dispose();
                Register registerFrame = new Register(server);
            }

            /* 鼠标移入 */
            @Override
            public void mouseEntered(MouseEvent e) {
                register_label.setForeground(Color.gray);
            }

            /* 鼠标移除 */
            @Override
            public void mouseExited(MouseEvent e) {
                register_label.setForeground(Color.blue);
            }
        });

        submit.setBounds(250, 320, 85, 50);
        submit.setFont(new Font("宋体", Font.PLAIN, 20));
        submit.addActionListener(e -> {
            //  登录功能
            /* 点击登录按钮触发的事件 */
            /* 获取输入的用户名 */
            String username = username_text.getText();
            /* 如果用户名是空的 */
            if (username == null || username.isEmpty()) {
                /* 提示 */
                JOptionPane.showMessageDialog(null, "用户名不能为空", "错误信息", JOptionPane.ERROR_MESSAGE);
                return;
            }
            /* 获取用户输入的密码 */
            String password = new String(password_text.getPassword());
            if (password.isEmpty()) {
                /* 提示 */
                JOptionPane.showMessageDialog(null, "密码不能为空", "错误信息", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Message message = new Message();
            message.setType(1);
            message.setContent(username + "|" + password);
            String line = JSON.toJSONString(message);

            try {
                Poster.sendMessage(server, line);
                String res = Poster.receiveMessage(server);
                Message ret = JSON.parseObject(res, Message.class);
                if (ret.getType().equals(-1)) {
                    JOptionPane.showMessageDialog(null, ret.getContent(), "ERROR", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, ret.getContent(), "MESSAGE", JOptionPane.PLAIN_MESSAGE);
                    dispose();
                    new Chatroom(server);
                }
            } catch (IOException ex) {
                throw new RuntimeException();
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

