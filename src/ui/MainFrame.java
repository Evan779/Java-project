package ui;

import dao.*;
import db.DBConnection;
import models.*;
import service.PriceCalculatorService;
import service.PriceCalculatorService.PriceBreakdown;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends JFrame {
    private RestaurantDAO restaurantDAO = new RestaurantDAO();
    private FoodDAO foodDAO = new FoodDAO();
    private PriceCalculatorService pcs = new PriceCalculatorService();

    private JComboBox<Restaurant> cbRestaurants;
    private DefaultListModel<FoodItem> menuModel = new DefaultListModel<>();
    private JList<FoodItem> listMenu = new JList<>(menuModel);

    private DefaultTableModel cartTableModel = new DefaultTableModel(new Object[]{"Item", "Qty", "Price"}, 0);
    private JTable cartTable = new JTable(cartTableModel);

    private DefaultTableModel searchTableModel = new DefaultTableModel(new Object[]{"Item", "Base Price", "Restaurant"}, 0);
    private JTable searchTable = new JTable(searchTableModel);

    private JTextField tfQty;
    private JTextField tfSearch;
    private JTextField tfCoupon;
    private JLabel lblTotal = new JLabel("₹0.00");
    private JLabel lblSubTotal = new JLabel("₹0.00");
    private JLabel lblPackaging = new JLabel("₹0.00");
    private JLabel lblDelivery = new JLabel("₹0.00");
    private JLabel lblPlatform = new JLabel("₹0.00");
    private JLabel lblSurge = new JLabel("₹0.00");
    private JLabel lblDiscount = new JLabel("₹0.00");
    private JLabel lblGST = new JLabel("₹0.00");
    private String loggedUser;

    public MainFrame(String loggedUser) {
        this.loggedUser = loggedUser;

        setTitle("Evan's Food Delivery App");
        setSize(1400, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 6-Image Grid Background
        SixImageGridPanel bgPanel = new SixImageGridPanel("src/ui/images/bg/");
        bgPanel.setLayout(new BorderLayout(15, 15));
        bgPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top Panel - Header
        JPanel topPanel = createTopPanel();

        // Center Panel - Main Content
        JPanel centerPanel = createCenterPanel();

        bgPanel.add(topPanel, BorderLayout.NORTH);
        bgPanel.add(centerPanel, BorderLayout.CENTER);

        setContentPane(bgPanel);

        loadRestaurants();
        setupListeners();

        setVisible(true);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 80));
                g2.fillRoundRect(3, 3, getWidth() - 3, getHeight() - 3, 20, 20);
                g2.setColor(new Color(255, 255, 255, 245));
                g2.fillRoundRect(0, 0, getWidth() - 6, getHeight() - 6, 20, 20);
                g2.dispose();
            }
        };

        panel.setOpaque(false);
        panel.setLayout(new BorderLayout(15, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // Left side - Welcome
        JLabel welcomeLabel = new JLabel("Welcome, " + loggedUser);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(new Color(51, 51, 51));

        // Right side - Restaurant selection and search
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        controlsPanel.setOpaque(false);

        JLabel lblRest = new JLabel("Restaurant:");
        lblRest.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblRest.setForeground(new Color(70, 70, 70));

        cbRestaurants = new JComboBox<>();
        cbRestaurants.setPreferredSize(new Dimension(200, 35));
        cbRestaurants.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JButton btnLoad = createStyledButton("Load Menu", new Color(66, 133, 244), 120, 35);

        tfSearch = createStyledTextField(250);
        tfSearch.setToolTipText("Search for food items...");

        controlsPanel.add(lblRest);
        controlsPanel.add(cbRestaurants);
        controlsPanel.add(btnLoad);
        controlsPanel.add(new JLabel("  ")); // spacer
        controlsPanel.add(tfSearch);

        panel.add(welcomeLabel, BorderLayout.WEST);
        panel.add(controlsPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 15, 0));
        panel.setOpaque(false);

        // Left Section - Menu
        JPanel leftPanel = createMenuPanel();

        // Right Section - Search & Cart
        JPanel rightPanel = createRightPanel();

        panel.add(leftPanel);
        panel.add(rightPanel);

        return panel;
    }

    private JPanel createMenuPanel() {
        JPanel panel = createStyledPanel("Restaurant Menu");

        JScrollPane scrollPane = new JScrollPane(listMenu);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        listMenu.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        listMenu.setSelectionBackground(new Color(66, 133, 244, 40));

        // Add to cart controls
        JPanel addPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        addPanel.setOpaque(false);

        JLabel lblQty = new JLabel("Quantity:");
        lblQty.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblQty.setForeground(new Color(70, 70, 70));

        tfQty = createStyledTextField(60);
        tfQty.setText("1");

        JButton btnAdd = createStyledButton("Add to Cart", new Color(52, 168, 83), 150, 40);

        addPanel.add(lblQty);
        addPanel.add(tfQty);
        addPanel.add(btnAdd);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(addPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(false);

        // Search Results - smaller section
        JPanel searchPanel = createStyledPanel("Search Results");
        searchPanel.setPreferredSize(new Dimension(0, 200));
        JScrollPane searchScroll = new JScrollPane(searchTable);
        searchScroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        searchTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        searchPanel.add(searchScroll, BorderLayout.CENTER);

        // Cart - larger, more prominent section
        JPanel cartPanel = createStyledPanel("Your Cart 🛒");
        JScrollPane cartScroll = new JScrollPane(cartTable);
        cartScroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        cartTable.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        cartTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        cartTable.setRowHeight(25);

        // Price breakdown and checkout
        JPanel bottomPanel = createPricePanel();

        cartPanel.add(cartScroll, BorderLayout.CENTER);
        cartPanel.add(bottomPanel, BorderLayout.SOUTH);

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(cartPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createPricePanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));

        // Price breakdown
        JPanel priceGrid = new JPanel(new GridLayout(0, 2, 10, 8));
        priceGrid.setOpaque(false);

        Font labelFont = new Font("Segoe UI", Font.PLAIN, 13);
        Font valueFont = new Font("Segoe UI", Font.BOLD, 13);
        Color labelColor = new Color(70, 70, 70);
        Color valueColor = new Color(51, 51, 51);

        String[] labels = {"Subtotal:", "Packaging:", "Delivery:", "Platform:", "Surge:", "GST:", "Discount:", "Total:"};
        JLabel[] valueLabels = {lblSubTotal, lblPackaging, lblDelivery, lblPlatform, lblSurge, lblGST, lblDiscount, lblTotal};

        for (int i = 0; i < labels.length; i++) {
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(i == labels.length - 1 ? new Font("Segoe UI", Font.BOLD, 15) : labelFont);
            lbl.setForeground(labelColor);

            valueLabels[i].setFont(i == labels.length - 1 ? new Font("Segoe UI", Font.BOLD, 16) : valueFont);
            valueLabels[i].setForeground(i == labels.length - 1 ? new Color(52, 168, 83) : valueColor);
            valueLabels[i].setHorizontalAlignment(SwingConstants.RIGHT);

            priceGrid.add(lbl);
            priceGrid.add(valueLabels[i]);
        }

        // Coupon and checkout
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        actionPanel.setOpaque(false);

        JLabel lblCoupon = new JLabel("Coupon:");
        lblCoupon.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblCoupon.setForeground(new Color(70, 70, 70));

        tfCoupon = createStyledTextField(100);

        JButton btnCalc = createStyledButton("Calculate", new Color(66, 133, 244), 100, 35);
        JButton btnCheckout = createStyledButton("Checkout", new Color(234, 67, 53), 120, 40);

        actionPanel.add(lblCoupon);
        actionPanel.add(tfCoupon);
        actionPanel.add(btnCalc);
        actionPanel.add(btnCheckout);

        panel.add(priceGrid, BorderLayout.CENTER);
        panel.add(actionPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createStyledPanel(String title) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 80));
                g2.fillRoundRect(3, 3, getWidth() - 3, getHeight() - 3, 20, 20);
                g2.setColor(new Color(255, 255, 255, 245));
                g2.fillRoundRect(0, 0, getWidth() - 6, getHeight() - 6, 20, 20);
                g2.dispose();
            }
        };

        panel.setOpaque(false);
        panel.setLayout(new BorderLayout(0, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(51, 51, 51));

        // Add item count for cart
        if (title.contains("Cart")) {
            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setOpaque(false);
            headerPanel.add(titleLabel, BorderLayout.WEST);

            final JLabel cartCount = new JLabel("(0 items)");
            cartCount.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            cartCount.setForeground(new Color(120, 120, 120));
            headerPanel.add(cartCount, BorderLayout.EAST);

            // Update cart count when items are added
            cartTableModel.addTableModelListener(e -> {
                int count = cartTableModel.getRowCount();
                cartCount.setText("(" + count + " item" + (count != 1 ? "s" : "") + ")");
                if (count > 0) {
                    cartCount.setForeground(new Color(52, 168, 83));
                } else {
                    cartCount.setForeground(new Color(120, 120, 120));
                }
            });

            panel.add(headerPanel, BorderLayout.NORTH);
        } else {
            panel.add(titleLabel, BorderLayout.NORTH);
        }

        return panel;
    }

    private JTextField createStyledTextField(int width) {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        field.setPreferredSize(new Dimension(width, 35));
        return field;
    }

    private JButton createStyledButton(String text, Color bgColor, int width, int height) {
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
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setPreferredSize(new Dimension(width, height));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void setupListeners() {
        // Load menu button
        for (Component comp : ((JPanel)((JPanel)((SixImageGridPanel)getContentPane()).getComponent(0)).getComponent(1)).getComponents()) {
            if (comp instanceof JButton && ((JButton)comp).getText().equals("Load Menu")) {
                ((JButton)comp).addActionListener(e -> loadMenuForSelectedRestaurant());
                break;
            }
        }

        // Search as you type
        tfSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { doSearch(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { doSearch(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { doSearch(); }
        });

        // Add to cart
        for (Component comp : findComponentByType(getContentPane(), JButton.class, "Add to Cart")) {
            if (comp instanceof JButton) {
                ((JButton)comp).addActionListener(e -> addToCart());
                break;
            }
        }

        // Calculate button
        for (Component comp : findComponentByType(getContentPane(), JButton.class, "Calculate")) {
            if (comp instanceof JButton) {
                ((JButton)comp).addActionListener(e -> updateTotal());
                break;
            }
        }

        // Checkout button
        for (Component comp : findComponentByType(getContentPane(), JButton.class, "Checkout")) {
            if (comp instanceof JButton) {
                ((JButton)comp).addActionListener(e -> doCheckout());
                break;
            }
        }
    }

    private List<Component> findComponentByType(Container container, Class<?> type, String text) {
        List<Component> found = new ArrayList<>();
        for (Component comp : container.getComponents()) {
            if (type.isInstance(comp)) {
                if (text == null || (comp instanceof JButton && ((JButton)comp).getText().equals(text))) {
                    found.add(comp);
                }
            }
            if (comp instanceof Container) {
                found.addAll(findComponentByType((Container)comp, type, text));
            }
        }
        return found;
    }

    private void loadRestaurants() {
        try {
            cbRestaurants.removeAllItems();
            for (Restaurant r : restaurantDAO.getOpenRestaurants()) {
                cbRestaurants.addItem(r);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed loading restaurants: " + ex.getMessage());
        }
    }

    private void loadMenuForSelectedRestaurant() {
        Restaurant r = (Restaurant) cbRestaurants.getSelectedItem();
        if (r == null) return;
        try {
            menuModel.clear();
            for (FoodItem f : foodDAO.getMenuByRestaurant(r.restaurantId)) {
                menuModel.addElement(f);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed loading menu: " + ex.getMessage());
        }
    }

    private void doSearch() {
        String text = tfSearch.getText().trim();
        searchTableModel.setRowCount(0);

        if (text.isEmpty()) return;

        String sql = "SELECT f.item_name, f.base_price, r.restaurant_name " +
                "FROM food_master f JOIN restaurant_master r " +
                "ON f.restaurant_id = r.restaurant_id " +
                "WHERE f.item_name LIKE ?";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "%" + text + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                searchTableModel.addRow(new Object[]{
                        rs.getString(1), rs.getDouble(2), rs.getString(3)
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void addToCart() {
        FoodItem selected = listMenu.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select an item first", "No Item Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int qty = 1;
        try {
            qty = Integer.parseInt(tfQty.getText());
            if (qty <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity must be greater than 0", "Invalid Quantity", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid quantity", "Invalid Quantity", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Add item to cart
        cartTableModel.addRow(new Object[]{selected.itemName, qty, selected.basePrice});

        // Show success message
        showCartNotification(selected.itemName + " added to cart!");

        // Update total
        updateTotal();

        // Reset quantity field
        tfQty.setText("1");
    }

    private void showCartNotification(String message) {
        // Create a temporary label to show the notification
        JLabel notification = new JLabel(message);
        notification.setFont(new Font("Segoe UI", Font.BOLD, 13));
        notification.setForeground(new Color(52, 168, 83));
        notification.setHorizontalAlignment(SwingConstants.CENTER);

        // Flash effect using a timer
        Timer timer = new Timer(2000, e -> notification.setText(""));
        timer.setRepeats(false);
        timer.start();
    }

    private void updateTotal() {
        try {
            double total = 0, subtotal = 0, packaging = 0, delivery = 0;
            double platform = 0, surge = 0, discount = 0, gst = 0;

            String coupon = tfCoupon.getText().trim();
            Restaurant r = (Restaurant) cbRestaurants.getSelectedItem();
            if (r == null) return;

            for (int i = 0; i < cartTableModel.getRowCount(); i++) {
                String name = (String) cartTableModel.getValueAt(i, 0);
                int qty = (int) cartTableModel.getValueAt(i, 1);
                List<FoodItem> menu = foodDAO.getMenuByRestaurant(r.restaurantId);
                FoodItem matched = null;
                for (FoodItem fi : menu) {
                    if (fi.itemName.equals(name)) {
                        matched = fi;
                        break;
                    }
                }
                if (matched != null) {
                    PriceBreakdown pb = pcs.calculatePrice(matched.itemId, qty, coupon);
                    total += pb.total;
                    subtotal += pb.subtotal;
                    packaging += pb.packaging;
                    delivery += pb.delivery;
                    platform += pb.platform;
                    surge += pb.surge;
                    discount += pb.discount;
                    gst += pb.gst;
                }
            }

            lblSubTotal.setText(String.format("₹%.2f", subtotal));
            lblPackaging.setText(String.format("₹%.2f", packaging));
            lblDelivery.setText(String.format("₹%.2f", delivery));
            lblPlatform.setText(String.format("₹%.2f", platform));
            lblSurge.setText(String.format("₹%.2f", surge));
            lblDiscount.setText(String.format("₹%.2f", discount));
            lblGST.setText(String.format("₹%.2f", gst));
            lblTotal.setText(String.format("₹%.2f", total));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doCheckout() {
        if (cartTableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Cart is empty");
            return;
        }

        try {
            double grandTotal = 0;
            String coupon = tfCoupon.getText().trim();
            Restaurant r = (Restaurant) cbRestaurants.getSelectedItem();

            for (int i = 0; i < cartTableModel.getRowCount(); i++) {
                String name = (String) cartTableModel.getValueAt(i, 0);
                int qty = (int) cartTableModel.getValueAt(i, 1);
                List<FoodItem> menu = foodDAO.getMenuByRestaurant(r.restaurantId);
                FoodItem matched = null;
                for (FoodItem fi : menu) {
                    if (fi.itemName.equals(name)) {
                        matched = fi;
                        break;
                    }
                }
                if (matched != null) {
                    PriceBreakdown pb = pcs.calculatePrice(matched.itemId, qty, coupon);
                    grandTotal += pb.total;
                }
            }

            JOptionPane.showMessageDialog(this,
                    "Order placed successfully!\n\nCustomer: " + loggedUser +
                            "\nTotal Amount: ₹" + String.format("%.2f", grandTotal),
                    "Order Confirmed", JOptionPane.INFORMATION_MESSAGE);

            cartTableModel.setRowCount(0);
            updateTotal();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error during checkout: " + ex.getMessage());
        }
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
                for (File imgFile : files) {
                    if (images.size() >= TOTAL_IMAGES) break;
                    images.add(new ImageIcon(imgFile.getAbsolutePath()).getImage());
                }
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
            int cellW = w / COLS;
            int cellH = h / ROWS;

            for (int i = 0; i < TOTAL_IMAGES; i++) {
                if (i >= images.size()) break;
                int col = i % COLS;
                int row = i / COLS;
                int x = col * cellW;
                int y = row * cellH;
                int currentW = (col == COLS - 1) ? (w - x) : cellW;
                int currentH = (row == ROWS - 1) ? (h - y) : cellH;
                g2.drawImage(images.get(i), x, y, currentW, currentH, null);
            }
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        new LoginPage();
    }
}