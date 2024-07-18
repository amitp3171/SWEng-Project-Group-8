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
				SubscribedClient connection = new SubscribedClient(client);
				SubscribersList.add(connection);
				message.setMessage("client added successfully");
				client.sendToClient(message);
			}

			else if (request.equals("get Branch list")){
				// get data
				List<Branch> receivedData = db.getAll(Branch.class, true);
				// modify message
				List<String> branchLocations = new ArrayList<>();
				// get branch locations
				for (Branch branch : receivedData) {
					branchLocations.add(branch.getLocation());
				}
				// send message
				message.setMessage("updated branch list successfully");
				String listAsJson = mapper.writeValueAsString(branchLocations);
				message.setData(listAsJson);

				client.sendToClient(message);
				System.out.println("Branch request satisfied");
			}

			else if (request.startsWith("get InTheaterMovie list")){
				// parse message (message_text,branch_location,force_refresh)
				String[] splitMessage = message.getMessage().split(",");
				String branchLocation = splitMessage[1];
				boolean forceRefresh = Boolean.parseBoolean(splitMessage[2]);
				// get DatabaseBridge instance
				DatabaseBridge db = DatabaseBridge.getInstance();
				// get data
				List<InTheaterMovie> receivedData = db.getAll(InTheaterMovie.class, forceRefresh);
				ArrayList<String> movieToString = new ArrayList<>();
				// filter movies
				for (InTheaterMovie movie : receivedData) {
					for (Branch branch : movie.getBranches()) {
						if (branch.getLocation().equals(branchLocation)) {
							movieToString.add(movie.toString());
							break;
						}
					}
				}
				// send message
				message.setMessage("updated InTheaterMovie list successfully");
				String listAsJson = mapper.writeValueAsString(movieToString);
				message.setData(listAsJson);

				client.sendToClient(message);
				System.out.println(String.format("InTheaterMovieList request satisfied for branch %s", branchLocation));
			}

			else if (request.startsWith("get ScreeningTime list")){
				// parse message (message_text,branch_location,movie_id)
				String[] splitMessage = message.getMessage().split(",");
				String branchLocation = splitMessage[1];
				int movieId = Integer.parseInt(splitMessage[2]);
				boolean forceRefresh = Boolean.parseBoolean(splitMessage[3]);
				// get DatabaseBridge instance
				DatabaseBridge db = DatabaseBridge.getInstance();
				// get data
				List<ScreeningTime> receivedData = db.getAll(ScreeningTime.class, forceRefresh);
				ArrayList<String> screeningTimeToString = new ArrayList<>();
				// filter movies
				for (int i = 0; i < receivedData.size(); i++) {
					if (receivedData.get(i).getInTheaterMovie().getId() == movieId && receivedData.get(i).getBranch().getLocation().equals(branchLocation)) {
						screeningTimeToString.add(receivedData.get(i).toString());
					}
				}
				// send message
				message.setMessage("updated ScreeningTime list successfully");
				String listAsJson = mapper.writeValueAsString(screeningTimeToString);

				message.setData(listAsJson);

				client.sendToClient(message);
				System.out.println(String.format("ScreeningTime request satisfied for branch %s", branchLocation));
			}

			else if (request.equals("set ScreeningTime")) {
				// id, day, time, theater.getTheaterID()
				String[] splitMessage = message.getData().split(",");
				ScreeningTime screening = db.executeNativeQuery(String.format("SELECT * FROM ScreeningTimes WHERE id=%s", splitMessage[0]), ScreeningTime.class).get(0);
				screening.setTime(splitMessage[2]);
				db.updateEntity(screening);
				// send message
				message.setMessage("set new ScreeningTime successfully");
				message.setData(null);
				client.sendToClient(message);
				System.out.println(String.format("ScreeningTime update request satisfied"));
			}

			else if (request.equals("verify Customer id")) {
				List<Customer> result = db.executeNativeQuery(String.format("SELECT * FROM customers WHERE idNum=%s", message.getData()), Customer.class);
				if (result.isEmpty()) {//TODO: handle

				}

				else {
					Customer customer = result.get(0);
				}

				// send message
				message.setMessage("set new ScreeningTime successfully");
				message.setData(null);
				client.sendToClient(message);
				System.out.println(String.format("ScreeningTime update request satisfied"));
			}

			else if (request.startsWith("multiply")){
				//add code here to multiply 2 numbers received in the message and send result back to client
				//(use substring method as shown above)
				//message format: "multiply n*m"
				String[] operands = request.substring(9).split("[*]");
				int result = Integer.parseInt(operands[0])*Integer.parseInt(operands[1]);
				message.setMessage(Integer.toString(result));
				client.sendToClient(message);
			}else{
				//add code here to send received message to all clients.
				//The string we received in the message is the message we will send back to all clients subscribed.
				//Example:
					// message received: "Good morning"
					// message sent: "Good morning"
				//see code for changing submitters IDs for help
				message.setMessage(request);
				sendToAllClients(message);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
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
