package client;

import observer.ChatObserver;
import server.ChatService;

import javax.swing.*;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ChatClient extends UnicastRemoteObject implements ChatObserver {

    private final String userEmail;
    private final String nickname;
    private ChatWindow chatWindow;

    protected ChatClient(String userEmail, String nickname) throws RemoteException {
        super();
        this.userEmail = userEmail;
        this.nickname = nickname;

        // Start chat window
        SwingUtilities.invokeLater(() -> {
            chatWindow = new ChatWindow(this, nickname);
        });
    }

    public void startClient() {
        try {
            ChatService chatService = (ChatService) Naming.lookup("rmi://localhost:1099/ChatService");
            chatService.registerObserver(this);
            chatService.subscribeUserToChat(userEmail);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error connecting to chat server!");
        }
    }

    @Override
    public void update(String message) throws RemoteException {
        if (chatWindow != null) {
            SwingUtilities.invokeLater(() -> chatWindow.appendMessage(message));
        }
    }
}
