package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.YearMonth;
import java.util.ArrayList;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import logic.AmountDivisionReport;
import logic.CancellationsReport;
import logic.ParkDailySummary;
import logic.ParkFullDaySummary;
import logic.UsageReport;
import logic.VisitsReport;

/**
 * The {@code ReportGenerator} class is responsible for generating various reports related to park visitations,
 * usage, cancellations, and visitor amount divisions. It supports generating these reports in PDF format,
 * utilizing JFreeChart for graphical representations and iTextPDF for PDF generation.
 * @author Tamer Amer, Gal Bitton, Rabea Lahham, Bahaldeen Swied, Ron Sisso, Nadav Reubens.
 */
public class ReportGenerator {
	
	/**
	 * Generates a usage report for a specified park and time period as a PDF blob.
	 * The report includes information on park usage, highlighting days and times
	 * that did not reach full capacity, along with visual representations through bar charts.
	 * 
	 * @param report The {@link UsageReport} object containing usage data for the park.
	 * @return A byte array representing the generated PDF report, or {@code null} if an error occurs.
	*/
	public static byte[] generateUsageReportAsPdfBlob(UsageReport report)
	{
		Document document = new Document();
		YearMonth yearMonth = YearMonth.of(report.getYear(),report.getMonth());
		int daysInMonth= yearMonth.lengthOfMonth();
		// Create a temporary file
		Path tempFilePath = null;
		File tempFile = null;
		try {
			tempFilePath = Files.createTempFile("usage_report", ".pdf");
			tempFile = tempFilePath.toFile();
			// Initialize PdfWriter to write to the temporary file
			PdfWriter.getInstance(document, new FileOutputStream(tempFile));
			document.open();

			// Header Font
			Font headerFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
			Font boldFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
			Font normalFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);

			// Header
			Paragraph header = new Paragraph("Usage Report", headerFont);
			header.setAlignment(Element.ALIGN_CENTER);
			document.add(header);

			// Adding some space
			document.add(new Paragraph("\n"));

			// Park, Year, Month Info
			document.add(new Paragraph("Park: " + report.getRequestedPark().name(), boldFont));
			document.add(new Paragraph("Year: " + report.getYear(), boldFont));
			document.add(new Paragraph("Month: " + report.getMonth(), boldFont));

			// Adding some space before the table
			document.add(new Paragraph("\n"));
			//****************************************added by nadav************************************
			// Table
			PdfPTable table = new PdfPTable(3); // 3 columns.
			table.setWidthPercentage(100); // Width 100%
			table.setSpacingBefore(10f); // Space before table

			// Table headers
			String[] tableHeaders = { "Time", "Days that didnt reach full capacity in specific time","Percentage"};
			for (String headerText : tableHeaders) {
				PdfPCell headerCell = new PdfPCell(new Paragraph(headerText, boldFont));
				headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(headerCell);
			}
			// Table data
			// Assuming reportData is a LinkedHashMap or TreeMap to maintain order
			for (Integer hour : report.getReportData().keySet()) {
				ParkFullDaySummary summary = report.getReportData().get(hour);
				String specifHour=String.format("%d:00", hour);
				int notFull=daysInMonth-summary.getTimesFullInSpecificHour();
				String timesOfFull=String.format("%d",notFull );
				double perc=((double)summary.getTimesFullInSpecificHour())/daysInMonth;
				perc*=100;
				double notFullPerc=100-perc;
				String percentageOfFull=String.format("%.2f%%", notFullPerc);
				
				table.addCell(new PdfPCell(new Paragraph(specifHour, normalFont)));
				table.addCell(new PdfPCell(new Paragraph(timesOfFull, normalFont)));
				table.addCell(new PdfPCell(new Paragraph(percentageOfFull, normalFont)));
			}
			
			document.add(table);
			document.add(new Paragraph("\n\n\n"));
			
			//Bar Chart
			DefaultCategoryDataset dataset1 = new DefaultCategoryDataset();
			for (Integer hour : report.getReportData().keySet()) {
				
				ParkFullDaySummary summary = report.getReportData().get(hour);
				double avg=((double)summary.getTimesFullInSpecificHour())/daysInMonth;
				avg*=100;
				String time=String.format("%d:00", hour);
				dataset1.addValue(avg, "avg full capacity", time);
		        dataset1.addValue(100-avg, "avg not full capacity",time);
			}

			
			
	        // Create the bar chart
	        JFreeChart barChart = ChartFactory.createBarChart(
	                "", // chart title
	                "Category",              // domain axis label
	                "Percentage of Full Days",                 // range axis label
	                dataset1);
	        CategoryPlot plot1 = (CategoryPlot) barChart.getPlot();
	        // Get the renderer and cast it to BarRenderer
	        BarRenderer renderer = (BarRenderer) plot1.getRenderer();
	        // Set the bar location
	        renderer.setItemMargin(0); // Adjust the margin to move the bars closer to each other  
	        // Get the domain axis (category axis)
	        CategoryAxis domainAxis1 = plot1.getDomainAxis();
	        domainAxis1.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
	        double barWidthPercentage = 0.2; 
	        renderer.setMaximumBarWidth(barWidthPercentage);
	        
	        
	       
	        // Create and set up the chart panel
	        Path chartPath1 = Files.createTempFile("chart_", ".png");
	        
			ChartUtils.saveChartAsPNG(chartPath1.toFile(), barChart, 500, 300);
			Image chartImage1 = Image.getInstance(chartPath1.toString());
			PdfPTable table1 = new PdfPTable(1);
			table1.setWidthPercentage(100); // Make table width 100% of the document
			// Add the chart image to the table cell
			table1.addCell(chartImage1);
			Paragraph chartParagraph = new Paragraph();
			
			chartParagraph.add(table1);
			chartParagraph.setAlignment(Element.ALIGN_CENTER); // Align the image to the center
			document.add(chartParagraph);
			document.add(new Paragraph("\n"));
	
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			document.close();
		}

