//Jonathan Hanobik and Seth Stayer
//CS1555 - Fall 2016, Term Project
//Administrator/User Interface

import java.sql.*;  				//import the file containing definitions for the parts
import java.text.ParseException;	//needed by java for database connection and manipulation
import java.util.*;					// Needed for Scanner
import java.io.*;					// Needed for file i/o

public class PittToursInterface
{
	private static Connection connection; //used to hold the jdbc connection to the DB
	private static Statement statement; //used to create an instance of the connection
    private static PreparedStatement prepStatement; //used to create a prepared statement, that will be later reused
    private static ResultSet resultSet; //used to hold the result of your query (if one exists)
    private static String query;  //this will hold the query we are using
	
	public PittToursInterface(Connection conn)
	{
		connection = conn;
	}
	
	public static void main(String[] args)
	{
		try
		{
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
			connection = DriverManager.getConnection("jdbc:oracle:thin:@class3.cs.pitt.edu:1521:dbclass", "ses167", "3929130");
			statement = connection.createStatement();
		}
		catch(SQLException sqle)
		{
			System.out.println("Connection to database failed.");
			System.exit(1);
		}
		
		String userSelection;
		System.out.println("Welcome to PittTours by Hanobik/Stayer!");
		Scanner reader = new Scanner(System.in);
		do
		{
			System.out.println("Please select either Administrator or User By Entering Corresponding Number:");
			System.out.printf("\t%-20s\n\t%-20s\n\t%-20s\n", "(1) Administrator", "(2) User", "(3) Exit");
			userSelection = reader.nextLine();
		
			while(!userSelection.equals("1") && !userSelection.equals("2") && !userSelection.equals("3"))
			{
					System.out.println("Invalid Input! Please Try Again!");
					System.out.println("Please select either Administrator or User By Entering Corresponding Number:");
					System.out.printf("\t%-20s\n\t%-20s\n\t%-20s\n", "(1) Administrator", "(2) User", "(3) Exit");
					userSelection = reader.nextLine();
			}
		
			if(userSelection.equals("1"))
			{
				try
				{
					administratorInterface(reader);
				}
				catch(SQLException adminErr)
				{
					System.out.println(adminErr.toString());
				}
			}
			else if(userSelection.equals("2"))
			{
				try
				{
					userInterface(reader);
				}
				catch(SQLException userErr)
				{
					System.out.println(userErr.toString());
				}
			}
			else
			{
				System.out.println("Thank you for choosing Pitt Tours by Hanobik/Stayer!");
				userSelection = "3";
			}
		}while(userSelection != "3");
		
		try
		{
			connection.close();
		}
		catch(SQLException err)
		{
			System.out.println(err.toString());
		}
	}
	
	//Interface Methods/Functions for Administrator
	public static void administratorInterface(Scanner reader) throws SQLException
	{
		Scanner userKeyboard = new Scanner(System.in);
		String inputFile;
		int flag = 0;
		
		System.out.println("Please Select An Option From The Following List:");
		System.out.printf("%-50s\n", "(1) Erase Database");
		System.out.printf("%-50s\n", "(2) Load Airline Information");
		System.out.printf("%-50s\n", "(3) Load Schedule Information");
		System.out.printf("%-50s\n", "(4) Load Pricing Information");
		System.out.printf("%-50s\n", "(5) Load Plane Information");
		System.out.printf("%-50s\n", "(6) Generate Passenger Manifest For Specific Flight On Given Day");
		System.out.printf("%-50s\n", "(7) Exit");
			
		int selection = -1;
		boolean redo = false;
		do
		{
			do
			{
				try
				{
					selection = reader.nextInt();
					reader.nextLine();			// Clear buffer
					redo = false;
				}
				catch(InputMismatchException badInput)
				{
					System.out.print("Please choose an option from above. ");
					redo = true;
				}
			}while(redo);						// Loop until input doesn't produce errors
		}while(selection > 6 || selection < 1);	// Loop until integer is within the range
		
		switch(selection)
		{	
			// TASK 1 - Erase the Database
			case 1:
				System.out.print("Please confirm database deletion by typing 'yes': ");
				String confirm = userKeyboard.nextLine();
				admin_task1(confirm);
				break;
				
			// TASK 2 - Load Airline Information
			case 2:
				do
				{
					System.out.print("Name of file: ");
					inputFile = reader.nextLine();
					if(!inputFile.equals("-1")) flag = admin_task2(inputFile);
				}while(flag != 0 && !inputFile.equals("-1"));
				break;
			
			// TASK 3 - Load Schedule Information
			case 3:		
				do
				{	
					System.out.print("Name of file: ");
					inputFile = reader.nextLine();
					if(!inputFile.equals("-1")) flag = admin_task3(inputFile);
				}while(flag != 0 && !inputFile.equals("-1"));
				break;
			
			// TASK 4 - Load Pricing Information
			case 4:
				do
				{
					System.out.print("Name of file: ");
					inputFile = reader.nextLine();
					if(!inputFile.equals("-1")) flag = admin_task4(inputFile);
				}while(flag != 0 && !inputFile.equals("-1"));
				break;
			
			// TASK 5 - Load Plane Information
			case 5:
				do
				{
					System.out.print("Name of file: ");
					inputFile = reader.nextLine();
					if(!inputFile.equals("-1")) flag = admin_task5(inputFile);
				}while(flag != 0 && !inputFile.equals("-1"));
				break;
			
			// TASK 6 - Generate Passenger Manifest For Specific Flight On Given Day
			case 6:
				String fDate;
				String fNum;
				System.out.print("Enter Flight Date: ");
				fDate = userKeyboard.nextLine();
				System.out.print("Enter Flight Number: ");
				fNum = userKeyboard.nextLine();
				if(!fDate.equals("-1") && !fNum.equals("-1")) 
				{
					flag = admin_task6(fNum, fDate);
					if(flag != 0)
					{
						System.out.println("Cannot Generate Manifest for that day or flight.");
					}
				}
				break;
			case 7:
				// Do Nothing and Exit
		}
	}
	
