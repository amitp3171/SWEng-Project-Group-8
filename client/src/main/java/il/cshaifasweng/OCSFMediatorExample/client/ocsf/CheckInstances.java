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
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Random;

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
        configuration.addAnnotatedClass(ComingSoonMovie.class);
        configuration.addAnnotatedClass(HomeMovie.class);

        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
        configuration.setProperty("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver");
        configuration.setProperty("hibernate.connection.url", "jdbc:mysql://localhost:3306/projectdatabase?serverTimezone=Asia/Jerusalem");
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

    private static void generateBranches(Branch[] branches) throws Exception {
        for(Branch branch:branches){
            session.save(branch);
            session.flush();
        }

    }


    private static void generateScreeningTimes(Branch[] branches,Theater[] theaters,InTheaterMovie[] inTheaterMovies) throws Exception {
        Random random = new Random();
        ScreeningTime.Day[] daysOfWeek = ScreeningTime.Day.values();

        for (InTheaterMovie movie:inTheaterMovies){
            for(int i=0;i<5;i++){
                int randomHour1 = random.nextInt(24);
                Theater randomTheater1 = theaters[random.nextInt(theaters.length)];
                Branch randomBranch1 = branches[random.nextInt(branches.length)];
                ScreeningTime.Day randomDay1 = daysOfWeek[random.nextInt(daysOfWeek.length)];
                ScreeningTime screeningTime1 = new ScreeningTime(randomBranch1, randomDay1, LocalTime.of(randomHour1, 0), randomTheater1);
                movie.addScreeningTime(screeningTime1);
            }
            session.save(movie);
            session.flush();
        }

    }

    private static void generateComingSoonMovie() throws Exception {
        List<String> mainActors = new ArrayList<>();
        mainActors.add("Ryan");
        mainActors.add("Yu");
        LocalDate localDate = LocalDate.of(2024, 7, 27);

        // Convert LocalDate to java.util.Date
        Date specificDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        ComingSoonMovie comingSoonMovie = new ComingSoonMovie("Deadpool דדפול", "Shon", mainActors, "Funny Movie", "pic", specificDate);
        session.save(comingSoonMovie);
        session.flush();
    }

    private static void generateHomeMovie() throws Exception {
        List<String> mainActors = new ArrayList<>();
        mainActors.add("Simba");
        mainActors.add("Scar");
        HomeMovie homeMovie = new HomeMovie("Lion King מלך האריות", "Don", mainActors, "Great movie, lots of animals", "pic", "link");
        session.save(homeMovie);
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



            Branch[] branches = new Branch[5];
            branches[0] = new Branch("Haifa");
            branches[1] = new Branch("Tel-Aviv");
            branches[2] = new Branch("Eilat");
            branches[3] = new Branch("Rishon");
            branches[4] = new Branch("Jerusalem");
            for(Branch branch : branches) {
                Theater[] theaters = generateTheaters();
                for (Theater theater : theaters) {
                    branch.addTheaterToList(theater);
                }
            }

            generateBranches(branches);

            InTheaterMovie[] inTheaterMovies= new InTheaterMovie[5];

            List<String> mainActors1 = new ArrayList<String>();
            mainActors1.add("Zohar");
            mainActors1.add("Dan");
            inTheaterMovies[0] = new InTheaterMovie("Mad Max מקס הזועם", "Amit Perry", mainActors1, "Good movie", "pic1");

            List<String> mainActors2 = new ArrayList<>();
            mainActors2.add("Ceaser");
            inTheaterMovies[1] = new InTheaterMovie("Planet of the Apes כוכב הקופים", "Peter", mainActors2, "Movie about apes", "pic2");

            List<String> mainActors3 = new ArrayList<>();
            mainActors3.add("Daniel");
            inTheaterMovies[2] = new InTheaterMovie("Harry Potter הארי פוטר", "David", mainActors3, "Movie about friendship and magics", "pic3");

            List<String> mainActors4 = new ArrayList<>();
            mainActors4.add("Luke");
            mainActors4.add("Han");
            mainActors4.add("Lia");
            inTheaterMovies[3] = new InTheaterMovie("Star Wars מלחמת הכוכבים", "George Lucas", mainActors4, "Movie about some guys waving light swords", "pic4" );

            List<String> mainActors5 = new ArrayList<>();
            mainActors5.add("Kevin");
            inTheaterMovies[4] = new InTheaterMovie("The Usual Suspects החשוד המיידי", "Bryan", mainActors5, "Thrilling movie", "pic5");

            Theater[] theaters = generateTheaters();

            generateScreeningTimes(branches, theaters,inTheaterMovies);

            printAllBranches();
            printAllSeats();

            generateComingSoonMovie();
            generateHomeMovie();

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