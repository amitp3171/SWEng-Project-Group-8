package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.client.events.*;
import il.cshaifasweng.OCSFMediatorExample.entities.Message;
import org.greenrobot.eventbus.EventBus;

import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;

import java.util.Arrays;
import java.util.List;

public class SimpleClient extends AbstractClient {
	private static SimpleClient client = null;

	List<String> successfulPurchaseStatus = Arrays.asList(new String[]{"created SubscriptionCard Purchase successfully", "created Ticket Purchase successfully", "created Link Purchase successfully"});

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

		else if(message.getMessage().equals("updated ComingSoonMovie list successfully")){
			EventBus.getDefault().post(new NewComingSoonMovieListEvent(message));
		}

		else if(message.getMessage().equals("updated HomeMovie list successfully")){
			EventBus.getDefault().post(new NewHomeMovieListEvent(message));
		}

		else if(message.getMessage().equals("updated ScreeningTime list successfully")){
			EventBus.getDefault().post(new NewScreeningTimeListEvent(message));
		}

		else if(message.getMessage().equals("updated Theater ID list successfully")){
			EventBus.getDefault().post(new NewTheaterIdListEvent(message));
		}

		else if(message.getMessage().equals("updated Seat list successfully")){
			EventBus.getDefault().post(new NewSeatListEvent(message));
		}

		else if(message.getMessage().equals("created new ScreeningTime successfully")){
			EventBus.getDefault().post(new NewCreatedScreeningTimeEvent(message));
		}

		else if(message.getMessage().equals("created new ComingSoonMovie successfully")){
			EventBus.getDefault().post(new NewAddedComingSoonMovieEvent(message));
		}

		else if(message.getMessage().equals("removed ComingSoonMovie successfully")) {
			EventBus.getDefault().post(new NewRemovedComingSoonMovieEvent(message));
		}

		else if(message.getMessage().equals("created HomeMovie successfully")) {
			EventBus.getDefault().post(new NewAddedHomeMovieEvent(message));
		}

		else if(message.getMessage().equals("remove HomeMovie successfully")) {
			EventBus.getDefault().post(new NewRemovedHomeMovieEvent(message));
		}

		else if(message.getMessage().equals("verified Customer id successfully")){
			EventBus.getDefault().post(new NewVerifiedCustomerIdEvent(message));
		}

		else if(message.getMessage().equals("verified Employee credentials successfully")){
			EventBus.getDefault().post(new NewVerifiedEmployeeCredentialsEvent(message));
		}

		else if(message.getMessage().equals("created Customer credentials successfully")){
			EventBus.getDefault().post(new NewCreateCustomerCredentialsEvent(message));
		}

		else if (message.getMessage().equals("updated Product price successfully")){
			EventBus.getDefault().post(new NewProductPriceEvent(message));
		}

		else if (successfulPurchaseStatus.contains(message.getMessage())){
			EventBus.getDefault().post(new NewPurchaseStatusEvent(message));
		}

		else if (message.getMessage().equals("updated Customer Complaint list successfully")){
			EventBus.getDefault().post(new NewComplaintListEvent(message));
		}

		else if(message.getMessage().equals("updated Customer Message list successfully")) {
			EventBus.getDefault().post(new NewCustomerMessageListEvent(message));
		}

		else if (message.getMessage().equals("updated Customer Purchase list successfully")){
			EventBus.getDefault().post(new NewPurchaseListEvent(message));
		}

		else if (message.getMessage().equals("updated Product details successfully")){
			EventBus.getDefault().post(new NewProductDetailsEvent(message));
		}

		else if (message.getMessage().equals("updated Customer SubscriptionCard list successfully")){
			EventBus.getDefault().post(new NewSubscriptionCardListEvent(message));
		}

		else if (message.getMessage().equals("used SubscriptionCard successfully")){
			EventBus.getDefault().post(new NewSubscriptionCardUsedEvent(message));
		}

		else if(message.getMessage().equals("client added successfully")){
			EventBus.getDefault().post(new NewSubscriberEvent(message));
		}

		else if(message.getMessage().equals("Error! we got an empty message")){
			EventBus.getDefault().post(new ErrorEvent(message));
		}

		else if (message.getMessage().equals("updated ServiceEmployee Complaint list successfully")) {
			EventBus.getDefault().post(new NewServiceEmployeeComplaintListEvent(message));
		}

		else if(message.getMessage().equals("handled Complaint successfully")) {
			EventBus.getDefault().post(new NewHandledComplaintEvent(message));
		}

		else if(message.getMessage().equals("updated PriceChangeRequest list successfully")) {
			EventBus.getDefault().post(new NewPriceChangeListRequestEvent(message));
		}

		else if(message.getMessage().equals("updated Branch Ticket report successfully")) {
			EventBus.getDefault().post(new NewBranchTicketReportEvent(message));
		}

		else if(message.getMessage().equals("updated Company Ticket report successfully")) {
			EventBus.getDefault().post(new NewCompanyTicketReportEvent(message));
		}

		else if(message.getMessage().equals("updated Company SubscriptionCardLink report successfully")) {
			EventBus.getDefault().post(new NewCompanySubscriptionCardLinkReportEvent(message));
		}

		else if(message.getMessage().equals("updated Company Complaint report successfully")) {
			EventBus.getDefault().post(new NewCompanyComplaintReportEvent(message));
		}

		else if(message.getMessage().equals("updated Branch Complaint report successfully")) {
			EventBus.getDefault().post(new NewBranchComplaintReportEvent(message));
		}

		else {
			EventBus.getDefault().post(new MessageEvent(message));
		}
	}
	
	public static SimpleClient getClient() {
		if (client == null) {
			client = new SimpleClient(CinemaClient.ip, 3000);
		}
		return client;
	}

}
