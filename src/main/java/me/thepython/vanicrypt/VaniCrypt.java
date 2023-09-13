package me.thepython.vanicrypt;

import me.thepython.vanicrypt.socket.SocketHandler;

public class VaniCrypt {

    public static VaniCrypt INSTANCE;

    public VaniCryptGUI mainGui;
    public VaniCryptLogin loginGui;
    public SocketHandler socketHandler;
    public String username;

    public static void main(String[] args) {
        INSTANCE = new VaniCrypt();
        INSTANCE.socketHandler = new SocketHandler();
        if(INSTANCE.socketHandler.connect()) {
            INSTANCE.loginGui = new VaniCryptLogin();
        }
    }

}