		// Now, read the content of the temporary file into a byte array
		try (FileInputStream input = new FileInputStream(tempFile)) {
			byte[] fileAsBytes = new byte[(int) tempFile.length()];
			input.read(fileAsBytes);
			return fileAsBytes;

		} catch (Exception e) {
			e.printStackTrace();
			return null;

		}
	}
	
	/**
	 * Generates a cancellations report for a specified park and time period as a PDF blob.
	 * The report details cancellation metrics, including total cancellations, cancellations over time,
	 * and total orders, complemented by graphical line charts for a clearer overview.
	 * 
	 * @param report The {@link CancellationsReport} object containing cancellation data for the park.
	 * @return A byte array representing the generated PDF report, or {@code null} if an error occurs.
	 */
	public static byte[] generateCancellationsReportAsPdfBlob(CancellationsReport report) {

		Document document = new Document();
		// Create a temporary file
		Path tempFilePath = null;
		File tempFile = null;
		try {
			tempFilePath = Files.createTempFile("cancellations_report", ".pdf");
			tempFile = tempFilePath.toFile();
			// Initialize PdfWriter to write to the temporary file
			PdfWriter.getInstance(document, new FileOutputStream(tempFile));
			document.open();

			// Header Font
			Font headerFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
			Font boldFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
			Font normalFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);

			// Header
			Paragraph header = new Paragraph("Cancellations Report", headerFont);
			header.setAlignment(Element.ALIGN_CENTER);
			document.add(header);

			// Adding some space
			document.add(new Paragraph("\n"));

			// Park, Year, Month Info
			document.add(new Paragraph("Park: " + report.getRequestedPark().name(), boldFont));
			document.add(new Paragraph("Year: " + report.getYear(), boldFont));
			document.add(new Paragraph("Month: " + report.getMonth(), boldFont));

			// Adding some space before the table
			document.add(new Paragraph("\n"));

			// Table
			PdfPTable table = new PdfPTable(4); // 4 columns.
			table.setWidthPercentage(100); // Width 100%
			table.setSpacingBefore(10f); // Space before table

			// Table headers
			String[] tableHeaders = { "Day", "Cancels", "Time Passed", "Total Orders" };
			for (String headerText : tableHeaders) {
				PdfPCell headerCell = new PdfPCell(new Paragraph(headerText, boldFont));
				headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(headerCell);
			}

			// Table data
			// Assuming reportData is a LinkedHashMap or TreeMap to maintain order
			for (Integer day : report.getReportData().keySet()) {
				ParkDailySummary summary = report.getReportData().get(day);
				table.addCell(new PdfPCell(new Paragraph(day.toString(), normalFont)));
				table.addCell(new PdfPCell(new Paragraph(String.valueOf(summary.getCancelsOrders()), normalFont)));
				table.addCell(new PdfPCell(new Paragraph(String.valueOf(summary.getTimePassedOrders()), normalFont)));
				table.addCell(new PdfPCell(new Paragraph(String.valueOf(summary.getTotalOrders()), normalFont)));
			}

			document.add(table);

			// Line Chart
			DefaultCategoryDataset dataset = new DefaultCategoryDataset();
			for (Integer day : report.getReportData().keySet()) {
				ParkDailySummary summary = report.getReportData().get(day);
				dataset.addValue(summary.getCancelsOrders(), "Cancels", day);
				dataset.addValue(summary.getTimePassedOrders(), "Time Passed", day);
				dataset.addValue(summary.getTotalOrders(), "Total Orders", day);
			}

			JFreeChart lineChart = ChartFactory.createLineChart("Monthly Statistics", "Day", "Count", dataset,
					PlotOrientation.VERTICAL, true, true, false);

			// Get the plot and configure the range (Y) and domain (X) axes
			CategoryPlot plot = (CategoryPlot) lineChart.getPlot();
			CategoryAxis domainAxis = plot.getDomainAxis();

			// Optional: rotate domain axis labels to make them more readable
			domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);

			// Optional: set the lower margin or category margin if needed
			domainAxis.setLowerMargin(0.01);
			domainAxis.setCategoryMargin(0.01);
			// Save chart as image and add to document
			Path chartPath = Files.createTempFile("chart_", ".png");
			ChartUtils.saveChartAsPNG(chartPath.toFile(), lineChart, 500, 300);
			Image chartImage = Image.getInstance(chartPath.toString());
			document.add(chartImage);
			
			document.add(new Paragraph("\n"));

			// Cancels Average
			Paragraph cancelsAveragePara = new Paragraph(String.format("Cancels Average: %.2f", report.getAverageCancels()), normalFont);
			cancelsAveragePara.setAlignment(Element.ALIGN_CENTER);
			document.add(cancelsAveragePara);

			// Cancels Median
			Paragraph cancelsMedianPara = new Paragraph(String.format("Cancels Median: %.2f", report.getMedianCancels()), normalFont);
			cancelsMedianPara.setAlignment(Element.ALIGN_CENTER);
			document.add(cancelsMedianPara);


		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			document.close();
		}

		// Now, read the content of the temporary file into a byte array
		try (FileInputStream input = new FileInputStream(tempFile)) {
			byte[] fileAsBytes = new byte[(int) tempFile.length()];
			input.read(fileAsBytes);
			return fileAsBytes;

		} catch (Exception e) {
			e.printStackTrace();
			return null;

		}

	}
	
	/**
	 * Generates a comprehensive visits report for a specified park and time period as a PDF.
	 * The report outlines the distribution of visitor types across different times and their idle times within the park,
	 * featuring both tabular data and graphical bar charts for an intuitive understanding of park visits.
	 * 
	 * @param report The {@link VisitsReport} object containing visitation data for the park.
	 * @return A byte array representing the generated PDF report, or {@code null} if an error occurs.
	 */
	public static byte[] generateVisitsReportAsPdf(VisitsReport report) {
		Document document = new Document();
		// Create a temporary file
		Path tempFilePath = null;
		File tempFile = null;
		try {
			tempFilePath = Files.createTempFile("visits_report", ".pdf");
			tempFile = tempFilePath.toFile();
			// Initialize PdfWriter to write to the temporary file
			PdfWriter.getInstance(document, new FileOutputStream(tempFile));
			document.open();

			// Header Font
			Font headerFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
			Font boldFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);

			// Header
			Paragraph header = new Paragraph("Visits Report", headerFont);
			header.setAlignment(Element.ALIGN_CENTER);
			document.add(header);

			// Adding some space
			document.add(new Paragraph("\n"));

			document.add(new Paragraph("This report contains total amount of visitors distributed by visitors type. ", boldFont));
			document.add(new Paragraph("In addition, this report includes the total idle time inside the park. ", boldFont));
			
			// Park, Year, Month Info
			document.add(new Paragraph("Park: " + report.getRequestedPark().name(), boldFont));
			document.add(new Paragraph("Year: " + report.getYear(), boldFont));
			document.add(new Paragraph("Month: " + report.getMonth(), boldFont));

			// Adding some space before the table
			document.add(new Paragraph("\n"));

			// Table with 4 columns
			PdfPTable table = new PdfPTable(4); 
			table.setWidthPercentage(100); // Set table width to 100% of the page
			table.setSpacingBefore(10f); // Set spacing before the table

			// Define the headers
			String[] headers = new String[] {"Hour", "Solo Visits", "Family Visits", "Group Visits"};

			// Define column widths to match the layout in the image
			float[] columnWidths = new float[] {2f, 2f, 2f, 2f};
			table.setWidths(columnWidths);

			// Add the header row
			for (String head : headers) {
			    PdfPCell cell = new PdfPCell(new Phrase(head));
			    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			    cell.setPadding(5); // Set padding for a bit of space, adjust as needed
			    table.addCell(cell);
			}

			// Add the data rows
			String[] hours = new String[] {
			    "08:00-08:59",
			    "09:00-09:59",
			    "10:00-10:59",
			    "11:00-11:59",
			    "12:00-12:59",
			    "13:00-13:59",
			    "14:00-14:59",
			    "15:00-16:00",
			};

			ArrayList<Integer> soloOccasional = report.getTotalVisitsByEnterTime().get(0);
			ArrayList<Integer> soloPreorder = report.getTotalVisitsByEnterTime().get(5);
			ArrayList<Integer> familyOccasional = report.getTotalVisitsByEnterTime().get(1);
			ArrayList<Integer> familyPreorder = report.getTotalVisitsByEnterTime().get(4);
			ArrayList<Integer> groupOccasional = report.getTotalVisitsByEnterTime().get(2);
			ArrayList<Integer> groupPreorder = report.getTotalVisitsByEnterTime().get(3);
			int i=0;
			// For each hour slot, add a row to the table
			for (String hour : hours) {
			    
			    // Add hour cell
			    PdfPCell cellHour = new PdfPCell(new Phrase(hour));
			    cellHour.setHorizontalAlignment(Element.ALIGN_CENTER);
			    table.addCell(cellHour);
			    
			    Integer totalSolo = soloOccasional.get(i)+soloPreorder.get(i);
			    Integer totalFamily = familyOccasional.get(i)+familyPreorder.get(i);
			    Integer totalGroup = groupOccasional.get(i)+groupPreorder.get(i);
			    
		        PdfPCell cellCount = new PdfPCell(new Phrase(String.valueOf(totalSolo)));
		        cellCount.setHorizontalAlignment(Element.ALIGN_CENTER);
		        table.addCell(cellCount);
		        
		        cellCount = new PdfPCell(new Phrase(String.valueOf(totalFamily)));
		        cellCount.setHorizontalAlignment(Element.ALIGN_CENTER);
		        table.addCell(cellCount);
		        
		        cellCount = new PdfPCell(new Phrase(String.valueOf(totalGroup)));
		        cellCount.setHorizontalAlignment(Element.ALIGN_CENTER);
		        table.addCell(cellCount);
		        i++;
			}

			// Add table to document
			document.add(table);
			
			document.add(new Paragraph("\n\n"));
			
			table = new PdfPTable(4);
			table.setWidthPercentage(100); // Set table width to 100% of the page
			table.setSpacingBefore(10f); // Set spacing before the table

			// Define the headers
			headers = new String[] {"Idle Time", "Solo Visits", "Family Visits", "Group Visits"};

			// Define column widths to match the layout in the image
			columnWidths = new float[] {2f, 2f, 2f, 2f};
			table.setWidths(columnWidths);

			// Add the header row
			for (String head : headers) {
			    PdfPCell cell = new PdfPCell(new Phrase(head));
			    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			    cell.setPadding(5); // Set padding for a bit of space, adjust as needed
			    table.addCell(cell);
			}

			// Add the data rows
			String[] idleTime = new String[] {
			    "0 up to 1",
			    "1 up to 2",
			    "2 up to 3",
			    "3 up to 4",
			    "4+",
			};

			int time=0;
			// For each hour slot, add a row to the table
			for (String idle : idleTime) {
			    
			    // Add hour cell
			    PdfPCell cellHour = new PdfPCell(new Phrase(idle));
			    cellHour.setHorizontalAlignment(Element.ALIGN_CENTER);
			    table.addCell(cellHour);
			    
			    ArrayList<Integer> currentIdle = report.getIdleTimeData(time++);
			    // Add visits count cells
			    for (int count : currentIdle) {
			        PdfPCell cellCount = new PdfPCell(new Phrase(String.valueOf(count)));
			        cellCount.setHorizontalAlignment(Element.ALIGN_CENTER);
			        table.addCell(cellCount);
			    }
			}

			// Add table to document
			document.add(table);
			

			// Line Chart
			DefaultCategoryDataset dataset = new DefaultCategoryDataset();
			for (int j=0;j<5;j++) {
				ArrayList<Integer> data = report.getIdleTimeData(j);
				String column = String.format("%s up to %s",j,j+1);
				if(j==4)
					column = "4+";
				dataset.addValue(data.get(0), "Solo",column);
				dataset.addValue(data.get(1),"Family",column);
				dataset.addValue(data.get(2),"Group",column);
			}

			JFreeChart chart = createBarChart(dataset,"Total Visits Distributed By Idle Time","Idle Time","Visits");
			Path chartPath = Files.createTempFile("chart_", ".png");
			ChartUtils.saveChartAsPNG(chartPath.toFile(), chart, 450, 400);
			
			// Add the grouped bar chart image to the PDF
			Image chartImage = Image.getInstance(chartPath.toString());
			document.add(chartImage);

			// Add the second grouped bar chart
			dataset = new DefaultCategoryDataset();
			for (int j=0;j<8;j++) {
				String column;
			    Integer totalSolo = soloOccasional.get(j)+soloPreorder.get(j);
			    Integer totalFamily = familyOccasional.get(j)+familyPreorder.get(j);
			    Integer totalGroup = groupOccasional.get(j)+groupPreorder.get(j);
				if(j!=7)
					column = String.format("%s:00-%s:59",8+j,8+j);
				else
					column = "15:00-16:00";
				dataset.addValue(totalSolo, "Solo",column);
				dataset.addValue(totalFamily,"Family",column);
				dataset.addValue(totalGroup,"Group",column);
			}

			chart = createBarChart(dataset,"Total Visits Distributed By Enter Time","Enter Time","Visits");
			chartPath = Files.createTempFile("chart_2", ".png");
			ChartUtils.saveChartAsPNG(chartPath.toFile(), chart, 450, 400);
			
			// Add the grouped bar chart image to the PDF
			chartImage = Image.getInstance(chartPath.toString());
			document.add(chartImage);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			document.close();
		}

		// Now, read the content of the temporary file into a byte array
		try (FileInputStream input = new FileInputStream(tempFile)) {
			byte[] fileAsBytes = new byte[(int) tempFile.length()];
			input.read(fileAsBytes);
			return fileAsBytes;

		} catch (Exception e) {
			e.printStackTrace();
			return null;

		}
	}
	
	/**
	 * Generates a report on the total number of visitors, segmented by visitor type, for a specified park
	 * and time period, rendered as a PDF. The report includes a pie chart visualizing the distribution
	 * of different visitor types as percentages of the total.
	 * 
	 * @param report The {@link AmountDivisionReport} object containing visitor distribution data for the park.
	 * @return A byte array representing the generated PDF report, or {@code null} if an error occurs.
	 */
	@SuppressWarnings("unchecked")
	public static byte[] generateTotalVisitorsAmountReportAsPdf(AmountDivisionReport report) {
		Document document = new Document();
		// Create a temporary file
		Path tempFilePath = null;
		File tempFile = null;
		try {
			tempFilePath = Files.createTempFile("TotalAmount_report", ".pdf");
			tempFile = tempFilePath.toFile();
			// Initialize PdfWriter to write to the temporary file
			PdfWriter.getInstance(document, new FileOutputStream(tempFile));
			document.open();

			// Header Font
			Font headerFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
			Font boldFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
			// Header
			Paragraph header = new Paragraph("Total amount of travlers report", headerFont);
			header.setAlignment(Element.ALIGN_CENTER);
			document.add(header);

			// Adding some space
			document.add(new Paragraph("\n"));

			// Park, Year, Month Info
			document.add(new Paragraph("Park: " + report.getRequestedPark().name(), boldFont));
			document.add(new Paragraph("Year: " + report.getYear(), boldFont));
			document.add(new Paragraph("Month: " + report.getMonth(), boldFont));

			// Adding some space before the table
			document.add(new Paragraph("\n"));

			// pie Chart
			@SuppressWarnings("rawtypes")
			DefaultPieDataset dataset = new DefaultPieDataset();
			int sum = report.getReportData().getAmountSolo()+report.getReportData().getAmountFamily()+report.getReportData().getAmountGroup();
			
			dataset.setValue(String.format("Solo - %.2f%%", (double)report.getReportData().getAmountSolo()/sum*100), report.getReportData().getAmountSolo());
	        dataset.setValue(String.format("Family - %.2f%%", (double)report.getReportData().getAmountFamily()/sum*100), report.getReportData().getAmountFamily());
	        dataset.setValue(String.format("Group - %.2f%%", (double)report.getReportData().getAmountGroup()/sum*100), report.getReportData().getAmountGroup());

	        JFreeChart pieChart = ChartFactory.createPieChart(
	                "Order Distribution", // chart title
	                dataset, // dataset
	                true, // include legend
	                true,
	                false);


			// Save chart as image and add to document
			Path chartPath = Files.createTempFile("chart_", ".png");
			ChartUtils.saveChartAsPNG(chartPath.toFile(), pieChart, 500, 300);
			Image chartImage = Image.getInstance(chartPath.toString());
			document.add(chartImage);
			document.add(new Paragraph("\n"));

		
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			document.close();
		}

		// Now, read the content of the temporary file into a byte array
		try (FileInputStream input = new FileInputStream(tempFile)) {
			byte[] pdfAsBytes = new byte[(int) tempFile.length()];
			input.read(pdfAsBytes);
			
			return pdfAsBytes;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}


	}
	
	/**
	 * Creates a bar chart based on the provided dataset, with customizable chart title, X-axis label, and Y-axis label.
	 * This method configures the chart appearance, including label positions and item margins, for optimal readability.
	 * 
	 * @param dataset  The {@link CategoryDataset} containing the data to plot.
	 * @param charTitle The title of the chart.
	 * @param xAxis     The label for the X-axis.
	 * @param yAxis     The label for the Y-axis.
	 * @return A {@link JFreeChart} object representing the configured bar chart.
	 */
	private static JFreeChart createBarChart(final CategoryDataset dataset,String charTitle, String xAxis,
			String yAxis) {
	    // Create the chart
	    JFreeChart chart = ChartFactory.createBarChart(
	    		charTitle,      // Chart title
	    		xAxis,                 // Domain axis label
	    		yAxis,                    // Range axis label
	            dataset,                     // Data
	            PlotOrientation.VERTICAL,
	            true,                        // Include legend
	            true,
	            false);

	    // Customizations for the chart, such as color, etc.
	    CategoryPlot plot = (CategoryPlot) chart.getPlot();
	    CategoryAxis domainAxis = plot.getDomainAxis();

	    // Rotate labels for better fit if needed
	    domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
	    BarRenderer renderer = (BarRenderer) plot.getRenderer();

	    // Custom renderer to display the grouped bars
	    renderer.setItemMargin(0.1); // Gap between groups

	    return chart;
	}
	
}
