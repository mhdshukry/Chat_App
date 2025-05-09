package dao;

import model.Chat;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HibernateUtil;

public class ChatDao {

    private static final Logger logger = LoggerFactory.getLogger(ChatDao.class);

    public void saveChat(Chat chat) {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(chat);

            if (transaction.getStatus() == TransactionStatus.ACTIVE) {
                transaction.commit();
                logger.info("✅ Chat record saved successfully.");
            }
        } catch (Exception e) {
            if (transaction != null && transaction.getStatus() == TransactionStatus.ACTIVE) {
                transaction.rollback();
                logger.error("❌ Transaction rolled back due to an error.");
            }
            logger.error("🚨 Error saving chat record: ", e);
        }
    }
}
