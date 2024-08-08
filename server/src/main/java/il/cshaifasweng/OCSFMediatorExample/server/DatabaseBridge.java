package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.server.dataClasses.*;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.NativeQuery;
import org.hibernate.service.ServiceRegistry;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;


/*
* DatabaseBridge is a singleton class which manages the connection between our code and the DB
* */

public class DatabaseBridge {
    private static Session session = null;

    private static DatabaseBridge instance;

    private static SessionFactory getSessionFactory() throws HibernateException {
        Configuration configuration = new Configuration();

        // database configurations
        configuration.addAnnotatedClass(Branch.class);
        configuration.addAnnotatedClass(ScreeningTime.class);
        configuration.addAnnotatedClass(InTheaterMovie.class);
        configuration.addAnnotatedClass(Seat.class);
        configuration.addAnnotatedClass(Theater.class);
        configuration.addAnnotatedClass(ComingSoonMovie.class);
        configuration.addAnnotatedClass(HomeMovie.class);
        configuration.addAnnotatedClass(Customer.class);
        configuration.addAnnotatedClass(Link.class);
        configuration.addAnnotatedClass(SubscriptionCard.class);
        configuration.addAnnotatedClass(Ticket.class);
        configuration.addAnnotatedClass(Purchase.class);
        configuration.addAnnotatedClass(Complaint.class);
        configuration.addAnnotatedClass(Purchase.class);
        configuration.addAnnotatedClass(Complaint.class);
        configuration.addAnnotatedClass(ServiceEmployee.class);
        configuration.addAnnotatedClass(CompanyManager.class);
        configuration.addAnnotatedClass(BranchManager.class);
        configuration.addAnnotatedClass(ContentManager.class);
        configuration.addAnnotatedClass(AbstractProduct.class);

        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
        configuration.setProperty("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver");
        configuration.setProperty("hibernate.connection.url", "jdbc:mysql://localhost:3306/projectdatabase?serverTimezone=Asia/Jerusalem");
        configuration.setProperty("hibernate.connection.username", "root");
        configuration.setProperty("hibernate.connection.password", "amit1717");
        configuration.setProperty("hibernate.show_sql", "true");
        configuration.setProperty("hibernate.hbm2ddl.auto", "update");

        ServiceRegistry serviceRegistry = new
                StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties())
                .build();

        return configuration.buildSessionFactory(serviceRegistry);
    }

    public static <T> List<T> getAll(Class<T> entityClass, boolean forceRefresh) {
        if(forceRefresh) session.clear();

        // set session to read only
        session.setDefaultReadOnly(true);

        // query DB
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(entityClass);
        query.from(entityClass);

        // get list of extracted type
        List<T> data = session.createQuery(query).getResultList();

        if (forceRefresh) {
            // refresh each entity
            for (T entity : data)
                session.refresh(entity);
        }

        // set to not read only
        session.setDefaultReadOnly(false);
        return data;
    }

    public static <T> List<T> executeNativeQuery(String sqlQuery, Class<T> resultClass, Object... params) {
        try {
            session.clear();
            NativeQuery<T> query = session.createNativeQuery(sqlQuery, resultClass);
            for (int i = 0; i < params.length; i++) {
                query.setParameter(i + 1, params[i]);
            }
            List<T> result = query.getResultList();
            return result;
        } catch (Exception e) {
            System.err.println("An error occurred in executeNativeQuery: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static <T> void addInstance(T newInstance) {
        try {
            session.beginTransaction();
            session.save(newInstance);
            session.flush();
            session.getTransaction().commit();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static <T> void updateEntity(T entity) {
        try {
            // start transaction
            session.beginTransaction();
            // update the entity
            session.clear();
            session.update(entity);
            session.flush();
            // commit transaction
            session.getTransaction().commit();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static synchronized DatabaseBridge getInstance() {
        // if first access to class
        if (instance == null) {
            // create instance
            instance = new DatabaseBridge();
            // get session
            SessionFactory sessionFactory = getSessionFactory();
            session = sessionFactory.openSession();
        }
        return instance;
    }

    private DatabaseBridge() {}
}
