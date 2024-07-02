package il.cshaifasweng.OCSFMediatorExample.server;

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
import java.util.List;

public class SimpleServer extends AbstractServer {
	private static ArrayList<SubscribedClient> SubscribersList = new ArrayList<>();

	public SimpleServer(int port) {
		super(port);
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
				// cast received message to requested class
				// Message<Branch> receivedMessage = (Message<Branch>) msg;
				Message receivedMessage = (Message) msg;
				// get DatabaseBridge instance
				DatabaseBridge db = DatabaseBridge.getInstance();
				// get data
				List<Branch> receivedData = db.getAll(Branch.class, true);
				// modify message
				receivedMessage.setMessage("updated branch list successfully");
				receivedMessage.setDataList(receivedData);
				// send message
				try {
					client.sendToClient(receivedMessage);
					System.out.println("Branch request satisfied");
				} catch (IOException e) {
					System.out.println("Message Failed");
					e.printStackTrace();
				}
			}
			//we got a message from client requesting to echo Hello, so we will send back to client Hello world!
			else if(request.startsWith("echo Hello")){
				message.setMessage("Hello World!");
				client.sendToClient(message);
			}
			else if(request.startsWith("send Submitters IDs")){
				//add code here to send submitters IDs to client
				message.setMessage("214732745, 211754502");
				client.sendToClient(message);
			}
			else if (request.startsWith("send Submitters")){
				//add code here to send submitters names to client
				message.setMessage("Daniel, Dan");
				client.sendToClient(message);
			}
			else if (request.equals("what’s the time?") || request.equals("what's the time?")) {
				//add code here to send the time to client
				DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);
				String currentDate = LocalDate.now().format(formatter);
				message.setMessage(currentDate);
				client.sendToClient(message);
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
