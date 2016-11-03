import java.util.Scanner;
import java.util.Random;
import java.util.*;
import java.io.*;

public class Data_Generator
{
	private static PrintWriter output;
	private static String[] femaleNames = {"Kathy", "Staci", "Gay", "Kay", "Fay", "Day", "Audrey", "Monica", "Rebecca", "Emily", "Sara", "Demeara", "Kim", "Jessica", "Annie", "Katie", "Lisa", "Martha", "Breanna", "Penelope", "Nicole"};
	private static String[] maleNames = {"Seth", "Jonathan", "Panos", "Steve", "Mark", "John", "Paul", "Tim", "Spencer", "Matt", "Dan", "Will", "Jesse", "Wyatt", "Michael", "Sam", "Caleb", "Adam", "Hunter", "Joshua"};
	private static String[] lastNames = {"Miller", "Smith", "Johnson", "Brown", "Hanobik", "Stayer", "Richardson", "Stevenson", "Carroll", "McKinney", "Gosa", "Getz", "Sinatra", "Jonas", "Butcher", "Frey", "Misuraca", "Tuft", "Roosevelt", "Ross"};
	private static String[] city = {"Pittsburgh", "Philadelphia", "York", "Hershey", "Harrisburg", "Albany", "Columbus", "Indianapolis", "Chicago", "Des Moines", "Houston", "Tallahassee", "New Orleans", "Atlanta", "Providence", "Boston", "Los Angeles", "Honolulu", "Anchorage", "Billings"};
	private static String[] state = {"PA", "PA", "PA", "PA", "PA", "NY", "OH", "IA", "IL", "IA", "TX", "FL", "LA", "GA", "RI", "MA", "CA", "HI", "AK", "MT"};
	private static String[] street = {"Main Street", "Walnut Street", "Bigelow Boulevard", "Oakland Avenue", "Orchard Circle", "McKee Street", "Pine Road", "West Avenue", "Friendship Lane", "Forbes Avenue", "Fifth Avenue", "Cherry Street", "New School Lane", "Distillery Road", "Memory Lane", "Kenyon Trail", "Yuletide Road", "Swanson Street", "Cathedral Road", "Farm Lane", "Union Street"};
	
	public static void main(String[] args)
	{
		try
		{
			output = new PrintWriter("data.txt");
		}
		catch(IOException excep)
		{
			System.out.println("Did not create file.");
		}
		
		Random numGen = new Random();
		int pick1;						// Random Number
		int pick2;
		int counter = 0;				// User Counter
		
		while(counter < 200)
		{
			pick1 = numGen.nextInt(100) + 1;
			pick2 = numGen.nextInt(20);
			int tmp = counter;
			
			// CID
			output.printf("INSERT INTO CUSTOMER VALUES (%08d, ", tmp);
			
			// SALUTATION, F_NAME, L_NAME
			if(pick1 > 50)
			{
				maleName(pick1 % 20, pick2);				// First name 0 - 19; Last Name 0 - 19
			}
			else
			{
				femaleName(pick1, pick2);					// First name 1 - 100; Last Name 0 - 19
			}
			
			// FREQUENT MILES
			frequentMiles(pick1 % 10);						// 0 - 9
			
			// CREDIT CARD NUMBER
			ccNum();
			
			// CREDIT CARD DATE
			ccDate((pick2 % 12 + 1), (pick1 - 1));			// Month, Year
			
			// STREET NAME
			streetName(pick2);								// 0 - 19
			
			// CITY AND STATE
			cityState(pick1 % 20);							// 0 - 19
			
			// PHONE
			phone();
			
			// EMAIL
			email(tmp);
			
			output.println(");");
			counter ++;
		}
		output.close();
		System.out.println("Customers Created");
	}
	
	// Generate Mrs. or Ms. modularly with a first and last name.
	// Print result to file
	public static void femaleName(int name1, int name2)
	{
	//System.out.println("F Name1: " + name1 + " Name2: " + name2);
		if(name1 >= 1 && name1 <= 25)
			output.printf("%s, ", "'Ms'");
		else if(name1 >=26 && name1 <=50)
			output.printf("%s, ", "'Mrs'");
			
		output.printf("'%s', ", femaleNames[name1 % 20]);			// Assumes array
		output.printf("'%s', ", lastNames[name2 % 20]);				// Assumes array
	}
	
	// Generate random first and last name
	// Print result to file
	public static void maleName(int name1, int name2)
	{
	//System.out.println("M Name1: " + name1 + " Name2: " + name2);
		output.printf("%s, ", "'Mr'");
		output.printf("'%s', ", maleNames[name1]);
		output.printf("'%s', ", lastNames[name2]);
	}
	
	// Generates y/n modularly
	public static void frequentMiles(int num)
	{
		if(num > 7)
			output.printf("%s, ", "'Yes'");
		else
			output.printf("%s, ", "'No'");
	}
	
	// Generate random 16 digit credit card number
	public static void ccNum()
	{
		Random tmp = new Random();
		int num;
		
		output.printf("'");
		for(int i = 0; i < 9; i++)
		{
			num = tmp.nextInt(10);						// 0 - 9
			output.print(num);
		}
		output.printf("', ");
	}
	
	// Generate "to_date("<rand_mon>/<rand_year>", "MM/YY")"
	public static void ccDate(int mon, int year)
	{
		output.print("to_date('");
		output.print(mon + "/" + year + "', 'MM/YY'), ");
	}
	
	// Randomly select name from list
	public static void streetName(int num)
	{
		output.printf("'%s', ", street[num]);
	}
	
	// Randomly select city and state from list
	public static void cityState(int num)
	{
		output.printf("'%s', '%s', ", city[num], state[num]);
	}
	
	// Randomly generate phone number
	public static void phone()
	{
		Random tmp = new Random();
		
		output.print("'");
		for(int i = 0; i < 10; i++)
		{
			output.printf("%d", tmp.nextInt(10));
		}
		output.print("', ");
	}
	
	// Use first and last name to create email with cid "@pitt.edu"
	public static void email(int usrID)
	{
		output.printf("'%s@pitt.edu'", usrID);
	}
}