	//Interface Methods/Functions for User
	public static void userInterface(Scanner reader) throws SQLException
	{
		System.out.println("Please Select An Option From The Following List:");
		System.out.printf("%-50s\n", "(1) Add Customer");
		System.out.printf("%-50s\n", "(2) Show Customer Information");
		System.out.printf("%-50s\n", "(3) Find Flight Price Between Two Cities");
		System.out.printf("%-50s\n", "(4) Find Routes Between Two Cities");
		System.out.printf("%-50s\n", "(5) Find Routes Between Two Cities Specific To Airline");
		System.out.printf("%-50s\n", "(6) Find All Routes Between Two Cities With Available Seats On Given Date");
		System.out.printf("%-50s\n", "(7) Find All Routes Between Two Cities On Specified Airline With Available Seats On Given Date");
		System.out.printf("%-50s\n", "(8) Add Reservation");
		System.out.printf("%-50s\n", "(9) Show Reservation Information");
		System.out.printf("%-50s\n", "(10) Buy Ticket From Existing Reservation");
		System.out.printf("%-50s\n", "(11) Quit");
		
		int selection = -1;
		boolean redo = false;
		do
		{
			do
			{
				try
				{
					selection = reader.nextInt();
					reader.nextLine();			// Clear buffer
					redo = false;
				}
				catch(InputMismatchException badInput)
				{
					System.out.print("Please choose an option from above. ");
					redo = true;
				}
			}while(redo);						// Loop until input doesn't produce errors
		}while(selection > 11 || selection < 1);	// Loop until integer is within the range

		String salutation, fName, lName, ccNum, ccExpire, street, city, state, phone, email, freqMiles;
		String city1, city2;
		
		switch(selection)
		{
			// TASK 1 - Add Customer
			case 1:
				System.out.print("Please enter your preferred salutation: ");
				salutation = reader.nextLine();
				System.out.print("Please enter your first name: ");
				fName = reader.nextLine();
				System.out.print("Please enter your last name: ");
				lName = reader.nextLine();
				System.out.print("Please enter your credit card number (without any delimiters): ");
				ccNum = reader.nextLine();
				System.out.print("Please enter your credit card's expiration date in the form MM/YY: ");
				ccExpire = reader.nextLine();
				System.out.print("Please enter your street address (including number): ");
				street = reader.nextLine();
				System.out.print("Please enter your city: ");
				city = reader.nextLine();
				System.out.print("Please enter your state: ");
				state = reader.nextLine();
				System.out.print("Please enter your phone number (without any delimiters): ");
				phone = reader.nextLine();
				System.out.print("Please enter your email address: ");
				email = reader.nextLine();
				
				user_task1(salutation, fName, lName, ccNum, ccExpire, street, city, state, phone, email);
				break;
				
			// TASK 2 - Show Customer Info, Given Customer Name
			case 2:			
				System.out.print("Please Enter Customer First Name: ");
				String customerFirstName = reader.nextLine();
				System.out.print("Please Enter Customer Last Name: ");			
				String customerLastName = reader.nextLine();
				
				user_task2(customerFirstName, customerLastName);
				break;
			
			// TASK 3 - Find Price for Flights Between Two Cities
			case 3:
				System.out.print("Please enter the first city airport code: ");
				city1 = reader.nextLine();
		
				do
				{
					System.out.print("Please enter the second city airport code: ");
					city2 = reader.nextLine();
				}while(city1.equals(city2));
		
				user_task3(city1, city2);
				break;
			
			// TASK 4 - Find All Routes Between Two Cities
			case 4:
				System.out.print("Please enter the first city airport code: ");
				city1 = reader.nextLine();
			
				do
				{
					System.out.print("Please enter the second city airport code: ");
					city2 = reader.nextLine();
				}while(city1.equals(city2));
				
				user_task4(city1, city2);
				break;
			
			// TASK 5 - Find All Routes Between Two Cities Of A Given Airline
			case 5:
				System.out.print("Please enter the first city airport code: ");
				city1 = reader.nextLine();
			
				do
				{
					System.out.print("Please enter the second city airport code: ");
					city2 = reader.nextLine();
				}while(city1.equals(city2));
				
				System.out.print("Please Enter An Airline ID: ");
				String airlineID = reader.nextLine();
				
				user_task5(city1, city2, airlineID);
				break;
			
			// TASK 6 - Find All Routes With Available Seats Between Two Cities On Given Day
			case 6:
				System.out.print("Please enter the first city airport code: ");
				city1 = reader.nextLine();
			
				do
				{
					System.out.print("Please enter the second city airport code: ");
					city2 = reader.nextLine();
				}while(city1.equals(city2));
			
				System.out.print("Please Enter A Date (MM/DD/YYYY): ");
				String givenDate = reader.nextLine();
			
				user_task6(city1, city2, givenDate);
				break;
			
			// TASK 7 - For A Given Airline, Find All Routes With Available Seats Between Two Cities On A Given Date
			case 7:
				System.out.print("Please enter the first city airport code: ");
				city1 = reader.nextLine();
			
				do
				{
					System.out.print("Please enter the second city airport code: ");
					city2 = reader.nextLine();
				}while(city1.equals(city2));
			
				System.out.print("Please Enter A Date (MM/DD/YYYY): ");
				givenDate = reader.nextLine();
			
				System.out.print("Please Enter the Airline Name: ");
				String airlineName = reader.nextLine();
			
				user_task7(city1, city2, givenDate, airlineName);
				break;
			
			// TASK 8 - Add Reservation
			case 8:
				int legNum = 0;
				String[][] legInfo = new String[4][2]; //To Hold User Flight Info
				for(int i = 0; i < 4; i++)
				{
					System.out.print("Please Enter the Flight Number for Leg " + legNum + ": ");
					legInfo[i][0] = reader.nextLine();
					if(legInfo[i][0].equals("0"))
					{
						legInfo[i][0] = null;
						break;
					}
					
					System.out.print("Please Enter a Date For Flight " + legInfo[i][0] + ": ");
					legInfo[i][1] = reader.nextLine();
					legNum++;
				}
				
				System.out.println("What is your Customer ID (CID): ");
				String custID = reader.nextLine();
				
				System.out.print("Please enter the credit card number you would like to use for this transaction: ");
				String ccNumber = reader.nextLine();
				
				user_task8(legNum, legInfo, custID, ccNumber);
				break;
			
			// TASK 9 - Show Reservation Information, Given Reservation Number
			case 9:
				System.out.println("Please Enter a Reservation Number: ");
				String reservationNum = reader.nextLine();
				user_task9(reservationNum);
				break;
			
			//Task 10 - Buy Ticket From Existing Reservation
			case 10:
				System.out.println("Please Enter a Reservation Number: ");
				reservationNum = reader.nextLine();
			
				user_task10(reservationNum);
			break;
			
			case 11:
			// DO NOTHING
		}
	}
	
