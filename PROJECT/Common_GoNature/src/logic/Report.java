package logic;

import java.io.Serializable;

import utils.enums.ParkNameEnum;
import utils.enums.ReportType;

/**
 * Represents a report with specific parameters.
 */
public class Report implements Serializable {
	/** Serial version UID for serialization. */
	private static final long serialVersionUID = 6207770687406496946L;

	/** The type of report. */
	protected ReportType reportType;

	/** The park for which the report is requested. */
	protected ParkNameEnum requestedPark;

	/** The month of the report. */
	protected int month;

	/** The year of the report. */
	protected int year;

	/** The content of the report in PDF format. */
	protected byte[] blobPdfContent;

	/**
	 * Constructs a Report object with the specified report type.
	 * 
	 * @param reportType The type of report.
	 */
	public Report(ReportType reportType) {
		this.reportType = reportType;
	}

	/**
	 * Retrieves the type of report.
	 * 
	 * @return The type of report.
	 */
	public ReportType getReportType() {
		return reportType;
	}

	/**
	 * Sets the type of report.
	 * 
	 * @param reportType The type of report to set.
	 */
	public void setReportType(ReportType reportType) {
		this.reportType = reportType;
	}

	/**
	 * Retrieves the park for which the report is requested.
	 * 
	 * @return The park for which the report is requested.
	 */
	public ParkNameEnum getRequestedPark() {
		return requestedPark;
	}

	/**
	 * Sets the park for which the report is requested.
	 * 
	 * @param requestedPark The park for which the report is requested.
	 */
	public void setRequestedPark(ParkNameEnum requestedPark) {
		this.requestedPark = requestedPark;
	}

	/**
	 * Retrieves the month of the report.
	 * 
	 * @return The month of the report.
	 */
	public int getMonth() {
		return month;
	}

	/**
	 * Sets the month of the report.
	 * 
	 * @param month The month of the report to set.
	 */
	public void setMonth(int month) {
		this.month = month;
	}

	/**
	 * Retrieves the year of the report.
	 * 
	 * @return The year of the report.
	 */
	public int getYear() {
		return year;
	}

	/**
	 * Sets the year of the report.
	 * 
	 * @param year The year of the report to set.
	 */
	public void setYear(int year) {
		this.year = year;
	}

	/**
	 * Retrieves the content of the report in PDF format.
	 * 
	 * @return The content of the report in PDF format.
	 */
	public byte[] getBlobPdfContent() {
		return blobPdfContent;
	}

	/**
	 * Sets the content of the report in PDF format.
	 * 
	 * @param content The content of the report in PDF format to set.
	 */
	public void setBlobPdfContent(byte[] content) {
		blobPdfContent = content;
	}
}
