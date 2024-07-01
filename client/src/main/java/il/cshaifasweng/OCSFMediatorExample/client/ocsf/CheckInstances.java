package il.cshaifasweng.OCSFMediatorExample.client.ocsf;

import il.cshaifasweng.OCSFMediatorExample.client.ocsf.*;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
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
        configuration.setProperty("hibernate.connection.url", "jdbc:mysql://localhost:3306/MyFirstDataBase?serverTimezone=Asia/Jerusalem");
        configuration.setProperty("hibernate.connection.username", "root");
        configuration.setProperty("hibernate.connection.password", "20danny05");
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
            Theater theater = new Theater(i+1);
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

    private static List<Branch> getAllBranches() throws Exception{
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Branch> query = builder.createQuery(Branch.class);
        query.from(Branch.class);
        List<Branch> data = session.createQuery(query).getResultList();
        return data;
    }

    private static List<Seat> getAllSeats() throws Exception{
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Seat> query = builder.createQuery(Seat.class);
        query.from(Seat.class);
        List<Seat> data = session.createQuery(query).getResultList();
        return data;
    }

    private static List<Theater> getAllTheaters() throws Exception{
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Theater> query = builder.createQuery(Theater.class);
        query.from(Theater.class);
        List<Theater> data = session.createQuery(query).getResultList();
        return data;
    }



    private static void printAllBranches() throws Exception {
        List<Branch> branches = getAllBranches();
        for(Branch branch : branches){
            System.out.print("Branch location: " + branch.getLocation());

            System.out.println('\n');

        }
    }

    private static void printAllSeats() throws Exception {
        List<Seat> seats = getAllSeats();
        for(Seat seat : seats){
            System.out.print(seat.getId());
            System.out.println('\n');
        }
    }

    private static void printAllTheaters() throws Exception {
        List<Theater> theaters = getAllTheaters();
        for(Theater theater : theaters){
            System.out.print(theater.getTheaterID());
            System.out.println('\n');
        }
    }



    public static void main( String[] args ) {

        try {
            SessionFactory sessionFactory = getSessionFactory();
            session = sessionFactory.openSession();
            session.beginTransaction();

            Theater[] theaters = generateTheaters();
            Branch[] branches = generateBranches(theaters);
            generateScreeningTimes(branches[0], theaters[0]);

            printAllBranches();
            printAllSeats();

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