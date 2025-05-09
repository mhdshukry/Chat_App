package client;

import server.ChatService;
import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.rmi.Naming;

public class LoginFrame extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton, registerButton, exitButton;
    private JLabel appLogo, appName;

    public LoginFrame() {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf()); // Elegant Dark Mode
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        setTitle("🔐 Secure Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(750, 500);
        setLocationRelativeTo(null);

        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(20, 22, 27));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font customFont = new Font("SansSerif", Font.BOLD, 15);

        // 🔹 **App Logo & Name**
        appLogo = new JLabel(new ImageIcon("logo.png"));
        appName = new JLabel("🌐 MyChat App", JLabel.CENTER);
        appName.setForeground(Color.WHITE);
        appName.setFont(new Font("SansSerif", Font.BOLD, 20));

        JLabel emailLabel = createStyledLabel("📧 Email:");
        emailField = createStyledField();

        JLabel passwordLabel = createStyledLabel("🔑 Password:");
        passwordField = createStyledPasswordField();

        loginButton = createStyledButton("🚀 Login", new Color(3, 169, 244));
        registerButton = createStyledButton("📝 Register", new Color(239, 83, 80));
        exitButton = createStyledButton("❌ Exit", new Color(220, 20, 60));

        // ✅ Attach Login Action
        loginButton.addActionListener(this::loginAction);

        registerButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                new RegisterFrame();
                setVisible(false);
            });
        });

        exitButton.addActionListener(e -> System.exit(0));

        // 🔹 **Layout Fixes**
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(appLogo, gbc);

        gbc.gridy++;
        panel.add(appName, gbc);

        addFormField(panel, emailLabel, emailField, gbc);
        addFormField(panel, passwordLabel, passwordField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        panel.add(loginButton, gbc);

        gbc.gridx = 1;
        panel.add(registerButton, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(exitButton, gbc);

        add(panel);
    }

    private void addFormField(JPanel panel, JLabel label, JComponent field, GridBagConstraints gbc) {
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        panel.add(label, gbc);

        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("SansSerif", Font.BOLD, 15));
        return label;
    }

    private JTextField createStyledField() {
        JTextField field = new JTextField(20);
        field.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        field.setBackground(new Color(35, 39, 42));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField(20);
        field.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        field.setBackground(new Color(35, 39, 42));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        return field;
    }

    private JButton createStyledButton(String text, Color background) {
        JButton button = new JButton(text);
        button.setBackground(background);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 15));
        return button;
    }

    private void loginAction(ActionEvent e) {
        System.out.println("Login button clicked!"); // ✅ Debugging

        String email = emailField.getText();
        String password = String.valueOf(passwordField.getPassword());

        try {
            ChatService service = (ChatService) Naming.lookup("rmi://localhost:1099/ChatService");

            if (service == null) {
                JOptionPane.showMessageDialog(this, "❌ Server connection failed!");
                return;
            }

            boolean isAdmin = service.login(email, password);

            if (isAdmin) {
                JOptionPane.showMessageDialog(this, "🎉 Admin Login successful!");
                dispose();
                new AdminPanel();
            } else {
                String nickname = service.getNicknameByEmail(email);
                if (nickname == null) {
                    JOptionPane.showMessageDialog(this, "❌ Login failed: User not found.");
                    return;
                }

                JOptionPane.showMessageDialog(this, "✅ Welcome " + nickname + "!");
                dispose();

                ChatClient chatClient = new ChatClient(email, nickname);
                chatClient.startClient();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "❌ Login failed: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginFrame::new);
    }
}
