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
			//we got an empty message, so we will send back an error message with the error details.
			if (request.isBlank()){
				message.setMessage("Error! we got an empty message");
				client.sendToClient(message);
			}
			//we got a request to add a new client as a subscriber.
			else if (request.equals("add client")){
				SubscribedClient connection = new SubscribedClient(client);
				SubscribersList.add(connection);
				message.setMessage("client added successfully");
				client.sendToClient(message);
			}
			// if received message requests the list of branches
			else if (request.equals("get branch list")){
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
				try {
					client.sendToClient(message);
					System.out.println("Branch request satisfied");
				} catch (IOException e) {
					System.out.println("Message Failed");
					e.printStackTrace();
				}
			}
			else if (request.startsWith("get movie list")){
				Message receivedMessage = (Message) msg;
				// parse message
				String[] splitMessage = receivedMessage.getMessage().split(",");
				String branchLocation = splitMessage[1];
				boolean forceRefresh = Boolean.parseBoolean(splitMessage[2]);
				// get DatabaseBridge instance
				DatabaseBridge db = DatabaseBridge.getInstance();
				// get data
				List<InTheaterMovie> receivedData = db.getAll(InTheaterMovie.class, forceRefresh);
				ArrayList<String> movieNameAndId = new ArrayList<>();
				// filter movies
				for (InTheaterMovie movie : receivedData) {
					for (Branch branch : movie.getBranches()) {
						if (branch.getLocation().equals(branchLocation)) {
							movieNameAndId.add(String.format("%s,%s", movie.getId(), movie.getMovieName()));
							break;
						}
					}
				}
				// send message
				receivedMessage.setMessage("updated InTheaterMovie list successfully");
				String listAsJson = mapper.writeValueAsString(movieNameAndId);
				receivedMessage.setData(listAsJson);
				try {
					client.sendToClient(receivedMessage);
					System.out.println(String.format("InTheaterMovieList request satisfied for branch %s", branchLocation));
				} catch (IOException e) {
					System.out.println("Message Failed");
					e.printStackTrace();
				}
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
