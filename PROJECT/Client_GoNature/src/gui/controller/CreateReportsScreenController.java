package gui.controller;

import java.net.URL;
import java.util.ResourceBundle;

import client.ClientApplication;
import client.ClientCommunication;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import logic.AmountDivisionReport;
import logic.CancellationsReport;
import logic.ClientRequestDataContainer;
import logic.Employee;
import logic.Report;
import logic.ServerResponseBackToClient;
import logic.UsageReport;
import logic.VisitsReport;
import utils.AlertPopUp;
import utils.CurrentDateAndTime;
import utils.enums.ClientRequest;
import utils.enums.EmployeeTypeEnum;
import utils.enums.ParkNameEnum;
import utils.enums.ReportType;

/**
 * Controller class responsible for managing the UI interactions related to
 * report generation. Implements the Initializable interface to initialize
 * JavaFX components.
 */
public class CreateReportsScreenController implements Initializable {
	@FXML
	public Label dateLabel;
	@FXML
	public ComboBox<ReportType> selectReportComboBox;
	@FXML
	public ComboBox<ParkNameEnum> parkSelector;
	@FXML
	public ComboBox<String> yearSelector;
	@FXML
	public ComboBox<String> monthSelector;
	@FXML
	public Button generateReportButton;
	@FXML
	public HBox errorSection;
	@FXML
	public Label errorMessageLabel;

	@SuppressWarnings("unused")
	private ObservableList<ReportType> reportsList = FXCollections.observableArrayList(ReportType.UsageReport,
			ReportType.VisitsReports, ReportType.CancellationsReport, ReportType.TotalVisitorsReport);
	private ObservableList<ParkNameEnum> parksList = FXCollections.observableArrayList(ParkNameEnum.Banias,
			ParkNameEnum.Herodium, ParkNameEnum.Masada, ParkNameEnum.North, ParkNameEnum.South);
	private ObservableList<String> yearsList = FXCollections.observableArrayList("2024", "2023", "2022", "2021");
	private ObservableList<String> monthsList = FXCollections.observableArrayList("1", "2", "3", "4", "5", "6", "7",
			"8", "9", "10", "11", "12");

	private String selectedYear = "";
	private String selectedMonth = "";
	private ParkNameEnum selectedPark = ParkNameEnum.None;
	private Employee employee;
	private ReportType selectedReportType = ReportType.Unsupported;
	private ObservableList<ReportType> reportList = FXCollections.observableArrayList();

	/**
	 * Constructs a new CreateReportsScreenController with the given employee.
	 * Adjusts available reports and parks based on the employee's type.
	 * 
	 * @param employee The employee for whom the controller is created.
	 */
	public CreateReportsScreenController(Object employee) {
		this.employee = (Employee) employee;
		if (this.employee.getEmployeeType() == EmployeeTypeEnum.Park_Manager) {
			reportsList = FXCollections.observableArrayList(ReportType.UsageReport, ReportType.TotalVisitorsReport);
			parksList = FXCollections.observableArrayList(this.employee.getRelatedPark());
		} else {
			reportsList = FXCollections.observableArrayList(ReportType.UsageReport, ReportType.VisitsReports,
					ReportType.CancellationsReport, ReportType.TotalVisitorsReport);
			parksList = FXCollections.observableArrayList(ParkNameEnum.Banias, ParkNameEnum.Herodium,
					ParkNameEnum.Masada);
		}
	}

