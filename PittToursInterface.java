//Jonathan Hanobik and Seth Stayer
//CS1555 - Fall 2016, Term Project
//Administrator/User Interface

import java.sql.*;  //import the file containing definitions for the parts
import java.text.ParseException; //needed by java for database connection and manipulation

public class PittToursInterface
{
	private static Connection connection; //used to hold the jdbc connection to the DB
	private Statement statement; //used to create an instance of the connection
    private PreparedStatement prepStatement; //used to create a prepared statement, that will be later reused
    private ResultSet resultSet; //used to hold the result of your query (if one exists)
    private String query;  //this will hold the query we are using
	
	public static void main(String[] args)
	{
		connection = DriverManager.getConnection(<URL>, <Username>, <Password>);
		statement = connection.createStatement();
		
		System.out.println("Welcome to PittTours by Stayer/Hanobik!");
		Scanner reader = new Scanner(System.in);
		System.out.println("Please select either Administrator or User By Entering Corresponding Number:");
		System.out.printf("\t%-20s\n\t%-20s", "(1) Administrator", "(2) User");
		String userSelection = reader.nextLine();
		
		while(!userSelection.equals("1") || !userSelection.equals("2"))
		{
				System.out.println("Invalid Input! Please Try Again!");
				System.out.println("Please select either Administrator or User By Entering Corresponding Number:");
				System.out.printf("\t%-20s\n\t%-20s", "(1) Administrator", ")2) User");
				userSelection = reader.nextLine();
		}
		
		if(userSelection.equals("1"))
		{
			administratorInterface();
		}
		
		else
		{
			userInterface();
		}
	}
	
	public static void administratorInterface(Scanner reader) //Interface Methods/Functions for Administrator
	{
		//Task 1 - Erase the Database
		
		//Task 2 - Load Airline Information
		
		//Task 3 - Load Schedule Information
		
		//Task 4 - Load Pricing Information
		
		//Task 5 - Load Plane Information
		
		//Task 6 - Generate Passenger Manifest For Specific Flight On Given Day
	}
	
	public static void userInterface(Scanner reader) //Interface Methods/Functions for User
	{
		System.out.println("Please Select An Option From The Following List");
		System.out.printf("%-50s", "(1) Add Customer");
		System.out.printf("%-50s", "(2) Show Customer Information");
		System.out.printf("%-50s", "(3) Find Flight Price Between Two Cities");
		System.out.printf("%-50s", "(4) Find Routes Between Two Cities");
		System.out.printf("%-50s", "(5) Find Routes Between Two Cities Specific To Airline");
		System.out.printf("%-50s", "(6) Find All Routes Between Two Cities With Available Seats On Given Date");
		System.out.printf("%-50s", "(7) Find All Routes Between Two Cities On Specified Airline With Available Seats On Given Date");
		System.out.printf("%-50s", "(8) Add Reservation");
		System.out.printf("%-50s", "(9) Show Reservation Information");
		System.out.printf("%-50s", "(10) Buy Ticket From Existing Reservation");
		System.out.printf("%-50s", "(11) Quit");

		
		//Task 1 - Add Customer
		
		//Task 2 - Show Customer Info, Given Customer Name
		System.out.println("Please Enter A Name To See Customer Information");
		String customerName = scanner.nextLine();
		try
		{
			resultSet = statement.executeQuery("SELECT * FROM CUSTOMER WHERE name = " + customerName); //Execute Query
			String cid, salutation, fName, lName, ccNum, ccExpire, street, city, phone, email, freqMiles;
			while(resultSet.next())
			{
				cid = resultSet.getString("cid");
				salutation = resultSet.getString("salutation");
				fName = resultSet.getString("first_name");
				lName = resultSet.getString("last_name");
				ccNum = resultSet.getString("credit_card_num");
				ccExpire = resultSet.getString("credit_card_expire");
				street = resultSet.getString("street");
				city = resultSet.getString("city");
				phone = resultSet.getString("phone");
				email = resultSet.getString("email");
				freqMiles = resultSet.getString("frequent_miles");
				
				System.out.println("CID:\t" + cid);
				System.out.println("Salutation:\t" + salutation);
				System.out.println("First Name:\t" + fName);
				System.out.println("Last Name:\t" + lName);
				System.out.println("Credit Card Number:\t" + ccNum);
				System.out.println("Credit Card Expire Date:\t" + ccExpire);
				System.out.printf("Address:\t%s, %s", street, city);
				System.out.println("Phone:\t" + phone);
				System.out.println("Email:\t" + email);
				if(freqMiles != null)
					System.out.println("Frequent Miles Number:\t" + freqMiles);
			}
			
		}
		
		catch(SQLException e)
		{
			System.out.println(e.getMessage());
		}
		//Task 3 - Find Price for Flights Between Two Cities
		
		//Task 4 - Find All Routes Between Two Cities
		
		//Task 5 - Find All Routes Between Two Cities Of A Given Airline
		
		//Task 6 - Find All Routes With Available Seats Between Two Cities On Given Day
		
		//Task 7 - For A Given Airline, Find All Routes With Available Seats Between Two Cities On A Given Date
		
		//Task 8 - Add Reservation
		
		//Task 9 - Show Reservation Information, Given Reservation Number
		
		//Task 10 - Buy Ticket From Existing Reservation
	}
}