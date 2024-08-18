package il.cshaifasweng.OCSFMediatorExample.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import il.cshaifasweng.OCSFMediatorExample.server.creationalClasses.DatabaseBridge;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.SubscribedClient;
import il.cshaifasweng.OCSFMediatorExample.server.dataClasses.*;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.lang.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SimpleServer extends AbstractServer {
	private static ArrayList<SubscribedClient> SubscribersList = new ArrayList<>();
	private static ObjectMapper mapper = new ObjectMapper();
	DatabaseBridge db = DatabaseBridge.getInstance();

	Map<Integer, Boolean> connectedUsers = new HashMap<>();

	public SimpleServer(int port) {
		super(port);
		mapper.registerModule(new JavaTimeModule());
		setLinksScheduler();
	}

	private void sendMessage(Message message, String newMessage, List<String> newData, ConnectionToClient client) throws IOException {
		message.setMessage(newMessage);
		String listAsJson = mapper.writeValueAsString(newData);
		message.setData(listAsJson);
		client.sendToClient(message);
		System.out.println(newMessage);
	}

	private void sendMessage(Message message, String newMessage, String newData, ConnectionToClient client) throws IOException {
		message.setMessage(newMessage);
		message.setData(newData);
		client.sendToClient(message);
		System.out.println(newMessage);
	}

	private void handleNewClient(Message message, ConnectionToClient client) throws IOException {
		SubscribedClient connection = new SubscribedClient(client);
		SubscribersList.add(connection);
		message.setMessage("client added successfully");
		client.sendToClient(message);
	}

	public void setLinksScheduler() {
		List<Link> links = db.getAll(Link.class, false);
		for (Link link : links) {
			LocalDateTime availableTime = LocalDateTime.of(link.getAvailableDay(), link.getAvailableHour());
			if(availableTime.isAfter(LocalDateTime.now())) {
				scheduleMessage(link.getOwner(), link.getHomeMovie(), link, link.getAvailableDay(), link.getAvailableHour());
			}
		}
	}

	public void scheduleMessage(Customer owner, HomeMovie homeMovie, Link newLink, LocalDate selectedDate, LocalTime selectedTime) {
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

		// Calculate the time for the message
		LocalDateTime notificationTime = LocalDateTime.of(selectedDate, selectedTime).minusHours(1);
		LocalDateTime now = LocalDateTime.now();
		long delay = Duration.between(now, notificationTime).toSeconds();

		if (delay > 0) {
			Runnable messageTask = new Runnable() {
				public void run() {
						String messageBody = "הלינק שרכשת לסרט " + homeMovie.getMovieName() + " יהפוך לזמין בעוד שעה. " + "\n" + "הלינק: " + newLink.getLink() + "\n" + ". זמין מ:  " + newLink.getAvailableHour() + " עד " + newLink.getExpiresAt().toLocalTime();

					CustomerMessage customerMessage = new CustomerMessage(
								"לינק זמין בקרוב",
								"[" + messageBody + "]",
								LocalDateTime.now(),
								owner
						);

						db.addInstance(customerMessage); // Save the message to the database
						owner.addMessageToList(customerMessage); // Add the message to the customer's list
						db.updateEntity(owner);
						System.out.println("created upcoming available link message successfully");

				}
			};
			scheduler.schedule(messageTask, delay, TimeUnit.SECONDS);
		} else {
			System.out.println("Notification time is in the past. Cannot schedule message.");
		}
	}


	private void handleBranchListRequest(Message message, ConnectionToClient client) throws IOException {
		List<Branch> receivedData = db.getAll(Branch.class, true);
		// modify message
		List<String> branchLocations = new ArrayList<>();
		// get branch locations
		for (Branch branch : receivedData)
			branchLocations.add(branch.getLocation());

		// send message
		sendMessage(message, "updated branch list successfully", branchLocations, client);
	}

	private void handleInTheaterMovieListRequest(Message message, ConnectionToClient client) throws IOException {
		// parse message
		String[] parsedData = message.getData().split(",");
		String selectedBranchLocation = parsedData[0];
		boolean forceRefresh = Boolean.parseBoolean(parsedData[1]);

		// get data
		List<InTheaterMovie> receivedData = db.getAll(InTheaterMovie.class, forceRefresh);
		ArrayList<String> movieToString = new ArrayList<>();

		System.out.println("LOCAL TIME:" + LocalTime.now());

		// attach flag
		for (InTheaterMovie movie : receivedData) {
			String isInBranch = ",false";
			String hasActiveScreenings = ",false";

			for (ScreeningTime screeningTime : movie.getScreenings()) {
				if (
						screeningTime.getBranch().getLocation().equals(selectedBranchLocation)
								&& ((screeningTime.getTime().isAfter(LocalTime.now()) && screeningTime.getDate().isEqual(LocalDate.now())) || screeningTime.getDate().isAfter(LocalDate.now()))
				) {
					hasActiveScreenings = ",true";
				}
			}

			for (Branch branch : movie.getBranches()) {
				if (branch.getLocation().equals(selectedBranchLocation)) {
					isInBranch = ",true";
					break;
				}
			}
			movieToString.add(movie.toString() + isInBranch + hasActiveScreenings);
		}

		sendMessage(message, "updated InTheaterMovie list successfully", movieToString, client);
	}

	private void handleScreeningTimeListRequest(Message message, ConnectionToClient client) throws IOException {
		// parse message (message_text,branch_location,movie_id)
		String[] splitMessage = message.getData().split(",");
		String branchLocation = splitMessage[0];
		int movieId = Integer.parseInt(splitMessage[1]);
		boolean forceRefresh = Boolean.parseBoolean(splitMessage[2]);

		// get data
		List<ScreeningTime> receivedData = db.getAll(ScreeningTime.class, forceRefresh);
		ArrayList<String> screeningTimeToString = new ArrayList<>();
		// filter movies
		for (int i = 0; i < receivedData.size(); i++) {
			String ticketsExist = ",false";
			if (receivedData.get(i).getInTheaterMovie().getId() == movieId && receivedData.get(i).getBranch().getLocation().equals(branchLocation)) {
				for (Seat seat : receivedData.get(i).getSeats()) {
					if (seat.isTaken()) {
						ticketsExist = ",true";
						break;
					}
				}
				screeningTimeToString.add(receivedData.get(i).toString() + ticketsExist);
			}
		}

		sendMessage(message, "updated ScreeningTime list successfully", screeningTimeToString, client);
	}

	private void handleSeatListRequest(Message message, ConnectionToClient client) throws IOException {
		String screeningId = message.getData();
		// get data
		List<Seat> receivedSeats = db.executeNativeQuery(
				"SELECT * FROM seats WHERE screeningtime_id = ?",
				Seat.class,
				screeningId
		);

		List<String> items = new ArrayList<>();

		for (Seat seat : receivedSeats)
			items.add(seat.toString());

		// send message
		sendMessage(message, "updated Seat list successfully", items, client);
	}

	private void handleTheaterListRequest(Message message, ConnectionToClient client) throws IOException {
		String branchLocation = message.getData();
		// get data
		List<Theater> receivedTheaters = db.executeNativeQuery(
				"SELECT theaterID, theaterNumber FROM theaters JOIN branches_theaters ON theaters.theaterID = branches_theaters.theaterList_theaterID WHERE branches_theaters.branch_location = ?",
				Theater.class,
				branchLocation
		);

		List<String> items = new ArrayList<>();

		for (Theater theater : receivedTheaters)
			items.add(theater.toString());

		// send message
		sendMessage(message, "updated Theater list successfully", items, client);
	}

	private void handleTheaterIdListRequest(Message message, ConnectionToClient client) throws IOException {
		String branchLocation = message.getData();

		List<Theater> receivedTheaters = db.executeNativeQuery(
				"SELECT * FROM theaters JOIN branches_theaters ON theaters.theaterID = branches_theaters.theaterList_theaterID WHERE branches_theaters.branch_location = ?",
				Theater.class,
				branchLocation
		);

		List<String> items = new ArrayList<>();

		for (Theater theater : receivedTheaters)
			items.add(Integer.toString(theater.getTheaterID()));

		sendMessage(message, "updated Theater ID list successfully", items, client);
	}

	private void handleComingSoonMovieListRequest(Message message, ConnectionToClient client) throws IOException {
		// parse forceRefresh from the message data
		boolean forceRefresh = Boolean.parseBoolean(message.getData());

		// get data
		List<ComingSoonMovie> receivedData = db.getAll(ComingSoonMovie.class, forceRefresh);
		ArrayList<String> movieToString = new ArrayList<>();


		for (ComingSoonMovie movie : receivedData) {
			movieToString.add(movie.toString());
		}

		sendMessage(message, "updated ComingSoonMovie list successfully", movieToString, client);
	}

	private void handleHomeMovieListRequest(Message message, ConnectionToClient client) throws IOException {
		// parse forceRefresh from the message data
		boolean forceRefresh = Boolean.parseBoolean(message.getData());

		// get data
		List<HomeMovie> receivedData = db.getAll(HomeMovie.class, forceRefresh);
		ArrayList<String> movieToString = new ArrayList<>();

		List<Link> links = new ArrayList<>();
		for (HomeMovie movie : receivedData) {
			links = db.executeNativeQuery("SELECT * FROM links WHERE homeMovie_id=?", Link.class, movie);
			movieToString.add(movie.toString() + "," + !links.isEmpty());
		}

		sendMessage(message, "updated HomeMovie list successfully", movieToString, client);
	}

	private void handleSetScreeningTimeRequest(Message message, ConnectionToClient client) throws IOException {
		// id, day, time, theater.getTheaterID()
		String[] splitMessage = message.getData().split(",");

		// check for existing screenings in the specified theater
		List<ScreeningTime> existingScreenings = db.executeNativeQuery("SELECT * FROM ScreeningTimes WHERE theater_theaterID=? AND time=? AND date=?", ScreeningTime.class, splitMessage[3], splitMessage[2], splitMessage[1]);

		String data = "request failed";

		if (existingScreenings.isEmpty()) {
			ScreeningTime screening = db.executeNativeQuery("SELECT * FROM ScreeningTimes WHERE id=?", ScreeningTime.class, splitMessage[0]).get(0);
			InTheaterMovie relatedMovie = screening.getInTheaterMovie();
			relatedMovie.removeScreeningTime(screening);
			screening.setTime(splitMessage[2]);
			screening.setDate(LocalDate.parse(splitMessage[1], DateTimeFormatter.ofPattern("yyyy-MM-dd")));
			screening.setTheater(db.executeNativeQuery("SELECT * FROM Theaters WHERE theaterID=?", Theater.class, splitMessage[3]).get(0));
			relatedMovie.addScreeningTime(screening);


			db.updateEntity(relatedMovie);
			db.updateEntity(screening);
			data = "request successful";
		}

		sendMessage(message, "set new ScreeningTime successfully", data, client);
	}

	private void handleCreateScreeningTimeRequest(Message message, ConnectionToClient client) throws IOException {
		// parse message: branchLocation, date, time, theater, movie
		String[] splitMessage = message.getData().split(",");

		Branch selectedBranch = db.executeNativeQuery("SELECT * FROM Branches WHERE location=?", Branch.class, splitMessage[0]).get(0);
		LocalDate selectedDate = LocalDate.parse(splitMessage[1], DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		LocalTime selectedTime = LocalTime.parse(splitMessage[2], DateTimeFormatter.ofPattern("HH:mm"));
		Theater selectedTheater = db.executeNativeQuery("SELECT * FROM Theaters WHERE theaterID=?", Theater.class, splitMessage[3]).get(0);
		InTheaterMovie selectedMovie = db.executeNativeQuery("SELECT * FROM InTheaterMovies WHERE id=?", InTheaterMovie.class, splitMessage[4]).get(0);

		String data = "request failed";

		// check for existing screenings in the specified theater
		List<ScreeningTime> existingScreenings = db.executeNativeQuery("SELECT * FROM ScreeningTimes WHERE theater_theaterID=? AND time=? AND date=?", ScreeningTime.class, splitMessage[3], splitMessage[2], splitMessage[1]);

		if (existingScreenings.isEmpty()) {
			// add screeningTime
			ScreeningTime newScreening = new ScreeningTime(selectedBranch, selectedDate, selectedTime, selectedTheater, selectedMovie);

			db.beginTransaction();
			for (Seat seat : newScreening.getSeats()) {
				db.saveInstance(seat);
			}
			db.saveInstance(newScreening);
			db.flushSession();
			db.commitTransaction();
			// if this screeningTime is the first for the selected movie
			if (!selectedMovie.getBranches().contains(selectedBranch)) {
				selectedMovie.getBranches().add(selectedBranch);
				selectedBranch.getInTheaterMovieList().add(selectedMovie);
				db.updateEntity(selectedBranch);
			}
			selectedMovie.addScreeningTime(newScreening);
			db.updateEntity(selectedMovie);
			db.updateEntity(newScreening);
			data = "request successful";
		}

		sendMessage(message, "created new ScreeningTime successfully", data, client);
	}

	private void handleAddComingSoonMovie(Message message, ConnectionToClient client) throws IOException {
		// parse message: coming soon movie details by order in the constructor
		String[] splitMessage = message.getData().split(",");
		LocalDate releaseDate = LocalDate.parse(splitMessage[splitMessage.length-1], DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		List<String> mainActorsList = Arrays.asList(splitMessage[2].split(";"));

		//add comingSoonMovie
		ComingSoonMovie newComingSoonMovie = new ComingSoonMovie(splitMessage[0], splitMessage[1], mainActorsList, splitMessage[3], splitMessage[4], releaseDate);

		String data = "request successful";
		db.addInstance(newComingSoonMovie);

		sendMessage(message, "created new ComingSoonMovie successfully", data, client);
	}

	private void handleAddHomeMovie(Message message, ConnectionToClient client) throws IOException {
		// parse message: home movie details by order in the constructor
		String[] splitMessage = message.getData().split(",");
		double movieLength = Double.parseDouble(splitMessage[splitMessage.length-1]);
		List<String> mainActorsList = Arrays.asList(splitMessage[2].split(";"));

		//add homeMovie
		HomeMovie newHomeMovie = new HomeMovie(splitMessage[0], splitMessage[1], mainActorsList, splitMessage[3], splitMessage[4], movieLength);

		String data = "request successful";
		db.addInstance(newHomeMovie);

		sendMessage(message, "created new HomeMovie successfully", data, client);
	}

	private void handleAddInTheaterMovieRequest(Message message, ConnectionToClient client) throws IOException {
		String[] splitMovieData = message.getData().split(",");
		String branchLocation = splitMovieData[splitMovieData.length-1];

		Branch branch = db.executeNativeQuery("SELECT * FROM Branches WHERE location=?", Branch.class, branchLocation).get(0);

		//add inTheaterMovie
		List<String> mainActorsList = Arrays.asList(splitMovieData[2].split(";"));
		InTheaterMovie inTheaterMovie = new InTheaterMovie(splitMovieData[0], splitMovieData[1], mainActorsList, splitMovieData[3], splitMovieData[4]);

		inTheaterMovie.addBranch(branch);
		branch.addInTheaterMovieToList(inTheaterMovie);

		String data = "request successful";

		db.addInstance(inTheaterMovie);

		//send message to subscription card owners
		String messageBody = "הסרט: " + inTheaterMovie.getMovieName() + " נוסף לקולנוע. " + "\n" + "ניתן לצפות באתר בזמני ההקרנה של הסרט.";
		List<SubscriptionCard> subscriptionCards = db.executeNativeQuery("SELECT * FROM subscriptioncards WHERE remainingTickets>0", SubscriptionCard.class);
		ArrayList<Customer> cardOwners = new ArrayList<>();
		for(SubscriptionCard subscriptionCard: subscriptionCards){
			if(!cardOwners.contains(subscriptionCard.getOwner())){
				cardOwners.add(subscriptionCard.getOwner());
			}
		}
		for (Customer customer : cardOwners) {
				CustomerMessage customerMessage = new CustomerMessage("סרט חדש בקולנוע!", "[" + messageBody + "]", LocalDateTime.now(), customer);
				customer.addMessageToList(customerMessage);
				db.addInstance(customerMessage);
		}
		for (Customer customer : cardOwners) {
			db.updateEntity(customer);
		}

		sendMessage(message, "created new InTheaterMovie successfully", data, client);
	}

	private void handleRemoveComingSoonMovie(Message message, ConnectionToClient client) throws IOException {
		// Parse the incoming movie data string
		String movieId = message.getData();
		System.out.println(movieId);
		ComingSoonMovie movieToRemove = db.executeNativeQuery("SELECT * FROM ComingSoonMovie WHERE id = ?", ComingSoonMovie.class, movieId).get(0);

		//Remove movie
		db.removeInstance(movieToRemove);
		// Send success message back to the client
		sendMessage(message, "removed ComingSoonMovie successfully", "success", client);

	}

	private void handleRemoveHomeMovie(Message message, ConnectionToClient client) throws IOException {
		// Parse the incoming movie data string
		String movieId = message.getData();

		HomeMovie movieToRemove = db.executeNativeQuery("SELECT * FROM HomeMovie WHERE id = ?", HomeMovie.class, movieId).get(0);

		//Remove movie
		db.removeInstance(movieToRemove);
		// Send success message back to the client
		sendMessage(message, "removed HomeMovie successfully", "success", client);
	}

	private void handleVerifyCustomerIdRequest(Message message, ConnectionToClient client) throws IOException {
		List<Customer> result = db.executeNativeQuery("SELECT * FROM customers WHERE govId=?", Customer.class, message.getData());
		String data = "user invalid";

		if (!result.isEmpty()) {
			Customer customer = result.get(0);
			if (connectedUsers.containsKey(customer.getId()))
				data = "user already logged in";
			else {
				data = String.join(",", String.valueOf(customer.getId()), customer.getFirstName(), customer.getLastName());
				connectedUsers.put(customer.getId(), true);
			}
		}

		sendMessage(message, "verified Customer id successfully", data, client);
	}

	private void handleVerifyEmployeeCredentialsRequest(Message message, ConnectionToClient client) throws IOException {
		List<AbstractEmployee> employees = db.getAll(AbstractEmployee.class, false);

		String[] providedCredentials = message.getData().split(",");

		String data = "user invalid";

		for (AbstractEmployee employee : employees) {
			if (employee.getUsername().equals(providedCredentials[0]) && employee.getPassword().equals(providedCredentials[1])) {
				if (connectedUsers.containsKey(employee.getId()))
					data = "user already logged in";
				else {
					String employeeType = employee.getClass().getName().substring(55);
					data = String.join(",", String.valueOf(employee.getId()), employee.getFirstName(), employee.getLastName(), employeeType);
					if (employeeType.equals("BranchManager"))
						data += "," + ((BranchManager) employee).getBranch().getLocation();
					connectedUsers.put(employee.getId(), true);
				}
				break;
			}
		}

		sendMessage(message, "verified Employee credentials successfully", data, client);
	}

	private void handleProductPriceRequest(Message message, ConnectionToClient client) throws IOException {
		Price productPrice = db.executeNativeQuery("SELECT * FROM prices WHERE productClass=?", Price.class, message.getData()).get(0);

		sendMessage(message, "updated Product price successfully", String.valueOf(productPrice.getPrice()), client);
	}

	private void handleProductPriceChangeRequest(Message message, ConnectionToClient client) throws IOException {
		String[] messageData = message.getData().split(",");

		Price productPrice = db.executeNativeQuery("SELECT * FROM prices WHERE productClass = ?", Price.class, messageData[0]).get(0);

		productPrice.setPrice(Double.parseDouble(messageData[1]));

		db.updateEntity(productPrice);

		sendMessage(message, "changed Product price successfully", "price changed successfully", client);
	}

	private void handleCreateCustomerCredentials(Message message, ConnectionToClient client) throws IOException {
		// id, firstname, lastname
		String[] providedCredentials = message.getData().split(",");

		List<Customer> result = db.executeNativeQuery("SELECT * FROM customers WHERE govId=?", Customer.class, providedCredentials[0]);
		String data;

		// user exists
		if (!result.isEmpty()) {
			//Customer customer = result.get(0);
			data = "customer exists";//String.format("%s,%s", customer.getFirstName(), customer.getLastName());
		}
		else {
			Customer newCustomer = new Customer(providedCredentials[1], providedCredentials[2], providedCredentials[0]);
			db.addInstance(newCustomer);
			connectedUsers.put(newCustomer.getId(), true);
			data = String.valueOf(newCustomer.getId());
		}

		sendMessage(message, "created Customer credentials successfully", data, client);
	}

	private void handleCreateTicketPurchase(Message message, ConnectionToClient client) throws IOException {
		// govId, screeningId, seatIds, amountOfTickets, ticketPrice
		String[] messageData = message.getData().split(",(?![^\\[]*\\])");

		String customerGovId = messageData[0];
		String screeningTimeId = messageData[1];
		String[] selectedSeatIds = messageData[2].substring(1, messageData[2].length()-1).split(",");

		List<Seat> selectedSeats = db.executeNativeQuery("SELECT * FROM seats WHERE id IN (" + String.join(",", Collections.nCopies(selectedSeatIds.length, "?")) + ")", Seat.class, selectedSeatIds);
		String seatNumbers = "";
		for (Seat seat : selectedSeats) {
			if(seatNumbers != "")
				seatNumbers += ", " + seat.getSeatNumber();
			else
				seatNumbers += seat.getSeatNumber();
		}
		String productPrice = messageData[3];

		Customer owner = db.executeNativeQuery("SELECT * FROM customers WHERE govId=?", Customer.class, customerGovId).get(0);
		ScreeningTime screeningTime = db.executeNativeQuery("SELECT * FROM screeningtimes WHERE id=?", ScreeningTime.class, screeningTimeId).get(0);

		CustomerMessage customerMessage = new CustomerMessage("רכישת כרטיסים חדשים", "", LocalDateTime.now(), owner);
		String messageContent = "תודה שרכשת כרטיסים לסרט: " + screeningTime.getInTheaterMovie().getMovieName() + "\n";
		messageContent += "בסניף " + screeningTime.getBranch().getLocation() + " באולם מספר: " + screeningTime.getTheater().getTheaterNumber() + "\n";
		messageContent += "מספר המושבים: " + selectedSeats.size() + "\n";
		messageContent +=	" כיסאות מספר: " + seatNumbers;
		messageContent = "[" + messageContent + "]";
		System.out.println(seatNumbers);
		System.out.println(messageContent);
		customerMessage.setMessageBody(messageContent);
		owner.addMessageToList(customerMessage);
		db.addInstance(customerMessage);

		for (int i = 0; i < selectedSeats.size(); i++) {
			Ticket newTicket = new Ticket(owner, Double.parseDouble(productPrice), screeningTime.getInTheaterMovie().getMovieName(), screeningTime, selectedSeats.get(i));
			Purchase newPurchase = new Purchase(newTicket, owner, messageData[4], LocalDate.now(), LocalTime.now());
			Seat selectedSeat = selectedSeats.get(i);
			selectedSeat.setTaken(true);
			owner.addTicketToList(newTicket);
			owner.addPurchaseToList(newPurchase);
			db.addInstance(newTicket);
			db.addInstance(newPurchase);
			db.updateEntity(selectedSeat);
			db.updateEntity(owner);
		}

		sendMessage(message, "created Ticket Purchase successfully", "payment successful", client);
	}

	private void handleCreateLinkPurchase(Message message, ConnectionToClient client) throws IOException {
		String[] messageData = message.getData().split(",");
		String customerGovId = messageData[0];
		String movieId = messageData[1];
		String productPrice = messageData[2];
		String selectedDateString = messageData[3]; //format yyyy-MM-dd
		String selectedTimeString = messageData[4]; //format hh-mm


		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate selectedDate = LocalDate.parse(selectedDateString, formatter);
		DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("HH:mm");
		LocalTime selectedTime = LocalTime.parse(selectedTimeString, formatter1);

		Customer owner = db.executeNativeQuery("SELECT * FROM customers WHERE govId=?", Customer.class, customerGovId).get(0);
		HomeMovie homeMovie = db.executeNativeQuery("SELECT * FROM homemovie WHERE id=?", HomeMovie.class, movieId).get(0);

		Link newLink = new Link(owner, Double.parseDouble(productPrice), homeMovie, selectedDate, selectedTime);
		Purchase newPurchase = new Purchase(newLink, owner, "Credit Card", LocalDate.now(), LocalTime.now());

		String messageBody = "תודה שרכשת לינק לצפייה ביתית לסרט: " + homeMovie.getMovieName() + "\n" + "הקישור לסרט: " + newLink.getLink() + "\n" + "יהיה זמין בשעות: " + newLink.getAvailableHour() + "-" + newLink.getExpiresAt();
		CustomerMessage customerMessage = new CustomerMessage("לינק חדש",  "[" + messageBody + "]", LocalDateTime.now(), owner);

		owner.addMessageToList(customerMessage);
		owner.addLinkToList(newLink);
		owner.addPurchaseToList(newPurchase);
		db.addInstance(customerMessage);
		db.addInstance(newLink);
		db.addInstance(newPurchase);
		db.updateEntity(owner);
		sendMessage(message, "created Link Purchase successfully", "payment successful", client);

		// Schedule the customer message creation
		scheduleMessage(owner, homeMovie, newLink, selectedDate, selectedTime);
	}

	private void handleCreateSubscriptionCardPurchase(Message message, ConnectionToClient client) throws IOException {
		// govId, amount, price
		String[] messageData = message.getData().split(",");

		String customerGovId = messageData[0];
		double price = Double.parseDouble(messageData[2]);

		Customer owner = db.executeNativeQuery("SELECT * FROM customers WHERE govId=?", Customer.class, customerGovId).get(0);

		for (int i = 0; i < Integer.parseInt(messageData[1]); i++) {
			SubscriptionCard newSubscriptionCard = new SubscriptionCard(owner, price);
			Purchase newPurchase = new Purchase(newSubscriptionCard, owner, "Credit Card", LocalDate.now(), LocalTime.now());
			owner.addSubscriptionCardToList(newSubscriptionCard);
			owner.addPurchaseToList(newPurchase);
			CustomerMessage customerMessage = new CustomerMessage("כרטיסייה חדשה", "[תודה שרכשת כרטיסיית סרטים, נותרו 20 כרטיסים]", LocalDateTime.now(), owner);
			owner.addMessageToList(customerMessage);
			db.addInstance(newSubscriptionCard);
			db.addInstance(newPurchase);
			db.addInstance(customerMessage);
			db.updateEntity(owner);
		}

		sendMessage(message, "created SubscriptionCard Purchase successfully", "payment successful", client);
	}

	private void handleCustomerComplaintListRequest(Message message, ConnectionToClient client) throws IOException{
		String govId = message.getData();
		// get data
		List<Customer> customer = db.executeNativeQuery(
				"SELECT * FROM customers WHERE govId = ?",
				Customer.class,
				govId);
		List<Complaint> receivedComplaints = db.executeNativeQuery(
				"SELECT * FROM complaints WHERE creator_id = ?",
				Complaint.class,
				customer.get(0).getId());
		List<String> complaintsContents = new ArrayList<>();
		for (Complaint complaint: receivedComplaints){
			complaintsContents.add(complaint.toString());
		}
		sendMessage(message, "updated Customer Complaint list successfully", complaintsContents, client);
	}

	private void handleCustomerMessageListRequest(Message message, ConnectionToClient client) throws IOException{
		String govId = message.getData();
		// get data
		List<Customer> customer = db.executeNativeQuery(
				"SELECT * FROM customers WHERE govId = ?",
				Customer.class,
				govId);
		List<CustomerMessage> receivedCustomerMessages = db.executeNativeQuery(
				"SELECT * FROM customerMessages WHERE customer_id = ?",
				CustomerMessage.class,
				customer.get(0).getId());
		List<String> messagesContents = new ArrayList<>();
		for (CustomerMessage customerMessage: receivedCustomerMessages){
			messagesContents.add(customerMessage.toString());
		}
		sendMessage(message, "updated Customer Message list successfully", messagesContents, client);
	}

	private void handleServiceEmployeeComplaintListRequest(Message message, ConnectionToClient client) throws IOException {
		List<Complaint> receivedComplaints = db.getAll(Complaint.class, false);



		List<String> complaintsContents = new ArrayList<>();

		for (Complaint complaint: receivedComplaints){
			complaintsContents.add(complaint.toString());
		}

		sendMessage(message, "updated ServiceEmployee Complaint list successfully", complaintsContents, client);


	}

	private void handleCustomerPurchaseListRequest(Message message, ConnectionToClient client) throws IOException{
		String govId = message.getData();
		// get data
		Customer customer = db.executeNativeQuery(
				"SELECT * FROM customers WHERE govId = ?",
				Customer.class,
				govId).get(0);

		List<Purchase> customerPurchases = db.executeNativeQuery(
				"SELECT * FROM purchases WHERE customer_id = ?",
				Purchase.class,
				customer.getId());

		List<String> purchasesToString = new ArrayList<>();

		for (Purchase purchase: customerPurchases){
			purchasesToString.add(purchase.toString());
		}

		sendMessage(message, "updated Customer Purchase list successfully", purchasesToString, client);
	}

	private void handleCustomerProductDetailsRequest(Message message, ConnectionToClient client) throws IOException{
		int selectedId = Integer.parseInt(message.getData());

		List<AbstractProduct> products = db.getAll(AbstractProduct.class, false);

		String item = null;

		for (AbstractProduct product : products) {
			if (selectedId == product.getId()) {
				item = String.join(",", product.getClass().getName().substring(55), product.toString());
				break;
			}
		}

		sendMessage(message, "updated Product details successfully", item, client);
	}

	private void handleCustomerSubscriptionCardListRequest(Message message, ConnectionToClient client) throws IOException{
		String govId = message.getData();
		// get data
		Customer customer = db.executeNativeQuery(
				"SELECT * FROM customers WHERE govId = ?",
				Customer.class,
				govId).get(0);

		List<SubscriptionCard> customerSubscriptionCards = db.executeNativeQuery(
				"SELECT * FROM subscriptioncards WHERE owner_id = ?",
				SubscriptionCard.class,
				customer.getId());

		List<String> purchasesToString = new ArrayList<>();

		for (SubscriptionCard subscriptionCard: customerSubscriptionCards){
			purchasesToString.add(subscriptionCard.toString());
		}

		sendMessage(message, "updated Customer SubscriptionCard list successfully", purchasesToString, client);
	}

	private void handleCustomerUseSubscriptionCardRequest(Message message, ConnectionToClient client) throws IOException{
		String[] splitMessage = message.getData().split(",");

		int subscriptionCardId = Integer.parseInt(splitMessage[0]);
		int amountOfTickets = Integer.parseInt(splitMessage[1]);

		SubscriptionCard selectedSubscriptionCard = db.executeNativeQuery("SELECT * FROM subscriptioncards WHERE id = ?", SubscriptionCard.class, subscriptionCardId).get(0);

		selectedSubscriptionCard.useTickets(amountOfTickets);

		db.updateEntity(selectedSubscriptionCard);

		sendMessage(message, "used SubscriptionCard successfully", "payment successful", client);
	}

	private void handleCustomerTicketRefundRequest(Message message, ConnectionToClient client) throws IOException{
		String productId = message.getData();

		Ticket selectedTicket = db.executeNativeQuery("SELECT * FROM tickets WHERE id = ?", Ticket.class, productId).get(0);

		Purchase relatedPurchase = db.executeNativeQuery("SELECT * FROM purchases WHERE relatedProduct_id = ?", Purchase.class, productId).get(0);

		List<Complaint> relatedComplaints = db.executeNativeQuery("SELECT * FROM complaints WHERE relatedPurchase_id = ?", Complaint.class, relatedPurchase.getId());

		if (relatedComplaints != null && !relatedComplaints.isEmpty()) {
			for (int i=0;i<relatedComplaints.size();i++) {
				Complaint relatedComplaint = relatedComplaints.get(0);
				//detach Complaint
				relatedComplaint.setRelatedPurchase(null);
				relatedComplaint.setResponse("הרכישה בוטלה");
			}

		}

		Seat selectedSeat = selectedTicket.getSeat();

		Customer owner = selectedTicket.getOwner();

		CustomerMessage customerMessage = new CustomerMessage("פיצוי כספי", "קיבלת פיצוי כספי עבור רכישת כרטיס לסרט: " + selectedTicket.getMovieName() + " בסך " + selectedTicket.getPrice() + " שח ", LocalDateTime.now(), owner);

		owner.addMessageToList(customerMessage);

		db.addInstance(customerMessage);

		// free Seat
		selectedSeat.setTaken(false);

		// detach Customer
		relatedPurchase.setCustomer(null);
		selectedTicket.setOwner(null);

		// detach Ticket
		relatedPurchase.setRelatedProduct(null);

		// detach Ticket and Purchase
		owner.removeTicketFromList(selectedTicket);
		owner.removePurchaseFromList(relatedPurchase);

		db.updateEntity(selectedSeat);
		db.updateEntity(relatedPurchase);
		db.updateEntity(selectedTicket);

		db.updateEntity(owner);
		db.removeInstance(selectedTicket);
		db.removeInstance(relatedPurchase);

		sendMessage(message, "refunded Ticket successfully", "refund successful", client);
	}

	private void handleCustomerLinkRefundRequest(Message message, ConnectionToClient client) throws IOException{
		String productId = message.getData();

		Link selectedLink = db.executeNativeQuery("SELECT * FROM links WHERE id = ?", Link.class, productId).get(0);

		Purchase relatedPurchase = db.executeNativeQuery("SELECT * FROM purchases WHERE relatedProduct_id = ?", Purchase.class, productId).get(0);

		List<Complaint> relatedComplaints = db.executeNativeQuery("SELECT * FROM complaints WHERE relatedPurchase_id = ?", Complaint.class, relatedPurchase.getId());

		if (relatedComplaints != null && !relatedComplaints.isEmpty()) {
			for (int i=0;i<relatedComplaints.size();i++) {
				Complaint relatedComplaint = relatedComplaints.get(0);
				//detach Complaint
				relatedComplaint.setRelatedPurchase(null);
				relatedComplaint.setResponse("הרכישה בוטלה");
			}

		}

		Customer owner = selectedLink.getOwner();

		CustomerMessage customerMessage = new CustomerMessage("פיצוי כספי", "קיבלת פיצוי כספי עבור רכישת לינק לסרט: " + selectedLink.getHomeMovie().getMovieName() + " בסך " + selectedLink.getPrice() + " שח ", LocalDateTime.now(), owner);

		owner.addMessageToList(customerMessage);

		db.addInstance(customerMessage);

		// detach Customer
		relatedPurchase.setCustomer(null);
		selectedLink.setOwner(null);

		// detach Ticket
		relatedPurchase.setRelatedProduct(null);

		// detach Ticket and Purchase
		owner.removeLinkFromList(selectedLink);
		owner.removePurchaseFromList(relatedPurchase);

		db.updateEntity(relatedPurchase);
		db.updateEntity(selectedLink);

		db.updateEntity(owner);
		db.removeInstance(selectedLink);
		db.removeInstance(relatedPurchase);

		sendMessage(message, "refunded Link successfully", "refund successful", client);
	}

	private void handleRemoveScreeningTimeRequest(Message message, ConnectionToClient client) throws IOException{
		String screeningTimeId = message.getData();

		ScreeningTime selectedScreeningTime = db.executeNativeQuery("SELECT * FROM screeningtimes WHERE id = ?", ScreeningTime.class, screeningTimeId).get(0);

		InTheaterMovie relatedMovie = selectedScreeningTime.getInTheaterMovie();

		//debug
		System.out.println(relatedMovie.getMovieName());
		System.out.println(selectedScreeningTime.getInTheaterMovie().getMovieName());

		selectedScreeningTime.setInTheaterMovie(null);

		//more debug

		System.out.println(selectedScreeningTime.getTime());

		relatedMovie.removeScreeningTime(selectedScreeningTime);

		db.updateEntity(relatedMovie);



		List<Seat> screeningSeats = selectedScreeningTime.getSeats();

		for (Seat seat : screeningSeats) {
			seat.setScreening(null);
			seat.setTheater(null);
		}

		selectedScreeningTime.setSeats(null);
		db.updateEntity(selectedScreeningTime);

		for (Seat seat : screeningSeats) {
			db.removeInstance(seat);
		}

		selectedScreeningTime.setBranch(null);
		selectedScreeningTime.setTheater(null);

		db.updateEntity(selectedScreeningTime);
		db.removeInstance(selectedScreeningTime);

		sendMessage(message, "removed ScreeningTime successfully", "ScreeningTime removal successful", client);
	}

	private void handleRemoveInTheaterMovieFromBranchRequest(Message message, ConnectionToClient client) throws IOException{
		String[] messageData = message.getData().split(",");

		String inTheaterMovieId = messageData[0];

		InTheaterMovie selectedMovie = db.executeNativeQuery("SELECT * FROM intheatermovies WHERE id = ?", InTheaterMovie.class, inTheaterMovieId).get(0);

		Branch selectedBranch = db.executeNativeQuery("SELECT * FROM branches WHERE location = ?", Branch.class, messageData[1]).get(0);

		selectedMovie.removeBranch(selectedBranch);
		selectedBranch.removeInTheaterMovieList(selectedMovie);

		db.updateEntity(selectedMovie);
		db.updateEntity(selectedBranch);

		sendMessage(message, "removed intheatermovie from branch successfully", "intheatermovie removal successful", client);
	}

	private void handleLogOutRequest(Message message, ConnectionToClient client) throws IOException{
		Integer userId = Integer.parseInt(message.getData());
		connectedUsers.remove(userId);
	}

	private void handleCreateComplaintRequest(Message message, ConnectionToClient client) throws IOException{

		String[] messageData = message.getData().split(","); // id, title, content, relatedPurchase_id

		String customerId = messageData[0];

		Customer customer = db.executeNativeQuery("SELECT * FROM customers WHERE id = ?", Customer.class, customerId).get(0);


		Purchase purchase  = db.executeNativeQuery("SELECT * FROM purchases WHERE id = ?", Purchase.class, messageData[3]).get(0);


		String complaintTitle = messageData[1];
		String complaintContent = messageData[2];
		String productType;

		if (purchase.getRelatedProduct().getClass().getName().substring(55).equals("Ticket")) {
			productType = "כרטיס";
		}
		else if (purchase.getRelatedProduct().getClass().getName().substring(55).equals("Link")) {
			productType = "לינק";
		}
		else {
			productType = "כרטיסייה";
		}

		String messageBody = "תלונתך בנוגע לרכישת: " + productType + " התקבלה ותטופל בתוך 24 שעות";

		CustomerMessage customerMessage = new CustomerMessage("תלונה חדשה",  "[ " + messageBody + " ]", LocalDateTime.now(), customer)  ;
		customer.addMessageToList(customerMessage);
		db.addInstance(customerMessage);

		Complaint newComplaint = new Complaint(purchase, customer, LocalTime.now(), complaintTitle, complaintContent);
		customer.addComplaintToList(newComplaint);


		db.addInstance(newComplaint);
		db.updateEntity(customer);


		sendMessage(message, "added Complaint successfully", "complaint submission successful", client);
	}

	public void handleHandleComplaint(Message message, ConnectionToClient client) throws IOException {
		String[] messageData = message.getData().split(","); //id, refund, response
		Complaint complaint = db.executeNativeQuery("SELECT * FROM complaints WHERE id = ?", Complaint.class, messageData[0]).get(0);
		String response = "סכום הזיכוי: " + messageData[1] + "\n";
		response += messageData[2];
		String responseFormat = "[" + response + "]";
		// add the response to the matching complaint
		complaint.setResponse(responseFormat);
		db.updateEntity(complaint);

		//update the complaint sender's entity
		Customer customer = complaint.getCreator();
		db.updateEntity(customer);

		sendMessage(message, "handled Complaint successfully", "complaint submission successful", client);

	}

	public void handleCreateNewPriceChangeRequest(Message message, ConnectionToClient client) throws IOException {
		String[] messageData = message.getData().split(",");

		ContentManager requestingEmployee = db.executeNativeQuery("SELECT * FROM contentmanagers WHERE id = ?", ContentManager.class, messageData[0]).get(0);

		PriceChangeRequest newRequest = new PriceChangeRequest(requestingEmployee, messageData[1], Double.parseDouble(messageData[2]));

		db.addInstance(newRequest);
		db.updateEntity(requestingEmployee);


		sendMessage(message, "create NewPriceRequest successfully", "NewPriceRequest creation successful", client);
	}

	private void handlePriceChangeRequestListRequest(Message message, ConnectionToClient client) throws IOException{
		List<PriceChangeRequest> requestList = db.getAll(PriceChangeRequest.class, false);

		ArrayList<String> requestsToString = new ArrayList<>();

		for (PriceChangeRequest request: requestList){
			requestsToString.add(request.toString());
		}

		sendMessage(message, "updated PriceChangeRequest list successfully", requestsToString, client);
	}

	private void handlePriceChangeUpdateRequest(Message message, ConnectionToClient client) throws IOException {
		String[] messageData = message.getData().split(",");

		PriceChangeRequest request = db.executeNativeQuery("SELECT * FROM pricechangerequest WHERE id = ?", PriceChangeRequest.class, messageData[0]).get(0);

		if (messageData[1].equals("accept")) {
			request.setStatus("accepted");

			Price productPrice = db.executeNativeQuery("SELECT * FROM prices WHERE productClass = ?", Price.class, request.getSelectedProduct()).get(0);

			productPrice.setPrice(request.getNewPrice());

			db.updateEntity(productPrice);
		}
		else {
			request.setStatus("rejected");
		}

		db.updateEntity(request);

		sendMessage(message, "updated PriceChangeRequest successfully", "updated request successfully", client);
	}

	private void handleBranchTicketReportRequest(Message message, ConnectionToClient client) throws IOException {
		String branchLocation = message.getData();

		List<Purchase> allPurchases = db.getAll(Purchase.class, false);

		ArrayList<String> reportToString = new ArrayList<>();
		ArrayList<Integer> ticketAmounts = new ArrayList<>();

		for (int i = 0; i < 31; ++i) {
			ticketAmounts.add(0);
		}

		for (Purchase purchase: allPurchases){
			// if payment was made in current month
			if (purchase.getPaymentDate().getMonthValue() == LocalDate.now().getMonthValue() && purchase.getPaymentDate().getYear() == LocalDate.now().getYear()) {
				// if product is ticket
				if (purchase.getRelatedProduct().getClass().getName().substring(55).equals("Ticket")) {
					// if the branch is equal to the specified one
					if (((Ticket) purchase.getRelatedProduct()).getScreeningTime().getBranch().getLocation().equals(branchLocation)) {
						ticketAmounts.set(purchase.getPaymentDate().getDayOfMonth() - 1, ticketAmounts.get(purchase.getPaymentDate().getDayOfMonth() - 1) + 1);
					}
				}
			}
		}

		for (int amount : ticketAmounts){
			reportToString.add(String.valueOf(amount));
		}

		System.out.println(reportToString);

		sendMessage(message, "updated Branch Ticket report successfully", reportToString, client);
	}

	private void handleCompanyTicketReportRequest(Message message, ConnectionToClient client) throws IOException {

		List<Purchase> allPurchases = db.getAll(Purchase.class, false);

		ArrayList<String> reportToString = new ArrayList<>();
		ArrayList<Integer> ticketAmounts = new ArrayList<>();

		for (int i = 0; i < 31; ++i) {
			ticketAmounts.add(0);
		}

		for (Purchase purchase: allPurchases){
			// if payment was made in current month
			if (purchase.getPaymentDate().getMonthValue() == LocalDate.now().getMonthValue() && purchase.getPaymentDate().getYear() == LocalDate.now().getYear()) {
				// if product is ticket
				if (purchase.getRelatedProduct().getClass().getName().substring(55).equals("Ticket")) {
					ticketAmounts.set(purchase.getPaymentDate().getDayOfMonth() - 1, ticketAmounts.get(purchase.getPaymentDate().getDayOfMonth() - 1) + 1);
				}
			}
		}

		for (int amount : ticketAmounts){
			reportToString.add(String.valueOf(amount));
		}

		System.out.println(reportToString);

		sendMessage(message, "updated Company Ticket report successfully", reportToString, client);
	}

	private void handleCompanySubscriptionCardLinkReportRequest(Message message, ConnectionToClient client) throws IOException {

		List<Purchase> allPurchases = db.getAll(Purchase.class, false);

		ArrayList<String> reportToString = new ArrayList<>();
		ArrayList<Integer> amounts = new ArrayList<>();

		for (int i = 0; i < 31; ++i) {
			amounts.add(0);
		}

		for (Purchase purchase: allPurchases){
			// if payment was made in current month
			if (purchase.getPaymentDate().getMonthValue() == LocalDate.now().getMonthValue() && purchase.getPaymentDate().getYear() == LocalDate.now().getYear()) {
				// if product is ticket
				if (!purchase.getRelatedProduct().getClass().getName().substring(55).equals("Ticket")) {
					amounts.set(purchase.getPaymentDate().getDayOfMonth() - 1, amounts.get(purchase.getPaymentDate().getDayOfMonth() - 1) + 1);
				}
			}
		}

		for (int amount : amounts){
			reportToString.add(String.valueOf(amount));
		}

		System.out.println(reportToString);

		sendMessage(message, "updated Company SubscriptionCardLink report successfully", reportToString, client);
	}

//	private void handleBranchComplaintReportRequest(Message message, ConnectionToClient client) throws IOException {
//
//		List<Purchase> allPurchases = db.getAll(Purchase.class, false);
//
//		ArrayList<String> reportToString = new ArrayList<>();
//		ArrayList<Integer> ticketAmounts = new ArrayList<>();
//
//		for (int i = 0; i < 31; ++i) {
//			ticketAmounts.add(0);
//		}
//
//		for (Purchase purchase: allPurchases){
//			// if payment was made in current month
//			if (purchase.getPaymentDate().getMonthValue() == LocalDate.now().getMonthValue() && purchase.getPaymentDate().getYear() == LocalDate.now().getYear()) {
//				// if product is ticket
//				if (purchase.getRelatedProduct().getClass().getName().substring(55).equals("Ticket")) {
//					ticketAmounts.set(purchase.getPaymentDate().getDayOfMonth() - 1, ticketAmounts.get(purchase.getPaymentDate().getDayOfMonth() - 1) + 1);
//				}
//			}
//		}
//
//		for (int amount : ticketAmounts){
//			reportToString.add(String.valueOf(amount));
//		}
//
//		System.out.println(reportToString);
//
//		sendMessage(message, "updated Company Ticket report successfully", reportToString, client);
//	}

	private void handleCompanyComplaintReportRequest(Message message, ConnectionToClient client) throws IOException {

		List<Complaint> allComplaints = db.getAll(Complaint.class, false);

		ArrayList<String> reportToString = new ArrayList<>();
		ArrayList<Integer> complaintAmounts = new ArrayList<>();

		for (int i = 0; i < 31; ++i) {
			complaintAmounts.add(0);
		}

		for (Complaint complaint : allComplaints){
			// if payment was made in current month
			if (complaint.getReceivedDate().getMonthValue() == LocalDate.now().getMonthValue() && complaint.getReceivedDate().getYear() == LocalDate.now().getYear()) {
				complaintAmounts.set(complaint.getReceivedDate().getDayOfMonth() - 1, complaintAmounts.get(complaint.getReceivedDate().getDayOfMonth() - 1) + 1);
			}

		}

		for (int amount : complaintAmounts){
			reportToString.add(String.valueOf(amount));
		}

		System.out.println(reportToString);

		sendMessage(message, "updated Company Complaint report successfully", reportToString, client);
	}

	public void handleBranchComplaintReportRequest(Message message, ConnectionToClient client) throws IOException {

		String[] messageData = message.getData().split(",");

		Branch branch = db.executeNativeQuery("SELECT * FROM branches WHERE location = ?", Branch.class, messageData[0]).get(0);

		List<Complaint> allComplaints = db.getAll(Complaint.class, false);

		List<Complaint> branchComplaints = new ArrayList<>();

		for(Complaint complaint : allComplaints){
			if (complaint.getRelatedPurchase().getRelatedProduct().getClass().getName().substring(55).equals("Ticket")) {
				Ticket ticket = db.executeNativeQuery("SELECT * FROM tickets WHERE id = ?", Ticket.class, complaint.getRelatedPurchase().getRelatedProduct().getId()).get(0);
				if (ticket.getScreeningTime().getBranch().getLocation().equals(branch.getLocation())) {
					System.out.println(ticket);
					branchComplaints.add(complaint);
				}
			}
		}

		ArrayList<String> reportToString = new ArrayList<>();
		ArrayList<Integer> complaintAmounts = new ArrayList<>();

		for (int i = 0; i < 31; ++i) {
			complaintAmounts.add(0);
		}

		for (Complaint complaint : branchComplaints){
			// if payment was made in current month
			if (complaint.getReceivedDate().getMonthValue() == LocalDate.now().getMonthValue() && complaint.getReceivedDate().getYear() == LocalDate.now().getYear()) {
				complaintAmounts.set(complaint.getReceivedDate().getDayOfMonth() - 1, complaintAmounts.get(complaint.getReceivedDate().getDayOfMonth() - 1) + 1);
			}

		}

		for (int amount : complaintAmounts){
			reportToString.add(String.valueOf(amount));
		}

		System.out.println(reportToString);

		sendMessage(message, "updated Branch Complaint report successfully", reportToString, client);
	}



	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
		Message message = (Message) msg;
		String request = message.getMessage();
		System.out.println("Received request: " + request);
		try {
			// if empty message is received
			if (request.isBlank()){
				message.setMessage("Error! we got an empty message");
				client.sendToClient(message);
			}

			else if (request.equals("add client")){
				handleNewClient(message, client);
			}

			else if (request.equals("get Branch list")){
				handleBranchListRequest(message, client);
			}

			else if (request.equals("get InTheaterMovie list")){
				handleInTheaterMovieListRequest(message, client);
			}

			else if (request.equals("get ComingSoonMovie list")){
				handleComingSoonMovieListRequest(message, client);
			}

			else if(request.equals("get HomeMovie list")) {
				handleHomeMovieListRequest(message, client);
			}

			else if (request.equals("get ScreeningTime list")){
				handleScreeningTimeListRequest(message, client);
			}

			else if (request.equals("get Seat list")) {
				handleSeatListRequest(message, client);
			}

			else if (request.equals("get Theater ID list")) {
				handleTheaterIdListRequest(message, client);
			}

			else if (request.equals("get Theater list")){
				handleTheaterListRequest(message, client);
			}

			else if (request.equals("set ScreeningTime")) {
				handleSetScreeningTimeRequest(message, client);
			}

			else if (request.equals("create ScreeningTime")) {
				handleCreateScreeningTimeRequest(message, client);
			}

			else if (request.equals("verify Customer id")) {
				handleVerifyCustomerIdRequest(message, client);
			}

			else if (request.equals("verify Employee credentials")) {
				handleVerifyEmployeeCredentialsRequest(message, client);
			}

			else if (request.equals("create Customer credentials")) {
				handleCreateCustomerCredentials(message, client);
			}

			else if (request.equals("get Product price")) {
				handleProductPriceRequest(message, client);
			}

			else if (request.equals("create Ticket Purchase")) {
				handleCreateTicketPurchase(message, client);
			}

			else if (request.equals("create Link Purchase")) {
				handleCreateLinkPurchase(message, client);
			}

			else if (request.equals("create SubscriptionCard Purchase")) {
				handleCreateSubscriptionCardPurchase(message, client);
			}

			else if(request.equals("add new coming soon movie")) {
				handleAddComingSoonMovie(message, client);
			}

			else if(request.equals("remove coming soon movie")) {
				handleRemoveComingSoonMovie(message, client);
			}

			else if(request.equals("add new home movie")) {
				handleAddHomeMovie(message, client);
			}

			else if(request.equals("remove home movie")) {
				handleRemoveHomeMovie(message, client);
			}

			else if (request.equals("get Customer Complaint list")) {
				handleCustomerComplaintListRequest(message, client);
			}

			else if (request.equals("get Customer Purchase list")) {
				handleCustomerPurchaseListRequest(message, client);
			}

			else if (request.equals("request Product details")) {
				handleCustomerProductDetailsRequest(message, client);
			}

			else if (request.equals("get Customer SubscriptionCard list")) {
				handleCustomerSubscriptionCardListRequest(message, client);
			}

			else if (request.equals("use SubscriptionCard")) {
				handleCustomerUseSubscriptionCardRequest(message, client);
			}

			else if (request.equals("Customer Ticket Refund")) {
				handleCustomerTicketRefundRequest(message, client);
			}

			else if (request.equals("Customer Link Refund")) {
				handleCustomerLinkRefundRequest(message, client);
			}
			else if(request.equals("get Customer Message list")) {
				handleCustomerMessageListRequest(message, client);
			}

			else if (request.equals("remove ScreeningTime")) {
				handleRemoveScreeningTimeRequest(message, client);
			}

			else if (request.equals("add new in theaters movie")) {
				handleAddInTheaterMovieRequest(message, client);
			}

			else if (request.equals("remove in theaters movie")) {
				handleRemoveInTheaterMovieFromBranchRequest(message, client);
			}

			else if (request.equals("logout user")) {
				handleLogOutRequest(message, client);
			}

			else if (request.equals("submit Customer Complaint")) {
				handleCreateComplaintRequest(message, client);
			}

			else if(request.equals("get Complaint list for ServiceEmployee")) {
				handleServiceEmployeeComplaintListRequest(message, client);
			}

			else if(request.equals("handle complaint")) {
				handleHandleComplaint(message, client);
			}

			else if(request.equals("add new Price change request")) {
				handleCreateNewPriceChangeRequest(message, client);
			}

			else if(request.equals("get Employee Price change requests")) {
				handlePriceChangeRequestListRequest(message, client);
			}

			else if(request.equals("update Product price")) {
				handleProductPriceChangeRequest(message, client);
			}

			else if(request.equals("update PriceChangeRequest")) {
				handlePriceChangeUpdateRequest(message, client);
			}

			else if (request.equals("get Branch Ticket Report")) {
				handleBranchTicketReportRequest(message, client);
			}

			else if (request.equals("get Company Ticket Report")) {
				handleCompanyTicketReportRequest(message, client);
			}

			else if (request.equals("get Company SubscriptionCardLink Report")) {
				handleCompanySubscriptionCardLinkReportRequest(message, client);
			}

			else if (request.equals("get Company Complaint Report")) {
				handleCompanyComplaintReportRequest(message, client);
			}

			else if(request.equals("get Branch Complaint Report")) {
				handleBranchComplaintReportRequest(message, client);
			}

			else {
				//add code here to send received message to all clients.
				//The string we received in the message is the message we will send back to all clients subscribed.
				//Example:
					// message received: "Good morning"
					// message sent: "Good morning"
				//see code for changing submitters IDs for help
//				message.setMessage(request);
//				sendToAllClients(message);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendToAllClients(Message message) {
		try {
			for (SubscribedClient SubscribedClient : SubscribersList) {
				SubscribedClient.getClient().sendToClient(message);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

}
