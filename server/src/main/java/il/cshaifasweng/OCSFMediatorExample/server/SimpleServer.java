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
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.lang.*;

public class SimpleServer extends AbstractServer {
	private static ArrayList<SubscribedClient> SubscribersList = new ArrayList<>();
	private static ObjectMapper mapper = new ObjectMapper();
	DatabaseBridge db = DatabaseBridge.getInstance();

	public SimpleServer(int port) {
		super(port);
		mapper.registerModule(new JavaTimeModule());
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

		// attach flag
		for (InTheaterMovie movie : receivedData) {
			String isInBranch = ",false";
			for (Branch branch : movie.getBranches()) {
				if (branch.getLocation().equals(selectedBranchLocation)) {
					isInBranch = ",true";
					break;
				}
			}
			movieToString.add(movie.toString() + isInBranch);
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
			if (receivedData.get(i).getInTheaterMovie().getId() == movieId && receivedData.get(i).getBranch().getLocation().equals(branchLocation)) {

				screeningTimeToString.add(receivedData.get(i).toString() + "," + receivedData.get(i).isEmptyScreeningTime());
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

		System.out.println(receivedSeats);

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

		for (HomeMovie movie : receivedData)
			movieToString.add(movie.toString());

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
			screening.setTime(splitMessage[2]);
			screening.setDate(LocalDate.parse(splitMessage[1], DateTimeFormatter.ofPattern("yyyy-MM-dd")));
			screening.setTheater(db.executeNativeQuery("SELECT * FROM Theaters WHERE theaterID=?", Theater.class, splitMessage[3]).get(0));
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
			db.addInstance(newScreening);
			// if this screeningTime is the first for the selected movie
			if (!selectedMovie.getBranches().contains(selectedBranch)) {
				selectedMovie.getBranches().add(selectedBranch);
				selectedBranch.getInTheaterMovieList().add(selectedMovie);
				db.updateEntity(selectedMovie);
				db.updateEntity(selectedBranch);
			}
			data = "request successful";
		}

		sendMessage(message, "created new ScreeningTime successfully", data, client);
	}

//	private void handleRemoveScreeningTime(Message message, ConnectionToClient client) throws IOException {
//		// parse message: date, time, theater, movie
//		String[] splitMessage = message.getData().split(",");
//
//		ScreeningTime screeningTime = db.executeNativeQuery("SELECT * FROM ScreeningTimes WHERE id=?", ScreeningTime.class, splitMessage[0]).get(0);
//		screeningTime.getInTheaterMovie().getScreenings().remove(screeningTime);
//		db.updateEntity(screeningTime);
//		db.removeInstance(screeningTime);
//
//		String data = "request successful";
//
//		sendMessage(message, "removed screeningTime successfully", data, client);
//
//	}

	private void handleRemoveScreeningTime(Message message, ConnectionToClient client) throws IOException {
		String[] splitMessage = message.getData().split(",");
		int screeningTimeId = Integer.parseInt(splitMessage[0]);

		try {
			db.getSession().beginTransaction();

			// Fetch the ScreeningTime entity
			ScreeningTime screeningTime = db.getSession().get(ScreeningTime.class, screeningTimeId);

			if (screeningTime != null) {
				System.out.println("Removing associations...");

				// Handle Ticket associations
				for (Ticket ticket : new ArrayList<>(screeningTime.getTickets())) {
					ticket.setScreeningTime(null);
					ticket.setSeat(null);
					db.getSession().update(ticket);
					screeningTime.removeTicket(ticket);
				}

				// Remove from InTheaterMovie
				InTheaterMovie movie = screeningTime.getInTheaterMovie();
				if (movie != null) {
					movie.removeScreeningTime(screeningTime);
					db.getSession().update(movie);
				}

				// Remove from Branch
				Branch branch = screeningTime.getBranch();
				if (branch != null) {
					branch.getScreeningTimes().remove(screeningTime);
					screeningTime.setBranch(null);
					db.getSession().update(branch);
				}

				// Remove from Theater
				Theater theater = screeningTime.getTheater();
				if (theater != null) {
					theater.getScreeningTimes().remove(screeningTime);
					screeningTime.setTheater(null);
					db.getSession().update(theater);
				}

				System.out.println("Clearing seats...");
				for (Seat seat : new ArrayList<>(screeningTime.getSeats())) {
					seat.setTheater(null);
					seat.setScreening(null);
					db.getSession().update(seat);
				}
				screeningTime.getSeats().clear();

				// Flush the session to ensure all updates are processed
				db.getSession().flush();

				// Check if the entity still exists before deleting
				if (db.getSession().contains(screeningTime)) {
					System.out.println("Removing ScreeningTime...");
					db.getSession().delete(screeningTime);
					db.getSession().flush();

					// Double-check if the entity was actually deleted
					if (!db.getSession().contains(screeningTime)) {
						// Commit the transaction
						db.getSession().getTransaction().commit();
						System.out.println("ScreeningTime removed successfully.");
						String data = "request successful";
						sendMessage(message, "removed screeningTime successfully", data, client);
					} else {
						throw new Exception("Failed to delete ScreeningTime");
					}
				} else {
					System.out.println("ScreeningTime no longer exists in the session.");
					db.getSession().getTransaction().commit();
					sendMessage(message, "ScreeningTime already removed", "warning", client);
				}
			} else {
				db.getSession().getTransaction().rollback();
				sendMessage(message, "ScreeningTime not found", "Error", client);
			}
		} catch (Exception e) {
			System.out.println("Error removing ScreeningTime: " + e.getMessage());
			e.printStackTrace();
			db.getSession().getTransaction().rollback();
			sendMessage(message, "Error removing ScreeningTime", e.getMessage(), client);
		}
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
		InTheaterMovie inTheaterMovie = new InTheaterMovie(splitMovieData[0], splitMovieData[1], mainActorsList, splitMovieData[3], splitMovieData[3]);
		inTheaterMovie.addBranch(branch);
		branch.addInTheaterMovieToList(inTheaterMovie);
		System.out.println(inTheaterMovie);
		String data = "request successful";
		db.addInstance(inTheaterMovie);
		sendMessage(message, "created new InTheaterMovie successfully", data, client);

	}

	private void handleRemoveComingSoonMovie(Message message, ConnectionToClient client) throws IOException {
		// Parse the incoming movie data string
		String[] splitMovieData = message.getData().split(",");

		ComingSoonMovie movieToRemove = db.executeNativeQuery("SELECT * FROM ComingSoonMovie WHERE movieName=?", ComingSoonMovie.class, splitMovieData[1]).get(0);

		//Remove movie
		db.removeInstance(movieToRemove);
		// Send success message back to the client
		sendMessage(message, "removed ComingSoonMovie successfully", "success", client);

	}

	private void handleRemoveHomeMovie(Message message, ConnectionToClient client) throws IOException {
		// Parse the incoming movie data string
		String[] splitMovieData = message.getData().split(",");

		HomeMovie movieToRemove = db.executeNativeQuery("SELECT * FROM HomeMovie WHERE movieName=?", HomeMovie.class, splitMovieData[1]).get(0);

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
			data = String.format("%s,%s", customer.getFirstName(), customer.getLastName());
		}

		sendMessage(message, "verified Customer id successfully", data, client);
	}