	/**
	 * Initializes the controller after its root element has been completely
	 * processed. Initializes UI components and sets event handlers.
	 * 
	 * @param location  The location used to resolve relative paths for the root
	 *                  object.
	 * @param resources The resources used to localize the root object.
	 */
	@SuppressWarnings("incomplete-switch")
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		dateLabel.setText(CurrentDateAndTime.getCurrentDate("'Today' yyyy-MM-dd"));
		hideErrorMessage();
		switch (employee.getEmployeeType()) {
		case Department_Manager:
			reportList = FXCollections.observableArrayList(ReportType.CancellationsReport, ReportType.VisitsReports);
			break;
		case Park_Manager:
			reportList = FXCollections.observableArrayList(ReportType.TotalVisitorsReport, ReportType.UsageReport);
			parksList = FXCollections.observableArrayList(employee.getRelatedPark());
			break;
		}
		selectReportComboBox.setItems(reportList);
		parkSelector.setItems(parksList);
		yearSelector.setItems(yearsList);
		monthSelector.setItems(monthsList);
		selectReportComboBox.setOnAction(this::onChangeReportSelection);
		parkSelector.setOnAction(this::onParkChangeSelection);
		yearSelector.setOnAction(this::onYearChangeSelection);
		monthSelector.setOnAction(this::onMonthChangeSelection);
	}

	/**
	 * Event handler triggered when the user selects a park from the park selection
	 * ComboBox. Updates the selectedPark field with the newly selected park.
	 * 
	 * @param event The ActionEvent triggered by selecting a park.
	 */
	private void onParkChangeSelection(ActionEvent event) {
		selectedPark = parkSelector.getValue();
	}

	/**
	 * Event handler triggered when the user selects a year from the year selection
	 * ComboBox. Updates the selectedYear field with the newly selected year.
	 * 
	 * @param event The ActionEvent triggered by selecting a year.
	 */
	private void onYearChangeSelection(ActionEvent event) {
		selectedYear = yearSelector.getValue();
	}

	/**
	 * Event handler triggered when the user selects a month from the month
	 * selection ComboBox. Updates the selectedMonth field with the newly selected
	 * month.
	 * 
	 * @param event The ActionEvent triggered by selecting a month.
	 */
	private void onMonthChangeSelection(ActionEvent event) {
		selectedMonth = monthSelector.getValue();
	}

	/**
	 * Event handler invoked when the user selects a report type from the ComboBox.
	 * Updates the selected report type accordingly.
	 * 
	 * @param event The ActionEvent triggered by selecting a report type.
	 */
	private void onChangeReportSelection(ActionEvent event) {
		selectedReportType = selectReportComboBox.getValue();
		if (this.employee.getEmployeeType() == EmployeeTypeEnum.Department_Manager) {
			switch (selectedReportType) {
			case CancellationsReport:
				parksList = FXCollections.observableArrayList(ParkNameEnum.Banias, ParkNameEnum.Herodium,
						ParkNameEnum.Masada, ParkNameEnum.North, ParkNameEnum.South);
				break;
			default:
				parksList = FXCollections.observableArrayList(ParkNameEnum.Banias, ParkNameEnum.Herodium,
						ParkNameEnum.Masada);
				break;
			}
			parkSelector.setItems(parksList); // Set the items of parkSelector with the updated parksList
		}
	}

	/**
	 * Event handler invoked when the user clicks the "Generate Report" button.
	 * Initiates the generation of the selected report.
	 */
	@SuppressWarnings("incomplete-switch")
	public void onGenerateReportClicked() {
		Report report;
		ClientRequest reportRequest;
		if (!validateGuiFields()) {
			return;
		}
		hideErrorMessage();
		if (selectedReportType == ReportType.CancellationsReport) {
			report = new CancellationsReport(Integer.parseInt(selectedMonth), Integer.parseInt(selectedYear),
					selectedPark);
			reportRequest = ClientRequest.Create_Cancellations_Report;
		} else if (selectedReportType == ReportType.VisitsReports) {
			report = new VisitsReport(Integer.parseInt(selectedMonth), Integer.parseInt(selectedYear), selectedPark);
			reportRequest = ClientRequest.Create_Visits_Report;
		} else if (selectedReportType == ReportType.TotalVisitorsReport) {
			report = new AmountDivisionReport(Integer.parseInt(selectedMonth), Integer.parseInt(selectedYear),
					selectedPark);
			reportRequest = ClientRequest.Create_Total_Visitors_Report;
		} else {
			report = new UsageReport(Integer.parseInt(selectedMonth), Integer.parseInt(selectedYear), selectedPark);
			reportRequest = ClientRequest.Create_Usage_Report;
		}

		ClientRequestDataContainer request = new ClientRequestDataContainer(reportRequest, report);
		ClientApplication.client.accept(request);
		ServerResponseBackToClient response = ClientCommunication.responseFromServer;
		AlertPopUp alert;

		switch (response.getRensponse()) {
		case Report_Generated_Successfully:
			alert = new AlertPopUp(AlertType.INFORMATION, "Success", "Report generated successfully", "...");
			alert.showAndWait();
			break;
		case Report_Failed_Generate:
			alert = new AlertPopUp(AlertType.ERROR, "FAIL", "fail", "fail");
			alert.showAndWait();
			break;
		}
	}

	/**
	 * Validates the input fields in the graphical user interface (GUI) for
	 * generating a report. Checks if the report type, park, year, and month are
	 * properly selected.
	 *
	 * @return {@code true} if all fields are validated successfully, indicating
	 *         that the user input is valid; {@code false} otherwise, along with an
	 *         error message displayed for the invalid field(s).
	 */
	private boolean validateGuiFields() {
		if (selectedReportType == ReportType.Unsupported) {
			showErrorMessage("You must select reportType");
			return false;
		}
		if (selectedPark == ParkNameEnum.None) {
			showErrorMessage("You must select park");
			return false;
		}
		if (selectedYear.equals("")) {
			showErrorMessage("You must select year");
			return false;
		}
		if (selectedMonth.equals("")) {
			showErrorMessage("You must select month");
			return false;
		}

		return true;
	}

	/**
	 * Hides the error message section in the UI by clearing the error message text
	 * and setting the error section to invisible. This method is typically called
	 * when there are no errors to display.
	 */
	private void hideErrorMessage() {
		errorMessageLabel.setText("");
		errorSection.setVisible(false);
	}

	/**
	 * Displays an error message in the UI by setting the error message text and
	 * making the error section visible. This method is typically called when there
	 * is an error to be displayed.
	 * 
	 * @param error The error message to be displayed.
	 */
	@SuppressWarnings("unused")
	private void showErrorMessage(String error) {
		errorSection.setVisible(true);
		errorMessageLabel.setText(error);
	}

}
