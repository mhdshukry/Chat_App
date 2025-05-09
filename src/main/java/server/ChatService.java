package server;

import observer.ChatObserver;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

public interface ChatService extends Remote {
    void registerUser(String email, String username, String password, String nickname, String profilePicture) throws RemoteException;
    void startChat() throws RemoteException;
    void sendMessage(String nickname, String message) throws RemoteException;
    void leaveChat(String nickname) throws RemoteException;
    boolean login(String email, String password) throws RemoteException;
    void registerObserver(ChatObserver observer) throws RemoteException;
    void removeObserver(ChatObserver observer) throws RemoteException;
    void subscribeUserToChat(String email) throws RemoteException;
    void unsubscribeUserFromChat(String email) throws RemoteException;
    String getNicknameByEmail(String email) throws RemoteException;

    // ✅ New Method: Fetch List of Subscribed Users
    Set<String> listSubscribedUsers() throws RemoteException;
}
