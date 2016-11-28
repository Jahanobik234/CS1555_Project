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
			connection = DriverManager.getConnection("ses167@thoth.cs.pitt.edu", "ses167", "3929130");
			statement = connection.createStatement();
		}
		catch(SQLException sqle)
		{
			System.out.println("Connection to database failed.");
			System.exit(1);
		}
		
		System.out.println("Welcome to PittTours by Hanobik/Stayer!");
		Scanner reader = new Scanner(System.in);
		System.out.println("Please select either Administrator or User By Entering Corresponding Number:");
		System.out.printf("\t%-20s\n\t%-20s", "(1) Administrator", "(2) User");
		String userSelection = reader.nextLine();
		
		while(!userSelection.equals("1") && !userSelection.equals("2"))
		{
				System.out.println("Invalid Input! Please Try Again!");
				System.out.println("Please select either Administrator or User By Entering Corresponding Number:");
				System.out.printf("\t%-20s\n\t%-20s", "(1) Administrator", ")2) User");
				userSelection = reader.nextLine();
		}
		
		if(userSelection.equals("1"))
		{
			administratorInterface(reader);
		}
		else
		{
			userInterface(reader);
		}
	}
	
	public static void administratorInterface(Scanner reader) //Interface Methods/Functions for Administrator
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
			//Task 1 - Erase the Database
			case 1:
				break;
			//Task 2 - Load Airline Information
			case 2:
				String airline_id;
				String airline_name;
				String airline_abbreviation;
				String year_founded;
				String insert = "INSERT INTO Airline VALUES ";	// For concatenation later
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
						redo = true;
					}		
				}while(redo);
			
				// READ FILE AND PERFORM UPDATES
				while(file.hasNext())
				{
					line = file.nextLine();				// Read a single line in the file
					String[] parsed = line.split(",");	// Split the line into its different parts
			
					// STORE DATA
					airline_id = parsed[0];
					airline_name = parsed[1];
					airline_abbreviation = parsed[2];
					year_founded = parsed[3];
				
					// Concatenate all data and previous line to create insert statement
					line = insert.concat("('" + airline_id + "', '" + airline_name + "' ,'" + airline_abbreviation + "', '" + year_founded + "')");
			
					try									// Perform and commit update
					{
						connection.setAutoCommit(false);
						statement.executeUpdate(line);
						connection.commit(); 
					}
					catch(SQLException e1)				// Rollback if update failed
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
				file.close();							// Close file
				break;
			//Task 3 - Load Schedule Information
			case 3:
				String flight_number;
				airline_id = null;						// Already declared above
				String plane_type;
				String departure_city;
				String arrival_city;
				String departure_time;
				String arrival_time;
				String weekly_schedule;
				
				insert = "INSERT INTO Flight VALUES ";	// For concatenation later
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
					line = insert.concat("('" + flight_number + "', '" + airline_id + "', '" + plane_type + "', '" + 
											departure_city + "', '" + arrival_city + "', '" + departure_time + "', '" + 
											arrival_time + "', '" + weekly_schedule + ")");
			
					try									// Perform and commit update
					{
						connection.setAutoCommit(false);
						statement.executeUpdate(line);
						connection.commit(); 
					}
					catch(SQLException e1)				// Rollback if update failed
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
				file.close();							// Close file
				break;
			//Task 4 - Load Pricing Information
			case 4:
				departure_city = null;					// Already declared above
				arrival_city = null;					// Already declared above
				airline_id = null;						// Already declared above
				String high_price;
				String low_price;
				
				insert = "INSERT INTO Price VALUES ";	// For concatenation later
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
						redo = true;
					}		
				}while(redo);
			
				// READ FILE AND PERFORM UPDATES
				while(file.hasNext())
				{
					line = file.nextLine();				// Read a single line in the file
					String[] parsed = line.split(",");	// Split the line into its different parts
			
					// STORE DATA
					departure_city = parsed[0];
					arrival_city = parsed[1];
					airline_id = parsed[2];
					high_price = parsed[3];
					low_price = parsed[4];
					
					// Concatenate all data and previous line to create insert statement
					line = insert.concat("('" + departure_city + "', '" + arrival_city + "', '" + airline_id + "', " + 
											high_price + ", " + low_price + ")");
			
					try									// Perform and commit update
					{
						connection.setAutoCommit(false);
						statement.executeUpdate(line);
						connection.commit(); 
					}
					catch(SQLException e1)				// Rollback if update failed
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
				file.close();							// Close file
				break;
			//Task 5 - Load Plane Information
			case 5:
				plane_type = null;						// Already declared above
				String manufacturer;
				String plane_capacity;
				String last_service;
				String year;
				String owner_id;
				
				insert = "INSERT INTO Plane VALUES ";	// For concatenation later
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
					line = insert.concat("('" + plane_type + "', '" + manufacturer + "', " + plane_capacity + ", '" + 
											last_service + "', to_date('" + year + "','MM/DD/YYYY'), '" + owner_id + "')");
			
					try									// Perform and commit update
					{
						connection.setAutoCommit(false);
						statement.executeUpdate(line);
						connection.commit(); 
					}
					catch(SQLException e1)				// Rollback if update failed
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
				file.close();							// Close file
				break;
			//Task 6 - Generate Passenger Manifest For Specific Flight On Given Day
			case 6:
				break;
		}
	}
	
	public static void userInterface(Scanner reader) //Interface Methods/Functions for User
	{
		System.out.println("Please Select An Option From The Following List:");
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

		switch(selection)
		{
			case 1:
			//Task 1 - Add Customer
			String cid, salutation, fName, lName, ccNum, ccExpire, street, city, phone, email, freqMiles;
			System.out.print("Please enter your preferred salutation: ");
			salutation = reader.nextLine();
			System.out.print("Please enter your first name: ");
			fName = reader.nextLine();
			System.out.print("Please enter your last name: ");
			lName = reader.nextLine();
			
			String nameTest = "SELECT COUNT(*) FROM CUSTOMER WHERE first_name = '" + fName + "' AND last_name = '" + lName + "';";
			resultSet = statement.executeQuery(nameTest); //Checking To See If We Have Same First and Last Name
			if(resultSet.next() == null)
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
				
				String cidReceiver = "SELECT cid FROM CUSTOMER;";
				resultSet = statement.executeQuery(cidReceiver);
				int newCID = Integer.parseInt(resultSet.last()) + 1;
				
				String insertStatement = "INSERT INTO CUSTOMER VALUES('" + newCID + "', '" + salutation + "', '" + fName + "', '" + lName + "', '" 
				+ "', '" + ccNum + "', to_date('" + ccExpire + "', 'MM/YY')" + "', '" + street + "', '" + city + "', '" + state + "', '" + phone 
				+ "', '" + email + "NULL";
				
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
			
			case 2:
			//Task 2 - Show Customer Info, Given Customer Name
			System.out.println("Please Enter A Name To See Customer Information");
			String customerName = reader.nextLine();
			try
			{
				resultSet = statement.executeQuery("SELECT * FROM CUSTOMER WHERE name = " + customerName); //Execute Query
				String cid, salutation, fName, lName, ccNum, ccExpire, street, city, state, phone, email, freqMiles;
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
					state = resultSet.getState("state");
					phone = resultSet.getString("phone");
					email = resultSet.getString("email");
					freqMiles = resultSet.getString("frequent_miles");
					
					System.out.println("CID:\t" + cid);
					System.out.println("Salutation:\t" + salutation);
					System.out.println("First Name:\t" + fName);
					System.out.println("Last Name:\t" + lName);
					System.out.println("Credit Card Number:\t" + ccNum);
					System.out.println("Credit Card Expire Date:\t" + ccExpire);
					System.out.printf("Address:\t%s %s, %s", street, city, state);
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
			
			break;
			
			case 3:
			//Task 3 - Find Price for Flights Between Two Cities
			String city1, city2;
			System.out.print("Please enter the first city airport code: ");
			city1 = reader.nextLine();
			
			do
			{
				System.out.print("Please enter the second city airport code: ");
				city2 = reader.nextLine();
			}while(city1.equals(city2));
			
			String flightQuery = "SELECT * FROM FLIGHT WHERE departure_city = '" + city1 + "' AND arrival_city = '" + city2 + "';";
			resultSet = statement.executeQuery(flightQuery);
			while(resultSet.next())
			{
				System.out.println("One-Way Between " + city1 + " and " + city2 + "on Airline: " + resultSet.getString("airline_id"));
				System.out.println("High Price: " + resultSet.getInt("high_price"));
				System.out.println("Low Price: " + resultSet.getInt("low_price"));
			}
			
			flightQuery = "SELECT * FROM FLIGHT WHERE departure_city = '" + city2 + "' AND arrival_city = '" + city1 + "';";
			resultSet = statement.executeQuery(flightQuery);
			while(resultSet.next())
			{
				System.out.println("One-Way Between " + city2 + " and " + city1 + "on Airline: " + resultSet.getString("airline_id"));
				System.out.println("High Price: " + resultSet.getInt("high_price"));
				System.out.println("Low Price: " + resultSet.getInt("low_price"));
			}
			
			flightQuery = "SELECT * FROM FLIGHT WHERE departure_city = '" + city1 + "' AND arrival_city = '" + city2 + "';";
			resultSet = statement.executeQuery(flightQuery);
			while(resultSet.next())
			{
				int depTime, arrTime;
				depTime = Integer.parseInt(resultSet.nextString("departure_time");
				arrTime = Integer.parseInt(resultSet.nextString("arrival_time");
				
				if(depTime > arrTime)
				{
					System.out.println("Roundtrip Between " + city1 + " and " + city2 + "on Airline: " + resultSet.getString("airline_id"));
					System.out.println("Price: " + resultSet.getInt("high_price");
				}
				
				else
				{
					System.out.println("Roundtrip Between " + city1 + " and " + city2 + "on Airline: " + resultSet.getString("airline_id"));
					System.out.println("Price: " + resultSet.getInt("low_price");
				}
			}
			break;
			
			//Task 4 - Find All Routes Between Two Cities
			case: 4
			String city1, city2;
			System.out.print("Please enter the first city airport code: ");
			city1 = reader.nextLine();
			
			do
			{
				System.out.print("Please enter the second city airport code: ");
				city2 = reader.nextLine();
			}while(city1.equals(city2));
			
			//Roundtrip First
			String onewayQuery = "SELECT * FROM FLIGHT WHERE departure_city = '" + city1 + "' AND arrival_city = '" + city2 + "';";
			
			resultSet = statement.executeQuery(onewayQuery);
			
			System.out.println("One-Way Flights Between " + city1 + " and " + city2);
			while(resultSet.next())
			{
				System.out.printf("Flight Number: %s\n", resultSet.getString("airline_id"));
				System.out.printf("Depature City: %s\n", resultSet.getString("departure_city"));
				System.out.printf("Arrival City: %s\n", resultSet.getString("arrival_city"));
				System.out.printf("Departure Time: %s\n", resultSet.getString("departure_time"));
				System.out.printf("Arrival Time: %s\n", resultSet.getString("arrival_time"));
				System.out.println("--------------------");
			}
			
			//Trips With Connections
			String connectionFlights = "SELECT * FROM (FLIGHT F JOIN FLIGHT G ON F.arrival_city = G.departure_city) T WHERE (F.departure_city = '"
			+ city1 + "' AND G.arrival_city = '" + city2 + "');"; //This Will Have to Be Filtered A Bit More
			
			resultSet = statement.executeQuery(connectionFlights);
			System.out.println("Connecting Flights Between " + city1 + " and " + city2);
			while(resultSet.next())
			{
				if(checkSchedules(resultSet.getString(8), resultSet.getString(16)) && (Integer.parseInt(resultSet.getString(7)) + 1) % 2400 < Integer.parseInt(resultSet.getString(14)))
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
			}
			
			break;
			
			//Task 5 - Find All Routes Between Two Cities Of A Given Airline
			case 5:
			String city1, city2, airlineID;
			System.out.print("Please enter the first city airport code: ");
			city1 = reader.nextLine();
			
			do
			{
				System.out.print("Please enter the second city airport code: ");
				city2 = reader.nextLine();
			}while(city1.equals(city2));
			
			System.out.print("Please Enter An Airline ID: ");
			airlineID = reader.nextLine();
			
			//Roundtrip First
			String onewayQuery = "SELECT * FROM FLIGHT WHERE departure_city = '" + city1 + "' AND arrival_city = '" + city2 + "' AND airline_id = '" + airlineID + "';";
			
			resultSet = statement.executeQuery(onewayQuery);
			
			System.out.println("One-Way Flights Between " + city1 + " and " + city2 + " on Airline" + airlineID);
			while(resultSet.next())
			{
				System.out.printf("Flight Number: %s\n", resultSet.getString("airline_id"));
				System.out.printf("Depature City: %s\n", resultSet.getString("departure_city"));
				System.out.printf("Arrival City: %s\n", resultSet.getString("arrival_city"));
				System.out.printf("Departure Time: %s\n", resultSet.getString("departure_time"));
				System.out.printf("Arrival Time: %s\n", resultSet.getString("arrival_time"));
				System.out.println("--------------------");
			}
			
			//Trips With Connections
			String connectionFlights = "SELECT * FROM (FLIGHT F JOIN FLIGHT G ON F.arrival_city = G.departure_city) T WHERE (F.departure_city = '"
			+ city1 + "' AND G.arrival_city = '" + city2 + "' AND airline_id = '" + airlineID + "');"; //This Will Have to Be Filtered A Bit More
			
			resultSet = statement.executeQuery(connectionFlights);
			System.out.println("Connecting Flights Between " + city1 + " and " + city2 + " on Airline" + airlineID);
			while(resultSet.next())
			{
				if(checkSchedules(resultSet.getString(8), resultSet.getString(16)) && (Integer.parseInt(resultSet.getString(7)) + 1) % 2400 < Integer.parseInt(resultSet.getString(14)))
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
			}
			
			break;
			
			//Task 6 - Find All Routes With Available Seats Between Two Cities On Given Day
			
			//Task 7 - For A Given Airline, Find All Routes With Available Seats Between Two Cities On A Given Date
			
			//Task 8 - Add Reservation
			int legNum = 0;
			String[4][2] legInfo = new String[4][2]; //To Hold User Flight Info
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
				currCapacity = resultSet.nextLine();
				
				resultSet = statement.executeQuery("SELECT plane_capacity FROM FLIGHT F JOIN PLANE P ON F.plane_type = P.plane_type WHERE flight_number = " + legInfo[i][0]);
				resultSet.next();
				maxCapacity = Integer.parseInt(resultSet.nextString());
				
				if(currCapacity >= maxCapacity) //There is room on the plane_capacity
				{
					System.out.println("Flight " + legInfo[i][0] + " does not have any open seats! Reservation Cancelled!");
					seatAvail = false;
				}
			}
			
			System.out.println("Seats Available On All Flights. Creating Reservation...");
			System.out.println("What is your Customer ID (CID): ");
			String custID = reader.nextLine();
			resultSet = statement.executeQuery("SELECT MAX(reservation_number) FROM RESERVATION;");
			resultSet.next();
			int newReservationNumber = Integer.parseInt(resultSet.nextString()) + 1;
			resultSet = statement.executeQuery("SELECT * FROM FLIGHT WHERE departure_city = (SELECT departure_city FROM FLIGHT WHERE flight_number = '" 
			+ legInfo[0][0] + "') AND arrival_city = (SELECT arrival_city FROM FLIGHT WHERE flight_number = '" + legInfo[legNum][0]
			+ "') AND airline_id = (SELECT airline_id FROM FLIGHT WHERE flight_number = '" + legInfo[0][0] + "';");
			resultSet.next();
			String flightNum = resultSet.getString("flight_number");
			String depCity = resultSet.getString("departure_city");
			String arrCity = resultSet.getString("arrival_city");
			int price;
			if(Integer.parseInt(resultSet.getString("departure_time")) < Integer.parseInt(resultSet.getString("arrivalTime"))) //High Price, Same Day
			{
				resultSet = statement.executeQuery("SELECT high_price FROM PRICE WHERE flight_number = '" + flightNum + "';");
				resultSet.next();
				price = resultSet.nextInt();
			}
			
			else
			{
				resultSet = statement.executeQuery("SELECT low_price FROM PRICE WHERE flight_number = '" + flightNum + "';");
				resultSet.next();
				price = resultSet.nextInt();
			}
			
			System.out.print("Please enter the credit card number you would like to use for this transaction: ");
			String ccNumber = reader.nextLine();
			
			try	// Perform and commit update
			{
				connection.setAutoCommit(false);
				statement.executeUpdate("INSERT INTO RESERVATION VALUES('" + newReservationNumber + "', '" + custID + "', '" +  depCity + "', '" 
				+ arrCity + "', '" + price + "', '" + "', '" + ccNumber + "', " + "to_date('" + legInfo[0][1] + "', 'MM-DD-YYYY'), 'Y';");
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
			
			break;
			
			//Task 9 - Show Reservation Information, Given Reservation Number
			case 9:
			System.out.println("Please Enter a Reservation Number: ");
			String reservationNum = reader.nextLine();
			
			String flightQuery = "SELECT flight_number FROM RESERVATION_DETAIL WHERE reservation_number = '" + reservationNum + "';";
			resultSet = statement.executeQuery(flightQuery);
			if(resultSet.next() == null)
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
			break;
			//Task 10 - Buy Ticket From Existing Reservation
			System.out.println("Please Enter a Reservation Number: ");
			String reservationNum = reader.nextLine();
			
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
		
		}
	}
	
	private static boolean checkSchedules(String sched1, String sched2) //Checks To See If Schedules Contain At Least One Similar Day
	{
		char[] s1, s2;
		s1 = sched1.toCharArray();
		s2 = sched2.toCharArray();
		for(int i = 0; i < sched1.length; i++)
		{
			if(s1[i] != '-' && s2[i] != "-")
				return true;
		}
		
		return false;
	}
}