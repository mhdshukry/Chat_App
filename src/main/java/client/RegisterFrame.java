package client;

import server.ChatService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.rmi.Naming;
import java.net.URL;

public class RegisterFrame extends JFrame {
    private JTextField emailField, usernameField, nicknameField;
    private JPasswordField passwordField;
    private JButton registerButton, browseButton, loginButton, exitButton;
    private JLabel appLogo, appName;

    public RegisterFrame() {
        setTitle("📝 Register");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(750, 500); // Increased screen size
        setLocationRelativeTo(null);

        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(20, 22, 27));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 15, 8, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        Font customFont = new Font("SansSerif", Font.BOLD, 15);

        // 🔹 **App Logo & Name with Error Handling**
        URL logoUrl = getClass().getClassLoader().getResource("logo.png");
        appLogo = (logoUrl != null) ? new JLabel(new ImageIcon(logoUrl)) : new JLabel("🔷 Logo Missing");

        appName = new JLabel("🌐 MyChat App", JLabel.CENTER);
        appName.setForeground(Color.WHITE);
        appName.setFont(new Font("SansSerif", Font.BOLD, 20));

        emailField = createStyledField();
        usernameField = createStyledField();
        passwordField = createStyledPasswordField();
        nicknameField = createStyledField();

        browseButton = createStyledButton("📂 Browse", new Color(255, 152, 0));
        registerButton = createStyledButton("✅ Register", new Color(3, 169, 244));
        loginButton = createStyledButton("🔑 Login", new Color(34, 150, 243));
        exitButton = createStyledButton("❌ Exit", new Color(220, 20, 60));
        exitButton.addActionListener(e -> System.exit(0));

        // **Proper Alignment with GridBagConstraints**
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(appLogo, gbc);

        gbc.gridy++;
        panel.add(appName, gbc);

        addFormField(panel, "📧 Email:", emailField, gbc);
        addFormField(panel, "👤 Username:", usernameField, gbc);
        addFormField(panel, "🔑 Password:", passwordField, gbc);
        addFormField(panel, "🎭 Nickname:", nicknameField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        panel.add(browseButton, gbc);

        gbc.gridx = 1;
        panel.add(registerButton, gbc);

        // **Login Button Added**
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(loginButton, gbc);

        gbc.gridy++;
        panel.add(exitButton, gbc);

        add(panel);

        // **Actions**
        registerButton.addActionListener(this::registerAction);
        browseButton.addActionListener(this::browsePicture);

        // ✅ Fix Login Button Navigation
        loginButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                new LoginFrame(); // Open LoginFrame
                setVisible(false); // Hide RegisterFrame instead of closing it
            });
        });
    }

    private void addFormField(JPanel panel, String labelText, JComponent field, GridBagConstraints gbc) {
        JLabel label = new JLabel(labelText);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("SansSerif", Font.BOLD, 15));

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        panel.add(label, gbc);

        gbc.gridx = 1;
        panel.add(field, gbc);
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

    private void browsePicture(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            JOptionPane.showMessageDialog(this, "✅ Profile Picture Selected!");
        }
    }

    private void registerAction(ActionEvent e) {
        try {
            String email = emailField.getText();
            String username = usernameField.getText();
            String password = String.valueOf(passwordField.getPassword());
            String nickname = nicknameField.getText();

            ChatService service = (ChatService) Naming.lookup("rmi://localhost:1099/ChatService");
            service.registerUser(email, username, password, nickname, "");

            JOptionPane.showMessageDialog(this, "✅ Registration successful!");
            SwingUtilities.invokeLater(() -> new LoginFrame());
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "❌ Registration failed!");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RegisterFrame::new);
    }
}
