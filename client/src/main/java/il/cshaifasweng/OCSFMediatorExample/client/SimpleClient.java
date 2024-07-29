package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.client.events.*;
import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import org.greenrobot.eventbus.EventBus;

import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;

public class SimpleClient extends AbstractClient {
	private static SimpleClient client = null;

	private SimpleClient(String host, int port) {
		super(host, port);
	}

	@Override
	protected void handleMessageFromServer(Object msg) {
		Message message = (Message) msg;
		System.out.println("Message received: " + message.getMessage());

		if(message.getMessage().equals("update submitters IDs")){
			EventBus.getDefault().post(new UpdateMessageEvent(message));
		}

		else if(message.getMessage().equals("updated branch list successfully")){
			EventBus.getDefault().post(new NewBranchListEvent(message));
		}

		else if(message.getMessage().equals("updated InTheaterMovie list successfully")){
			EventBus.getDefault().post(new NewInTheaterMovieListEvent(message));
		}

		else if(message.getMessage().equals("updated ScreeningTime list successfully")){
			EventBus.getDefault().post(new NewScreeningTimeListEvent(message));
		}

		else if(message.getMessage().equals("updated Theater ID list successfully")){
			EventBus.getDefault().post(new NewTheaterIdListEvent(message));
		}

		else if(message.getMessage().equals("verified Customer id successfully")){
			EventBus.getDefault().post(new NewVerifiedCustomerIdEvent(message));
		}

		else if(message.getMessage().equals("client added successfully")){
			EventBus.getDefault().post(new NewSubscriberEvent(message));
		}

		else if(message.getMessage().equals("Error! we got an empty message")){
			EventBus.getDefault().post(new ErrorEvent(message));
		}
		else {
			EventBus.getDefault().post(new MessageEvent(message));
		}
	}
	
	public static SimpleClient getClient() {
		if (client == null) {
			client = new SimpleClient("localhost", 3000);
		}
		return client;
	}

}
