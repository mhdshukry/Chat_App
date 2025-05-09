package server;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChatServer {
    private static final Logger logger = LoggerFactory.getLogger(ChatServer.class);

    public static void main(String[] args) {
        try {
            logger.info("🔧 Initializing RMI Registry on port 1099...");
            LocateRegistry.createRegistry(1099);

            ChatService chatService = new ChatServiceImpl() {
                @Override
                public Set<String> listSubscribedUsers() throws RemoteException {
                    return null;
                }
            };
            Naming.rebind("rmi://localhost:1099/ChatService", chatService);

            logger.info("✅ Server is running successfully on port 1099!");
        } catch (Exception e) {
            logger.error("❌ Server startup failed: ", e);
        }
    }
}
