package il.cshaifasweng.OCSFMediatorExample.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.AbstractServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.SubscribedClient;
import il.cshaifasweng.OCSFMediatorExample.server.dataClasses.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.lang.*;
import java.util.Map;

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
		String[] splitMessage = message.getMessage().split(",");
		String branchLocation = splitMessage[1];
		int movieId = Integer.parseInt(splitMessage[2]);
		boolean forceRefresh = Boolean.parseBoolean(splitMessage[3]);

		// get data
		List<ScreeningTime> receivedData = db.getAll(ScreeningTime.class, forceRefresh);
		ArrayList<String> screeningTimeToString = new ArrayList<>();
		// filter movies
		for (int i = 0; i < receivedData.size(); i++) {
			if (receivedData.get(i).getInTheaterMovie().getId() == movieId && receivedData.get(i).getBranch().getLocation().equals(branchLocation)) {
				screeningTimeToString.add(receivedData.get(i).toString());
			}
		}

		sendMessage(message, "updated ScreeningTime list successfully", screeningTimeToString, client);
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

	private void handleSetScreeningTimeRequest(Message message, ConnectionToClient client) throws IOException {
		// id, day, time, theater.getTheaterID()
		String[] splitMessage = message.getData().split(",");
		ScreeningTime screening = db.executeNativeQuery("SELECT * FROM ScreeningTimes WHERE id=?", ScreeningTime.class, splitMessage[0]).get(0);
		screening.setTime(splitMessage[2]);
		db.updateEntity(screening);

		sendMessage(message, "set new ScreeningTime successfully", "null", client);
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
		List<ScreeningTime> existingScreenings = db.executeNativeQuery("SELECT * FROM ScreeningTimes WHERE theater_theaterID=? AND time=?", ScreeningTime.class, splitMessage[3], splitMessage[2]);

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
			items.add(String.format("%s,%s", theater.getTheaterID(), theater.getTheaterNumber()));

		// send message
		sendMessage(message, "updated Theater list successfully", items, client);
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
				data = String.format("%s,%s,%s", employee.getFirstName(), employee.getLastName(), employee.getClass().getName().substring(55));
		}

		System.out.println(data);

		sendMessage(message, "verified Employee credentials successfully", data, client);
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

			else if (request.startsWith("get InTheaterMovie list")){
				handleInTheaterMovieListRequest(message, client);
			}

			else if (request.startsWith("get ScreeningTime list")){
				handleScreeningTimeListRequest(message, client);
			}

			else if (request.startsWith("get Theater ID list")) {
				handleTheaterIdListRequest(message, client);
			}

			else if (request.startsWith("get Theater list")){
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