	private static boolean checkSchedules(String sched1, String sched2) //Checks To See If Schedules Contain At Least One Similar Day
	{
		char[] s1, s2;
		s1 = sched1.toCharArray();
		s2 = sched2.toCharArray();
		for(int i = 0; i < sched1.length(); i++)
		{
			if(s1[i] != '-' && s2[i] != '-')
				return true;
		}
		return false;
	}
	
	// Used to open files for scanning
	private static Scanner openFile(String filename)
	{
		File file = null;
		Scanner fReader = null;
				
		try
		{
			file = new File(filename);			// Open file to read
			fReader = new Scanner(file);		// Open scanner for reading
		}
		catch(IOException ioe)
		{
			System.out.println(ioe.toString());
			fReader = null;
		}		
		
		return fReader;
	}
	
	// DELETION OF DATABASE
	public static void admin_task1(String confirmation)
	{
		if(confirmation.equals("yes"))
		{
			// Delete Database
			String deleteFrom;
			String[] tablename = {"Airline", "Customer", "Flight", "Price", "Plane", "Reservation", "Reservation Detail"};
			String temp;
			for(String table : tablename)
			{
				deleteFrom = "DELETE FROM ";
				temp = deleteFrom.concat(table);
				try									// Perform and commit update
				{
					connection.setAutoCommit(false);
					statement.executeUpdate(temp);
					connection.commit(); 
					System.out.println("Database Successfully Deleted.");
				}
				catch(SQLException e1)				// Rollback if update failed
				{
					try
					{
						connection.rollback();
						System.out.println(e1.toString());
					}
					catch(SQLException err)
					{
						System.out.println(err.toString());
					}
				}
			}
		}
		else
		{
			// Abort Deletion
			System.out.println("Database Deletion Aborted.");
		}
	}
	
	public static int admin_task2(String filename)
	{
		String airline_id;
		String airline_name;
		String airline_abbreviation;
		String year_founded;
		String insert;							// For concatenation later
		String line;
		
		Scanner file = openFile(filename);
		if(file.toString().equals(null))
		{
			System.out.println("File Not Found.");
			return -1;
		}
		
		// READ FILE AND PERFORM UPDATES
		while(file.hasNext())
		{
			insert = "INSERT INTO Airline VALUES ";
			line = file.nextLine();				// Read a single line in the file
			String[] parsed = line.split(",");	// Split the line into its different parts

			// STORE DATA
			try
			{
				airline_id = parsed[0];
				airline_name = parsed[1];
				airline_abbreviation = parsed[2];
				year_founded = parsed[3];
			}
			catch(ArrayIndexOutOfBoundsException wrongData)
			{
				System.out.println("Data is formatted incorrectly.");
				break;
			}			
			//Concatenate all data and previous line to create insert statement
			line = insert.concat("('" + airline_id + "', '" + airline_name + "' ,'" + airline_abbreviation + "', " + year_founded + ")");

			try									// Perform and commit update
			{
				connection.setAutoCommit(false);
				statement.executeUpdate(line);
				connection.commit();
				System.out.println("Insert Complete.");
			}
			catch(SQLException e1)				// Rollback if update failed
			{
				try
				{
					connection.rollback();
					System.out.println(e1.toString());
				}
				catch(SQLException e2)
				{
					System.out.println(e2.toString());
				}
			}
		}
		file.close();							// Close file
		return 0;
	}
	
	public static int admin_task3(String filename)
	{
		String flight_number;
		String airline_id = null;						// Already declared above
		String plane_type;
		String departure_city;
		String arrival_city;
		String departure_time;
		String arrival_time;
		String weekly_schedule;
		String insert;							// For concatenation later
		String line;
		
		Scanner file = openFile(filename);
		if(file.toString().equals(null))
		{
			System.out.println("File Not Found.");
			return -1;
		}
		
		// READ FILE AND PERFORM UPDATES
		while(file.hasNext())
		{
			line = file.nextLine();				// Read a single line in the file
			String[] parsed = line.split(",");	// Split the line into its different parts
	
			// STORE DATA
			flight_number = parsed[0];
			airline_id = parsed[1];
			plane_type = parsed[2];
			departure_city = parsed[3];
			arrival_city = parsed[4];
			departure_time = parsed[5];
			arrival_time = parsed[6];
			weekly_schedule = parsed[7];
		
			// Concatenate all data and previous line to create insert statement
			insert = "INSERT INTO Flight VALUES ";	// For concatenation
			line = insert.concat("('" + flight_number + "', '" + airline_id + "', '" + plane_type + "', '" + 
									departure_city + "', '" + arrival_city + "', '" + departure_time + "', '" + 
									arrival_time + "', '" + weekly_schedule + "')");
	
			try									// Perform and commit update
			{
				connection.setAutoCommit(false);
				statement.executeUpdate(line);
				connection.commit();
				System.out.println("Insert Complete.");
			}
			catch(SQLException e1)				// Rollback if update failed
			{
				try
				{
					connection.rollback();
					System.out.println(e1.toString());
				}
				catch(SQLException e2)
				{
					System.out.println(e2.toString());
				}
			}
		}
		file.close();							// Close file
		return 0;
	}
	
	public static int admin_task4(String filename)
	{
		String departure_city = null;
		String arrival_city = null;
		String airline_id = null;
		String high_price;
		String low_price;
		String insert;							// For concatenation later
		String line;
				
		Scanner file = openFile(filename);
		if(file.toString().equals(null))
		{
			System.out.println("File Not Found.");
			return -1;
		}
		
		// READ FILE AND PERFORM UPDATES
		while(file.hasNext())
		{
			insert = "INSERT INTO Price VALUES ";	// For concatenation later
			line = file.nextLine();					// Read a single line in the file
			String[] parsed = line.split(",");		// Split the line into its different parts
	
			// STORE DATA
			departure_city = parsed[0];
			arrival_city = parsed[1];
			airline_id = parsed[2];
			high_price = parsed[3];
			low_price = parsed[4];
			
			// Check for data existence, if so, then update the price
			String str = "SELECT * FROM Price WHERE (departure_city = ";
			try
			{
				str = str.concat("'" + departure_city + "') AND (arrival_city = '" +
								arrival_city + "') AND (airline_id = '" + airline_id +
								"')");
				connection.setAutoCommit(false);
				resultSet = statement.executeQuery(str);
				connection.commit();
			}
			catch(SQLException invalidQ)
			{
				System.out.println(invalidQ.toString());
			}
			try
			{
				if(resultSet.next())					// Tuple exists
				{
					// Check price, only 1 tuple at most since match was made on primary key
					String high = resultSet.getString("high_price");
					String low = resultSet.getString("low_price");
					if(!high_price.equals(high) || !low_price.equals(low))	// Price is different than new price
					{
						// Perform update
						try
						{
							resultSet.updateString("high_price", high_price);
							resultSet.updateString("low_price", low_price);
							System.out.println("Update Complete.");
						}
						catch(SQLException err)
						{
							System.out.println(err.toString());
						}
					}
					// Otherwise, do nothing with the new information
				}
				// Otherwise, concatenate all data and previous line to create insert statement
				else
				{
					line = insert.concat("('" + departure_city + "', '" + arrival_city + "', '" + airline_id + "', " + 
										high_price + ", " + low_price + ")");
				
					try									// Perform and commit update
					{
						connection.setAutoCommit(false);
						statement.executeUpdate(line);
						connection.commit();
						System.out.println("Insert Complete."); 
					}
					catch(SQLException e1)				// Rollback if update failed
					{
						try
						{
							connection.rollback();
							System.out.println(e1.toString());
						}
						catch(SQLException e2)
						{
							System.out.println(e2.toString());
						}
					}
				}
			}
			catch(SQLException e)
			{
				System.out.println(e.toString());
			}
		}
		file.close();							// Close file
		return 0;
	}
	
