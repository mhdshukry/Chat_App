package client;


import server.ChatService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.rmi.Naming;

public class ChatWindow extends JFrame {

    private final JTextArea chatArea;
    private final JTextField messageField;
    private final ChatClient chatClient;
    private final String nickname;
    private final ChatService service;

    public ChatWindow(ChatClient chatClient, String nickname) {
        this.chatClient = chatClient;
        this.nickname = nickname;

        // Initialize RMI service
        try {
            service = (ChatService) Naming.lookup("rmi://localhost:1099/ChatService");
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect to ChatService", e);
        }

        // Initialize UI components
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);

        messageField = new JTextField(30);
        JButton sendButton = new JButton("Send");

        JPanel chatPanel = new JPanel(new BorderLayout());
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(messageField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);

        chatPanel.add(scrollPane, BorderLayout.CENTER);
        chatPanel.add(bottomPanel, BorderLayout.SOUTH);

        setTitle("Chat Window - " + nickname);
        setContentPane(chatPanel);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(500, 400);
        setVisible(true);

        sendButton.addActionListener(this::sendMessage);
    }

    private void sendMessage(ActionEvent e) {
        try {
            String message = messageField.getText().trim();
            if (message.isEmpty()) return;

            service.sendMessage(nickname, message);
            messageField.setText("");

            if ("Bye".equalsIgnoreCase(message)) {
                service.leaveChat(nickname);
                dispose();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Message send failed!");
        }
    }

    public void appendMessage(String message) {
        chatArea.append(message + "\n");
    }
}
