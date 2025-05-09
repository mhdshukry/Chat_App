package client;

import server.ChatService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.rmi.Naming;
import java.util.Set;

public class AdminPanel extends JFrame {

    private ChatService service;

    public AdminPanel() {
        setTitle("🛠️ Admin Panel");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(750, 500);
        setLocationRelativeTo(null);

        // Initialize RMI connection
        try {
            service = (ChatService) Naming.lookup("rmi://localhost:1099/ChatService");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "❌ Failed to connect to ChatService!", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return;
        }

        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        JPanel adminPanel = new JPanel(new GridLayout(6, 1, 10, 10));
        adminPanel.setBackground(new Color(20, 22, 27));

        JButton startChatButton = createStyledButton("💬 Start Chat", new Color(3, 169, 244));
        JButton subscribeUserButton = createStyledButton("✅ Subscribe User", new Color(76, 175, 80));
        JButton unsubscribeUserButton = createStyledButton("🔄 Unsubscribe User", new Color(255, 193, 7));
        JButton listUsersButton = createStyledButton("📜 List Subscribed Users", new Color(0, 150, 136));
        JButton removeUserButton = createStyledButton("🚫 Remove User", new Color(220, 20, 60));
        JButton exitButton = createStyledButton("❌ Exit Panel", new Color(128, 128, 128));

        startChatButton.addActionListener(this::startChat);
        subscribeUserButton.addActionListener(e -> modifySubscription(true));
        unsubscribeUserButton.addActionListener(e -> modifySubscription(false));
        listUsersButton.addActionListener(e -> listSubscribedUsers());
        removeUserButton.addActionListener(e -> removeUser());
        exitButton.addActionListener(e -> System.exit(0));

        adminPanel.add(startChatButton);
        adminPanel.add(subscribeUserButton);
        adminPanel.add(unsubscribeUserButton);
        adminPanel.add(listUsersButton);
        adminPanel.add(removeUserButton);
        adminPanel.add(exitButton);

        add(adminPanel);
    }

    private JButton createStyledButton(String text, Color background) {
        JButton button = new JButton(text);
        button.setBackground(background);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 15));
        return button;
    }

    private void startChat(ActionEvent e) {
        try {
            service.startChat();
            JOptionPane.showMessageDialog(this, "💬 Chat started successfully!");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "❌ Failed to start chat!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modifySubscription(boolean subscribe) {
        String email = JOptionPane.showInputDialog(this, "📧 Enter user email:");
        if (email == null || email.trim().isEmpty()) return;

        try {
            if (subscribe) {
                service.subscribeUserToChat(email);
                JOptionPane.showMessageDialog(this, "✅ User subscribed successfully!");
            } else {
                service.unsubscribeUserFromChat(email);
                JOptionPane.showMessageDialog(this, "🔄 User unsubscribed!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "❌ Subscription modification failed!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void listSubscribedUsers() {
        try {
            Set<String> users = service.listSubscribedUsers();
            if (users == null || users.isEmpty()) {
                JOptionPane.showMessageDialog(this, "📜 No users are currently subscribed.");
            } else {
                StringBuilder userList = new StringBuilder("📜 Subscribed Users:\n");
                for (String user : users) {
                    userList.append(" - ").append(user).append("\n");
                }
                JOptionPane.showMessageDialog(this, userList.toString());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "❌ Failed to fetch user list!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void removeUser() {
        String email = JOptionPane.showInputDialog(this, "🚫 Enter email of user to remove:");
        if (email == null || email.trim().isEmpty()) return;

        try {
            service.unsubscribeUserFromChat(email);
            JOptionPane.showMessageDialog(this, "🚫 User removed successfully!");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "❌ Failed to remove user!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AdminPanel::new);
    }
}