	public static int admin_task5(String filename)
	{
		String plane_type = null;
		String manufacturer;
		String plane_capacity;
		String last_service;
		String year;
		String owner_id;
		String insert;							// For concatenation later
		String line;
		
		Scanner file = openFile(filename);
		if(file.toString().equals(null))
		{
			System.out.println("File Not Found.");
			return -1;
		}
		
		// READ FILE AND PERFORM UPDATES
		while(file.hasNext())
		{
			line = file.nextLine();				// Read a single line in the file
			String[] parsed = line.split(",");	// Split the line into its different parts
	
			// STORE DATA
			plane_type = parsed[0];
			manufacturer = parsed[1];
			plane_capacity = parsed[2];
			last_service = parsed[3];
			year = parsed[4];
			owner_id = parsed[5];
			
			// Concatenate all data and previous line to create insert statement
			insert = "INSERT INTO Plane VALUES ";	// For concatenation
			line = insert.concat("('" + plane_type + "', '" + manufacturer + "', " + plane_capacity + ", '" + 
									last_service + "'," + year + ", '" + owner_id + "')");
	
			try									// Perform and commit update
			{
				connection.setAutoCommit(false);
				statement.executeUpdate(line);
				connection.commit();
				System.out.println("Insert Complete.");
			}
			catch(SQLException e1)				// Rollback if update failed
			{
				try
				{
					connection.rollback();
					System.out.println(e1.toString());
				}
				catch(SQLException e2)
				{
					System.out.println(e2.toString());
				}
			}
		}
		file.close();							// Close file
		return 0;
	}
	
	public static int admin_task6(String flight_num, String flight_date)
	{
		try
		{
			resultSet = statement.executeQuery("SELECT cid FROM (Reservation_detail D JOIN Reservation R ON D.reservation_number = R.reservation_number) " +
											   "WHERE D.flight_number = '" + flight_num + "' AND " + 
											   "D.flight_date = to_date('" + flight_date + "', 'MM/DD/YYYY') AND R.Ticketed = 'Y'");
			
			if(!resultSet.next())
			{
				System.out.println("Sorry, No Passengers Bought Tickets For That Flight On That Day");
			}
			
			else	
			{
				ResultSet resultSet2;
				do
				{
					resultSet2 = statement.executeQuery("SELECT salutation, first_name, last_name FROM CUSTOMER WHERE cid = '" + resultSet.getString(1) + "'");
					resultSet2.next();
					System.out.println(resultSet2.getString("salutation") + " " + resultSet2.getString("first_name") + " " + resultSet2.getString("last_name"));
				}while(resultSet.next());
			}
		}
		catch(SQLException invalidEntry1)
		{
			System.out.println(invalidEntry1.toString());
			return -1;
		}
		
		return 0;
	}
	
	public static void user_task1(String sal, String fName, String lName, String ccNum, String ccExpire, String street, 
									String city, String state, String phone, String email)
	{
		String existingCustomer = "SELECT COUNT(*) FROM CUSTOMER WHERE first_name = '" + fName + "' AND last_name = '" + lName + "'";
		int numCustomers = 0;
		try
		{
			resultSet = statement.executeQuery(existingCustomer);
			resultSet.next();
			numCustomers = resultSet.getInt("COUNT(*)");
		}
		catch(SQLException e)
		{
			System.out.println(e.toString());
		}	
		
		if(numCustomers > 0)
		{
			System.out.println("Sorry, A Customer With That Name Already Exists!");
		}
		
		else
		{
			String cidReceiver = "SELECT COUNT(cid) FROM CUSTOMER";
			int newCID = -1;
			try
			{
				resultSet = statement.executeQuery(cidReceiver);
				resultSet.next();
				newCID = resultSet.getInt("COUNT(cid)") + 1;
			}
			
			catch(SQLException e)
			{
				System.out.println(e.toString());
			}	
			
			System.out.println("NEW CID: " + newCID);
					
			int flag = 0;
			String insertStatement = "INSERT INTO CUSTOMER VALUES('" + newCID + "', '" + sal + "', '" + fName + "', '" + lName + "', '" 
								+ ccNum + "', to_date('" + ccExpire + "', 'MM/YY'),	'" + street + "', '" + city + "', '" + state + "', '" + phone 
								+ "', '" + email + "', NULL)";
			
			try	// Perform and commit update
			{
				connection.setAutoCommit(false);
				statement.executeUpdate(insertStatement);
				connection.commit();
				System.out.println("Addition Successful with for Customer " + newCID);
			}
			catch(SQLException e1)	// Rollback if update failed
			{
				try
				{
					connection.rollback();
					System.out.println(e1.toString());
				}
				catch(SQLException e2)
				{
					System.out.println(e2.toString());
				}
			}
		}
	}
	
