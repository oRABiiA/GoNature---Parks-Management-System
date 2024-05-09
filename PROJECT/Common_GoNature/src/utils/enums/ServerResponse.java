package utils.enums;

public enum ServerResponse {
	

	Password_Incorrect, // user found but password is incorrect
	User_Already_Connected, // user is already connected to the server
	User_Does_Not_Found, // there is no such user in database table
	
	Employee_Connected_Successfully, // when user (guide/employee) connected successfully.
	Guide_Status_Pending, // when guide is in database, but still waiting for service employee approve.
	Guide_Connected_Successfully,
	Visitor_Have_No_Orders_Yet, // did not find visitor with such ID in orders table with relevant order
	Visitor_Connected_Successfully, //did find visitor with such ID in orders table with relevant order
	Guides_With_Status_Pending_Not_Found,
	Guides_With_Status_Pending_Found,
	
	Updated_Guides_To_Approved_Successfully,
	Updated_Guides_To_Approved_Failed,
	
	Order_PhoneNumber_Updated,
	Order_Paid_And_Confirmed,
	Order_Not_Paid,
	Order_Not_Confirmed,
	Order_Not_Found,
	Order_Found,
	Order_Added_Successfully,
	Order_Added_Failed,
	Order_Cancelled_Successfully,
	Order_Cancelled_Failed,
	Requested_Order_Date_Unavaliable,
	Requested_Order_Date_Is_Available,
	Too_Many_Visitors,
	Occasional_Visit_Order_Ready,
	Occasional_Visit_Added_Successfully,
	Park_Is_Full_For_Such_Occasional_Order,
	No_Orders_For_Today,
	Order_ExitDate_Updated,
	Order_Email_Updated,
	Order_Number_Of_Visitors_Updated,
	Order_Type_Updated,
	Order_EnterDate_Updated,
	
	
	Such_Report_Not_Found,
	Cancellations_Report_Found,
	Report_Generated_Successfully,
	Report_Failed_Generate,
	
	//Park Section
	Fetched_Park_Details_Successfully,
	Fetched_Park_Details_Failed,
	Park_Table_Is_Empty,
	Park_List_Names_Is_Created,
	Park_Price_Returned_Successfully,
	
	Order_Updated_Successfully,
	Order_Updated_Failed,
	Order_Deleted_Successfully,
	Order_Deleted_Failed,
	
	// Request Section
	Last_Request_With_Same_Type_Still_Pending,
	Request_Sent_To_Department_Successfully,
	There_Are_Not_Pending_Requests,
	Pending_Requests_Found_Successfully,
	Updated_Requests_Successfully,
	Updated_Requests_Failed,
	
	No_Notifications_Found,
	Notifications_Found,
	Import_All_Orders_Successfully,
	
	Exception_Was_Thrown, // when the server catch an exception
	Server_Closed, // when server is closing itself, he should send to all clients
	Server_Disconnected, // when server is disconnected, he should send to all clients
	
	User_Logout_Successfully, // irrelevant, user can logout by it's own.
	Query_Failed,
}
