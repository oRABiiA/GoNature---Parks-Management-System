package utils.enums;

public enum ClientRequest {
	
	Login_As_Guide, // After select guide in landing page, and clicked login
	Login_As_Visitor, // After select visitor in landing page, and clicked login
	Login_As_Employee, // After select employee in landing page, and clicked login
	Logout, // Common for all clients, after clicked logout.
	
	Insert_New_Order_As_Wait_Notify, // After customer clicked make order, add order status as wait notification
	Add_New_Order_If_Available,
	Update_Order_Status_WaitNotification, // After order created successfully.
	Update_Order_Status_SentNotification, // After sent notification to client to confirm the order.
	Update_Order_Status_Confirmed, // After the client confirmed the order, after notification was sent.
	Update_Order_Status_Canceled, // After the client cancel the order
	Update_Order_Status_InWaitingList, // After the client entered waiting list
	Update_Order_Status_AvailableSpot, // After a spot was released from waiting list
	Update_Order_Status_In_Park, // After the client entered park (by park entrance of park employee)
	Update_Order_Status_Time_Passed, // After the datetime of order passed, and client didn't enter park.
	Update_Order_Status_Completed,
	
//	Search_Time_Passed_Orders,
	Search_Order_For_Enter_Park, // After the park employee click enter park or new visit (occasional visit), check if order exists.
	Calculate_Order_Price_After_Discount, // After the park employee click enter park for relevant order.
	
	Search_For_Relevant_Order, // After customer search order to manage, return such order (except canceled, done, in park, time passed).
	
	Add_Occasional_Visit_As_In_Park, // After park employee add new occasional visit by clicking new visit 
	
	Import_Available_Dates_Week_Ahead, // After the customer select available dates in finish order controller
	Add_New_Order_To_Waiting_List, // After the customer select enter waiting list and click confirm on finish order controller,
	
	Import_Guide_Details, // After Service Employee search for guide id,
	Search_For_Guides_Status_Pending,
	Update_Guide_As_Approved, // After Service Employee click on Add
	Update_Guide_As_Denied, // After Service Employee click on Cancel
	
	Import_Park_Spots_Data, // After Park Employee / Park Manager click on Park Spots, Or Department Manager select park
	
	Make_New_Park_Capacity_Request, // After Park Manager make request
	Make_New_Park_Reserved_Entries_Request, // After Park Manager make request
	Make_New_Park_Estimated_Visit_Time_Request, // After Park Manager make request
	Update_Park_Capacity_Request_Approved, // After department manager approve request.
	Update_Park_Capacity_Request_Denied, // After department manager deny request.
	Update_Park_Reserved_Entries_Request_Approved, // After department manager approve request.
	Update_Park_Reserved_Entries_Request_Denied, // After department manager deny request.
	Update_Park_Estimated_Visit_Time_Request_Approved, // After department manager approve request.
	Update_Park_Estimated_Visit_Time_Request_Denied, // After department manager deny request.
	Import_All_Pending_Requests,
	Update_Request_In_Database,
	Delete_Old_Order,
	
	Create_Visits_Report, // After department manager generate visits report
	Import_Visits_Report, // After department manager request view visits report
	Create_Cancellations_Report, // After department manager generate cancellations report
	Import_Cancellations_Report,// After department manager request view cancellations report
	Create_Total_Visitors_Report, // After park manager generate total visitors report
	Import_Total_Visitors_Report,// After park manager request view visitors report
	Create_Usage_Report, // After park manager generate usage reports.
	Import_Usage_Report,// After park manager request view usage reports.
	
	Show_Payment_At_Entrance,
	Search_For_Notified_Orders,
	Search_For_Available_Date, // gal - added
	Search_For_Specific_Park,
	Import_All_Orders_For_Now,
	Prepare_New_Occasional_Order,
}
