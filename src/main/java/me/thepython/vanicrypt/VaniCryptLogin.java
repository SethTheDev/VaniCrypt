package me.thepython.vanicrypt;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VaniCryptLogin extends JFrame {

    private JTextField usernameField;
    private JButton loginBtn;

    public VaniCryptLogin() {
        this.setTitle("VaniCrypt Login");
        this.setSize(400, 200);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        usernameField = new JTextField();
        usernameField.setToolTipText("Username");
        add(usernameField, BorderLayout.NORTH);

        loginBtn = new JButton("Login");
        loginBtn.setFont(new Font("Arial", Font.BOLD, 16));
        loginBtn.setPreferredSize(new Dimension(120, 70));
        add(loginBtn, BorderLayout.SOUTH);

        loginBtn.addActionListener(e -> {
            VaniCrypt.INSTANCE.socketHandler.sendData("=LOGIN " + usernameField.getText());
            System.out.println(usernameField.getText());
            VaniCrypt.INSTANCE.username = usernameField.getText();
        });

        this.setVisible(true);
    }

}
