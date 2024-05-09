package logic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import utils.enums.ParkNameEnum;
import utils.enums.ReportType;

/**
 * The VisitsReport class represents a report containing information about
 * visits and idle times. It extends the Report class and implements the
 * Serializable interface.
 */
public class VisitsReport extends Report implements Serializable {

	/**
	 * The serial version UID for serialization.
	 */
	private static final long serialVersionUID = -8835433837087468966L;

	/**
	 * HashMap storing total visits categorized by enter time. Key: Integer
	 * representing the time. Value: ArrayList of Integers representing the total
	 * visits at that time.
	 */
	private HashMap<Integer, ArrayList<Integer>> totalVisitsByEnterTime;

	/**
	 * HashMap storing total idle time categorized by gap. Key: Integer representing
	 * the gap. Value: ArrayList of Integers representing the total idle time for
	 * that gap.
	 */
	private HashMap<Integer, ArrayList<Integer>> totalIdleTimeByGap;

	/**
	 * Constructs a VisitsReport object with a specified report type.
	 * 
	 * @param reportType The type of the report.
	 */
	public VisitsReport(ReportType reportType) {
		super(reportType);
	}

	/**
	 * Constructs a VisitsReport object with specified month, year, and requested
	 * park.
	 * 
	 * @param month         The month for the report.
	 * @param year          The year for the report.
	 * @param requestedPark The park for which the report is requested.
	 */
	public VisitsReport(int month, int year, ParkNameEnum requestedPark) {
		super(ReportType.VisitsReports);
		this.requestedPark = requestedPark;
		this.month = month;
		this.year = year;
	}

	/**
	 * Retrieves the total visits by enter time.
	 * 
	 * @return HashMap&lt;Integer, ArrayList&lt;Integer&gt;&gt; containing total visits by enter
	 *         time.
	 */
	public HashMap<Integer, ArrayList<Integer>> getTotalVisitsByEnterTime() {
		return totalVisitsByEnterTime;
	}

	/**
	 * Sets the total visits by enter time.
	 * 
	 * @param totalVisitsByEnterTime HashMap containing total visits by enter time.
	 */
	public void setTotalVisitsEnterByTime(HashMap<Integer, ArrayList<Integer>> totalVisitsByEnterTime) {
		this.totalVisitsByEnterTime = totalVisitsByEnterTime;
	}

	/**
	 * Retrieves the total idle time by gap.
	 * 
	 * @return HashMap&lt;Integer, ArrayList&lt;Integer&gt;&gt; containing total idle time by
	 *         gap.
	 */
	public HashMap<Integer, ArrayList<Integer>> getTotalIdleTimeByGap() {
		return totalIdleTimeByGap;
	}

	/**
	 * Sets the total idle time by gap.
	 * 
	 * @param totalIdleTimeByGap HashMap containing total idle time by gap.
	 */
	public void setTotalIdleTimeByGap(HashMap<Integer, ArrayList<Integer>> totalIdleTimeByGap) {
		this.totalIdleTimeByGap = totalIdleTimeByGap;
	}

	/**
	 * Retrieves the idle time data for a specific index.
	 * 
	 * @param index The index for which to retrieve the idle time data.
	 * @return ArrayList&lt;Integer&gt; representing the idle time data.
	 */
	public ArrayList<Integer> getIdleTimeData(int index) {
	    return totalIdleTimeByGap.get(index);
	}

	/**
	 * Retrieves the enter time data for a specific index.
	 * 
	 * @param index The index for which to retrieve the enter time data.
	 * @return ArrayList&lt;Integer&gt; representing the enter time data.
	 */
	public ArrayList<Integer> getEnterTimeData(int index) {
	    return totalVisitsByEnterTime.get(index);
	}
}
