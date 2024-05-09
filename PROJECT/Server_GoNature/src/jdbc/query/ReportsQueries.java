package jdbc.query;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;


import jdbc.MySqlConnection;
import logic.AmountDivisionReport;
import logic.CancellationsReport;
import logic.ParkAmountSummary;
import logic.ParkDailySummary;
import logic.ParkFullDaySummary;
import logic.UsageReport;
import logic.VisitsReport;
import utils.ReportGenerator;
import utils.enums.ParkNameEnum;

/**
 * Manages database operations related to generating and retrieving reports for parks. This includes daily summaries,
 * usage reports, cancellation reports, and total visitor amount reports. The class supports operations like generating
 * new reports, inserting report data into the database, and fetching existing reports as PDF blobs.
 * @author Tamer Amer, Gal Bitton, Rabea Lahham, Bahaldeen Swied, Ron Sisso, Nadav Reubens.
 */
public class ReportsQueries {
	
	/**
	 * Retrieves a summary of daily activities for a specified park on a given day and month. The summary includes
	 * total cancelled orders, time passed orders, and the total number of orders.
	 *
	 * @param month The month for which the summary is requested.
	 * @param day   The day for which the summary is requested.
	 * @param parkId The ID of the park for which the summary is requested.
	 * @return A {@link ParkDailySummary} object containing the summary for the given day, or null if an error occurs.
	 */
	public ParkDailySummary getParkDailySummaryByDay(int month,int day, int parkId) { 
		ParkDailySummary currentDaySummary = new ParkDailySummary();
		try {
			Connection con = MySqlConnection.getInstance().getConnection();
			PreparedStatement stmt;
			switch(parkId) {
			case(4):
			case(5):
				stmt = con
				.prepareStatement("SELECT"
			              + " SUM(CASE WHEN OrderStatus = 'Cancelled' THEN 1 ELSE 0 END) AS TotalCanceledOrders,"
			              + " SUM(CASE WHEN OrderStatus = 'Time Passed' THEN 1 ELSE 0 END) AS TotalTimePassedOrders,"
			              + " COUNT(*) AS TotalOrders"
			              + " FROM"
			              + " preorders"
			              + " WHERE"
			              + " ParkId IN (SELECT ParkId FROM parks WHERE district = ?)"
			              + " AND MONTH(EnterDate) = ?"
			              + " AND DAY(EnterDate) = ?");

				stmt.setInt(1, parkId);
				stmt.setInt(2, month);
				stmt.setInt(3, day);
				break;
			default:
				stmt = con
				.prepareStatement("SELECT COUNT(CASE WHEN OrderStatus = 'Cancelled' THEN 1 END) AS CanceledOrders, "
						+ "COUNT(CASE WHEN OrderStatus = 'Time Passed' THEN 1 END) AS TimePassedOrders, "
						+ "COUNT(*) AS TotalOrders " + "FROM preorders " + "WHERE ParkId = ? "
						+ "AND MONTH(EnterDate) = ? AND DAY(EnterDate) = ?");

				stmt.setInt(1, parkId);
				stmt.setInt(2, month);
				stmt.setInt(3, day);
				break;
		
			}
			ResultSet rs = stmt.executeQuery();
			// if the query ran successfully, but returned as empty table.
			if (!rs.next()) {
				currentDaySummary.setCancelsOrders(0);
				currentDaySummary.setDay(day);
				currentDaySummary.setTimePassedOrders(0);
				currentDaySummary.setTotalOrders(0);
				currentDaySummary.setPark(ParkNameEnum.fromParkId(parkId));
				return currentDaySummary;
			}
			currentDaySummary.setCancelsOrders(rs.getInt(1));
			currentDaySummary.setDay(day);
			currentDaySummary.setTimePassedOrders(rs.getInt(2));
			currentDaySummary.setTotalOrders(rs.getInt(3));
			currentDaySummary.setPark(ParkNameEnum.fromParkId(parkId));
			return currentDaySummary;

		} catch (SQLException ex) {
//		serverController.printToLogConsole("Query search for user failed");
			return null;
		}

	}
	
