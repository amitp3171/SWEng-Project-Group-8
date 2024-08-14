package il.cshaifasweng.OCSFMediatorExample.server.dataClasses;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Random;
import il.cshaifasweng.OCSFMediatorExample.server.dataClasses.CompanyManager;

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
        configuration.addAnnotatedClass(Purchase.class);
        configuration.addAnnotatedClass(Complaint.class);
        configuration.addAnnotatedClass(ServiceEmployee.class);
        configuration.addAnnotatedClass(CompanyManager.class);
        configuration.addAnnotatedClass(BranchManager.class);
        configuration.addAnnotatedClass(ContentManager.class);
        configuration.addAnnotatedClass(Price.class);
        configuration.addAnnotatedClass(CustomerMessage.class);

        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
        configuration.setProperty("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver");
        configuration.setProperty("hibernate.connection.url", "jdbc:mysql://localhost:3306/projectdatabase?serverTimezone=Asia/Jerusalem");
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

    private static ArrayList<ScreeningTime> generateScreeningTimes(Branch[] branches, Theater[] theaters, InTheaterMovie[] inTheaterMovies) throws Exception {
        // Create an array to hold ScreeningTime objects
//        ScreeningTime[] st = new ScreeningTime[5];
        ArrayList<ScreeningTime> st = new ArrayList<>();
        Random random = new Random();
        LocalDate startDate = LocalDate.now(); // starting from today
        LocalDate endDate = startDate.plusDays(30); // up to 30 days in the future

        for (InTheaterMovie movie : inTheaterMovies) {
            for (int i = 0; i < 5; i++) {
                // Generate a random date within the specified range
                long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
                LocalDate randomDate = startDate.plusDays(random.nextInt((int) daysBetween + 1));

                int randomHour = random.nextInt(24);
                Branch randomBranch = branches[random.nextInt(branches.length)];
                Theater randomTheater = randomBranch.getTheaterList().get(random.nextInt(10));

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
                st.add(screeningTime);

//                // Stop if the array is full
//                if (index >= st.length) {
//                    break;
//                }
            }
            session.save(movie);
            session.flush();

            // Stop if the array is full
//            if (index >= st.length) {
//                break;
//            }
        }

        return st;
    }

    private static ComingSoonMovie generateComingSoonMovie() throws Exception {
        List<String> mainActors = new ArrayList<>();
        mainActors.add("Ryan");
        mainActors.add("Yu");
        LocalDate localDate = LocalDate.of(2024, 7, 27);

        // Convert LocalDate to java.util.Date
        LocalDate ld = LocalDate.now();
        ComingSoonMovie comingSoonMovie = new ComingSoonMovie("Deadpool דדפול", "Shon", mainActors, "[Funny Movie]", "pic", ld);
        session.save(comingSoonMovie);
        session.flush();
        return  comingSoonMovie;
    }

    private static HomeMovie generateHomeMovie() throws Exception {
        List<String> mainActors = new ArrayList<>();
        mainActors.add("Simba");
        mainActors.add("Scar");
        HomeMovie homeMovie = new HomeMovie("Lion King מלך האריות", "Don", mainActors, "[Great movie, lots of animals]", "pic", 2);
        session.save(homeMovie);
        session.flush();
        return homeMovie;
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

    private static void generateContentManagers(ContentManager[] contentManagers) throws Exception {
        for (ContentManager contentManager : contentManagers) {
            session.save(contentManager);
            session.flush();
        }
    }

    private static void generatePrices(Price[] prices) throws Exception {
        for (Price price : prices) {
            session.save(price);
            session.flush();
        }
    }

    private static void generateCustomerMessages(CustomerMessage[] customerMessages) throws Exception {
        for (CustomerMessage customerMessage: customerMessages) {
            session.save(customerMessage);
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
            branches[3] = new Branch("Rishon LeTsiyon");
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
            inTheaterMovies[0] = new InTheaterMovie("Mad Max מקס הזועם", "Amit Perry", mainActors1, "[Good movie]", "pic1");

            List<String> mainActors2 = new ArrayList<>();
            mainActors2.add("Ceaser");
            inTheaterMovies[1] = new InTheaterMovie("Planet of the Apes כוכב הקופים", "Peter", mainActors2, "[Movie about apes]", "pic2");

            List<String> mainActors3 = new ArrayList<>();
            mainActors3.add("Daniel");
            inTheaterMovies[2] = new InTheaterMovie("Harry Potter הארי פוטר", "David", mainActors3, "[Movie about friendship and magics]", "pic3");

            List<String> mainActors4 = new ArrayList<>();
            mainActors4.add("Luke");
            mainActors4.add("Han");
            mainActors4.add("Lia");
            inTheaterMovies[3] = new InTheaterMovie("Star Wars מלחמת הכוכבים", "George Lucas", mainActors4, "[Movie about some guys waving light swords]", "pic4" );

            List<String> mainActors5 = new ArrayList<>();
            mainActors5.add("Kevin");
            inTheaterMovies[4] = new InTheaterMovie("The Usual Suspects החשוד המיידי", "Bryan", mainActors5, "[Thrilling movie]", "pic5");

            Theater[] theaters = generateTheaters();

            ArrayList<ScreeningTime> screeningTimes = generateScreeningTimes(branches, theaters,inTheaterMovies);

            printAllBranches();
            printAllSeats();

            ComingSoonMovie comingSoonMovie = generateComingSoonMovie();
            HomeMovie homeMovie = generateHomeMovie();

            //new classes instances: ---------19.07---------

            Purchase[] purchases = new Purchase[15];


            Customer[] customers = new Customer[5];
            customers[0] = new Customer("Zohar", "Sahar", "530167976");
            customers[1] = new Customer("Dan", "Zingerman", "122236188");
            customers[2] = new Customer("Amit", "Perry", "209134389");
            customers[3] = new Customer("Daniel", "Rubinstein", "252410942");
            customers[4] = new Customer("Kfir", "Back", "421344941");

            CustomerMessage[] customerMessages = new CustomerMessage[5];
            for (int i = 0; i < 5; i++) {
                customerMessages[i] = new CustomerMessage("hello message", "hello " + customers[i].getFirstName(), LocalDateTime.now(), customers[i]);
            }

            Link[] links =new Link[5];
            for(int i=0;i<links.length;i++){
                links[i] = new Link(customers[i],20, homeMovie, LocalDate.now(), LocalTime.now(), LocalTime.now().plusHours((long)(homeMovie.getMovieLength())+1), "thisisanexampleforalink");
                purchases[i] = new Purchase(links[i], customers[i], "Credit Card", LocalDate.now(), LocalTime.now());
                customers[i].addPurchaseToList(purchases[i]);
            }


            SubscriptionCard[] sc = new SubscriptionCard[5];
            for(int i=0;i<sc.length;i++) {
                sc[i] = new SubscriptionCard(customers[i], 700);
                purchases[i+5] = new Purchase(sc[i], customers[i], "Credit Card", LocalDate.now(), LocalTime.now());
                customers[i].addPurchaseToList(purchases[i+5]);

            }

            Ticket[] tickets = new Ticket[5];
            for(int i=0;i<tickets.length;i++) {
                tickets[i] = new Ticket(customers[i], 40,screeningTimes.get(i).getInTheaterMovie().getMovieName(),screeningTimes.get(i), screeningTimes.get(i).getSeat(i));
                purchases[i+10] = new Purchase(tickets[i], customers[i], "Credit Card", LocalDate.now(), LocalTime.now());
                customers[i].addPurchaseToList(purchases[i+10]);

            }


            Complaint[] complaints = new Complaint[5];
            complaints[0] = new Complaint(customers[0], LocalTime.now(), "[too expensive]", "[the tickets too expensive]");
            complaints[1] = new Complaint(customers[1], LocalTime.now(),"[too long]", "[the movie too long]");
            complaints[2] = new Complaint(customers[2], LocalTime.now(), "[too short]", "[the movie too short]");
            complaints[3] = new Complaint(customers[3], LocalTime.now(), "[bad service]", "[the tickets seller was rude]");
            complaints[4] = new Complaint(customers[4], LocalTime.now(),"[too cheap]", "[the tickets are too cheap]");
            ServiceEmployee[] serviceEmployees = new ServiceEmployee[5];
            serviceEmployees[0] = new ServiceEmployee("John", "Doe", "j", "j");
            serviceEmployees[1] = new ServiceEmployee("Jane", "Smith", "janesmith", "password2");
            serviceEmployees[2] = new ServiceEmployee("Michael", "Brown", "michaelbrown", "password3");
            serviceEmployees[3] = new ServiceEmployee("Emily", "Davis", "emilydavis", "password4");
            serviceEmployees[4] = new ServiceEmployee("David", "Wilson", "davidwilson", "password5");

            CompanyManager[] companyManagers = new CompanyManager[5];
            companyManagers[0] = new CompanyManager("Yosi", "Levi", "yosilevi", "password1");
            companyManagers[1] = new CompanyManager("Moshe", "Cohen", "moshecohen", "password2");
            companyManagers[2] = new CompanyManager("Yogev", "Perry", "yogevperry", "password3");
            companyManagers[3] = new CompanyManager("Noam", "Platipus", "noamplatipus", "password4");
            companyManagers[4] = new CompanyManager("Orpaz", "Filusim", "orpazfilusim", "password5");

            BranchManager[] branchManagers = new BranchManager[5];
            branchManagers[0] = new BranchManager("Alice", "Johnson", "alicejohnson", "password1", branches[0]);
            branchManagers[1] = new BranchManager("Bob", "Williams", "bobwilliams", "password2", branches[1]);
            branchManagers[2] = new BranchManager("Charlie", "Jones", "charliejones", "password3", branches[2]);
            branchManagers[3] = new BranchManager("Diana", "Garcia", "dianagarcia", "password4", branches[3]);
            branchManagers[4] = new BranchManager("Evan", "Martinez", "evanmartinez", "password5", branches[4]);

            ContentManager[] contentManagers = new ContentManager[2];
            contentManagers[0] = new ContentManager("Ron", "Weasley", "ronweasly", "itsmagicinnit");
            contentManagers[1] = new ContentManager("Zohar", "Sahar", "z", "z");

            for(int i=0;i<customers.length;i++) {
                customers[i].addComplaintToList(complaints[i]);
                customers[i].addLinkToList(links[i]);
                customers[i].addSubscriptionCardToList(sc[i]);
                customers[i].addTicketToList(tickets[i]);
                customers[i].addMessageToList(customerMessages[i]);
            }

            Price ticketPrice = new Price("Ticket", 40);
            Price linkPrice = new Price("Link", 20);
            Price subscriptionCardPrice = new Price("SubscriptionCard", 700);

            generatePrices(new Price[]{ticketPrice, linkPrice, subscriptionCardPrice});

            generateCustomerMessages(customerMessages);
            generateCustomers(customers);
            generatePurchases(purchases);
            generateComplaints(complaints);
            generateServiceEmployees(serviceEmployees);
            generateCompanyManagers(companyManagers);
            generateBranchManagers(branchManagers);
            generateContentManagers(contentManagers);
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