package gui.view;

public enum ApplicationViewType {
	
	Landing_Page_Screen, // Common Landing Page : Include Connect Server and Login app.
	Customer_Screen, // Customer Screen : BorderPane load to center each relevant frame
	Customer_Homepage_Screen, // Customer Homepage : Common for all Customers
	Identication_Screen, // Customer (Guide/Visitor) : Search for order to manage
	Reschedule_Order_Screen, //Customer : Choose new Date or Enter waiting list.
	Handle_Order_Screen, // Customer (Guide/Visitor) : Manage current order, opens after Identication Screen
	Make_Order_Screen, // Customer (Guide/Visitor/New Visitor) : Make New Order to Park.
	Order_Summary_Screen, // Customer (Visitor/Guide/New Visitor) : Order Confirmation Message.
	
	Employee_Homepage_Screen, // Employee Homepage : Common for all employees
	Employee_Screen, // Employee Screen : BorderPane load to center each relevant frame
	Park_Available_Spots_Screen, // Employee (Department Manager/Park Manager/Park Employee) : See current in Park and available spots
	Park_Entrance_Screen, // Park Employee : Handle Entrance To Park.HandleOccasionalVisitScreen, // Park Employee: Handle Visitor who arrived at the park
	Payment_Receipt_Screen, // Park Employee : Show Receipts to Visitors
	HandleOccasionalVisitScreen,
	
	Manage_Guides_Screen, // Service Employee : Search for guide to register.
	
	Park_Settings_Screen, // Park Manager : Change Park Settings Requests.
	Create_Reports_Screen, // Employee (Department Manager/Park Manager) : Can Create Reports.
	Request_Table_Screen, // Department Manager : Requests need to be approved.
	View_Reports_Screen, // Department Manager : Can view reports.
	
	
}
