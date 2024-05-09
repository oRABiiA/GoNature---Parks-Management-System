package gui.controller;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import client.ClientApplication;
import client.ClientCommunication;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
import utils.CurrentDateAndTime;
import utils.enums.ClientRequest;
import utils.enums.EmployeeTypeEnum;
import utils.enums.ParkNameEnum;
import utils.enums.ReportType;

/**
 * This class is responsible for controlling the view reports screen in the
 * application. It allows users, particularly park managers and department
 * managers, to select and view different types of reports for parks, including
 * usage, visits, cancellations, and total visitors reports for specific months
 * and years.
 */
public class ViewReportsScreenController implements Initializable {

	@FXML
	public Label dateLabel;
	@FXML
	public ComboBox<ReportType> reportSelector;
	@FXML
	public ComboBox<ParkNameEnum> parkSelector;
	@FXML
	public ComboBox<String> yearSelector;
	@FXML
	public ComboBox<String> monthSelector;
	@FXML
	public HBox errorSection;
	@FXML
	public Label errorMessageLabel;

	private ObservableList<ReportType> reportsList = FXCollections.observableArrayList(ReportType.UsageReport,
			ReportType.VisitsReports, ReportType.CancellationsReport, ReportType.TotalVisitorsReport);
	private ObservableList<ParkNameEnum> parksList = FXCollections.observableArrayList(ParkNameEnum.Banias,
			ParkNameEnum.Herodium, ParkNameEnum.Masada, ParkNameEnum.North, ParkNameEnum.South);
	private ObservableList<String> yearsList = FXCollections.observableArrayList("2024", "2023", "2022", "2021");
	private ObservableList<String> monthsList = FXCollections.observableArrayList("1", "2", "3", "4", "5", "6", "7",
			"8", "9", "10", "11", "12");

	private Report requestedReport = null;
	private ReportType selectedReport = ReportType.Unsupported;
	private ParkNameEnum selectedPark = ParkNameEnum.None;
	private String selectedYear = "";
	private String selectedMonth = "";
	private Employee employee;

	/**
	 * Constructor for the ViewReportsScreenController. Initializes the controller
	 * with the employee object and adjusts the available reports and parks based on
	 * the employee's role.
	 *
	 * @param employee The employee object, used to determine available reports and
	 *                 parks based on role.
	 */
	public ViewReportsScreenController(Object employee) {
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
	 * Initializes the controller class. This method is called after the FXML fields
	 * have been loaded.
	 *
	 * @param location  The location used to resolve relative paths for the root
	 *                  object, or null if the location is not known.
	 * @param resources The resources used to localize the root object, or null if
	 *                  the root object was not localized.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		dateLabel.setText(CurrentDateAndTime.getCurrentDate("'Today' yyyy-MM-dd"));
		reportSelector.getItems().addAll(reportsList);
		parkSelector.getItems().addAll(parksList);
		yearSelector.getItems().addAll(yearsList);
		monthSelector.getItems().addAll(monthsList);

		reportSelector.setOnAction(this::onReportChangeSelection);
		parkSelector.setOnAction(this::onParkChangeSelection);
		yearSelector.setOnAction(this::onYearChangeSelection);
		monthSelector.setOnAction(this::onMonthChangeSelection);
		hideErrorMessage();

	}