	private void handleVerifyEmployeeCredentialsRequest(Message message, ConnectionToClient client) throws IOException {
		List<AbstractEmployee> employees = db.getAll(AbstractEmployee.class, false);

		String[] providedCredentials = message.getData().split(",");

		String data = "user invalid";

		for (AbstractEmployee employee : employees) {
			if (employee.getUsername().equals(providedCredentials[0]) && employee.getPassword().equals(providedCredentials[1]))
				data = String.join(",", employee.getFirstName(), employee.getLastName(), employee.getClass().getName().substring(55));
		}

		System.out.println(data);

		sendMessage(message, "verified Employee credentials successfully", data, client);
	}

	private void handleProductPriceRequest(Message message, ConnectionToClient client) throws IOException {
		Price productPrice = db.executeNativeQuery("SELECT * FROM prices WHERE productClass=?", Price.class, message.getMessage().split("\\s+")[1]).get(0);

		sendMessage(message, "updated Product price successfully", String.valueOf(productPrice.getPrice()), client);
	}

	private void handleCreateCustomerCredentials(Message message, ConnectionToClient client) throws IOException {
		// id, firstname, lastname
		String[] providedCredentials = message.getData().split(",");

		List<Customer> result = db.executeNativeQuery("SELECT * FROM customers WHERE govId=?", Customer.class, providedCredentials[0]);
		String data = "user created";

		// user exists
		if (!result.isEmpty()) {
			Customer customer = result.get(0);
			data = String.format("%s,%s", customer.getFirstName(), customer.getLastName());
		}
		else {
			Customer newCustomer = new Customer(providedCredentials[1], providedCredentials[2], providedCredentials[0]);
			db.addInstance(newCustomer);
		}

		sendMessage(message, "created Customer credentials successfully", data, client);
	}