	public static void user_task2(String first, String last)
	{
		String cid;
		String sal;
		String fName;
		String lName;
		String ccNum;
		String ccExpire;
		String street; 
		String city;
		String state;
		String phone;
		String email;
		String freqMiles;
				
		try
		{
			resultSet = statement.executeQuery("SELECT * FROM CUSTOMER WHERE first_name = '" + first + "' AND " +
												"last_name = '" + last + "'"); //Execute Query
			if(!resultSet.next())
			{
				System.out.println("No Customer With That Name Exists.");
				return;
			}
			do
			{
				cid = resultSet.getString("cid");
				sal = resultSet.getString("salutation");
				fName = resultSet.getString("first_name");
				lName = resultSet.getString("last_name");
				ccNum = resultSet.getString("credit_card_num");
				ccExpire = resultSet.getString("credit_card_expire");
				street = resultSet.getString("street");
				city = resultSet.getString("city");
				state = resultSet.getString("state");
				phone = resultSet.getString("phone");
				email = resultSet.getString("email");
				freqMiles = resultSet.getString("frequent_miles");
				
				System.out.println("--------------------");
				System.out.print("\tCID:" + cid);
				System.out.println("\tName: " + sal + ". " + fName + " " + lName);
				System.out.print("\tCredit Card Number: " + ccNum);
				System.out.println("\tCredit Card Expire Date: " + ccExpire);
				System.out.printf("\tAddress: %s %s, %s\n", street, city, state);
				System.out.print("\tPhone: " + phone);
				System.out.println("\tEmail: " + email);
				if(freqMiles != null) System.out.println("\tFrequent Miles Number: " + freqMiles);
				System.out.println("--------------------");
			}while(resultSet.next());
		}
		catch(SQLException t2err)
		{
			System.out.println(t2err.getMessage());
		}
	}
	
	public static void user_task3(String city1, String city2)
	{
		String flightQuery;
		try
		{
			flightQuery = "SELECT * FROM Price WHERE departure_city = '" + city1 + "' AND arrival_city = '" + city2 + "'";
			resultSet = statement.executeQuery(flightQuery);
			
			if(!resultSet.next())
			{
				System.out.println("No Flights Exist.");
				return;
			}
			
			do
			{
				System.out.println("\nOne-Way Between " + city1 + " and " + city2 + " on Airline: " + resultSet.getString("airline_id"));
				System.out.println("High Price: $" + resultSet.getInt("high_price"));
				System.out.println("Low Price: $" + resultSet.getInt("low_price"));
			}while(resultSet.next());
		
			flightQuery = "SELECT * FROM Price WHERE departure_city = '" + city2 + "' AND arrival_city = '" + city1 + "'";
			resultSet = statement.executeQuery(flightQuery);
			
			if(!resultSet.next())
			{
				System.out.println("No Flights Exist.");
				return;
			}
			
			do
			{
				System.out.println("\nOne-Way Between " + city2 + " and " + city1 + " on Airline: " + resultSet.getString("airline_id"));
				System.out.println("High Price: $" + resultSet.getInt("high_price"));
				System.out.println("Low Price: $" + resultSet.getInt("low_price"));
			}while(resultSet.next());
		
			flightQuery = "SELECT * " +
						  "FROM FLIGHT F JOIN FLIGHT G ON F.airline_id = G.airline_id " +
						  "WHERE F.departure_city = '" + city1 + "' AND F.arrival_city = '" + city2 + "' AND G.departure_city = '" + city2 + "' AND G.arrival_city = '" + city1 + "'";
			resultSet = statement.executeQuery(flightQuery);

			if(!resultSet.next())
			{
				System.out.println("No Flights Exist.");
				return;
			}
			
			ResultSet resultSet2, resultSet3;
			
			do
			{
				int depTime, arrTime;
				String currAirlineID = resultSet.getString(2);
				depTime = Integer.parseInt(resultSet.getString(14));
				arrTime = Integer.parseInt(resultSet.getString(7));
			
				if(depTime > arrTime)
				{
					int price = 0;
					resultSet2 = statement.executeQuery("SELECT high_price, airline_id FROM PRICE WHERE airline_id = '" + currAirlineID + "' AND departure_city = '" + city1 + "' AND arrival_city = '" + city2 + "'");
					resultSet2.next();
					price += resultSet2.getInt("high_price");
					resultSet3 = statement.executeQuery("SELECT high_price, airline_id FROM PRICE WHERE airline_id = '" + currAirlineID + "' AND departure_city = '" + city2 + "' AND arrival_city = '" + city1 + "'");
					resultSet3.next();
					price += resultSet3.getInt("high_price");
					System.out.println("\nRoundtrip Between " + city1 + " and " + city2 + " on Airline: " + currAirlineID);
					System.out.println("Price: $" + price);
				}
			
				else
				{
					int price = 0;
					resultSet2 = statement.executeQuery("SELECT low_price, airline_id FROM PRICE WHERE airline_id = '" + currAirlineID + "' AND departure_city = '" + city1 + "' AND arrival_city = '" + city2 + "'");
					resultSet2.next();
					price += resultSet2.getInt("low_price");
					resultSet3 = statement.executeQuery("SELECT low_price, airline_id FROM PRICE WHERE airline_id = '" + currAirlineID + "' AND departure_city = '" + city2 + "' AND arrival_city = '" + city1 + "'");
					resultSet3.next();
					price += resultSet3.getInt("low_price");
					System.out.println("\nRoundtrip Between " + city1 + " and " + city2 + " on Airline: " + currAirlineID);
					System.out.println("Price: $" + price);
				}
			}while(resultSet.next());
		}
		catch(SQLException t3err)
		{
			System.out.println(t3err.toString());
		}
	}
	
