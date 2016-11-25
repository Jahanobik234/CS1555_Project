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

		
		//Task 1 - Add Customer
		
		
		//Task 2 - Show Customer Info, Given Customer Name
		System.out.println("Please Enter A Name To See Customer Information");
		String customerName = reader.nextLine();
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