package me.thepython.vanicrypt;

import me.thepython.vanicrypt.socket.user.User;

import javax.swing.*;
import java.awt.*;

public class VaniCryptGUI extends JFrame {

    public JTextArea messagesArea;
    private JScrollPane messagesScrollPane;
    private JTextField textField;
    private JPanel bottomHolder;
    private JButton sendButton;
    public DefaultListModel<User> listModel;
    public JList<User> userList;
    private JScrollPane usersScrollPane;


    public VaniCryptGUI() {
        this.setTitle("VaniCrypt");
        this.setSize(1024, 576);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        listModel = new DefaultListModel<>();
        userList = new JList<>(listModel);
        usersScrollPane = new JScrollPane(userList);
        add(usersScrollPane);
        userList.setCellRenderer(new UserCellRenderer());

        userList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                User selectedValue = userList.getSelectedValue();
                String text = "";
                if(selectedValue != null) {
                    for (String message : selectedValue.getMessages()) {
                        text += message + "\n";
                    }
                }
                messagesArea.setText(text);
            }
        });

        messagesArea = new JTextArea();
        messagesArea.setBackground(new Color(240, 240, 240));
        messagesArea.setLineWrap(true);
        messagesArea.setWrapStyleWord(true);
        messagesArea.setEditable(false);
        messagesScrollPane = new JScrollPane(messagesArea);
        messagesArea.setPreferredSize(new Dimension(720, messagesScrollPane.getHeight()));
        add(messagesScrollPane, BorderLayout.EAST);

        bottomHolder = new JPanel();
        bottomHolder.setBackground(Color.white);
        bottomHolder.setLayout(new FlowLayout(FlowLayout.RIGHT));

        textField = new JTextField();
        textField.setBackground(new Color(220, 220, 220));
        textField.setPreferredSize(new Dimension(620, 20));
        bottomHolder.add(textField);

        sendButton = new JButton("Send");
        sendButton.setFont(new Font("Arial", Font.BOLD, 16));
        sendButton.setBackground(new Color(180, 180, 180));
        sendButton.setPreferredSize(new Dimension(100, 20));
        bottomHolder.add(sendButton);

        sendButton.addActionListener(e -> {
            if(userList.getSelectedValue() != null && textField.getText().length() > 0 && textField.getText().length() < 2048) {
                userList.getSelectedValue().sendMessage(textField.getText());
                textField.setText("");
            }
        });

        add(bottomHolder, BorderLayout.SOUTH);

        this.setVisible(true);
    }

    class UserCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            // Customize cell rendering here
            Component component = super.getListCellRendererComponent(list, ((User)value).username, index, isSelected, cellHasFocus);
            // Modify appearance as needed
            return component;
        }
    }

}