	public static void user_task4(String city1, String city2)
	{
		String onewayQuery, connectionFlights;
		try
		{
			//Roundtrip First
			onewayQuery = "SELECT * FROM FLIGHT WHERE departure_city = '" + city1 + "' AND arrival_city = '" + city2 + "'";
			
			resultSet = statement.executeQuery(onewayQuery);
			
			if(!resultSet.next())
			{
				System.out.println("No Flights Exist Between Those Cities.");
				return;
			}
			
			System.out.println("--------------------");
			System.out.println("One-Way Flights Between " + city1 + " and " + city2);
			do
			{
				System.out.printf("Flight Number: %s\n", resultSet.getString("flight_number"));
				System.out.printf("Depature City: %s\n", resultSet.getString("departure_city"));
				System.out.printf("Arrival City: %s\n", resultSet.getString("arrival_city"));
				System.out.printf("Departure Time: %s\n", resultSet.getString("departure_time"));
				System.out.printf("Arrival Time: %s\n", resultSet.getString("arrival_time"));
				System.out.println("--------------------");
			}while(resultSet.next());
			
			//Trips With Connections
			connectionFlights = "SELECT * "
							+ "FROM FLIGHT F JOIN FLIGHT G ON F.arrival_city = G.departure_city " 
							+ "WHERE F.departure_city = '" + city1 + "' AND G.arrival_city = '" + city2 + "'"; //This Will Have to Be Filtered A Bit More
			
			resultSet = statement.executeQuery(connectionFlights);
			
			if(!resultSet.next())
			{
				System.out.println("No Flights Exist Between Those Cities.");
				return;
			}
			
			System.out.println("Connecting Flights Between " + city1 + " and " + city2);
			do
			{
				if(checkSchedules(resultSet.getString(8), resultSet.getString(16)) && 
					(Integer.parseInt(resultSet.getString(7)) + 1) % 2400 < Integer.parseInt(resultSet.getString(15)))
				{
					System.out.printf("Flight Number (Flight 1): %s\n", resultSet.getString(1));
					System.out.printf("Depature City: %s\n", resultSet.getString(4));
					System.out.printf("Arrival City: %s\n", resultSet.getString(5));
					System.out.printf("Departure Time: %s\n", resultSet.getString(6));
					System.out.printf("Arrival Time: %s\n", resultSet.getString(7));
					System.out.printf("Flight Number (Flight 2): %s\n", resultSet.getString(9));
					System.out.printf("Depature City: %s\n", resultSet.getString(12));
					System.out.printf("Arrival City: %s\n", resultSet.getString(13));
					System.out.printf("Departure Time: %s\n", resultSet.getString(14));
					System.out.printf("Arrival Time: %s\n", resultSet.getString(15));
					System.out.println("--------------------");
				}
			}while(resultSet.next());
		}
		
		catch(SQLException t4err)
		{
			System.out.println(t4err.getMessage());
		}
	}
	
	public static void user_task5(String city1, String city2, String airlineID)
	{
		String onewayQuery, connectionFlights;
		try
		{
			//Roundtrip First
			onewayQuery = "SELECT * FROM FLIGHT WHERE airline_id = '" + airlineID + "' AND departure_city = '" + city1 + "' AND arrival_city = '" + city2 + "'";
			
			resultSet = statement.executeQuery(onewayQuery);
			
			if(!resultSet.next())
			{
				System.out.println("No Flights Exist Between Those Cities.");
				return;
			}
			
			System.out.println("--------------------");
			System.out.println("One-Way Flights Between " + city1 + " and " + city2);
			do
			{
				System.out.printf("Flight Number: %s\n", resultSet.getString("flight_number"));
				System.out.printf("Depature City: %s\n", resultSet.getString("departure_city"));
				System.out.printf("Arrival City: %s\n", resultSet.getString("arrival_city"));
				System.out.printf("Departure Time: %s\n", resultSet.getString("departure_time"));
				System.out.printf("Arrival Time: %s\n", resultSet.getString("arrival_time"));
				System.out.println("--------------------");
			}while(resultSet.next());
			
			//Trips With Connections
			connectionFlights = "SELECT * "
							+ "FROM FLIGHT F JOIN FLIGHT G ON F.arrival_city = G.departure_city " 
							+ "WHERE F.airline_id = '" + airlineID + "' AND G.airline_id = '" + airlineID + "' AND F.departure_city = '" + city1 + "' AND G.arrival_city = '" + city2 + "'"; //This Will Have to Be Filtered A Bit More
			
			resultSet = statement.executeQuery(connectionFlights);
			
			if(!resultSet.next())
			{
				System.out.println("No Flights Exist Between Those Cities.");
				return;
			}
			
			System.out.println("Connecting Flights Between " + city1 + " and " + city2);
			do
			{
				if(checkSchedules(resultSet.getString(8), resultSet.getString(16)) && 
					(Integer.parseInt(resultSet.getString(7)) + 1) % 2400 < Integer.parseInt(resultSet.getString(15)))
				{
					System.out.printf("Flight Number (Flight 1): %s\n", resultSet.getString(1));
					System.out.printf("Depature City: %s\n", resultSet.getString(4));
					System.out.printf("Arrival City: %s\n", resultSet.getString(5));
					System.out.printf("Departure Time: %s\n", resultSet.getString(6));
					System.out.printf("Arrival Time: %s\n", resultSet.getString(7));
					System.out.printf("Flight Number (Flight 2): %s\n", resultSet.getString(9));
					System.out.printf("Depature City: %s\n", resultSet.getString(12));
					System.out.printf("Arrival City: %s\n", resultSet.getString(13));
					System.out.printf("Departure Time: %s\n", resultSet.getString(14));
					System.out.printf("Arrival Time: %s\n", resultSet.getString(15));
				}
			}while(resultSet.next());
		}
		
		catch(SQLException t5err)
		{
			System.out.println(t5err.getMessage());
		}
	}
	