	private void handleCreateTicketPurchase(Message message, ConnectionToClient client) throws IOException {
		// govId, screeningId, seatIds, amountOfTickets, ticketPrice
		String[] messageData = message.getData().split(",(?![^\\[]*\\])");

		String customerGovId = messageData[0];
		String screeningTimeId = messageData[1];
		String[] selectedSeatIds = messageData[2].substring(1, messageData[2].length()-1).split(",");

		System.out.println(messageData[2]);
		System.out.println(selectedSeatIds);

		List<Seat> selectedSeats = db.executeNativeQuery("SELECT * FROM seats WHERE id IN (" + String.join(",", Collections.nCopies(selectedSeatIds.length, "?")) + ")", Seat.class, selectedSeatIds);

		System.out.println(selectedSeats);

		String productPrice = messageData[3];

		Customer owner = db.executeNativeQuery("SELECT * FROM customers WHERE govId=?", Customer.class, customerGovId).get(0);
		ScreeningTime screeningTime = db.executeNativeQuery("SELECT * FROM screeningtimes WHERE id=?", ScreeningTime.class, screeningTimeId).get(0);

		for (int i = 0; i < selectedSeats.size(); i++) {
			Ticket newTicket = new Ticket(owner, Double.parseDouble(productPrice), screeningTime.getInTheaterMovie().getMovieName(), screeningTime, selectedSeats.get(i));
			Purchase newPurchase = new Purchase(newTicket, "Credit Card", LocalTime.now());
			Seat selectedSeat = selectedSeats.get(i);
			selectedSeat.setTaken(true);
			db.addInstance(newTicket);
			db.addInstance(newPurchase);
			db.updateEntity(selectedSeat);
		}

		sendMessage(message, "created Ticket Purchase successfully", "payment successful", client);
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

			else if (request.equals("get Ticket price")) {
				handleProductPriceRequest(message, client);
			}

			else if (request.equals("create Ticket Purchase")) {
				handleCreateTicketPurchase(message, client);
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

			else if(request.equals("add new in theaters movie")) {
				handleAddInTheaterMovieRequest(message, client);
			}

			else if(request.equals("remove selected screening time")) {
				handleRemoveScreeningTime(message, client);
			}

			else {
				//add code here to send received message to all clients.
				//The string we received in the message is the message we will send back to all clients subscribed.
				//Example:
					// message received: "Good morning"
					// message sent: "Good morning"
				//see code for changing submitters IDs for help
				message.setMessage(request);
				sendToAllClients(message);
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
