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
		Connection connection;
		PittToursInterface pti;
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
			admin_task1("yes");
			System.out.println("Database cleared");
		}
		catch(SQLException err)
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
			System.out.printf("Cardinality of Plane Before Insert: %s", rs.next().getString("COUNT(*)"));
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
					System.out.printf("Cardinality of Plane After Insert: %s", rs.next().getString("COUNT(*)"));
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
			System.out.printf("Cardinality of Airline Before Insert: %s", rs.next().getString("COUNT(*)"));
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
					System.out.printf("Cardinality of Airline After Insert: %s", rs.next().getString("COUNT(*)"));
				}
				catch(SQLException err)
				{
					System.out.println(err.toString());
				}
			}
		}
		
		// Task 3 -- Load Schedule Information
		String[] task3File = {"admin_3.1.txt", "admin_3.2.txt", "admin_3.3.txt", "admin_3.4.txt", "admin_3.5.txt"};
		System.out.println("Testing Admin Task 3...");
		
		try
		{
			String before = "SELECT COUNT(*) FROM Flight";
			rs = statement.executeQuery(before);
			System.out.printf("Cardinality of Flight Before Insert: %s", rs.next().getString("COUNT(*)"));
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
					System.out.printf("Cardinality of Flight After Insert: %s", rs.next().getString("COUNT(*)"));
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
		System.out.println("Testing Admin Task 4...");
		
		try
		{
			String before = "SELECT COUNT(*) FROM Price";
			rs = statement.executeQuery(before);
			System.out.printf("Cardinality of Price Before Insert: %s", rs.next().getString("COUNT(*)"));
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
					System.out.printf("Cardinality of Price After Insert: %s", rs.next().getString("COUNT(*)"));
				}
				catch(SQLException err)
				{
					System.out.println(err.toString());
				}
			}
		}
		
		// Task 6 -- Passanger Manifests
		
		
		/******** USER ********/
		
		// Task 1 -- Add Customer
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
			System.out.printf("Cardinality of Customer Before Insert: %s", rs.next().getString("COUNT(*)"));
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
				System.out.printf("Cardinality of Customer After Insert: %s", rs.next().getString("COUNT(*)"));
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
		
		// Task 2 --
		
		// Task 3 --
		
		// Task 4 --
		
		// Task 5 --
		
		// Task 6 --
		
		// Task 7 --
		
		// Task 8 --
		
		// Task 9 --
		
		// Task 10 --
		
		/******** ADMINISTRATOR ********/
		
		// Task 1 -- Delete Database Information
	}
}