	public static void user_task6(String city1, String city2, String givenDate)
	{
		String onewayQuery, connectionFlights;
		try
		{
			//Roundtrip First
			onewayQuery = "SELECT * FROM (FLIGHT F NATURAL JOIN Reservation_Detail D) WHERE F.departure_city = '" + city1 + "' AND F.arrival_city = '" + city2 + 
							"' AND D.flight_date = to_date('" + givenDate + "', 'MM/DD/YYYY')";
			
			resultSet = statement.executeQuery(onewayQuery);
			
			if(!resultSet.next())
			{
				System.out.println("No Flights Exist Between Those Cities.");
				return;
			}
			
			System.out.println("--------------------");
			System.out.println("One-Way Flights Between " + city1 + " and " + city2 + " on Date " + givenDate);
			ResultSet resultSet2, resultSet3;
			do
			{
				resultSet2 = statement.executeQuery("SELECT COUNT(*) FROM RESERVATION_DETAIL WHERE flight_number = '" + resultSet.getString(1) + "'");
				resultSet2.next();
				resultSet3 = statement.executeQuery("SELECT plane_capacity FROM (FLIGHT F JOIN PLANE P ON F.airline_id = P.owner_id) WHERE F.flight_number = '" + resultSet.getString(1) + "'");
				resultSet3.next();
				if(resultSet2.getInt(1) < resultSet3.getInt(1))
				{
					System.out.printf("Flight Number: %s\n", resultSet.getString("F.airline_id"));
					System.out.printf("Departure City: %s\n", resultSet.getString("F.departure_city"));
					System.out.printf("Arrival City: %s\n", resultSet.getString("F.arrival_city"));
					System.out.printf("Departure Time: %s\n", resultSet.getString("F.departure_time"));
					System.out.printf("Arrival Time: %s\n", resultSet.getString("F.arrival_time"));
					System.out.println("--------------------");
				}
			}while(resultSet.next());
			
			//Trips With Connections
			connectionFlights = "SELECT * FROM (FLIGHT F JOIN FLIGHT G ON F.arrival_city = G.departure_city) T" +
								"NATURAL JOIN Reservation_Detail D WHERE (F.departure_city = '" +
								city1 + "' AND G.arrival_city = '" + city2 + 
								"' AND D.flight_date = to_date('" + givenDate + "', 'MM/DD/YYYY'))"; //This Will Have to Be Filtered A Bit More
			
			resultSet = statement.executeQuery(connectionFlights);
			
			if(!resultSet.next())
			{
				System.out.println("No Flights Exist Between Those Cities.");
				return;
			}
			
			System.out.println("Connecting Flights Between " + city1 + " and " + city2 + " on Date " + givenDate);
			ResultSet resultSet4, resultSet5;
			do
			{
				if(checkSchedules(resultSet.getString("F.weekly_schedule"), resultSet.getString("G.weekly_schedule")) && 
					(Integer.parseInt(resultSet.getString("F.arrival_time")) + 1) % 2400 < Integer.parseInt(resultSet.getString("G.departure_time")))
				{
					resultSet2 = statement.executeQuery("SELECT COUNT(*) FROM RESERVATION_DETAIL WHERE flight_number = '" + resultSet.getString(1) + "'");
					resultSet2.next();
					resultSet3 = statement.executeQuery("SELECT plane_capacity FROM (FLIGHT F JOIN PLANE P ON F.airline_id = P.owner_id) WHERE F.flight_number = '" + resultSet.getString(1) + "'");
					resultSet3.next();
					resultSet4 = statement.executeQuery("SELECT COUNT(*) FROM RESERVATION_DETAIL WHERE flight_number = '" + resultSet.getString(9) + "'");
					resultSet4.next();
					resultSet5 = statement.executeQuery("SELECT plane_capacity FROM (FLIGHT F JOIN PLANE P ON F.airline_id = P.owner_id) WHERE F.flight_number = '" + resultSet.getString(9) + "'");
					resultSet5.next();
					if(resultSet2.getInt(1) < resultSet3.getInt(1) && resultSet4.getInt(1) < resultSet5.getInt(1))
					{
						System.out.printf("Flight Number (Flight 1): %s\n", resultSet.getString("F.flight_number"));
						System.out.printf("Departure City: %s\n", resultSet.getString("F.departure_city"));
						System.out.printf("Arrival City: %s\n", resultSet.getString("F.arrival_city"));
						System.out.printf("Departure Time: %s\n", resultSet.getString("F.departure_city"));
						System.out.printf("Arrival Time: %s\n", resultSet.getString("F.arrival_city"));
						System.out.printf("Flight Number (Flight 2): %s\n", resultSet.getString("G.flight_number"));
						System.out.printf("Depature City: %s\n", resultSet.getString("G.departure_city"));
						System.out.printf("Arrival City: %s\n", resultSet.getString("G.arrival_city"));
						System.out.printf("Departure Time: %s\n", resultSet.getString("G.departure_time"));
						System.out.printf("Arrival Time: %s\n", resultSet.getString("G.arrival_time"));
						System.out.println("--------------------");
					}
				}
			}while(resultSet.next());
		}
		catch(SQLException t6err)
		{
			System.out.println(t6err.toString());
		}
	}
	
	public static void user_task7(String city1, String city2, String givenDate, String airlineName)
	{
		String onewayQuery, connectionFlights;
		try
		{
			//Roundtrip First
			onewayQuery = "SELECT * FROM (FLIGHT F NATURAL JOIN Reservation_Detail D) K " +
						"NATURAL JOIN Airline A WHERE F.departure_city = '" + city1 + "' AND F.arrival_city = '" + city2 + 
							"' AND D.flight_date = to_date('" + givenDate + "', 'MM/DD/YYYY') AND A.airline_name = '" + airlineName + "'";
			
			resultSet = statement.executeQuery(onewayQuery);
			
			if(!resultSet.next())
			{
				System.out.println("No Flights Exist Between Those Cities.");
				return;
			}
			
			System.out.println("--------------------");
			System.out.println("One-Way Flights Between " + city1 + " and " + city2 + " on Date " + givenDate);
			do
			{
				System.out.printf("Airline ID: %s\n", resultSet.getString("F.airline_id"));
				System.out.printf("Flight Number: %s\n", resultSet.getString("F.flight_number"));
				System.out.printf("Depature City: %s\n", resultSet.getString("F.departure_city"));
				System.out.printf("Arrival City: %s\n", resultSet.getString("F.arrival_city"));
				System.out.printf("Departure Time: %s\n", resultSet.getString("F.departure_time"));
				System.out.printf("Arrival Time: %s\n", resultSet.getString("arrival_time"));
				System.out.println("--------------------");
			}while(resultSet.next());
			
			//Trips With Connections
			connectionFlights = "SELECT * FROM (FLIGHT F JOIN FLIGHT G ON F.arrival_city = G.departure_city) T" +
								"NATURAL JOIN Reservation_Detail D NATURAL JOIN Airline A WHERE (F.departure_city = '" +
								city1 + "') AND (G.arrival_city = '" + city2 + 
								"') AND (D.flight_date = to_date('" + givenDate + "', 'MM/DD/YYYY')) AND " +
								"(A.airline_name = '" + airlineName + "')"; //This Will Have to Be Filtered A Bit More
			
			resultSet = statement.executeQuery(connectionFlights);
			
			if(!resultSet.next())
			{
				System.out.println("No Flights Exist Between Those Cities.");
				return;
			}
			
			System.out.println("Connecting Flights Between " + city1 + " and " + city2 + " on Date " + givenDate);
			do
			{
				if(checkSchedules(resultSet.getString("T.F.weekly_schedule"), resultSet.getString("T.G.weekly_schedule")) && 
					(Integer.parseInt(resultSet.getString("T.F.arrival_time")) + 1) % 2400 < Integer.parseInt(resultSet.getString("T.G.departure_time")))
				{
					System.out.printf("Airline ID (Flight 1): %s\n", resultSet.getString("F.airline_id"));
					System.out.printf("Flight Number (Flight 1): %s\n", resultSet.getString("F.flight_number"));
					System.out.printf("Depature City: %s\n", resultSet.getString("F.departure_city"));
					System.out.printf("Arrival City: %s\n", resultSet.getString("F.arrival_city"));
					System.out.printf("Departure Time: %s\n", resultSet.getString("F.departure_time"));
					System.out.printf("Arrival Time: %s\n", resultSet.getString("F.arrival_time"));
					System.out.printf("Airline ID (Flight 2): %s\n", resultSet.getString("G.airline_id"));
					System.out.printf("Flight Number (Flight 2): %s\n", resultSet.getString("G.flight_number"));
					System.out.printf("Depature City: %s\n", resultSet.getString("G.departure_city"));
					System.out.printf("Arrival City: %s\n", resultSet.getString("G.arrival_city"));
					System.out.printf("Departure Time: %s\n", resultSet.getString("G.departure_time"));
					System.out.printf("Arrival Time: %s\n", resultSet.getString("G.arrival_time"));
					System.out.println("--------------------");
				}
			}while(resultSet.next());
		}
		catch(SQLException t7err)
		{
			System.out.println(t7err.toString());
		}
	}
	
