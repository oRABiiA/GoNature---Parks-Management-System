package gui.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import logic.ExternalUser;
import logic.Order;
import logic.Park;
import logic.Visitor;
import utils.CurrentDateAndTime;
import utils.NotificationMessageTemplate;

/**
 * Controller class for the Payment Receipt screen, responsible for displaying
 * the payment receipt details.
 */
public class PaymentReceiptScreenController implements Initializable {

	@FXML
	public Label dateLabel;
	@FXML
	public Label paymentReceiptMessageLabel;

	private Order order;
	private Park park;
	private ExternalUser visitor;

	/**
	 * Constructs a new instance of PaymentReceiptScreenController with the
	 * specified order, park, and visitor objects.
	 * 
	 * @param order   The order object containing payment information.
	 * @param park    The park object related to the order.
	 * @param visitor The visitor object who made the payment.
	 */
	public PaymentReceiptScreenController(Object order, Object park, Object visitor) {
		this.order = (Order) order;
		this.visitor = (ExternalUser) visitor;
		this.park = (Park) park;
	}

	/**
	 * Initializes the Payment Receipt screen, setting the current date and
	 * calculating the price after discount.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		dateLabel.setText(CurrentDateAndTime.getCurrentDate("'Today' yyyy-MM-dd"));
		calculatePriceAfterDiscount();
		Visitor traveler = (Visitor) visitor;
//		String paymentReceipt = NotificationMessageTemplate.paymentReceiptMessage(order.getParkName(),traveler.getFirstName(),traveler.getLastName(),(double)order.getPrice(),park.getCurrentEstimatedStayTime());
//		paymentReceiptMessageLabel.setText(paymentReceipt);
	}

	/**
	 * Calculates the price after applying any applicable discounts. This method is
	 * currently empty and needs to be implemented based on discount calculation
	 * logic.
	 */
	private void calculatePriceAfterDiscount() {

	}

}
