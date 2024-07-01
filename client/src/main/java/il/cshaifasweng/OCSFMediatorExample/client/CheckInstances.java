package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.client.ocsf.*;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class CheckInstances {

    private static Session session;
    private static SessionFactory sessionFactory;


    private static SessionFactory getSessionFactory() throws HibernateException {
        Configuration configuration = new Configuration();

        // Add ALL of your entities here. You can also try adding a whole package.
        configuration.addAnnotatedClass(Branch.class);
        configuration.addAnnotatedClass(ScreeningTime.class);
        configuration.addAnnotatedClass(InTheaterMovie.class);
        configuration.addAnnotatedClass(Seat.class);
        configuration.addAnnotatedClass(Theater.class);

        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
        configuration.setProperty("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver");
        configuration.setProperty("hibernate.connection.url", "jdbc:mysql://localhost:3306/ProjectDataBase?serverTimezone=UTC");
        configuration.setProperty("hibernate.connection.username", "root");
        configuration.setProperty("hibernate.connection.password", "Gamal385");
        configuration.setProperty("hibernate.show_sql", "true");
        configuration.setProperty("hibernate.hbm2ddl.auto", "create");

        ServiceRegistry serviceRegistry = new
                StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties())
                .build();

        return configuration.buildSessionFactory(serviceRegistry);
    }

private static Theater[] generateTheaters() throws Exception {
        Theater[] theaters = new Theater[10];
      for(int i=0;i<10;i++){
              Theater theater = new Theater();
          theaters[i]=theater;
          session.save(theater);
          session.flush();
      }
      return theaters;
}
//Problem: creating branches with the same theaters belong to them(by Id).
private static Branch[] generateBranches(Theater[] theaters) {
        Branch[] branches = new Branch[3];
        for(int i=0;i<3;i++){
            Branch branch = new Branch("Haifa"+i);
            for (Theater theater : theaters) {
                branch.addTheaterToList(theater);
            }
            branches[i]=branch;
            session.save(branch);
            session.flush();
        }
        return branches;
}

private static void generateScreeningTimes(Branch branch,Theater theater) throws Exception {
    List<String> mainActors = new ArrayList<String>();
    mainActors.add("Zohar");
    mainActors.add("Dan");
    InTheaterMovie movie = new InTheaterMovie("Mad Max", "Amit Perry", mainActors, "Good movie", "pic");
    ScreeningTime screeningTime = new ScreeningTime(branch, ScreeningTime.Day.MONDAY, LocalTime.of(20, 00), theater);
    movie.addScreeningTime(screeningTime);
    branch.addInTheaterMovieToList(movie);
    session.save(movie);
    session.flush();
}




public static void main( String[] args ) {

    try {
        SessionFactory sessionFactory = getSessionFactory();
        session = sessionFactory.openSession();
        session.beginTransaction();

        Theater[] theaters = generateTheaters();
        Branch[] branches = generateBranches(theaters);
        generateScreeningTimes(branches[0], theaters[0]);

        session.getTransaction().commit(); // Save everything.

    } catch (Exception exception) {
        if (session != null) {
            session.getTransaction().rollback();
        }
        System.err.println("An error occurred, changes have been rolled back.");
        exception.printStackTrace();
    } finally {
        if (session != null) {
            session.close();
        }
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}
}