	public static void user_task8(int legNum, String[][] legInfo, String custID, String ccNumber)
	{
		ResultSet resultSet2;
		try
		{
			int currCapacity, maxCapacity;
			boolean seatAvail = true;
			for(int i = 0; i < legNum && seatAvail; i++)
			{
				resultSet = statement.executeQuery("SELECT COUNT(*) FROM RESERVATION_DETAIL WHERE flight_date = to_date('" + legInfo[i][1] + "', 'MM-DD-YYYY') AND flight_number = '" + legInfo[i][0] + "'");
				resultSet.next();
				currCapacity = resultSet.getInt(1);
				
				resultSet2 = statement.executeQuery("SELECT plane_capacity FROM FLIGHT F JOIN PLANE P ON F.plane_type = P.plane_type WHERE F.flight_number = '" + legInfo[i][0] + "'");
				resultSet2.next();
				maxCapacity = resultSet2.getInt("plane_capacity");
				if(currCapacity >= maxCapacity) //There is mo room on the plane_capacity
				{
					System.out.println("Flight " + legInfo[i][0] + " does not have any open seats! Reservation Cancelled!");
					seatAvail = false;
				}
			}
			
			
			resultSet = statement.executeQuery("SELECT COUNT(reservation_number) FROM RESERVATION");
			resultSet.next();
			int newReservationNumber = (resultSet.getInt(1)) + 1;
			resultSet = statement.executeQuery("SELECT departure_city FROM FLIGHT WHERE flight_number = '" + legInfo[0][0] + "'");
			resultSet.next();
			String deparCity = resultSet.getString(1);
			resultSet = statement.executeQuery("SELECT arrival_city FROM FLIGHT WHERE flight_number = '" + legInfo[legNum-1][0] + "'");
			resultSet.next();
			String arrivCity = resultSet.getString(1);
			resultSet = statement.executeQuery("SELECT airline_id FROM FLIGHT WHERE flight_number = '" + legInfo[0][0] + "'");
			resultSet.next();
			String airliID = resultSet.getString(1);
			resultSet = statement.executeQuery("SELECT * FROM FLIGHT WHERE departure_city = '" + deparCity + "' AND arrival_city = '" + arrivCity + "' "
						+ "AND airline_id = '" + airliID + "'");
			resultSet.next();
			String flightNum = resultSet.getString("flight_number");
			String depCity = resultSet.getString("departure_city");
			String arrCity = resultSet.getString("arrival_city");
			String arrivalTime = resultSet.getString("arrival_time");
			String departureTime = resultSet.getString("departure_time");
			int price;
			
			if(Integer.parseInt(departureTime) < Integer.parseInt(arrivalTime)) //High Price, Same Day
			{
				resultSet = statement.executeQuery("SELECT high_price FROM PRICE WHERE departure_city = '" + depCity + "' AND arrival_city = '" + arrivCity + "' AND airline_id = '" + airliID + "'");
				resultSet.next();
				price = Integer.parseInt(resultSet.getString(1));
			}
			
			else
			{
				resultSet = statement.executeQuery("SELECT low_price FROM PRICE WHERE departure_city = '" + depCity + "' AND arrival_city = '" + arrivCity + "' AND airline_id = '" + airliID + "'");
				resultSet.next();
				price = Integer.parseInt(resultSet.getString(1));
			}
			
			try	// Perform and commit update
			{
				connection.setAutoCommit(false);
				connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
				for(int j = 0; j < legNum; j++)
				{
					statement.executeUpdate("INSERT INTO RESERVATION_DETAIL VALUES('" + newReservationNumber + "', '" + legInfo[j][0] + "', to_date('" + legInfo[j][1] + "', 'MM-DD-YYYY'), " + j + ")");
				}
				
				statement.executeUpdate("INSERT INTO RESERVATION VALUES('" + newReservationNumber + "', '" + custID + "', '" +  depCity + "', '" 
						+ arrCity + "', '" + price + "', '" + ccNumber + "', to_date('" + legInfo[0][1] + "', 'MM-DD-YYYY'), 'Y')");
				connection.commit();
				System.out.println("Addition of Reservation # " + newReservationNumber);
			}
			catch(SQLException e1)	// Rollback if update failed
			{
				try
				{
					connection.rollback();
					System.out.println(e1.toString());
				}
				catch(SQLException e2)
				{
					System.out.println(e2.toString());
				}
			}
		}
		catch(SQLException t8err)
		{
			System.out.println(t8err.getMessage());
		}
	}
	
	public static void user_task9(String reservationNum)
	{
		String flightQuery;
		try
		{
			flightQuery = "SELECT flight_number FROM RESERVATION_DETAIL WHERE reservation_number = '" + reservationNum + "'";
			resultSet = statement.executeQuery(flightQuery);
			
			if(!resultSet.next())
			{
				System.out.println("No Reservations With That Number Exist.");
				return;
			}
			
			else
			{
				System.out.println("The following flight numbers make up this Reservation #" + reservationNum);
				do
				{
					System.out.println(resultSet.getString("flight_number"));
				}while(resultSet.next());
			}
		}
		catch(SQLException e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	public static void user_task10(String reservationNum)
	{
		String updateReservation = "UPDATE RESERVATION SET Ticketed = 'Y' WHERE reservation_number = '" + reservationNum + "'";
		try	// Perform and commit update
		{
			connection.setAutoCommit(false);
			statement.executeUpdate(updateReservation);
			connection.commit();
		}
		catch(SQLException e1)	// Rollback if update failed
		{
			try
			{
				connection.rollback();
				System.out.println(e1.toString());
			}
			catch(SQLException e2)
			{
				System.out.println(e2.toString());
			}
		}
	}
}