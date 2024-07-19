package il.cshaifasweng.OCSFMediatorExample.server.dataClasses;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
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
        configuration.addAnnotatedClass(Customer.class);
        configuration.addAnnotatedClass(Link.class);
        configuration.addAnnotatedClass(SubscriptionCard.class);
        configuration.addAnnotatedClass(Ticket.class);
        configuration.addAnnotatedClass(Purchase.class);
        configuration.addAnnotatedClass(Complaint.class);

        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
        configuration.setProperty("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver");
        configuration.setProperty("hibernate.connection.url", "jdbc:mysql://localhost:3306/sys?serverTimezone=Asia/Jerusalem");
        configuration.setProperty("hibernate.connection.username", "root");
        configuration.setProperty("hibernate.connection.password", "babun13");
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

    private static ScreeningTime[] generateScreeningTimes(Branch[] branches, Theater[] theaters, InTheaterMovie[] inTheaterMovies) throws Exception {
        // Create an array to hold ScreeningTime objects
        ScreeningTime[] st = new ScreeningTime[5];
        Random random = new Random();
        LocalDate startDate = LocalDate.now(); // starting from today
        LocalDate endDate = startDate.plusDays(30); // up to 30 days in the future

        int index = 0; // Index for the ScreeningTime array

        for (InTheaterMovie movie : inTheaterMovies) {
            for (int i = 0; i < 5 && index < st.length; i++) {
                // Generate a random date within the specified range
                long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
                LocalDate randomDate = startDate.plusDays(random.nextInt((int) daysBetween + 1));

                int randomHour = random.nextInt(24);
                Theater randomTheater = theaters[random.nextInt(theaters.length)];
                Branch randomBranch = branches[random.nextInt(branches.length)];

                ScreeningTime screeningTime = new ScreeningTime();
                screeningTime.setBranch(randomBranch);
                screeningTime.setDate(randomDate);
                screeningTime.setTime(LocalTime.of(randomHour, 0));
                screeningTime.setTheater(randomTheater);
                screeningTime.setInTheaterMovie(movie);

                if (!randomBranch.getInTheaterMovieList().contains(movie)) {
                    randomBranch.addInTheaterMovieToList(movie);
                }
                if (!movie.getBranches().contains(randomBranch)) {
                    movie.addBranch(randomBranch);
                }
                movie.addScreeningTime(screeningTime);

                // Add the ScreeningTime object to the array
                st[index++] = screeningTime;

                // Stop if the array is full
                if (index >= st.length) {
                    break;
                }
            }
            session.save(movie);
            session.flush();

            // Stop if the array is full
            if (index >= st.length) {
                break;
            }
        }

        return st;
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

    private static void generateLinks(Link[] links) throws Exception {
        for(Link link:links){
            session.save(link);
            session.flush();
        }
    }

    private static void generateSubscriptionCards(SubscriptionCard[] sc) throws Exception {
        for(SubscriptionCard subscriptionCard:sc){
            session.save(subscriptionCard);
            session.flush();
        }
    }

    private static void generateTickets(Ticket[] tickets) throws Exception {
        for(Ticket ticket:tickets){
            session.save(ticket);
            session.flush();
        }
    }

    private static void generateCustomers(Customer[] customers) throws Exception {
        for(Customer customer:customers){
            session.save(customer);
            session.flush();
        }
    }
    private static void generatePurchases(Purchase[] purchases) throws Exception {
        for (Purchase purchase : purchases) {
            session.save(purchase);
            session.flush();
        }
    }

    private static void generateComplaints(Complaint[] complaints) throws Exception {
        for (Complaint complaint : complaints) {
            session.save(complaint);
            session.flush();
        }
    }

    private static void generateServiceEmployees(ServiceEmployee[] serviceEmployees) throws Exception {
        for (ServiceEmployee serviceEmployee : serviceEmployees) {
            session.save(serviceEmployee);
            session.flush();
        }
    }

    private static void generateCompanyManagers(CompanyManager[] companyManagers) throws Exception {
        for (CompanyManager companyManager : companyManagers) {
            session.save(companyManager);
            session.flush();
        }
    }

    private static void generateBranchManagers(BranchManager[] branchManagers) throws Exception {
        for (BranchManager branchManager : branchManagers) {
            session.save(branchManager);
            session.flush();
        }
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

            ScreeningTime[] screeningTimes = generateScreeningTimes(branches, theaters,inTheaterMovies);

            printAllBranches();
            printAllSeats();

            generateComingSoonMovie();
            generateHomeMovie();

            //new classes instances: ---------19.07---------

            Customer[] customers = new Customer[5];
            customers[0] = new Customer("Zohar", "Sahar");
            customers[1] = new Customer("Dan", "Zingerman");
            customers[2] = new Customer("Amit", "Perry");
            customers[3] = new Customer("Daniel", "Rubinstein");
            customers[4] = new Customer("Kfir", "Back");

            Link[] links =new Link[5];
            links[0] = new Link(customers[0],20,"Lion King",LocalTime.now(),LocalTime.now().plusHours(168));
            links[1] = new Link(customers[1],20, "Star Wars", LocalTime.now(),LocalTime.now().plusHours(168));
            links[2] = new Link(customers[2],20,"Mad Max", LocalTime.now(),LocalTime.now().plusHours(168));
            links[3] = new Link(customers[3],20,"Avatar 2", LocalTime.now(),LocalTime.now().plusHours(168));
            links[4] = new Link(customers[4],20, "Toy Story", LocalTime.now(),LocalTime.now().plusHours(168));

            SubscriptionCard[] sc = new SubscriptionCard[5];
            for(int i=0;i<sc.length;i++) {
                sc[i] = new SubscriptionCard(customers[i],200);
            }

            Ticket[] tickets = new Ticket[5];
            for(int i=0;i<tickets.length;i++) {
                tickets[i] = new Ticket(customers[i],30,screeningTimes[i].getInTheaterMovie().getMovieName(),screeningTimes[i], screeningTimes[i].getTheater().getSeat(i));
            }

            Purchase[] purchases = new Purchase[5];
            purchases[0] = new Purchase(tickets[0], "Credit Card", LocalTime.now());
            purchases[1] = new Purchase(tickets[1], "Credit Card", LocalTime.now());
            purchases[2] = new Purchase(tickets[2], "Credit Card", LocalTime.now());
            purchases[3] = new Purchase(tickets[3], "Credit Card", LocalTime.now());
            purchases[4] = new Purchase(tickets[4], "Credit Card", LocalTime.now());

            Complaint[] complaints = new Complaint[5];
            complaints[0] = new Complaint(customers[0], LocalTime.now());
            complaints[1] = new Complaint(customers[1], LocalTime.now());
            complaints[2] = new Complaint(customers[2], LocalTime.now());
            complaints[3] = new Complaint(customers[3], LocalTime.now());
            complaints[4] = new Complaint(customers[4], LocalTime.now());

            ServiceEmployee[] serviceEmployees = new ServiceEmployee[5];
            serviceEmployees[0] = new ServiceEmployee("John", "Doe", "johndoe", "password1");
            serviceEmployees[1] = new ServiceEmployee("Jane", "Smith", "janesmith", "password2");
            serviceEmployees[2] = new ServiceEmployee("Michael", "Brown", "michaelbrown", "password3");
            serviceEmployees[3] = new ServiceEmployee("Emily", "Davis", "emilydavis", "password4");
            serviceEmployees[4] = new ServiceEmployee("David", "Wilson", "davidwilson", "password5");

            CompanyManager[] companyManagers = new CompanyManager[5];
            companyManagers[0] = new CompanyManager("Yosi", "Levi", "johndoe", "password1");
            companyManagers[1] = new CompanyManager("Moshe", "Cohen", "janesmith", "password2");
            companyManagers[2] = new CompanyManager("Yogev", "Perry", "michaelbrown", "password3");
            companyManagers[3] = new CompanyManager("Noam", "Platipus", "emilydavis", "password4");
            companyManagers[4] = new CompanyManager("Orpaz", "Filusim", "davidwilson", "password5");

            BranchManager[] branchManagers = new BranchManager[5];
            branchManagers[0] = new BranchManager("Alice", "Johnson", "alicejohnson", "password1", branches[0]);
            branchManagers[1] = new BranchManager("Bob", "Williams", "bobwilliams", "password2", branches[1]);
            branchManagers[2] = new BranchManager("Charlie", "Jones", "charliejones", "password3", branches[2]);
            branchManagers[3] = new BranchManager("Diana", "Garcia", "dianagarcia", "password4", branches[3]);
            branchManagers[4] = new BranchManager("Evan", "Martinez", "evanmartinez", "password5", branches[4]);


            generatePurchases(purchases);
            generateComplaints(complaints);
            generateServiceEmployees(serviceEmployees);
            generateCompanyManagers(companyManagers);
            generateBranchManagers(branchManagers);
            generateCustomers(customers);
            generateTickets(tickets);
            generateLinks(links);
            generateSubscriptionCards(sc);
            //___________________________________________________
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