	/**
	 * Checks if the park was full for each day of a given month and year, at a specific hour. This method is useful for
	 * understanding peak times and planning accordingly.
	 *
	 * @param month The month of interest.
	 * @param hour  The hour of interest.
	 * @param year  The year of interest.
	 * @param park  The {@link ParkNameEnum} representing the park of interest.
	 * @return A {@link ParkFullDaySummary} detailing the number of times the park was full during the specified hour across the month.
	 */
	public ParkFullDaySummary getIfParkWasFullEachDay(int month,int hour, int year, ParkNameEnum park) //added by tamir
	{
		ParkFullDaySummary currentDaySummary = new ParkFullDaySummary();
		String parkColumnName = park.name();
		try {
			Connection con = MySqlConnection.getInstance().getConnection();
			
			PreparedStatement stmt = con.prepareStatement("SELECT " +parkColumnName +" FROM parkfulldatetime WHERE Month=? AND Hour(Hour)=? AND year=?;");
	        stmt.setInt(1, month);
	        stmt.setInt(2, hour);
	        stmt.setInt(3, year);
	        ResultSet rs = stmt.executeQuery();
	        
	        if(!rs.next())
	        {
	             currentDaySummary = new ParkFullDaySummary();
	             currentDaySummary.setHour(hour);
	             currentDaySummary.setTimesFullInSpecificHour(0);
	             currentDaySummary.setPark(park);
	             return currentDaySummary;
	        }
	        
            currentDaySummary = new ParkFullDaySummary();
            currentDaySummary.setHour(hour);
            currentDaySummary.setTimesFullInSpecificHour(rs.getInt(1));
            currentDaySummary.setPark(park);
         
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return currentDaySummary;
	}
	
	/**
	 * Generates a usage report for a specific park, month, and year. The report includes details on park fullness at
	 * various times throughout the month.
	 *
	 * @param report The {@link UsageReport} object containing parameters for report generation.
	 * @return true if the report was successfully generated and saved, false otherwise.
	 */
	public boolean generateUsageReport(UsageReport report) {

		int year = report.getYear();
		int month = report.getMonth();

		HashMap<Integer, ParkFullDaySummary> parkSummaryByDays = new HashMap<Integer, ParkFullDaySummary>();

		for (int hour = 8; hour < 21; hour++) 
		{
			ParkFullDaySummary temp = getIfParkWasFullEachDay(month,hour,year,report.getRequestedPark());
			if (temp != null)
			{
				parkSummaryByDays.put(hour, temp);
			}
		}
		if (parkSummaryByDays.isEmpty())
			return false;

		report.setReportData(parkSummaryByDays);
		report.setBlobPdfContent(ReportGenerator.generateUsageReportAsPdfBlob(report));

		if (insertGeneratedUsageReportToDatabase(report))
			return true;

		return false;

	}
	
	/**
	 * Inserts a generated usage report into the database. If a report for the same park, year, and month already exists,
	 * it updates the existing record.
	 *
	 * @param report The {@link UsageReport} to insert into the database.
	 * @return true if the operation is successful, false otherwise.
	 */
	private boolean insertGeneratedUsageReportToDatabase(UsageReport report) {
		try {
			Connection con = MySqlConnection.getInstance().getConnection();
			PreparedStatement stmt = con.prepareStatement(
					"INSERT INTO usagereport (parkId, year, month, pdfblob) \r\n" + "VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE PDFBlob = VALUES(PDFBlob);");

			stmt.setInt(1, report.getRequestedPark().getParkId());
			stmt.setInt(2, report.getYear());
			stmt.setInt(3, report.getMonth());
			stmt.setBytes(4, report.getBlobPdfContent());

			int rs = stmt.executeUpdate();

			// if the query ran successfully, but returned as empty table.
			if (rs == 0) {
				return false;
			}

			return true;
		} catch (SQLException ex) {
//		serverController.printToLogConsole("Query search for user failed");
			return false;
		}
	}
	
	/**
	 * Retrieves the blob content of a previously generated usage report from the database.
	 *
	 * @param report The {@link UsageReport} identifying the report to retrieve.
	 * @return An array of bytes representing the PDF content of the report, or null if the report could not be found or an error occurred.
	 */
	public byte[] getRequestedUsageReport(UsageReport report) {
		try {
			Connection con = MySqlConnection.getInstance().getConnection();
			PreparedStatement stmt = con.prepareStatement(
					"SELECT pdfblob FROM usagereport WHERE Year = ? AND Month = ? AND ParkId = ?");

			stmt.setInt(1, report.getYear());
			stmt.setInt(2, report.getMonth());
			stmt.setInt(3, report.getRequestedPark().getParkId());

			ResultSet rs = stmt.executeQuery();

			if (!rs.next()) {
				return null;
			}
			// retrieve the Blob from the database
			Blob pdfBlob = rs.getBlob(1);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buf = new byte[50000];
			try (InputStream in = pdfBlob.getBinaryStream()) {
				int n;
				while ((n = in.read(buf)) > 0) {
					baos.write(buf, 0, n);
				}
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}

			byte[] pdfBytes = baos.toByteArray();
			return pdfBytes;
		} catch (SQLException ex) {
			return null;
		}
	}
	
	/**
	 * Retrieves the total amounts divided by order type (Solo, Family, Group) for a specific park in a given month and year.
	 * This method aggregates the amounts from both occasional visits and preorders that have been marked as 'Completed'.
	 * The results are then summarized in a {@link ParkAmountSummary} object, which includes the total amounts for each order type,
	 * as well as the month, year, and park ID for which the summary was generated.
	 *
	 * @param month The month for which the summary is to be generated.
	 * @param parkId The ID of the park for which the summary is to be generated.
	 * @param year The year for which the summary is to be generated.
	 * @return A {@link ParkAmountSummary} object containing the total amounts for Solo, Family, and Group orders,
	 *         or null if an SQL exception occurs during the operation.
	 *         If no data is found for the specified criteria, a {@link ParkAmountSummary} with all amounts set to 0 is returned.
	 */
	public ParkAmountSummary getAmountDivisionByOrderTypeInChoosenMonth(int month, int parkId,int year) {
				ParkAmountSummary parkAmountSum = new ParkAmountSummary();
				try {
					Connection con = MySqlConnection.getInstance().getConnection();
					PreparedStatement stmt = con
							.prepareStatement("SELECT\n"
									+ "    SUM(ParkSolo) AS TotalSolo,\n"
									+ "    SUM(ParkFamily) AS TotalFamily,\n"
									+ "    SUM(ParkGroup) AS TotalGroup\n"
									+ "FROM (\n"
									+ "    SELECT\n"
									+ "        SUM(CASE WHEN parkId = ? AND OrderType='Solo' THEN Amount ELSE 0 END) AS ParkSolo,\n"
									+ "        SUM(CASE WHEN parkId = ? AND OrderType='Family' THEN Amount ELSE 0 END) AS ParkFamily,\n"
									+ "        SUM(CASE WHEN parkId = ? AND OrderType='Group' THEN Amount ELSE 0 END) AS ParkGroup\n"
									+ "    FROM\n"
									+ "        occasionalvisits\n"
									+ "    WHERE\n"
									+ "        OrderStatus = 'Completed' \n"
									+ "        AND MONTH(EnterDate) = ?\n"
									+ "        AND YEAR(EnterDate) = ?\n"
									+ "    UNION ALL\n"
									+ "    SELECT\n"
									+ "        SUM(CASE WHEN parkId = ? AND OrderType='Solo Preorder' THEN Amount ELSE 0 END) AS ParkSolo,\n"
									+ "        SUM(CASE WHEN parkId = ? AND OrderType='Family Preorder' THEN Amount ELSE 0 END) AS ParkFamily,\n"
									+ "        SUM(CASE WHEN parkId = ? AND OrderType='Group Preorder' THEN Amount ELSE 0 END) AS ParkGroup\n"
									+ "\n"
									+ "    FROM\n"
									+ "        preorders\n"
									+ "    WHERE\n"
									+ "        OrderStatus = 'Completed' \n"
									+ "        AND MONTH(EnterDate) = ?\n"
									+ "        AND YEAR(EnterDate) = ?\n"
									+ ") AS subquery;");

					stmt.setInt(1, parkId);
					stmt.setInt(2, parkId);
					stmt.setInt(3, parkId);
					stmt.setInt(4, month);
					stmt.setInt(5, year);
					stmt.setInt(6, parkId);
					stmt.setInt(7, parkId);
					stmt.setInt(8, parkId);
					stmt.setInt(9, month);
					stmt.setInt(10, year);
					
					ResultSet rs = stmt.executeQuery();

					// if the query ran successfully, but returned as empty table.
					if (!rs.next()) {
						parkAmountSum.setAmountGroup(0);
						parkAmountSum.setAmountFamily(0);
						parkAmountSum.setAmountSolo(0);
						parkAmountSum.setMonth(month);
						parkAmountSum.setYear(year);
						parkAmountSum.setPark(ParkNameEnum.fromParkId(parkId));
						return parkAmountSum;
					}
					parkAmountSum.setAmountSolo(rs.getInt(1));
					parkAmountSum.setAmountFamily(rs.getInt(2));
					parkAmountSum.setAmountGroup(rs.getInt(3));
					parkAmountSum.setMonth(month);
					parkAmountSum.setYear(year);
					parkAmountSum.setPark(ParkNameEnum.fromParkId(parkId));
					return parkAmountSum;

				} catch (SQLException ex) {
//				serverController.printToLogConsole("Query search for user failed");
					return null;
				}

			}
	
	/**
	 * Generates a report detailing the cancellations for a given park, month, and year.
	 *
	 * @param report The {@link CancellationsReport} containing the parameters for report generation.
	 * @return true if the report was successfully generated and saved, false otherwise.
	 */
	public boolean generateCancellationsReport(CancellationsReport report) {
		int year = report.getYear();
		int month = report.getMonth();
		int parkId = report.getRequestedPark().getParkId();
		YearMonth yearMonth = YearMonth.of(year, month);
		int daysInMonth = yearMonth.lengthOfMonth();
		HashMap<Integer, ParkDailySummary> parkSummaryByDays = new HashMap<Integer, ParkDailySummary>();

		for (int day = 1; day <= daysInMonth; day++) {
			ParkDailySummary temp = getParkDailySummaryByDay(month, day, parkId);
			if (temp != null) {
				parkSummaryByDays.put(day, temp);
			}
		}
		if (parkSummaryByDays.isEmpty())
			return false;

		report.setReportData(parkSummaryByDays);
		report.setBlobPdfContent(ReportGenerator.generateCancellationsReportAsPdfBlob(report));

		if (insertGeneratedCancellationsReportToDatabase(report))
			return true;

		return false;

	}
	
	/**
	 * Generates a visits report for a given park, month, and year, including details on the number of visitors by type and visit duration.
	 *
	 * @param report The {@link VisitsReport} to be generated.
	 * @return true if the report was successfully generated and saved, false otherwise.
	 */
	public boolean generateVisitsReport(VisitsReport report) {
		boolean isQuerySucceed = getParkVisitsSummaryByEnterTime(report);
		if (!isQuerySucceed)
			return false;

		isQuerySucceed = getParkIdleVisitTimeSummary(report);
		if (!isQuerySucceed)
			return false;

		report.setBlobPdfContent(ReportGenerator.generateVisitsReportAsPdf(report));

		if (insertVisitsAmountReportToDatabase(report))
			return true;
		return false;
	}
	
	/**
	 * Generates a report that divides the total amount of visitors by their type (e.g., Solo, Family, Group) for a given
	 * park, month, and year.
	 *
	 * @param report The {@link AmountDivisionReport} containing the parameters for the report generation.
	 * @return true if the report was successfully generated and saved, false otherwise.
	 */
	public boolean generateTotalAmountDivisionReport(AmountDivisionReport report) {
		int year = report.getYear();
		int month = report.getMonth();
		int parkId = report.getRequestedPark().getParkId();
		ParkAmountSummary parkAmountSum=new ParkAmountSummary();

		parkAmountSum = getAmountDivisionByOrderTypeInChoosenMonth(month, parkId,year);
			
		if (parkAmountSum==null)
			return false;

		report.setReportData(parkAmountSum);
		report.setBlobPdfContent(ReportGenerator.generateTotalVisitorsAmountReportAsPdf(report));

		if (insertTotalAmountReportToDatabase(report))
			return true;

		return false;

	}
	
	/**
	 * Calculates the summary of park visits based on the time of entry, segmented into hourly intervals for various types of visits.
	 * This method combines data from both occasional visits and preorders, categorizing them by their respective order types
	 * (e.g., Solo Occasional, Family Preorder) and then aggregating the total number of visitors for each hourly interval throughout the day.
	 * The results are then stored in a map within the provided {@link VisitsReport} object, categorized by order type and time interval.
	 *
	 * @param report The {@link VisitsReport} object to which the calculated summary will be added. This report must include
	 *               the park ID, month, and year for which the visit summary is requested.
	 * @return true if the operation successfully retrieves and processes the data, adding the resulting summary to the report;
	 *         false if an SQL exception occurs or no data is found for the specified criteria.
	 */
	private boolean getParkVisitsSummaryByEnterTime(VisitsReport report) {
		try {
			Connection con = MySqlConnection.getInstance().getConnection();
			PreparedStatement stmt = con.prepareStatement(
					"SELECT " +
						    "    OrderTypes.OrderType, " +
						    "    COALESCE(SUM(CASE WHEN TIME(combined.EnterDate) >= '08:00:00' AND TIME(combined.EnterDate) <= '08:59:59' THEN combined.Amount ELSE 0 END), 0) AS Visitors_08_09, " +
						    "    COALESCE(SUM(CASE WHEN TIME(combined.EnterDate) >= '09:00:00' AND TIME(combined.EnterDate) <= '09:59:59' THEN combined.Amount ELSE 0 END), 0) AS Visitors_09_10, " +
						    "    COALESCE(SUM(CASE WHEN TIME(combined.EnterDate) >= '10:00:00' AND TIME(combined.EnterDate) <= '10:59:59' THEN combined.Amount ELSE 0 END), 0) AS Visitors_10_11, " +
						    "    COALESCE(SUM(CASE WHEN TIME(combined.EnterDate) >= '11:00:00' AND TIME(combined.EnterDate) <= '11:59:59' THEN combined.Amount ELSE 0 END), 0) AS Visitors_11_12, " +
						    "    COALESCE(SUM(CASE WHEN TIME(combined.EnterDate) >= '12:00:00' AND TIME(combined.EnterDate) <= '12:59:59' THEN combined.Amount ELSE 0 END), 0) AS Visitors_12_13, " +
						    "    COALESCE(SUM(CASE WHEN TIME(combined.EnterDate) >= '13:00:00' AND TIME(combined.EnterDate) <= '13:59:59' THEN combined.Amount ELSE 0 END), 0) AS Visitors_13_14, " +
						    "    COALESCE(SUM(CASE WHEN TIME(combined.EnterDate) >= '14:00:00' AND TIME(combined.EnterDate) <= '14:59:59' THEN combined.Amount ELSE 0 END), 0) AS Visitors_14_15, " +
						    "    COALESCE(SUM(CASE WHEN TIME(combined.EnterDate) >= '15:00:00' AND TIME(combined.EnterDate) <= '16:00:00' THEN combined.Amount ELSE 0 END), 0) AS Visitors_15_16 " +
						    "FROM ( " +
						    "    SELECT 'Solo Occasional' AS OrderType " +
						    "    UNION ALL " +
						    "    SELECT 'Family Occasional' " +
						    "    UNION ALL " +
						    "    SELECT 'Group Occasional' " +
						    "     UNION ALL " +
						    "    SELECT 'Group Preorder' " +
						    "     UNION ALL " +
						    "    SELECT 'Family Preorder' " +
						    "     UNION ALL " +
						    "    SELECT 'Solo Preorder' " +
						    ") AS OrderTypes " +
						    "LEFT JOIN ( " +
						    "    SELECT EnterDate, ExitDate, Amount, OrderType " +
						    "    FROM occasionalvisits " +
						    "    WHERE parkId = ? AND MONTH(EnterDate) = ? AND YEAR(EnterDate) = ? " +
						    "    UNION ALL " +
						    "    SELECT EnterDate, ExitDate, Amount, OrderType " +
						    "    FROM preorders " +
						    "    WHERE parkId = ? AND MONTH(EnterDate) = ? AND YEAR(EnterDate) = ? " +
						    ") AS combined ON OrderTypes.OrderType = combined.OrderType " +
						    "GROUP BY OrderTypes.OrderType;");

			stmt.setInt(1, report.getRequestedPark().getParkId());
			stmt.setInt(2, report.getMonth());
			stmt.setInt(3, report.getYear());
			stmt.setInt(4, report.getRequestedPark().getParkId());
			stmt.setInt(5, report.getMonth());
			stmt.setInt(6, report.getYear());

			ResultSet rs = stmt.executeQuery();
			if (!rs.next())
				return false;
			HashMap<Integer, ArrayList<Integer>> totalVisitsByEnterTime = new HashMap<Integer, ArrayList<Integer>>();
			rs.previous();
			while (rs.next()) {
				ArrayList<Integer> data = new ArrayList<Integer>();
				String orderType = rs.getString(1);
				int indexToAdd = (orderType.equals("Solo Occasional") ? 0
						: orderType.equals("Solo Preorder") ? 5
								: orderType.equals("Family Occasional") ? 1
										: orderType.equals("Family Preorder") ? 4
												: orderType.equals("Group Occasional") ? 2
														: orderType.equals("Group Preorder") ? 3 : 6);

				data.add(rs.getInt(2));
				data.add(rs.getInt(3));
				data.add(rs.getInt(4));
				data.add(rs.getInt(5));
				data.add(rs.getInt(6));
				data.add(rs.getInt(7));
				data.add(rs.getInt(8));
				data.add(rs.getInt(9));

				totalVisitsByEnterTime.put(indexToAdd, data);
			}

			report.setTotalVisitsEnterByTime(totalVisitsByEnterTime);

			return true;
		} catch (SQLException ex) {
			return false;
		}
	}
	
	/**
	 * Retrieves a summary of visitor idle times by type for a given park, month, and year. This method aggregates
	 * the duration visitors spent in the park, categorized into intervals, and differentiates between types of visits
	 * (Solo, Family, Group) and their methods of entry (Occasional, Preorder).
	 *
	 * @param report The {@link VisitsReport} object that includes park identification and the time frame for the report.
	 * @return true if the summary was successfully retrieved and set in the report object, false if there was an error or no data.
	 */
	private boolean getParkIdleVisitTimeSummary(VisitsReport report) {
		try {
			Connection con = MySqlConnection.getInstance().getConnection();
			PreparedStatement stmt = con.prepareStatement("SELECT "
				    + "SUM(CASE WHEN (OrderType='Solo Occasional' OR OrderType='Solo Preorder') AND Duration='0-1' THEN Amount ELSE 0 END) AS TotalSolo0_1, "
				    + "SUM(CASE WHEN (OrderType='Family Occasional' OR OrderType='Family Preorder') AND Duration='0-1' THEN Amount ELSE 0 END) AS TotalFamily0_1, "
				    + "SUM(CASE WHEN (OrderType='Group Occasional' OR OrderType='Group Preorder') AND Duration='0-1' THEN Amount ELSE 0 END) AS TotalGroup0_1, "
				    + "SUM(CASE WHEN (OrderType='Solo Occasional' OR OrderType='Solo Preorder') AND Duration='1-2' THEN Amount ELSE 0 END) AS TotalSolo1_2, "
				    + "SUM(CASE WHEN (OrderType='Family Occasional' OR OrderType='Family Preorder') AND Duration='1-2' THEN Amount ELSE 0 END) AS TotalFamily1_2, "
				    + "SUM(CASE WHEN (OrderType='Group Occasional' OR OrderType='Group Preorder') AND Duration='1-2' THEN Amount ELSE 0 END) AS TotalGroup1_2, "
				    + "SUM(CASE WHEN (OrderType='Solo Occasional' OR OrderType='Solo Preorder') AND Duration='2-3' THEN Amount ELSE 0 END) AS TotalSolo2_3, "
				    + "SUM(CASE WHEN (OrderType='Family Occasional' OR OrderType='Family Preorder') AND Duration='2-3' THEN Amount ELSE 0 END) AS TotalFamily2_3, "
				    + "SUM(CASE WHEN (OrderType='Group Occasional' OR OrderType='Group Preorder') AND Duration='2-3' THEN Amount ELSE 0 END) AS TotalGroup2_3, "
				    + "SUM(CASE WHEN (OrderType='Solo Occasional' OR OrderType='Solo Preorder') AND Duration='3-4' THEN Amount ELSE 0 END) AS TotalSolo3_4, "
				    + "SUM(CASE WHEN (OrderType='Family Occasional' OR OrderType='Family Preorder') AND Duration='3-4' THEN Amount ELSE 0 END) AS TotalFamily3_4, "
				    + "SUM(CASE WHEN (OrderType='Group Occasional' OR OrderType='Group Preorder') AND Duration='3-4' THEN Amount ELSE 0 END) AS TotalGroup3_4, "
				    + "SUM(CASE WHEN (OrderType='Solo Occasional' OR OrderType='Solo Preorder') AND Duration='4+' THEN Amount ELSE 0 END) AS TotalSolo4, "
				    + "SUM(CASE WHEN (OrderType='Family Occasional' OR OrderType='Family Preorder') AND Duration='4+' THEN Amount ELSE 0 END) AS TotalFamily4, "
				    + "SUM(CASE WHEN (OrderType='Group Occasional' OR OrderType='Group Preorder') AND Duration='4+' THEN Amount ELSE 0 END) AS TotalGroup4 "
				    + "FROM ( "
				    + "SELECT OrderId, EnterDate, ExitDate, Amount, OrderType, "
				    + "CASE "
				    + "WHEN TIMEDIFF(ExitDate, EnterDate) BETWEEN '00:00:00' AND '01:00:00' THEN '0-1' "
				    + "WHEN TIMEDIFF(ExitDate, EnterDate) BETWEEN '01:00:01' AND '02:00:00' THEN '1-2' "
				    + "WHEN TIMEDIFF(ExitDate, EnterDate) BETWEEN '02:00:01' AND '03:00:00' THEN '2-3' "
				    + "WHEN TIMEDIFF(ExitDate, EnterDate) BETWEEN '03:00:01' AND '04:00:00' THEN '3-4' "
				    + "ELSE '4+' "
				    + "END AS Duration "
				    + "FROM preorders "
				    + "WHERE parkId = ? AND MONTH(EnterDate) = ? AND YEAR(EnterDate) = ? "
				    + "UNION ALL "
				    + "SELECT OrderId, EnterDate, ExitDate, Amount, OrderType, "
				    + "CASE "
				    + "WHEN TIMEDIFF(ExitDate, EnterDate) BETWEEN '00:00:00' AND '01:00:00' THEN '0-1' "
				    + "WHEN TIMEDIFF(ExitDate, EnterDate) BETWEEN '01:00:01' AND '02:00:00' THEN '1-2' "
				    + "WHEN TIMEDIFF(ExitDate, EnterDate) BETWEEN '02:00:01' AND '03:00:00' THEN '2-3' "
				    + "WHEN TIMEDIFF(ExitDate, EnterDate) BETWEEN '03:00:01' AND '04:00:00' THEN '3-4' "
				    + "ELSE '4+' "
				    + "END AS Duration "
				    + "FROM occasionalvisits "
				    + "WHERE parkId = ? AND MONTH(EnterDate) = ? AND YEAR(EnterDate) = ? "
				    +") AS subquery;");
			
			
			stmt.setInt(1, report.getRequestedPark().getParkId());
			stmt.setInt(2, report.getMonth());
			stmt.setInt(3, report.getYear());
			stmt.setInt(4, report.getRequestedPark().getParkId());
			stmt.setInt(5, report.getMonth());
			stmt.setInt(6, report.getYear());
			
			ResultSet rs = stmt.executeQuery();
			if(!rs.next())
				return false;
			HashMap<Integer,ArrayList<Integer>> totalIdleTimeByGap= new HashMap<Integer, ArrayList<Integer>>();
			
			for(int i=0,j=0;i<15;i+=3,j+=1) {
				ArrayList<Integer> data= new ArrayList<Integer>();
				data.add(rs.getInt(i+1));
				data.add(rs.getInt(i+2));
				data.add(rs.getInt(i+3));
				totalIdleTimeByGap.put(j, data);
			}
			
			report.setTotalIdleTimeByGap(totalIdleTimeByGap);

			return true;
		} catch (SQLException ex) {
//			serverController.printToLogConsole("Query search for user failed");
				return false;
			}
	}
	
	/**
	 * Inserts or updates a generated cancellations report into the database for a specific park, month, and year.
	 * This method ensures that only one report exists for a given time period and park by overwriting any existing report.
	 *
	 * @param report The {@link CancellationsReport} containing the generated report details including the park, time frame, and the PDF blob.
	 * @return true if the report was successfully inserted or updated in the database, false otherwise.
	 */
	private boolean insertGeneratedCancellationsReportToDatabase(CancellationsReport report) {
		try {
			Connection con = MySqlConnection.getInstance().getConnection();
			PreparedStatement stmt = con.prepareStatement(
					"INSERT INTO cancellationsreports (parkId, year, month, pdfblob) \r\n" + "VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE PDFBlob = VALUES(PDFBlob);");

			stmt.setInt(1, report.getRequestedPark().getParkId());
			stmt.setInt(2, report.getYear());
			stmt.setInt(3, report.getMonth());
			stmt.setBytes(4, report.getBlobPdfContent());

			int rs = stmt.executeUpdate();

			// if the query ran successfully, but returned as empty table.
			if (rs == 0) {
				return false;
			}

			return true;
		} catch (SQLException ex) {
//		serverController.printToLogConsole("Query search for user failed");
			return false;
		}
	}
	
	/**
	 * Inserts or updates a generated visits amount report into the database for a specified park, month, and year.
	 * Utilizes the ON DUPLICATE KEY UPDATE mechanism to ensure unique reports per time period and park, updating existing entries if necessary.
	 *
	 * @param report The {@link VisitsReport} containing details of the generated report including park information, time frame, and the PDF content.
	 * @return true if the report was successfully inserted or updated in the database, false in case of any errors.
	 */
	private boolean insertVisitsAmountReportToDatabase(VisitsReport report) {
		try {
			Connection con = MySqlConnection.getInstance().getConnection();
			PreparedStatement stmt = con.prepareStatement(
					"INSERT INTO visitsreport (parkId, year, month, pdfblob) \r\n" + "VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE PDFBlob = VALUES(PDFBlob);");
 
			stmt.setInt(1, report.getRequestedPark().getParkId());
			stmt.setInt(2, report.getYear());
			stmt.setInt(3, report.getMonth());
			stmt.setBytes(4, report.getBlobPdfContent());

			int rs = stmt.executeUpdate();

			// if the query ran successfully, but returned as empty table.
			if (rs == 0) {
				return false;
			}

			return true;
		} catch (SQLException ex) {
//		serverController.printToLogConsole("Query search for user failed");
			return false;
		}
	}
	
	/**
	 * Inserts or updates the generated total amount division report into the database for a designated park, month, and year.
	 * If a report for the specified criteria already exists, it updates the existing record with the new PDF content.
	 *
	 * @param report The {@link AmountDivisionReport} containing the generated report's details, such as park ID, time frame, and the report's PDF blob.
	 * @return true if the report was successfully inserted or updated, false if the operation failed.
	 */
	private boolean insertTotalAmountReportToDatabase(AmountDivisionReport report) {
		try {
			Connection con = MySqlConnection.getInstance().getConnection();
			PreparedStatement stmt = con.prepareStatement(
					"INSERT INTO totalvisitorsreport (parkId, year, month, pdfblob) \r\n" + "VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE PDFBlob = VALUES(PDFBlob);");
 
			stmt.setInt(1, report.getRequestedPark().getParkId());
			stmt.setInt(2, report.getYear());
			stmt.setInt(3, report.getMonth());
			stmt.setBytes(4, report.getBlobPdfContent());

			int rs = stmt.executeUpdate();

			// if the query ran successfully, but returned as empty table.
			if (rs == 0) {
				return false;
			}

			return true;
		} catch (SQLException ex) {
			return false;
		}
	}
	
	/**
	 * Retrieves the blob content of a previously generated cancellations report from the database.
	 *
	 * @param report The {@link CancellationsReport} identifying the report to retrieve.
	 * @return An array of bytes representing the PDF content of the report, or null if the report could not be found or an error occurred.
	 */
	public byte[] getRequestedCancellationsReport(CancellationsReport report) {
		try {
			Connection con = MySqlConnection.getInstance().getConnection();
			PreparedStatement stmt = con.prepareStatement(
					"SELECT PdfBlob FROM cancellationsreports WHERE Year = ? AND Month = ? AND ParkId = ?");

			stmt.setInt(1, report.getYear());
			stmt.setInt(2, report.getMonth());
			stmt.setInt(3, report.getRequestedPark().getParkId());

			ResultSet rs = stmt.executeQuery();

			if (!rs.next()) {
				return null;
			}
			// retrieve the Blob from the database
			Blob pdfBlob = rs.getBlob(1);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buf = new byte[50000];
			try (InputStream in = pdfBlob.getBinaryStream()) {
				int n;
				while ((n = in.read(buf)) > 0) {
					baos.write(buf, 0, n);
				}
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}

			byte[] pdfBytes = baos.toByteArray();
			return pdfBytes;
		} catch (SQLException ex) {
			return null;
		}
	}
	
	/**
	 * Retrieves the blob content of a previously generated visits report from the database.
	 *
	 * @param report The {@link VisitsReport} identifying the report to retrieve.
	 * @return An array of bytes representing the PDF content of the report, or null if the report could not be found or an error occurred.
	 */
	public byte[] getRequestedVisitsReport(VisitsReport report) {
		try {
			Connection con = MySqlConnection.getInstance().getConnection();
			PreparedStatement stmt = con.prepareStatement(
					"SELECT PdfBlob FROM visitsreport WHERE Year = ? AND Month = ? AND ParkId = ?");

			stmt.setInt(1, report.getYear());
			stmt.setInt(2, report.getMonth());
			stmt.setInt(3, report.getRequestedPark().getParkId());

			ResultSet rs = stmt.executeQuery();

			if (!rs.next()) {
				return null;
			}
			// retrieve the Blob from the database
			Blob pdfBlob = rs.getBlob(1);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buf = new byte[50000];
			try (InputStream in = pdfBlob.getBinaryStream()) {
				int n;
				while ((n = in.read(buf)) > 0) {
					baos.write(buf, 0, n);
				}
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}

			byte[] pdfBytes = baos.toByteArray();
			return pdfBytes;
		} catch (SQLException ex) {
			return null;
		}
	}
	
	/**
	 * Retrieves the blob content of a previously generated total amount division report from the database.
	 *
	 * @param report The {@link AmountDivisionReport} identifying the report to retrieve.
	 * @return An array of bytes representing the PDF content of the report, or null if the report could not be found or an error occurred.
	 */
	public byte[] getRequestedTotalAmountReport(AmountDivisionReport report) {
			try {
				Connection con = MySqlConnection.getInstance().getConnection();
				PreparedStatement stmt = con.prepareStatement(
						"SELECT PdfBlob FROM totalvisitorsreport WHERE Year = ? AND Month = ? AND ParkId = ?");

				stmt.setInt(1, report.getYear());
				stmt.setInt(2, report.getMonth());
				stmt.setInt(3, report.getRequestedPark().getParkId());

				ResultSet rs = stmt.executeQuery();

				if (!rs.next()) {
					return null;
				}
				// retrieve the Blob from the database
				Blob pdfBlob = rs.getBlob(1);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] buf = new byte[50000];
				try (InputStream in = pdfBlob.getBinaryStream()) {
					int n;
					while ((n = in.read(buf)) > 0) {
						baos.write(buf, 0, n);
					}
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}

				byte[] pdfBytes = baos.toByteArray();
				return pdfBytes;
			} catch (SQLException ex) {
				return null;
			}

		}
	
		

}
