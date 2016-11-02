import java.util.Scanner;
import java.util.Random;
import java.util.*;
import java.io.*;

public class Data_Generator
{
	private enum FemaleNames = {Kathy, Staci, Gay, Kay, Fay, Day, Audrey, Monica, Rebecca, Emily, Sara, Demeara, Kim, Jessica, Annie, Katie, Lisa, Martha, Breanna, Penelope, Nicole};
	private enum MaleNames = {Seth, Jonathan, Panos, Steve, Mark, John, Paul, Tim, Spencer, Matt, Dan, Will, Jesse, Wyatt, Michael, Sam, Caleb, Adam, Hunter, Joshua};
	private enum LastNames = {Miller, Smith, Johnson, Brown, Hanobik, Stayer, Richardson, Stevenson, Carroll, McKinney, Gosa, Getz, Sinatra, Paul, Butcher, Frey, Misuraca, Tuft, Roosevelt, Ross};
	private enum City = {Pittsburgh, Philadelphia, York, Hershey, Harrisburg, Albany, Columbus, Indianapolis, Chicago, Des Moines, Houston, Tallahasee, New Orleans, Atlanta, Providence, Boston, Los Angeles, Honolulu, Anchorage, Billings};
	private enum State = {PA, PA, PA, PA, PA, NY, OH, IA, IL, IA, TX, FL, LA, GA, RI, MA, CA, HI, AK, MT};
	private enum Street = {Main Street, Walnut Street, Bigelow Boulevard, Oakland Avenue, Orchard Circle, McKee Street, Pine Road, West Avenue, Friendship Lane, Forbes Avenue, Fifth Avenue, Cherry Street, New School Lane, Distillery Road, Memory Lane, Kenyon Trail, Yuletide Road, Swanson Street, Cathedral Road, Farm Lane, Union Street};

	public static void main(Strings[] args)
	{
		Random numGen = new Random();
		int pick;						// Random Number
		int counter = 0;				// User Counter
		
		while(counter < 200)
		{
			pick1 = numGen.nextInt(100) + 1;
			pick2 = numGen.nextInt(20) + 1;
			int tmp = counter + 17;
			
			// CID
			System.out.printf("INSERT INTO CUSTOMER VALUES (%08d, ", tmp);
			
			// SALUTATION, F_NAME, L_NAME
			if(pick1 > 50)
			{
				maleName(pick1 - 50, pick2);
			}
			else
			{
				femaleName(pick1, pick2);
			}
			
			// FREQUENT MILES
			frequentMiles(pick1 % 20);
			
			// CREDIT CARD NUMBER
			ccNum();
			
			// CREDIT CARD DATE
			ccDate((pick2 % 12 + 1), (pick1 - 1));			// Month, Year
			
			// STREET NAME
			streetName(pick2);								// 1 - 20
			
			// CITY AND STATE
			cityState(pick1 % 20);							// 0 - 19
			
			// PHONE
			phone();
			
			// EMAIL
			email(tmp);
		}
	}
	
	// Generate Mrs. or Ms. modularly with a first and last name.
	// Print result
	public static void femaleName()
	{
		if(pick >= 1 && pick <= 25)
			printf("%s ", "'Ms'");
		else if(pick >=26 && pick <=50)
			printf("%s ", "'Mrs'");
	}
	
	// Generate random first and last name
	// Print result
	public static void maleName()
	{
	}
	
	// Generates y/n modularly
	public static void frequentMiles()
	{
	}
	
	// Generate random 16 digit credit card number
	public static void ccNum()
	{
	}
	
	// Generate "to_date("<rand_mon>/<rand_year>", "MM/YY")"
	public static void ccDate()
	{
	}
	
	// Randomly select name from list
	public static void streetName()
	{
	}
	
	// Randomly select city and state from list
	public static void cityState()
	{
	}
	
	// Randomly generate phone number
	public static void phone()
	{
	}
	
	// Use first and last name to create email with cid "@pitt.edu"
	public static void email()
	{
	}
}