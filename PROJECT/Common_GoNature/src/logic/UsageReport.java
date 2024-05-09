package logic;

import java.io.Serializable;
import java.util.HashMap;

import utils.enums.ParkNameEnum;
import utils.enums.ReportType;

/**
 * Represents a usage report, extending the base Report class, containing data
 * and summaries for park usage.
 */
public class UsageReport extends Report implements Serializable {

	/** The serial version UID for serialization. */
	private static final long serialVersionUID = -5930390679218481702L;

	/** The data containing full-day summaries for each day in the report. */
	private HashMap<Integer, ParkFullDaySummary> reportData;

	/** The summary for the current day. */
	private ParkFullDaySummary currentDaySummary;

	/**
	 * Constructs a new UsageReport object with the given report type.
	 * 
	 * @param reportType The type of the report.
	 */
	public UsageReport(ReportType reportType) {
		super(reportType);
	}

	/**
	 * Constructs a new UsageReport object with the specified month, year, and park.
	 * 
	 * @param month The month of the report.
	 * @param year  The year of the report.
	 * @param park  The park for which the report is generated.
	 */
	public UsageReport(int month, int year, ParkNameEnum park) {
		super(ReportType.UsageReport);
		this.month = month;
		this.year = year;
		this.requestedPark = park;
	}

	/**
	 * Retrieves the data containing full-day summaries for each day in the report.
	 * 
	 * @return The report data.
	 */
	public HashMap<Integer, ParkFullDaySummary> getReportData() {
		return reportData;
	}

	/**
	 * Sets the data containing full-day summaries for each day in the report.
	 * 
	 * @param reportData The report data to be set.
	 */
	public void setReportData(HashMap<Integer, ParkFullDaySummary> reportData) {
		this.reportData = reportData;
	}

	/**
	 * Retrieves the summary for the current day.
	 * 
	 * @return The current day summary.
	 */
	public ParkFullDaySummary getCurrentDaySummary() {
		return currentDaySummary;
	}

	/**
	 * Sets the summary for the current day.
	 * 
	 * @param currentDaySummary The current day summary to be set.
	 */
	public void setCurrentDaySummary(ParkFullDaySummary currentDaySummary) {
		this.currentDaySummary = currentDaySummary;
	}
}