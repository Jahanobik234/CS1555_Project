import java.util.Scanner;
import java.util.Random;
import java.util.*;
import java.io.*;

public class Customer_Generator
{
	private static final int DATA_COUNT = 200;
	private static String[] femaleNames = {"Kathy", "Staci", "Gay", "Kay", "Fay", "Day", "Audrey", "Monica", "Rebecca", "Emily", "Sara", "Demeara", "Kim", "Jessica", "Annie", "Katie", "Lisa", "Martha", "Breanna", "Penelope", "Nicole"};
	private static String[] maleNames = {"Seth", "Jonathan", "Panos", "Steve", "Mark", "John", "Paul", "Tim", "Spencer", "Matt", "Dan", "Will", "Jesse", "Wyatt", "Michael", "Sam", "Caleb", "Adam", "Hunter", "Joshua"};
	private static String[] lastNames = {"Miller", "Smith", "Johnson", "Brown", "Hanobik", "Stayer", "Richardson", "Stevenson", "Carroll", "McKinney", "Gosa", "Getz", "Sinatra", "Jonas", "Butcher", "Frey", "Misuraca", "Tuft", "Roosevelt", "Ross"};
	private static String[] city = {"Pittsburgh", "Philadelphia", "York", "Hershey", "Harrisburg", "Albany", "Columbus", "Indianapolis", "Chicago", "Des Moines", "Houston", "Tallahassee", "New Orleans", "Atlanta", "Providence", "Boston", "Los Angeles", "Honolulu", "Anchorage", "Billings"};
	private static String[] state = {"PA", "PA", "PA", "PA", "PA", "NY", "OH", "IA", "IL", "IA", "TX", "FL", "LA", "GA", "RI", "MA", "CA", "HI", "AK", "MT"};
	private static String[] street = {"Main Street", "Walnut Street", "Bigelow Boulevard", "Oakland Avenue", "Orchard Circle", "McKee Street", "Pine Road", "West Avenue", "Friendship Lane", "Forbes Avenue", "Fifth Avenue", "Cherry Street", "New School Lane", "Distillery Road", "Memory Lane", "Kenyon Trail", "Yuletide Road", "Swanson Street", "Cathedral Road", "Farm Lane", "Union Street"};
	private static String[] AirlineID = {"10001", "20001", "30001", "40001", "50001", "60001", "70001", "80001", "90001", "10010"};
	
	public String[] cids = new String[DATA_COUNT];
	public static String[] ccNumbers = new String[DATA_COUNT];
	
	private static PrintWriter output;
	
	public Customer_Generator()
	{
		try
		{
			output = new PrintWriter("cust_data.sql");
		}
		catch(IOException excep)
		{
			System.out.println("Did not create file.");
		}
		
		// Data Titles for Easier Readability to User Looking at Insert Statements
		output.println("-- cid, salutation, first_name, last_name, credit_card_num, credit_card_expire, street, city, state, phone, email --");		
		
		Random numGen = new Random();
		int pick1;						// Random Number
		int pick2;						// Random Number
		int counter = 0;				// User Counter
		
		while(counter < DATA_COUNT)
		{
			pick1 = numGen.nextInt(100) + 1;
			pick2 = numGen.nextInt(20);
			
			// CID
			cids[counter] = String.format("%08d", counter + 1);
			output.printf("INSERT INTO CUSTOMER VALUES (%08d, ", counter + 1);
			
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
			frequentMiles(numGen, (pick1 % 10));			// 0 - 9
			
			// CREDIT CARD NUMBER
			ccNum(counter);
			
			// CREDIT CARD DATE
			ccDate((pick2 % 12 + 1), numGen);			// Month, Year
			
			// STREET NAME
			streetName(pick2);								// 0 - 19
			
			// CITY AND STATE
			cityState(pick1 % 20);							// 0 - 19
			
			// PHONE
			phone();
			
			// EMAIL
			email(counter + 1);
			
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
		output.printf("%s, ", "'Mr'");
		output.printf("'%s', ", maleNames[name1]);
		output.printf("'%s', ", lastNames[name2]);
	}
	
	// Generates Random Airline Code 30% of the time, otherwise NULL
	public static void frequentMiles(Random gen, int num)
	{
		if(num > 7) {
			output.printf("'%s', ", AirlineID[gen.nextInt(10)]);
			}
		else
			output.printf("%s, ", "NULL");
	}
	
	// Generate random 16 digit credit card number
	public static void ccNum(int index)
	{
		Random tmp = new Random();
		int num;
		int num2 = tmp.nextInt(10);
		String no = Integer.toString(num2);
		
		for(int i = 0; i < 15; i++)
		{
			num = tmp.nextInt(10);						// 0 - 9
			no = no.concat(Integer.toString(num));
		}
		ccNumbers[index] = no;
		output.printf("'%s', ", no);
	}
	
	// Generate "to_date("<rand_mon>/<rand_year>", "MM/YY")"
	public static void ccDate(int mon, Random gen)
	{
		int year = gen.nextInt(10) + 16;
		output.printf("to_date('%02d/%02d', 'MM/YY'), ", mon, year);
	}
	
	// Randomly select name from list
	public static void streetName(int num)
	{
		Random tmp = new Random();
		output.printf("'%03d %s', ", (tmp.nextInt(999) + 1), street[num]);
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