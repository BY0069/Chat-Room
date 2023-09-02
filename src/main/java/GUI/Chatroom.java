package GUI;

import javax.swing.*;
import java.awt.*;

public class Chatroom {
    private JTextField inputField;
    private JButton sendButton;
    private JTextArea chatArea;

    public Chatroom() {
        JFrame Frame = new JFrame("Chat Interface");
        Frame.setSize(400, 400);
        Frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());

        inputPanel.add(inputField, BorderLayout.CENTER);

        sendButton.addActionListener(e -> {
            String message = inputField.getText();
            chatArea.append("You: " + message + "\n");
            inputField.setText("");
        });
        inputPanel.add(sendButton, BorderLayout.EAST);

        panel.add(inputPanel, BorderLayout.SOUTH);
        Frame.add(panel);

        Frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Chatroom::new);
    }
}


