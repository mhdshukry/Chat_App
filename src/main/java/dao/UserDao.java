package dao;

import model.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HibernateUtil;

public class UserDao {

    private static final Logger logger = LoggerFactory.getLogger(UserDao.class);

    public void saveUser(User user) {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(user);

            if (transaction.getStatus() == TransactionStatus.ACTIVE) {
                transaction.commit();
                logger.info("✅ User record saved successfully.");
            }
        } catch (Exception e) {
            if (transaction != null && transaction.getStatus() == TransactionStatus.ACTIVE) {
                transaction.rollback();
                logger.error("❌ Transaction rolled back due to an error.");
            }
            logger.error("🚨 Error saving user record: ", e);
        }
    }

    public User getUserByEmail(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM User WHERE email = :email", User.class)
                    .setParameter("email", email)
                    .uniqueResult();
        } catch (Exception e) {
            logger.error("❌ Error retrieving user by email: ", e);
            return null;
        }
    }

    public String getNickNameByEmail(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            User user = session.createQuery("FROM User WHERE email = :email", User.class)
                    .setParameter("email", email)
                    .uniqueResult();

            return user != null ? user.getNickname() : null;
        } catch (Exception e) {
            logger.error("❌ Error retrieving nickname by email: ", e);
            return null;
        }
    }
}
