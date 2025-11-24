package ui;

import dao.UserDAO;
import models.User;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

public class LoginPage extends JFrame {

    private JTextField tfUser;
    private JPasswordField tfPass;
    private UserDAO userDAO = new UserDAO();

    public LoginPage() {
        setTitle("Login Page");
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 6-Image Grid Background
        SixImageGridPanel bgPanel = new SixImageGridPanel("src/ui/images/bg/");
        bgPanel.setLayout(new GridBagLayout());

        // Login Box
        JPanel loginBox = createLoginBox();

        bgPanel.add(loginBox);
        setContentPane(bgPanel);

        setResizable(true);
        setVisible(true);
    }

    private JPanel createLoginBox() {
        JPanel box = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Darker shadow for legibility over the clear images
                g2.setColor(new Color(0, 0, 0, 100));
                g2.fillRoundRect(5, 5, getWidth() - 5, getHeight() - 5, 25, 25);

                // White box
                g2.setColor(new Color(255, 255, 255, 250));
                g2.fillRoundRect(0, 0, getWidth() - 10, getHeight() - 10, 25, 25);
                g2.dispose();
            }
        };

        box.setOpaque(false);
        box.setPreferredSize(new Dimension(380, 450));
        box.setLayout(new BorderLayout(0, 20));
        box.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Welcome Back", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(new Color(51, 51, 51));

        JLabel subtitleLabel = new JLabel("Please login to continue", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(120, 120, 120));

        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);

        // Form
        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);

        // User
        tfUser = createStyledTextField();
        JPanel userPanel = createFieldPanel("Username", tfUser);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        formPanel.add(userPanel, gbc);

        // Pass
        tfPass = createStyledPasswordField();
        JPanel passPanel = createFieldPanel("Password", tfPass);
        gbc.gridy = 1;
        formPanel.add(passPanel, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new GridLayout(2, 1, 0, 12));

        JButton btnLogin = createStyledButton("Login", new Color(66, 133, 244));
        btnLogin.addActionListener(e -> doLogin());

        JButton btnSignup = createStyledButton("Create Account", new Color(52, 168, 83));
        // OPEN SignUpPage when clicked
        btnSignup.addActionListener(e -> {
            // Open signup page and close login if you don't want both open.
            new SignUpPage();
            dispose();
        });

        buttonPanel.add(btnLogin);
        buttonPanel.add(btnSignup);

        gbc.gridy = 2;
        gbc.insets = new Insets(20, 0, 0, 0);
        formPanel.add(buttonPanel, gbc);

        box.add(headerPanel, BorderLayout.NORTH);
        box.add(formPanel, BorderLayout.CENTER);

        return box;
    }

    private JPanel createFieldPanel(String labelText, JComponent field) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BorderLayout(0, 5));
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(new Color(70, 70, 70));
        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField(20);
        styleInputField(field);
        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField(20);
        styleInputField(field);
        return field;
    }

    private void styleInputField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        field.setPreferredSize(new Dimension(280, 45));
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) g2.setColor(bgColor.darker());
                else if (getModel().isRollover()) g2.setColor(bgColor.brighter());
                else g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setPreferredSize(new Dimension(280, 45));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void doLogin() {
        String u = tfUser.getText().trim();
        String p = new String(tfPass.getPassword());
        if (u.isEmpty() || p.isEmpty()) {
            showStyledDialog("Please fill in all fields", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            User logged = userDAO.login(u, p);
            if (logged != null) {
                // Open MainFrame and close LoginPage
                // MainFrame constructor expects a String username (as per the MainFrame you provided)
                new MainFrame(logged.username);
                dispose();
            } else {
                showStyledDialog("Invalid Credentials", "Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            System.out.println("DAO Error: " + e.getMessage());
            e.printStackTrace();
            showStyledDialog("An error occurred. See console for details", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showStyledDialog(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    // ==================== 6-IMAGE GRID BACKGROUND PANEL ====================
    class SixImageGridPanel extends JPanel {

        private java.util.List<Image> images = new ArrayList<>();
        private final int TOTAL_IMAGES = 6;
        private final int COLS = 3;
        private final int ROWS = 2;

        public SixImageGridPanel(String folder) {
            setBackground(Color.DARK_GRAY);
            loadImages(folder);
        }

        private void loadImages(String folder) {
            File f = new File(folder);
            if (!f.exists() || !f.isDirectory()) {
                System.err.println("Folder not found: " + folder);
                return;
            }

            File[] files = f.listFiles((d, name) -> {
                String lower = name.toLowerCase();
                return lower.endsWith(".jpg") || lower.endsWith(".png") || lower.endsWith(".jpeg");
            });

            if (files != null) {
                // Load found images
                for (File imgFile : files) {
                    if (images.size() >= TOTAL_IMAGES) break; // Stop after 6 distinct images
                    images.add(new ImageIcon(imgFile.getAbsolutePath()).getImage());
                }

                // If we have fewer than 6 images, repeat existing ones to fill the grid
                while (images.size() < TOTAL_IMAGES && !images.isEmpty()) {
                    images.add(images.get(images.size() % files.length));
                }
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (images.isEmpty()) return;

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            int w = getWidth();
            int h = getHeight();

            // Calculate base cell sizes
            int cellW = w / COLS;
            int cellH = h / ROWS;

            for (int i = 0; i < TOTAL_IMAGES; i++) {
                if (i >= images.size()) break;

                // Calculate grid position (Row 0: 0,1,2 | Row 1: 3,4,5)
                int col = i % COLS;
                int row = i / COLS;

                int x = col * cellW;
                int y = row * cellH;

                // Determine Width/Height for this specific cell
                int currentW = (col == COLS - 1) ? (w - x) : cellW;
                int currentH = (row == ROWS - 1) ? (h - y) : cellH;

                // Draw the image stretched exactly to this cell
                g2.drawImage(images.get(i), x, y, currentW, currentH, null);
            }

            g2.dispose();
        }
    }

    public static void main(String[] args) {
        new LoginPage();
    }
}
