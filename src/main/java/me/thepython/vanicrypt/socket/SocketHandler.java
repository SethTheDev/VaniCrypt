package me.thepython.vanicrypt.socket;

import me.thepython.vanicrypt.VaniCrypt;
import me.thepython.vanicrypt.VaniCryptGUI;
import me.thepython.vanicrypt.socket.user.User;

import javax.crypto.Cipher;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class SocketHandler {

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private PublicKey outPublicKey; // Encrypt outgoing data
    private PrivateKey inPrivateKey; // Decrypt incoming data

    private boolean verified;
    private boolean loggedIn;

    public boolean connect() {
        try {
            socket = new Socket("73.237.204.137", 25030);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            listen();
            exchange();
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public void exchange() throws Exception {
        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
        gen.initialize(2048);
        KeyPair pair = gen.generateKeyPair();
        inPrivateKey = pair.getPrivate();
        out.writeUTF("=PUBLIC " + Base64.getEncoder().encodeToString(pair.getPublic().getEncoded()));
        out.flush();
        if(outPublicKey != null)
            if(!outPublicKey.toString().equals("")){
                verified = true;
            }
    }

    public void sendData(String data) {
        try {
            Cipher encryptCipher = Cipher.getInstance("RSA");
            encryptCipher.init(Cipher.ENCRYPT_MODE, outPublicKey);
            byte[] secretMessageBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] encryptedMessageBytes = encryptCipher.doFinal(secretMessageBytes);
            String encodedMessage = Base64.getEncoder().encodeToString(encryptedMessageBytes);

            out.writeUTF(encodedMessage);
            out.flush();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void listen() {
        Thread dataListener = new Thread(() -> {
            try {
                String data = "";
                while ((data = in.readUTF()) != null) {
                    if(data.startsWith("=PUBLIC ")) {
                        byte[] publicBytes = Base64.getDecoder().decode(data.split(" ")[1]);
                        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
                        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                        PublicKey pubKey = keyFactory.generatePublic(keySpec);
                        this.outPublicKey = pubKey;
                        System.out.println("received public key: " + data.split(" ")[1]);
                        if(inPrivateKey != null)
                            if(!inPrivateKey.toString().equals("")) {
                                verified = true;
                            }
                    }
                    else {
                        Cipher decryptCipher = Cipher.getInstance("RSA");
                        decryptCipher.init(Cipher.DECRYPT_MODE, inPrivateKey);
                        byte[] decryptedMessageBytes = decryptCipher.doFinal(Base64.getDecoder().decode(data));
                        String decryptedMessage = new String(decryptedMessageBytes, StandardCharsets.UTF_8);

                        if(decryptedMessage.startsWith("=LOGIN ")){
                            if(decryptedMessage.split(" ")[1].equals("SUCCESS")) {
                                VaniCrypt.INSTANCE.mainGui = new VaniCryptGUI();
                                VaniCrypt.INSTANCE.loginGui.setVisible(false);
                                loggedIn = true;
                            }
                            else {
                                System.exit(-1);
                            }
                        }
                        else if(decryptedMessage.startsWith("=USERJOIN ")) {
                            VaniCrypt.INSTANCE.mainGui.listModel.addElement(new User(decryptedMessage.split(" ")[1]));
                        }
                        else if(decryptedMessage.startsWith("=USERLEAVE ")) {
                            String name = decryptedMessage.split(" ")[1];
                            for(Object user : VaniCrypt.INSTANCE.mainGui.listModel.toArray()) {
                                if(((User)user).username.equals(name)) {
                                    VaniCrypt.INSTANCE.mainGui.listModel.removeElement(user);
                                }
                            }
                        }
                        else if(decryptedMessage.startsWith("=MESSAGE ")){
                            String from = decryptedMessage.split(" ")[1];
                            String message = decryptedMessage.replaceAll("=MESSAGE " + from + " ", "");
                            for(Object user : VaniCrypt.INSTANCE.mainGui.listModel.toArray()) {
                                if(((User)user).username.equals(from)) {
                                    ((User)user).getMessages().add(from + ": " + message);
                                }
                            }
                            if(VaniCrypt.INSTANCE.mainGui.userList.getSelectedValue() != null) {
                                String text = "";
                                for (String msg : VaniCrypt.INSTANCE.mainGui.userList.getSelectedValue().getMessages()) {
                                    text += msg + "\n";
                                }
                                VaniCrypt.INSTANCE.mainGui.messagesArea.setText(text);
                            }
                        }

                    }
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        });
        dataListener.start();
    }


}
