package me.thepython.vanicrypt.socket.user;

import me.thepython.vanicrypt.VaniCrypt;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class User {

    public String username;
    private List<String> messages = new CopyOnWriteArrayList<>();

    public User(String username) {
        this.username = username;
        this.messages.clear();
    }

    public void sendMessage(String message) {
        VaniCrypt.INSTANCE.socketHandler.sendData("=MESSAGE " + username + " " + message);
        messages.add(VaniCrypt.INSTANCE.username + ": " + message);
        if(VaniCrypt.INSTANCE.mainGui.userList.getSelectedValue() != null) {
            String text = "";
            for (String msg : VaniCrypt.INSTANCE.mainGui.userList.getSelectedValue().getMessages()) {
                text += msg + "\n";
            }
            VaniCrypt.INSTANCE.mainGui.messagesArea.setText(text);
        }
    }

    public List<String> getMessages() {
        return messages;
    }

}
