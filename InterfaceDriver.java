/* 
* Jonathan Hanobik and Seth Stayer
* CS1555 - Fall 2016, Term Project
* Administrator/User Interface Driver
*/

import java.sql.*;  				// Import the file containing definitions for the parts
import java.text.ParseException;	// Needed by java for database connection and manipulation
import java.util.*;					// Needed for Scanner
import java.io.*;					// Needed for file i/o

public class InterfaceDriver
{
	public static void main(String[] args)
	{
		Connection connection = null;
		Statement statement = null;
		PittToursInterface pti = null;
		ResultSet rs;
		
		// Create Connection and Call Interface
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
		
		try
		{
			pti = new PittToursInterface(connection);
		}
		catch(Exception e)
		{
			System.out.println(e.toString());
		}
		
		/******** ADMINISTRATOR ********/
		// Task 1 -- Delete Database (To Begin Fresh)
		try
		{
			pti.admin_task1("yes");
			System.out.println("Database cleared");
		}
		catch(Exception err)
		{
			System.out.println(err.toString());
		}
		
		// Task 5 -- Load Plane Information
		String[] task5File = {"admin_5.1.txt", "admin_5.2.txt", "admin_5.3.txt", "admin_5.4.txt"};
		System.out.println("Testing Admin Task 5...");
		
		try
		{
			String before = "SELECT COUNT(*) FROM Plane";
			rs = statement.executeQuery(before);
			rs.next();
			System.out.printf("Cardinality of Plane Before Insert: %s", rs.getString("COUNT(*)"));
		}
		catch(SQLException err)
		{
			System.out.println(err.toString());
		}
		
		for(String taskFile : task5File)
		{
			int t5 = pti.admin_task5(taskFile);
		
			if(t5 != 0)
			{
				System.out.println("The data was not inserted into the database.");
				System.out.println("Admin Task 5 Test Failed.");
			}
			else
			{
				try
				{
					String after = "SELECT COUNT(*) FROM Plane";
					rs = statement.executeQuery(after);
					rs.next();
					System.out.printf("Cardinality of Plane After Insert: %s", rs.getString("COUNT(*)"));
				}
				catch(SQLException err)
				{
					System.out.println(err.toString());
				}
			}
		}
		
		// Task 2 -- Load Airline Information
		String[] task2File = {"admin_2.1.txt", "admin_2.2.txt", "admin_2.3.txt"};
		System.out.println("Testing Admin Task 2...");
		
		try
		{
			String before = "SELECT COUNT(*) FROM Airline";
			rs = statement.executeQuery(before);
			rs.next();
			System.out.printf("Cardinality of Airline Before Insert: %s", rs.getString("COUNT(*)"));
		}
		catch(SQLException err)
		{
			System.out.println(err.toString());
		}
		
		for(String taskFile : task2File)
		{
			int t2 = pti.admin_task2(taskFile);
		
			if(t2 != 0)
			{
				System.out.println("The data was not inserted into the database.");
				System.out.println("Admin Task 2 Test Failed.");
			}
			else
			{
				try
				{
					String after = "SELECT COUNT(*) FROM Airline";
					rs = statement.executeQuery(after);
					rs.next();
					System.out.printf("Cardinality of Airline After Insert: %s", rs.getString("COUNT(*)"));
				}
				catch(SQLException err)
				{
					System.out.println(err.toString());
				}
			}
		}
		
		// Task 3 -- Load Schedule Information
		String[] task3File = {"admin_3.1.txt", "admin_3.2.txt", "admin_3.3.txt", "admin_3.4.txt", "admin_3.5.txt"};
		System.out.println("\t\tTesting Admin Task 3...");
		
		try
		{
			String before = "SELECT COUNT(*) FROM Flight";
			rs = statement.executeQuery(before);
			rs.next();
			System.out.printf("Cardinality of Flight Before Insert: %s", rs.getString("COUNT(*)"));
		}
		catch(SQLException err)
		{
			System.out.println(err.toString());
		}
		
		for(String taskFile : task3File)
		{
			int t3 = pti.admin_task3(taskFile);
		
			if(t3 != 0)
			{
				System.out.println("The data was not inserted into the database.");
				System.out.println("Admin Task 3 Test Failed.");
			}
			else
			{
				try
				{
					String after = "SELECT COUNT(*) FROM Flight";
					rs = statement.executeQuery(after);
					rs.next();
					System.out.printf("Cardinality of Flight After Insert: %s", rs.getString("COUNT(*)"));
				}
				catch(SQLException err)
				{
					System.out.println(err.toString());
				}
			}
		}
		
		// Task 4 -- Load Price Information
		String[] task4File = {"admin_4.1.txt", "admin_4.2.txt", "admin_4.3.txt", "admin_4.4.txt", "admin_4.5.txt", 
								"admin_4.6.txt", "admin_4.7.txt", "admin_4.8.txt", "admin_4.9.txt", "admin_4.10.txt"};
		System.out.println("\t\tTesting Admin Task 4...");
		
		try
		{
			String before = "SELECT COUNT(*) FROM Price";
			rs = statement.executeQuery(before);
			rs.next();
			System.out.printf("Cardinality of Price Before Insert: %s", rs.getString("COUNT(*)"));
		}
		catch(SQLException err)
		{
			System.out.println(err.toString());
		}
		
		for(String taskFile : task4File)
		{
			int t4 = pti.admin_task4(taskFile);
		
			if(t4 != 0)
			{
				System.out.println("The data was not inserted into the database.");
				System.out.println("Admin Task 4 Test Failed.");
			}
			else
			{
				try
				{
					String after = "SELECT COUNT(*) FROM Price";
					rs = statement.executeQuery(after);
					rs.next();
					System.out.printf("Cardinality of Price After Insert: %s", rs.getString("COUNT(*)"));
				}
				catch(SQLException err)
				{
					System.out.println(err.toString());
				}
			}
		}
		
		// Task 6 -- Passanger Manifests
		System.out.println("\t\tTesting Admin Task 6...");
		int t6 = pti.admin_task6("00008", "12/06/2016");
		if(t6 != 0)
		{
			System.out.println("Query Not Executable, An Error Occurred.");
		}
		
		t6 = pti.admin_task6("00009", "12/07/2016");
		if(t6 != 0)
		{
			System.out.println("Query Not Executable, An Error Occurred.");
		}
		
		t6 = pti.admin_task6("00010", "12/08/2016");
		if(t6 != 0)
		{
			System.out.println("Query Not Executable, An Error Occurred.");
		}
		
		/******** USER ********/
		
		// Task 1 -- Add Customer
		System.out.println("\t\tTesting User Task 1...");
			// Individual First
		String salutation = "Mr";
		String firstName = "Edward";
		String lastName = "Nigma";
		String ccNum = "6194837219582350";
		String ccExpire = "12/21";
		String street = "3959 Main Street"; 
		String city = "Pittsburgh";
		String state = "PA";
		String phone = "7310592";
		String email = "enigma@riddlers.com";
		
		try
		{
			String before = "SELECT COUNT(*) FROM Customer";
			rs = statement.executeQuery(before);
			rs.next();
			System.out.printf("Cardinality of Customer Before Insert: %s", rs.getString("COUNT(*)"));
		}
		catch(SQLException err)
		{
			System.out.println(err.toString());
		}
		
		try
		{
			pti.user_task1(salutation, firstName, lastName, ccNum, ccExpire, street, city, state, phone, email);
			try
			{
				String after = "SELECT COUNT(*) FROM Customer";
				rs = statement.executeQuery(after);
				rs.next();
				System.out.printf("Cardinality of Customer After Insert: %s", rs.getString("COUNT(*)"));
			}
			catch(SQLException err)
			{
				System.out.println(err.toString());
			}
		}
		catch(Exception err)
		{
			System.out.println(err.toString());
		}
		
			// Read in from file for stress test
		File f;
		Scanner reader = null;
		try
		{
			f = new File("user_1.txt");
			reader = new Scanner(f);
		}
		catch(Exception err)
		{
			System.out.println(err.toString());
		}
		
		while(reader.hasNext())
		{
			String[] values = reader.nextLine().split(",");
			salutation = values[0];
			firstName = values[1];
			lastName = values[2];
			ccNum = values[3];
			ccExpire = values[4];
			street = values[5]; 
			city = values[6];
			state = values[7];
			phone = values[8];
			email = values[9];
			
			try
			{
				pti.user_task1(salutation, firstName, lastName, ccNum, ccExpire, street, city, state, phone, email);
				try
				{
					String after = "SELECT COUNT(*) FROM Customer";
					rs = statement.executeQuery(after);
					rs.next();
					System.out.printf("Cardinality of Customer After Insert: %s", rs.getString("COUNT(*)"));
				}
				catch(SQLException err)
				{
					System.out.println(err.toString());
				}
			}
			catch(Exception err)
			{
				System.out.println(err.toString());
			}
		}
		reader.close();
		
		// Task 2 -- Show customer info, given customer name
		System.out.println("\t\tTesting User Task 2...");
		
		try
		{
			f = new File("user_2.txt");
			reader = new Scanner(f);
		}
		catch(Exception err)
		{
			System.out.println(err.toString());
		}
		
		while(reader.hasNext())
		{
			String[] name = reader.nextLine().split(",");
			String first = name[0];
			String last = name[1];
			
			try
			{
				pti.user_task2(first, last);
			}
			catch(Exception err)
			{
				System.out.println(err.toString());
			}
		}
		reader.close();
		
		// Task 3 -- Find price for flights between two cities
		System.out.println("\t\tTesting User Task 3...");
		String[] cities = {"PIT", "IND", "SAN", "BWI", "MCO"};
		
		for(int i = 0; i < 5; i++)
		{
			String city1 = cities[i];
			for(int j = 1; j < 5; j++)
			{
				String city2 = cities[j];
				try
				{
					pti.user_task3(city1, city2);
				}
				catch(Exception err)
				{
					System.out.println(err.toString());
				}
			}
		}
		
		// Task 4 -- Find all routes between two cities
		System.out.println("\t\tTesting User Task 4...");
		//String[] cities = {"PIT", "IND", "SAN", "BWI", "MCO"};
		
		for(int i = 0; i < 5; i++)
		{
			String city1 = cities[i];
			for(int j = 1; j < 5; j++)
			{
				String city2 = cities[j];
				try
				{
					pti.user_task4(city1, city2);
				}
				catch(Exception err)
				{
					System.out.println(err.toString());
				}
			}
		}
		
		// Task 5 -- Find all routes between two cities of a given airline
		System.out.println("\t\tTesting User Task 5...");
		//String[] cities = {"PIT", "IND", "SAN", "BWI", "MCO"};
		String[] airlines = {"00002", "00004", "00006", "00008", "00010"};
		
		for(int i = 0; i < 5; i++)
		{
			String airlineID = airlines[i];
			String city1 = cities[i];
			for(int j = 1; j < 5; j++)
			{
				String city2 = cities[j];
				try
				{
					pti.user_task5(city1, city2, airlineID);
				}
				catch(Exception err)
				{
					System.out.println(err.toString());
				}
			}
		}
		
		// Task 6 -- Find all routes with available seats between two cities on given date
		System.out.println("\t\tTesting User Task 6...");
		//String[] cities = {"PIT", "IND", "SAN", "BWI", "MCO"};
		String[] dates = {"12/06/2016", "12/07/2016", "12/09/2016", "12/04/2016", "12/05/2016"};
		
		for(int i = 0; i < 5; i++)
		{
			String date = dates[i];
			String city1 = cities[i];
			for(int j = 1; j < 5; j++)
			{
				String city2 = cities[j];
				try
				{
					pti.user_task6(city1, city2, date);
				}
				catch(Exception err)
				{
					System.out.println(err.toString());
				}
			}
		}
		
		// Task 7 -- For a given airline, find all routes with available seats between two cities on given date
		System.out.println("\t\tTesting User Task 7...");
		// String[] cities = {"PIT", "IND", "SAN", "BWI", "MCO"};
		// String[] dates = {"12/06/2016", "12/07/2016", "12/09/2016", "12/04/2016", "12/05/2016"};
		String[] airlineNames = {"Allegiant Air","Delta Air Lines","JetBlue Airways","Spirit Airlines","United Airlines"};
		
		for(int i = 0; i < 5; i++)
		{
			String date = dates[i];
			String city1 = cities[i];
			String airline = airlineNames[i];
			for(int j = 1; j < 5; j++)
			{
				String city2 = cities[j];
				try
				{
					pti.user_task7(city1, city2, date, airline);
				}
				catch(Exception err)
				{
					System.out.println(err.toString());
				}
			}
		}
		
		// Task 8 -- Add reservation
		System.out.println("Testing User Task 8...");
		Scanner reader = null;
		File f;
		try
		{
			f = new File("user_8.txt");
			reader = new Scanner(f);
		}
		catch(Exception err)
		{
			System.out.println(err.toString());
		}
		
		String s1, s2, s3, s4;
		String [][] legInfo = new String[4][2];
		
		while(reader.hasNext()
		{
			for(int i = 0; i < 4; i++)
			{
				String[] values = reader.nextLine().split(",");
				s1 = values[0];
				s2 = values[1];
				s3 = values[2];
				
				if(!s1.equals("0")) //Just Flight Number and Date
				{
					legInfo[i][0] = s1;
					legInfo[i][1] = s2;
				}
				
				else
				{
					pti.user_task8((i+1), legInfo, s2, s3);
				}
			
			}
			
		}
		System.out.println("User Task 8 Tested");
		
		// Task 9 -- Show reservation info, given reservation number
		System.out.println("Testing User Task 9...");
		for(int i = 1; i < 11; i++)
		{
			pti.user_task9(String.format("%05d", i));
		}
		
		// Task 10 -- Buy ticket from existing reservation
		System.out.println("Test User Task 10...");
		for(int i = 11; i < 21; i++)
		{
			pti.user_task10(String.format("%05d", i));
		}
		
		/******** ADMINISTRATOR ********/
		
		// Task 1 -- Delete Database Information
		try
		{
			pti.admin_task1("yes");
			System.out.println("Database cleared");
		}
		catch(Exception err)
		{
			System.out.println(err.toString());
		}
		
		try
		{
			connection.close();
		}
		catch(SQLException err)
		{
			System.out.println(err.toString());
		}
	}
}