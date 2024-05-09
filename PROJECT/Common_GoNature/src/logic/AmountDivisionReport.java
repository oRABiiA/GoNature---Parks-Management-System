package logic;

import java.io.Serializable;

import utils.enums.ParkNameEnum;
import utils.enums.ReportType;

/**
 * The AmountDivisionReport class represents a report containing park amount
 * summary data. It extends the Report class and implements the Serializable
 * interface.
 */
public class AmountDivisionReport extends Report implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5951223164371041856L;
	private ParkAmountSummary reportData; // Data representing park amount summary
	private byte[] blobPdfContent; // PDF content in byte array format

	/**
	 * Constructs a new AmountDivisionReport object with the specified report type.
	 * 
	 * @param reportType The type of the report
	 */
	public AmountDivisionReport(ReportType reportType) {
		super(reportType);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Constructs a new AmountDivisionReport object with the specified month, year,
	 * and park.
	 * 
	 * @param month The month for the report
	 * @param year  The year for the report
	 * @param park  The park for which the report is generated
	 */
	public AmountDivisionReport(int month, int year, ParkNameEnum park) {
		super(ReportType.TotalVisitorsReport);
		this.month = month;
		this.year = year;
		this.requestedPark = park;
	}

	/**
	 * Gets the park amount summary data of the report.
	 * 
	 * @return The park amount summary data
	 */
	public ParkAmountSummary getReportData() {
		return reportData;
	}

	/**
	 * Sets the park amount summary data of the report.
	 * 
	 * @param reportData The park amount summary data to set
	 */
	public void setReportData(ParkAmountSummary reportData) {
		this.reportData = reportData;
	}

	/**
	 * Gets the PDF content of the report in byte array format.
	 * 
	 * @return The PDF content of the report
	 */
	public byte[] getBlobPdfContent() {
		return blobPdfContent;
	}

	/**
	 * Sets the PDF content of the report in byte array format.
	 * 
	 * @param blobPdfContent The PDF content to set
	 */
	public void setBlobPdfContent(byte[] blobPdfContent) {
		this.blobPdfContent = blobPdfContent;
	}
}
