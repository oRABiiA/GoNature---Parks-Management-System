package gui.controller;

import java.net.URL;
import java.time.Duration;
import java.util.Optional;
import java.util.ResourceBundle;

import client.ClientApplication;
import client.ClientCommunication;
import gui.view.ApplicationViewType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import logic.ClientRequestDataContainer;
import logic.EntitiesContainer;
import logic.Order;
import logic.SceneLoaderHelper;
import logic.ServerResponseBackToClient;
import utils.AlertPopUp;
import utils.CurrentDateAndTime;
import utils.EntranceDiscount;
import utils.NotificationMessageTemplate;
import utils.enums.ClientRequest;
import utils.enums.OrderStatusEnum;

/**
 * Controller class for the Order Summary screen.
 */
public class OrderSummaryScreenController implements Initializable {
	@FXML
	public Label dateLabel;
	@FXML
	public Label messageLabel;
	@FXML
	public Button cancelButton;
	@FXML
	public Button continueButton;
	@FXML
	public HBox buttonsHbox;

	private Order order;
	private String orderSummaryMessage;
	private BorderPane screen;
	private double priceBeforeDiscount;
	private double priceAfterDiscount;
	private long estimatedVisitTime;

	/**
	 * Initializes the Order Summary screen controller.
	 * 
	 * @param screen The BorderPane representing the screen.
	 * @param order  The Order object.
	 */
	public OrderSummaryScreenController(BorderPane screen, Object order) {
		this.screen = screen;
		this.order = (Order) order;
		calculatePriceByOrderType();
		Duration duration = Duration.between(this.order.getEnterDate(), this.order.getExitDate());
		estimatedVisitTime = duration.toHours();

		orderSummaryMessage = NotificationMessageTemplate.orderSummaryMessage(this.order.getParkName().name(),
				this.order.getOrderType(), this.order.getEnterDate().toString(), this.order.getOrderType().name(),
				this.order.getNumberOfVisitors(), priceAfterDiscount, priceBeforeDiscount);
	}

	/**
	 * Calculates the price based on the order type.
	 */
	@SuppressWarnings("incomplete-switch")
	private void calculatePriceByOrderType() {
		switch (order.getOrderType()) {
		case Solo_PreOrder:
		case Family_PreOrder:
			priceBeforeDiscount = order.getPrice() * order.getNumberOfVisitors();
			priceAfterDiscount = priceBeforeDiscount * EntranceDiscount.SOLO_FAMILY_PREORDER_DISCOUNT;
			break;
		case Group_PreOrder:
			priceBeforeDiscount = order.getPrice() * order.getNumberOfVisitors()
					* EntranceDiscount.GROUP_PREORDER_DISCOUNT;
			priceAfterDiscount = priceBeforeDiscount * EntranceDiscount.ADDITIONAL_GROUP_DISCOUNT;
			break;
		}

	}

	/**
	 * Initializes the Order Summary screen.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		dateLabel.setText(CurrentDateAndTime.getCurrentDate("'Today' yyyy-MM-dd"));
		messageLabel.setText(orderSummaryMessage);
	}

	/**
	 * Handles the action when the cancel button is clicked.
	 */
	public void onCancelClicked() {
		AlertPopUp alert = new AlertPopUp(AlertType.CONFIRMATION, "Order Cancel", "Are you sure?",
				"Order will not be saved.", ButtonType.YES, ButtonType.CLOSE);
		Optional<ButtonType> result = alert.showAndWait();

		// Check which button was clicked and act accordingly
		if (result.isPresent() && result.get() == ButtonType.YES) {
			AnchorPane view = SceneLoaderHelper.getInstance().loadRightScreenToBorderPaneWithController(screen,
					"/gui/view/CustomerHomepageScreen.fxml", ApplicationViewType.Customer_Homepage_Screen,
					new EntitiesContainer(order));
			screen.setCenter(view);
		}

	}

	/**
	 * Handles the action when the continue button is clicked.
	 */
	@SuppressWarnings("incomplete-switch")
	public void onContinueClicked() {
		// TODO: update the order in database as Confirmed-Paid
		ButtonType payNow = new ButtonType("Pay Now");
		ButtonType payLater = new ButtonType("Pay Later");

		String paymentReceipt = NotificationMessageTemplate.prePaymentReceiptMessage(order.getParkName(),
				order.getOrderType(), order.getEnterDate().toString(), order.getNumberOfVisitors(),
				order.getFirstName(), order.getLastName(), priceAfterDiscount, priceBeforeDiscount, estimatedVisitTime);

		AlertPopUp alert = new AlertPopUp(AlertType.CONFIRMATION, "Payment Notification", "Pay Now", paymentReceipt,
				payNow, payLater, ButtonType.CLOSE);
		Optional<ButtonType> result = alert.showAndWait();

		// Check which button was clicked and act accordingly
		if (result.isPresent() && result.get() == payNow) {
			order.setPaid(true);
			order.setPrice(priceAfterDiscount);
		} else if (result.isPresent() && result.get() == payLater) {
			order.setPaid(false);
			order.setPrice(priceBeforeDiscount);
		} else {
			return;
		}

		order.setStatus(OrderStatusEnum.Wait_Notify);
		ClientRequestDataContainer request = new ClientRequestDataContainer(
				ClientRequest.Insert_New_Order_As_Wait_Notify, order);
		ClientApplication.client.accept(request);
		ServerResponseBackToClient response = ClientCommunication.responseFromServer;
		Order orderFullDetailed = (Order) response.getMessage();
		switch (response.getRensponse()) {
		case Order_Added_Successfully:
			buttonsHbox.setVisible(false);
			String orderSummaryAfterPaymentMessage = NotificationMessageTemplate.orderConfirmMessage(
					orderFullDetailed.getOrderId(), orderFullDetailed.getParkName().name(),
					orderFullDetailed.getEnterDate().toString(), orderFullDetailed.getOrderType().name(),
					orderFullDetailed.getNumberOfVisitors(), orderFullDetailed.getPrice(), orderFullDetailed.isPaid());
			messageLabel.setText(orderSummaryAfterPaymentMessage);
			return;
		case Order_Added_Failed:
			return;
		}

	}

}
