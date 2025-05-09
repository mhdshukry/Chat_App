package server;

import dao.ChatDao;
import dao.UserDao;
import model.Chat;
import model.User;
import observer.ChatObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.util.*;

public class ChatServiceImpl extends UnicastRemoteObject implements ChatService {

    private static final Logger logger = LoggerFactory.getLogger(ChatServiceImpl.class);
    private final List<ChatObserver> observers = new ArrayList<>();
    private final List<String> chatLog = new ArrayList<>();
    private final Set<String> subscribedUsers = new HashSet<>();
    private LocalDateTime chatStartTime;

    protected ChatServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public void registerUser(String email, String username, String password, String nickname, String profilePicture) {
        User user = new User(email, username, password, nickname, profilePicture, "user");

        try {
            new UserDao().saveUser(user);
            logger.info("✅ User registered successfully: {}", username);
        } catch (Exception e) {
            logger.error("❌ Error registering user: {}", username, e);
        }
    }

    @Override
    public void startChat() {
        chatStartTime = LocalDateTime.now();
        String startMessage = "Chat started at: " + chatStartTime;
        chatLog.add(startMessage);

        notifyObserversSafe(startMessage);
        logger.info(startMessage);
    }

    @Override
    public void sendMessage(String nickname, String message) {
        String formattedMessage = nickname + ": " + message;
        chatLog.add(formattedMessage);

        notifyObserversSafe(formattedMessage);
        logger.info("💬 Message sent: {}", formattedMessage);
    }

    @Override
    public void leaveChat(String nickname) {
        String leaveMessage = nickname + " left: " + LocalDateTime.now();
        chatLog.add(leaveMessage);

        notifyObserversSafe(leaveMessage);
        subscribedUsers.remove(nickname);

        if (subscribedUsers.isEmpty()) {
            endChat();
        }

        logger.info("🚪 User left the chat: {}", nickname);
    }

    private void endChat() {
        String endMessage = "Chat stopped at: " + LocalDateTime.now();
        chatLog.add(endMessage);

        notifyObserversSafe(endMessage);
        saveChatToFile();

        logger.info("📌 Chat session ended.");
    }

    private void saveChatToFile() {
        try {
            String fileName = "chat_" + System.currentTimeMillis() + ".txt";
            Path filePath = Paths.get(fileName);
            Files.write(filePath, chatLog, StandardCharsets.UTF_8);
            logger.info("💾 Chat saved to file: {}", filePath.toAbsolutePath());

            Chat chat = new Chat(chatStartTime, LocalDateTime.now(), filePath.toString());
            new ChatDao().saveChat(chat);

        } catch (IOException e) {
            logger.error("❌ Error saving chat to file.", e);
        }
    }

    public boolean login(String email, String password) {
        User user = new UserDao().getUserByEmail(email);
        if (user != null && password.equals(user.getPassword())) {
            logger.info("✅ User '{}' logged in successfully.", email);
            return "admin".equals(user.getRole());
        }

        logger.warn("🔒 Invalid login attempt for '{}'", email);
        return false;
    }

    @Override
    public void registerObserver(ChatObserver observer) {
        observers.add(observer);
        notifyObserversSafe("A new user has joined the chat.");
        logger.info("👥 Observer registered.");
    }

    @Override
    public void removeObserver(ChatObserver observer) {
        observers.remove(observer);
        logger.info("👥 Observer removed.");
    }

    @Override
    public void subscribeUserToChat(String email) throws RemoteException {
        subscribedUsers.add(email);
        logger.info("✅ User subscribed: {}", email);
    }

    @Override
    public void unsubscribeUserFromChat(String email) throws RemoteException {
        subscribedUsers.remove(email);
        logger.info("🔄 User unsubscribed: {}", email);
    }

    @Override
    public Set<String> listSubscribedUsers() throws RemoteException {
        return subscribedUsers;
    }

    private void notifyObserversSafe(String message) {
        for (ChatObserver observer : observers) {
            try {
                observer.update(message);
            } catch (RemoteException e) {
                logger.error("❌ Error notifying observer.", e);
            }
        }
    }

    public String getNicknameByEmail(String email) {
        return new UserDao().getNickNameByEmail(email);
    }
}
