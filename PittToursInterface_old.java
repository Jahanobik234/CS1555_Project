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
	}
	
	//Interface Methods/Functions for Administrator
	public static void administratorInterface(Scanner reader) throws SQLException
	{
		String inputFile;
		String line;
		
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
				Scanner userKeyboard = new Scanner(System.in);
				System.out.print("Please confirm database deletion by typing 'yes': ");
				String confirm = userKeyboard.nextLine();
				
				if(confirm.equals("yes"))
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
				
				break;
				
			// TASK 2 - Load Airline Information
			case 2:
				String airline_id;
				String airline_name;
				String airline_abbreviation;
				String year_founded;
				String insert;							// For concatenation later
				System.out.print("Name of file: ");
				inputFile = reader.nextLine();
			
				File toOpen = null;
				Scanner file = null;
				redo = false;
				do
				{
					try
					{
						toOpen = new File(inputFile);	// Open file to read
						file = new Scanner(toOpen);		// Open scanner for reading
						redo = false;
					}
					catch(IOException ioe)
					{
						System.out.print("File doesn't exist. Input proper filename: ");
						System.out.print("Name of file: ");
						inputFile = reader.nextLine();
						redo = true;
					}		
				}while(redo);
			
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
				break;
			
			// TASK 3 - Load Schedule Information
			case 3:
				String flight_number;
				airline_id = null;						// Already declared above
				String plane_type;
				String departure_city;
				String arrival_city;
				String departure_time;
				String arrival_time;
				String weekly_schedule;
				
				System.out.print("Name of file: ");
				inputFile = reader.nextLine();
			
				toOpen = null;							// Clear file data
				file = null;							// Clear scanner data
				redo = false;
				do
				{
					try
					{
						toOpen = new File(inputFile);	// Open file to read
						file = new Scanner(toOpen);		// Open scanner for reading
						redo = false;
					}
					catch(IOException ioe)
					{
						System.out.print("File doesn't exist. Input proper filename: ");
						inputFile = reader.nextLine();
						redo = true;
					}		
				}while(redo);
			
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
				break;
			
			// TASK 4 - Load Pricing Information
			case 4:
				departure_city = null;					// Already declared above
				arrival_city = null;					// Already declared above
				airline_id = null;						// Already declared above
				String high_price;
				String low_price;
				
				System.out.print("Name of file: ");
				inputFile = reader.nextLine();
			
				toOpen = null;							// Clear file data
				file = null;							// Clear scanner data
				redo = false;
				do
				{
					try
					{
						toOpen = new File(inputFile);	// Open file to read
						file = new Scanner(toOpen);		// Open scanner for reading
						redo = false;
					}
					catch(IOException ioe)
					{
						System.out.print("File doesn't exist. Input proper filename: ");
						inputFile = reader.nextLine();
						redo = true;
					}		
				}while(redo);
			
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
				file.close();							// Close file
				break;
			
			// TASK 5 - Load Plane Information
			case 5:
				plane_type = null;						// Already declared above
				String manufacturer;
				String plane_capacity;
				String last_service;
				String year;
				String owner_id;
				
				System.out.print("Name of file: ");
				inputFile = reader.nextLine();
			
				toOpen = null;							// Clear file data
				file = null;							// Clear scanner data
				redo = false;
				do
				{
					try
					{
						toOpen = new File(inputFile);	// Open file to read
						file = new Scanner(toOpen);		// Open scanner for reading
						redo = false;
					}
					catch(IOException ioe)
					{
						System.out.print("File doesn't exist. Input proper filename: ");
						inputFile = reader.nextLine();
						redo = true;
					}		
				}while(redo);
			
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
				break;
			
			// TASK 6 - Generate Passenger Manifest For Specific Flight On Given Day
			case 6:
				String userInput;
				userKeyboard = new Scanner(System.in);
				String fDate;							// Flight Date
				String fNum;							// Flight Number
				
				do
				{
					System.out.println("Which would you like to enter? (1) Flight Date or (2) Flight Number");
					userInput = userKeyboard.nextLine();
				}while(!userInput.equals("1") && !userInput.equals("2"));
				
				if(userInput.equals("1"))
				{
					// Get Flight Date
					System.out.print("Please enter flight date (MM/DD/YYYY): ");
					userInput = userKeyboard.nextLine();
					// Show all flights on given date
					try
					{
						resultSet = statement.executeQuery("SELECT flight_date, flight_num FROM Reservation_detail " +
															"WHERE flight_date = to_date('" + userInput +
															"', 'MM/DD/YYYY') GROUP BY flight_num");
						fDate = userInput;
					}
					catch(SQLException invalidDate)
					{
						System.out.println("No flights exist for this date.");
						break;
					}
					
					// PRINT resultSet
					while(resultSet.next())
					{
						String flight_date = resultSet.getString("flight_date");
						String flight_num = resultSet.getString("flight_num");
						System.out.println("Flight Number: " + flight_num + "\tDeparts On: " + flight_date);
					}
					
					// Get Flight Number
					try
					{
						resultSet = statement.executeQuery("SELECT R.cid FROM Reservation_detail D NATURAL JOIN Reservation R" +
															"WHERE D.flight_number = " + userInput + " AND " + 
															"D.flight_date = to_date('" + fDate + "', 'MM/DD/YYYY)");
						fNum = userInput; 
					}
					catch(SQLException invalidEntry1)
					{
						System.out.println(invalidEntry1.toString());
						break;
					}
				}
				else if(userInput.equals("2"))
				{
					// Get Flight Number
					System.out.print("Please enter flight number: ");
					userInput = userKeyboard.nextLine();
					// Show all flights with given flight number
					try
					{
						resultSet = statement.executeQuery("SELECT flight_date, flight_num FROM Reservation_detail " +
															"WHERE flight_number = " + userInput);
						fNum = userInput; 
					}
					catch(SQLException invalidNumber)
					{
						System.out.println("Invalid Flight Number.");
						break;
					}
					
					// PRINT resultSet
					while(resultSet.next())
					{
						String flight_date = resultSet.getString("flight_date");
						String flight_num = resultSet.getString("flight_num");
						System.out.println("Flight Number: " + flight_num + "\tDeparts On: " + flight_date);
					}
					
					// Get Flight Date
					System.out.print("Please Select Flight Date: ");
					userInput = userKeyboard.nextLine();
					
					try
					{
						resultSet = statement.executeQuery("SELECT R.cid FROM Reservation_detail D NATURAL JOIN Reservation R" +
															"WHERE D.flight_number = " + fNum + " AND " + 
															"D.flight_date = to_date('" + userInput + "', 'MM/DD/YYYY)");
						fDate = userInput; 
					}
					catch(SQLException invalidEntry2)
					{
						System.out.println(invalidEntry2.toString());
						break;
					}
				}
				else
				{
					// Unreachable
					System.out.println("Not a valid input.");
				}
				
				// Generate Manifest: NEED TO FIX THIS SOMEHOW
				
// 				String[] cids = resultSet.getArray("R.cid");
// 				resultSet.getArray("R.cid");
// 				
// 				for(int i = 0; i < cids.length; i++)
// 				{
// 					resultSet = statement.executeQuery("SELECT * FROM Customer WHERE cid = " + cids[i]);
// 					// Need to print out each customer
// 				}
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

		String cid, salutation, fName, lName, ccNum, ccExpire, street, city, state, phone, email, freqMiles;
		String city1, city2;
		String onewayQuery, connectionFlights, flightQuery, reservationNum;
		switch(selection)
		{
			// TASK 1 - Add Customer
			case 1:
			try
			{
				System.out.print("Please enter your preferred salutation: ");
				salutation = reader.nextLine();
				System.out.print("Please enter your first name: ");
				fName = reader.nextLine();
				System.out.print("Please enter your last name: ");
				lName = reader.nextLine();
				
				String nameTest = "SELECT COUNT(*) FROM CUSTOMER WHERE first_name = '" + fName + "' AND last_name = '" + lName + "'";
				resultSet = statement.executeQuery(nameTest); //Checking To See If We Have Same First and Last Name
				resultSet.next();
				if(resultSet.getInt(1) != 0)
				{
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
					
					String cidReceiver = "SELECT cid FROM CUSTOMER";
					resultSet = statement.executeQuery(cidReceiver);
					resultSet.last();
					int newCID = resultSet.getInt(1) + 1;
					
					String insertStatement = "INSERT INTO CUSTOMER VALUES('" + newCID + "', '" + salutation + "', '" + fName + "', '" + lName + "', '" 
					+ "', '" + ccNum + "', " + ccExpire + ", '" + street + "', '" + city + "', '" + state + "', '" + phone 
					+ "', '" + email + ", NULL)";
					
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
						}
						catch(SQLException e2)
						{
							System.out.println(e2.toString());
						}
					}
				}
				else
				{
					System.out.println("There is already a user with this first and last name! Returning To Main Menu!");
				}
				break;
			}
			
			catch(SQLException e)
			{
				System.out.println(e.getMessage());
			}
			
			// TASK 2 - Show Customer Info, Given Customer Name
			case 2:			
			System.out.print("Please Enter Customer First Name: ");
			String customerFirstName = reader.nextLine();
			System.out.print("Please Enter Customer Last Name: ");			
			String customerLastName = reader.nextLine();
			try
			{
				resultSet = statement.executeQuery("SELECT * FROM CUSTOMER WHERE first_name = '" + customerFirstName + "' AND " +
													"last_name = '" + customerLastName + "'"); //Execute Query
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
					state = resultSet.getString("state");
					phone = resultSet.getString("phone");
					email = resultSet.getString("email");
					freqMiles = resultSet.getString("frequent_miles");
					
					System.out.println("--------------------");
					System.out.print("\tCID:" + cid);
					System.out.println("\tName: " + salutation + ". " + fName + " " + lName);
					System.out.print("\tCredit Card Number: " + ccNum);
					System.out.println("\tCredit Card Expire Date: " + ccExpire);
					System.out.printf("\tAddress: %s %s, %s\n", street, city, state);
					System.out.print("\tPhone: " + phone);
					System.out.println("\tEmail: " + email);
					if(freqMiles != null)
						System.out.println("\tFrequent Miles Number: " + freqMiles);
					System.out.println("--------------------");
				}
				
			}
			
			catch(SQLException e)
			{
				System.out.println(e.getMessage());
			}
			
			break;
			
			// TASK 3 - Find Price for Flights Between Two Cities
			case 3:
			try
			{
				System.out.print("Please enter the first city airport code: ");
				city1 = reader.nextLine();
				
				do
				{
					System.out.print("Please enter the second city airport code: ");
					city2 = reader.nextLine();
				}while(city1.equals(city2));
				
				flightQuery = "SELECT * FROM Price WHERE departure_city = '" + city1 + "' AND arrival_city = '" + city2 + "'";
				resultSet = statement.executeQuery(flightQuery);
				while(resultSet.next())
				{
					System.out.println("One-Way Between " + city1 + " and " + city2 + " on Airline: " + resultSet.getString("airline_id"));
					System.out.println("\tHigh Price: " + resultSet.getInt("high_price"));
					System.out.println("\tLow Price: " + resultSet.getInt("low_price"));
				}
				
				flightQuery = "SELECT * FROM Price WHERE departure_city = '" + city2 + "' AND arrival_city = '" + city1 + "'";
				resultSet = statement.executeQuery(flightQuery);
				while(resultSet.next())
				{
					System.out.println("One-Way Between " + city2 + " and " + city1 + " on Airline: " + resultSet.getString("airline_id"));
					System.out.println("\tHigh Price: " + resultSet.getInt("high_price"));
					System.out.println("\tLow Price: " + resultSet.getInt("low_price"));
				}
				
				flightQuery = "SELECT * FROM Price P JOIN Flight F ON P.airline_id = F.airline_id WHERE P.departure_city = '" + city1 + "' AND P.arrival_city = '" + city2 + "'";
				resultSet = statement.executeQuery(flightQuery);
				while(resultSet.next())
				{
					int depTime, arrTime;
					depTime = Integer.parseInt(resultSet.getString("P.departure_time"));
					arrTime = Integer.parseInt(resultSet.getString("P.arrival_time"));
					
					if(depTime > arrTime)
					{
						System.out.println("Roundtrip Between " + city1 + " and " + city2 + "on Airline: " + resultSet.getString("airline_id"));
						System.out.println("\tPrice: " + resultSet.getInt("P.high_price"));
					}
					
					else
					{
						System.out.println("Roundtrip Between " + city1 + " and " + city2 + "on Airline: " + resultSet.getString("airline_id"));
						System.out.println("\tPrice: " + resultSet.getInt("P.low_price"));
					}
				}
			}
			
			catch(SQLException e)
			{
				System.out.println(e.getMessage());
			}
			break;
			
			// TASK 4 - Find All Routes Between Two Cities
			case 4:
			try
			{
				System.out.print("Please enter the first city airport code: ");
				city1 = reader.nextLine();
				
				do
				{
					System.out.print("Please enter the second city airport code: ");
					city2 = reader.nextLine();
				}while(city1.equals(city2));
				
				//Roundtrip First
				onewayQuery = "SELECT * FROM FLIGHT WHERE departure_city = '" + city1 + "' AND arrival_city = '" + city2 + "'";
				
				resultSet = statement.executeQuery(onewayQuery);
				
				System.out.println("--------------------");
				System.out.println("One-Way Flights Between " + city1 + " and " + city2);
				while(resultSet.next())
				{
					System.out.printf("Flight Number: %s\n", resultSet.getString("flight_number"));
					System.out.printf("Depature City: %s\n", resultSet.getString("departure_city"));
					System.out.printf("Arrival City: %s\n", resultSet.getString("arrival_city"));
					System.out.printf("Departure Time: %s\n", resultSet.getString("departure_time"));
					System.out.printf("Arrival Time: %s\n", resultSet.getString("arrival_time"));
					System.out.println("--------------------");
				}
				
				//Trips With Connections
				connectionFlights = "SELECT * "
								+ "FROM FLIGHT F JOIN FLIGHT G ON F.arrival_city = G.departure_city " 
								+ "WHERE F.departure_city = '" + city1 + "' AND G.arrival_city = '" + city2 + "'"; //This Will Have to Be Filtered A Bit More
				
				resultSet = statement.executeQuery(connectionFlights);
				System.out.println("Connecting Flights Between " + city1 + " and " + city2);
				while(resultSet.next())
				{
					if(checkSchedules(resultSet.getString("F.weekly_schedule"), resultSet.getString("G.weekly_schedule")) && 
						(Integer.parseInt(resultSet.getString("F.arrival_time")) + 1) % 2400 < Integer.parseInt(resultSet.getString("G.departure_time")))
					{
						System.out.printf("Flight Number (Flight 1): %s\n", resultSet.getString("F.flight_number"));
						System.out.printf("Depature City: %s\n", resultSet.getString("F.departure_city"));
						System.out.printf("Arrival City: %s\n", resultSet.getString("F.arrival_city"));
						System.out.printf("Departure Time: %s\n", resultSet.getString("F.departure_time"));
						System.out.printf("Arrival Time: %s\n", resultSet.getString("F.arrival_time"));
						System.out.printf("Flight Number (Flight 2): %s\n", resultSet.getString("G.flight_number"));
						System.out.printf("Depature City: %s\n", resultSet.getString("G.departure_city"));
						System.out.printf("Arrival City: %s\n", resultSet.getString("G.arrival_city"));
						System.out.printf("Departure Time: %s\n", resultSet.getString("G.departure_time"));
						System.out.printf("Arrival Time: %s\n", resultSet.getString("G.arrival_time"));
						System.out.println("--------------------");
					}
				}
			}
			
			catch(SQLException e)
			{
				System.out.println(e.getMessage());
			}
			break;
			
			// TASK 5 - Find All Routes Between Two Cities Of A Given Airline
			case 5:
			try
			{
				System.out.print("Please enter the first city airport code: ");
				city1 = reader.nextLine();
				
				do
				{
					System.out.print("Please enter the second city airport code: ");
					city2 = reader.nextLine();
				}while(city1.equals(city2));
				
				String airlineID;
				System.out.print("Please Enter An Airline ID: ");
				airlineID = reader.nextLine();
				
				//Roundtrip First
				onewayQuery = "SELECT * FROM FLIGHT WHERE departure_city = '" + city1 + "' AND arrival_city = '" + city2 + "' AND airline_id = '" + airlineID + "'";
				
				resultSet = statement.executeQuery(onewayQuery);
				
				System.out.println("--------------------");
				System.out.println("One-Way Flights Between " + city1 + " and " + city2 + " on Airline " + airlineID);
				while(resultSet.next())
				{
					System.out.printf("Flight Number: %s\n", resultSet.getString("flight_number"));
					System.out.printf("Depature City: %s\n", resultSet.getString("departure_city"));
					System.out.printf("Arrival City: %s\n", resultSet.getString("arrival_city"));
					System.out.printf("Departure Time: %s\n", resultSet.getString("departure_time"));
					System.out.printf("Arrival Time: %s\n", resultSet.getString("arrival_time"));
					System.out.println("--------------------");
				}
				
				//Trips With Connections
				connectionFlights = "SELECT * FROM (FLIGHT F JOIN FLIGHT G ON F.arrival_city = G.departure_city) T WHERE (F.departure_city = '"
							+ city1 + "' AND G.arrival_city = '" + city2 + "') AND (airline_id = '" + airlineID + "');"; //This Will Have to Be Filtered A Bit More
				
				resultSet = statement.executeQuery(connectionFlights);
				System.out.println("Connecting Flights Between " + city1 + " and " + city2 + " on Airline " + airlineID);
				while(resultSet.next())
				{
					if(checkSchedules(resultSet.getString("F.weekly_schedule"), resultSet.getString("G.weekly_schedule")) && 
						(Integer.parseInt(resultSet.getString("F.arrival_time")) + 1) % 2400 < Integer.parseInt(resultSet.getString("G.departure_time")))
					{
						System.out.printf("Flight Number (Flight 1): %s\n", resultSet.getString("F.flight_number"));
						System.out.printf("Depature City: %s\n", resultSet.getString("F.departure_city"));
						System.out.printf("Arrival City: %s\n", resultSet.getString("F.arrival_city"));
						System.out.printf("Departure Time: %s\n", resultSet.getString("F.departure_time"));
						System.out.printf("Arrival Time: %s\n", resultSet.getString("F.arrival_time"));
						System.out.printf("Flight Number (Flight 2): %s\n", resultSet.getString("G.flight_number"));
						System.out.printf("Depature City: %s\n", resultSet.getString("G.departure_city"));
						System.out.printf("Arrival City: %s\n", resultSet.getString("G.arrival_city"));
						System.out.printf("Departure Time: %s\n", resultSet.getString("G.departure_time"));
						System.out.printf("Arrival Time: %s\n", resultSet.getString("G.arrival_time"));
						System.out.println("--------------------");
					}
				}
			}
			catch(SQLException e)
			{
				System.out.println(e.getMessage());
			}
			break;
			
			// TASK 6 - Find All Routes With Available Seats Between Two Cities On Given Day
			case 6:
			try
			{
				System.out.print("Please enter the first city airport code: ");
				city1 = reader.nextLine();
				
				do
				{
					System.out.print("Please enter the second city airport code: ");
					city2 = reader.nextLine();
				}while(city1.equals(city2));
				
				String givenDate;
				System.out.print("Please Enter A Date (MM/DD/YYYY): ");
				givenDate = reader.nextLine();
				
				//Roundtrip First
				onewayQuery = "SELECT * FROM (FLIGHT F NATURAL JOIN Reservation_Detail D) WHERE F.departure_city = '" + city1 + "' AND F.arrival_city = '" + city2 + 
								"' AND D.flight_date = to_date('" + givenDate + "', 'MM/DD/YYYY')";
				
				resultSet = statement.executeQuery(onewayQuery);
				
				System.out.println("--------------------");
				System.out.println("One-Way Flights Between " + city1 + " and " + city2 + " on Date " + givenDate);
				while(resultSet.next())
				{
					System.out.printf("Flight Number: %s\n", resultSet.getString("F.airline_id"));
					System.out.printf("Departure City: %s\n", resultSet.getString("F.departure_city"));
					System.out.printf("Arrival City: %s\n", resultSet.getString("F.arrival_city"));
					System.out.printf("Departure Time: %s\n", resultSet.getString("F.departure_time"));
					System.out.printf("Arrival Time: %s\n", resultSet.getString("F.arrival_time"));
					System.out.println("--------------------");
				}
				
				//Trips With Connections
				connectionFlights = "SELECT * FROM (FLIGHT F JOIN FLIGHT G ON F.arrival_city = G.departure_city) T" +
									"NATURAL JOIN Reservation_Detail D WHERE (F.departure_city = '" +
									city1 + "' AND G.arrival_city = '" + city2 + 
									"' AND D.flight_date = to_date('" + givenDate + "', 'MM/DD/YYYY'))"; //This Will Have to Be Filtered A Bit More
				
				resultSet = statement.executeQuery(connectionFlights);
				System.out.println("Connecting Flights Between " + city1 + " and " + city2 + " on Date " + givenDate);
				while(resultSet.next())
				{
					if(checkSchedules(resultSet.getString("F.weekly_schedule"), resultSet.getString("G.weekly_schedule")) && 
						(Integer.parseInt(resultSet.getString("F.arrival_time")) + 1) % 2400 < Integer.parseInt(resultSet.getString("G.departure_time")))
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
			}
			catch(SQLException t6err)
			{
				System.out.println(t6err.toString());
			}
			break;
			
			// TASK 7 - For A Given Airline, Find All Routes With Available Seats Between Two Cities On A Given Date
			case 7:
				try
			{
				System.out.print("Please enter the first city airport code: ");
				city1 = reader.nextLine();
				
				do
				{
					System.out.print("Please enter the second city airport code: ");
					city2 = reader.nextLine();
				}while(city1.equals(city2));
				
				String givenDate;
				System.out.print("Please Enter A Date (MM/DD/YYYY): ");
				givenDate = reader.nextLine();
				
				String airlineName;
				System.out.print("Please Enter the Airline Name: ");
				airlineName = reader.nextLine();
				
				//Roundtrip First
				onewayQuery = "SELECT * FROM (FLIGHT F NATURAL JOIN Reservation_Detail D) K " +
							"NATURAL JOIN Airline A WHERE F.departure_city = '" + city1 + "' AND F.arrival_city = '" + city2 + 
								"' AND D.flight_date = to_date('" + givenDate + "', 'MM/DD/YYYY') AND A.airline_name = '" + airlineName + "'";
				
				resultSet = statement.executeQuery(onewayQuery);
				
				System.out.println("--------------------");
				System.out.println("One-Way Flights Between " + city1 + " and " + city2 + " on Date " + givenDate);
				while(resultSet.next())
				{
					System.out.printf("Airline ID: %s\n", resultSet.getString("F.airline_id"));
					System.out.printf("Flight Number: %s\n", resultSet.getString("F.flight_number"));
					System.out.printf("Depature City: %s\n", resultSet.getString("F.departure_city"));
					System.out.printf("Arrival City: %s\n", resultSet.getString("F.arrival_city"));
					System.out.printf("Departure Time: %s\n", resultSet.getString("F.departure_time"));
					System.out.printf("Arrival Time: %s\n", resultSet.getString("arrival_time"));
					System.out.println("--------------------");
				}
				
				//Trips With Connections
				connectionFlights = "SELECT * FROM (FLIGHT F JOIN FLIGHT G ON F.arrival_city = G.departure_city) T" +
									"NATURAL JOIN Reservation_Detail D NATURAL JOIN Airline A WHERE (F.departure_city = '" +
									city1 + "') AND (G.arrival_city = '" + city2 + 
									"') AND (D.flight_date = to_date('" + givenDate + "', 'MM/DD/YYYY')) AND " +
									"(A.airline_name = '" + airlineName + "')"; //This Will Have to Be Filtered A Bit More
				
				resultSet = statement.executeQuery(connectionFlights);
				System.out.println("Connecting Flights Between " + city1 + " and " + city2 + " on Date " + givenDate);
				while(resultSet.next())
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
				}
			}
			catch(SQLException t6err)
			{
				System.out.println(t6err.toString());
			}
			break;
			
			// TASK 8 - Add Reservation
			case 8:
			try
			{
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
				
				int currCapacity, maxCapacity;
				boolean seatAvail = true;
				for(int i = 0; i < legNum+1 && seatAvail; i++)
				{
					resultSet = statement.executeQuery("SELECT COUNT(*) FROM RESERVATION_DETAIL WHERE to_date(legInfo[i][1], 'MM-DD-YYYY') = flight_date AND flight_number = " + legInfo[i][0]);
					resultSet.next();
					currCapacity = resultSet.getInt(1);
					
					resultSet = statement.executeQuery("SELECT plane_capacity FROM FLIGHT F JOIN PLANE P ON F.plane_type = P.plane_type WHERE flight_number = " + legInfo[i][0]);
					resultSet.next();
					maxCapacity = Integer.parseInt(resultSet.getString("plane_capacity"));
					
					if(currCapacity >= maxCapacity) //There is room on the plane_capacity
					{
						System.out.println("Flight " + legInfo[i][0] + " does not have any open seats! Reservation Cancelled!");
						seatAvail = false;
					}
				}
				
				System.out.println("Seats Available On All Flights. Creating Reservation...");
				System.out.println("What is your Customer ID (CID): ");
				String custID = reader.nextLine();
				resultSet = statement.executeQuery("SELECT MAX(reservation_number) FROM RESERVATION");
				resultSet.next();
				int newReservationNumber = Integer.parseInt(resultSet.getString(1)) + 1;
				resultSet = statement.executeQuery("SELECT * FROM FLIGHT WHERE departure_city = (SELECT departure_city FROM FLIGHT WHERE flight_number = '" 
							+ legInfo[0][0] + "') AND arrival_city = (SELECT arrival_city FROM FLIGHT WHERE flight_number = '" + legInfo[legNum][0]
							+ "') AND airline_id = (SELECT airline_id FROM FLIGHT WHERE flight_number = '" + legInfo[0][0] + "'");
				resultSet.next();
				String flightNum = resultSet.getString("flight_number");
				String depCity = resultSet.getString("departure_city");
				String arrCity = resultSet.getString("arrival_city");
				int price;
				if(Integer.parseInt(resultSet.getString("departure_time")) < Integer.parseInt(resultSet.getString("arrivalTime"))) //High Price, Same Day
				{
					resultSet = statement.executeQuery("SELECT high_price FROM PRICE WHERE flight_number = '" + flightNum + "'");
					resultSet.next();
					price = Integer.parseInt(resultSet.getString(1));
				}
				
				else
				{
					resultSet = statement.executeQuery("SELECT low_price FROM PRICE WHERE flight_number = '" + flightNum + "'");
					resultSet.next();
					price = Integer.parseInt(resultSet.getString(1));
				}
				
				System.out.print("Please enter the credit card number you would like to use for this transaction: ");
				String ccNumber = reader.nextLine();
				
				try	// Perform and commit update
				{
					connection.setAutoCommit(false);
					statement.executeUpdate("INSERT INTO RESERVATION VALUES('" + newReservationNumber + "', '" + custID + "', '" +  depCity + "', '" 
							+ arrCity + "', '" + price + "', '" + "', '" + ccNumber + "', " + legInfo[0][1] + ", 'Y';");
					connection.commit();
					System.out.println("Addition Addition of Reservation # " + newReservationNumber);
				}
				catch(SQLException e1)	// Rollback if update failed
				{
					try
					{
						connection.rollback();
					}
					catch(SQLException e2)
					{
						System.out.println(e2.toString());
					}
				}
			}
			catch(SQLException e)
			{
				System.out.println(e.getMessage());
			}
			break;
			
			// TASK 9 - Show Reservation Information, Given Reservation Number
			case 9:
			try
			{
				System.out.println("Please Enter a Reservation Number: ");
				reservationNum = reader.nextLine();
				
				flightQuery = "SELECT flight_number FROM RESERVATION_DETAIL WHERE reservation_number = '" + reservationNum + "'";
				resultSet = statement.executeQuery(flightQuery);
				resultSet.next();
				if(resultSet.getString("flight_number") == null)
				{
					System.out.println("Error with Reservation Number");
				}
				
				else
				{
					System.out.println("The following flight numbers make up this Reservation #" + reservationNum);
					while(resultSet.next())
					{
						System.out.println(resultSet.getString("flight_number"));
					}
				}
			}
			catch(SQLException e)
			{
				System.out.println(e.getMessage());
			}
			break;
			
			//Task 10 - Buy Ticket From Existing Reservation
			case 10:
			System.out.println("Please Enter a Reservation Number: ");
			reservationNum = reader.nextLine();
			
			String updateReservation = "UPDATE RESERVATION SET Ticketed = 'Y' WHERE Reservation_Number = '" + reservationNum + "';";
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
				}
				catch(SQLException e2)
				{
					System.out.println(e2.toString());
				}
			}
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
}