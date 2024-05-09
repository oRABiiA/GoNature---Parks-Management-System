package logic;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import utils.enums.ParkNameEnum;
import utils.enums.ReportType;

/**
 * The CancellationsReport class represents a report containing cancellation
 * data for park daily summaries. It extends the Report class and implements the
 * Serializable interface.
 */
public class CancellationsReport extends Report implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4085720282255848226L;
	private HashMap<Integer, ParkDailySummary> reportData; // Data representing cancellation information for park daily
															// summaries
	private double averageCancels = 0; // Average number of cancellations
	private double medianCancels = 0; // Median number of cancellations

	/**
	 * Constructs a new CancellationsReport object with the specified report type.
	 * 
	 * @param reportType The type of the report
	 */
	public CancellationsReport(ReportType reportType) {
		super(reportType);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Constructs a new CancellationsReport object with the specified month, year,
	 * and park.
	 * 
	 * @param month The month for the report
	 * @param year  The year for the report
	 * @param park  The park for which the report is generated
	 */
	public CancellationsReport(int month, int year, ParkNameEnum park) {
		super(ReportType.CancellationsReport);
		this.month = month;
		this.year = year;
		this.requestedPark = park;
	}

	/**
	 * Placeholder method for showing the report. This method may be implemented
	 * later to display the report in a specific format.
	 */
	public void showReport() {
	}

	/**
	 * Gets the report data containing cancellation information for park daily
	 * summaries.
	 * 
	 * @return The report data
	 */
	public HashMap<Integer, ParkDailySummary> getReportData() {
		return reportData;
	}

	/**
	 * Computes and sets the average and median number of cancellations based on the
	 * provided report data.
	 * 
	 * @param reportData The report data containing cancellation information for
	 *                   park daily summaries
	 */
	public void setReportData(HashMap<Integer, ParkDailySummary> reportData) {
		this.reportData = reportData;

		double sum = 0;
		for (ParkDailySummary summary : reportData.values()) {
			sum += summary.getCancelsOrders();
		}
		setAverageCancels(sum / reportData.size());

		List<Integer> cancelsList = reportData.values().stream().map(ParkDailySummary::getCancelsOrders).sorted()
				.collect(Collectors.toList());

		int size = cancelsList.size();
		if (size % 2 == 0) {
			setMedianCancels((cancelsList.get(size / 2 - 1) + cancelsList.get(size / 2)) / 2.0);
		} else {
			setMedianCancels(cancelsList.get(size / 2));
		}

	}

	/**
	 * Gets the average number of cancellations.
	 * 
	 * @return The average number of cancellations
	 */
	public double getAverageCancels() {
		return averageCancels;
	}

	/**
	 * Sets the average number of cancellations.
	 * 
	 * @param averageCancels The average number of cancellations to set
	 */
	public void setAverageCancels(double averageCancels) {
		this.averageCancels = averageCancels;
	}

	/**
	 * Gets the median number of cancellations.
	 * 
	 * @return The median number of cancellations
	 */
	public double getMedianCancels() {
		return medianCancels;
	}

	/**
	 * Sets the median number of cancellations.
	 * 
	 * @param medianCancels The median number of cancellations to set
	 */
	public void setMedianCancels(double medianCancels) {
		this.medianCancels = medianCancels;
	}

}
