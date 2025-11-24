package ui;

import models.User;

import javax.swing.*;
import java.awt.*;

public class CheckoutPage extends JFrame {

    public CheckoutPage(User u) {
        setTitle("Checkout");
        setSize(300, 150);
        setLocationRelativeTo(null);

        add(new JLabel("Checkout for user: " + u.username), BorderLayout.CENTER);

        setVisible(true);
    }
}