	/**
	 * Handles the selection change of the report type.
	 *
	 * @param event The event triggered when the report type selection changes.
	 */
	private void onReportChangeSelection(ActionEvent event) {
		selectedReport = reportSelector.getValue();
		if (this.employee.getEmployeeType() == EmployeeTypeEnum.Department_Manager) {
			switch (selectedReport) {
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
	 * Handles the selection change of the park.
	 *
	 * @param event The event triggered when the park selection changes.
	 */
	private void onParkChangeSelection(ActionEvent event) {
		selectedPark = parkSelector.getValue();
	}

	/**
	 * Handles the selection change of the year.
	 *
	 * @param event The event triggered when the year selection changes.
	 */
	private void onYearChangeSelection(ActionEvent event) {
		selectedYear = yearSelector.getValue();
	}

	/**
	 * Handles the selection change of the month.
	 *
	 * @param event The event triggered when the month selection changes.
	 */
	private void onMonthChangeSelection(ActionEvent event) {
		selectedMonth = monthSelector.getValue();
	}

	/**
	 * Validates the input fields in the graphical user interface (GUI) for
	 * generating a report. Checks if the report type, park, year, and month are
	 * properly selected.
	 *
	 * @return {@code true} if all required fields are selected or entered
	 *         correctly; {@code false} otherwise, along with an error message
	 *         displayed for the invalid field(s).
	 */
	private boolean validateGuiFields() {
		if (selectedReport == ReportType.Unsupported) {
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
	 * Handles the action of viewing the selected report. Based on the selections
	 * made by the user, this method requests the report from the server and
	 * displays it.
	 */
	@SuppressWarnings("incomplete-switch")
	public void onViewReportClicked() {
		requestedReport = null;
		ClientRequest reportToOpen = null;
		if (!validateGuiFields()) {
			return;
		}
		hideErrorMessage();
		switch (selectedReport) {
		case CancellationsReport:
			requestedReport = new CancellationsReport(Integer.parseInt(selectedMonth), Integer.parseInt(selectedYear),
					selectedPark);
			reportToOpen = ClientRequest.Import_Cancellations_Report;
			break;
		case TotalVisitorsReport:
			requestedReport = new AmountDivisionReport(Integer.parseInt(selectedMonth), Integer.parseInt(selectedYear),
					selectedPark);
			reportToOpen = ClientRequest.Import_Total_Visitors_Report;
			break;
		case UsageReport:
			requestedReport = new UsageReport(Integer.parseInt(selectedMonth), Integer.parseInt(selectedYear),
					selectedPark);
			reportToOpen = ClientRequest.Import_Usage_Report;

			break;
		case VisitsReports:
			requestedReport = new VisitsReport(Integer.parseInt(selectedMonth), Integer.parseInt(selectedYear),
					selectedPark);
			reportToOpen = ClientRequest.Import_Visits_Report;
			break;
		default:
			return;
		}

		ClientRequestDataContainer request = new ClientRequestDataContainer(reportToOpen, requestedReport);
		ClientApplication.client.accept(request);
		ServerResponseBackToClient response = ClientCommunication.responseFromServer;

		switch (response.getRensponse()) {
		case Such_Report_Not_Found:
			showErrorMessage("Such report does not exist");
			return;
		case Cancellations_Report_Found:
			try {
				// Create a temp file
				String reportName = selectedReport + "_" + selectedYear + "_" + selectedMonth;
				File tempFile = File.createTempFile(reportName, ".pdf");
				tempFile.deleteOnExit(); // Request the file be deleted when the application exits

				// Write the PDF content to the temp file
				try (FileOutputStream fos = new FileOutputStream(tempFile)) {
					fos.write((byte[]) response.getMessage()); // Assuming response.getMessage() returns the correct
																// byte array for the PDF
				}
				// Open the file with the default system viewer
				Desktop.getDesktop().open(tempFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		}
	}

	/**
	 * Hides the error message label and error section in the graphical user
	 * interface (GUI). This method is typically used to clear any previously
	 * displayed error messages.
	 */
	private void hideErrorMessage() {
		errorMessageLabel.setText("");
		errorSection.setVisible(false);
	}

	/**
	 * Displays an error message in the graphical user interface (GUI). This method
	 * sets the specified error message text and makes the error section visible in
	 * the GUI.
	 *
	 * @param error The error message to be displayed in the GUI.
	 */
	private void showErrorMessage(String error) {
		errorSection.setVisible(true);
		errorMessageLabel.setText(error);
	}

}
