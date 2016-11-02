import java.util.Scanner;
import java.util.Random;
import java.util.*;
import java.io.*;

public class Data_Generator
{
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
			ccDate((pick2 % 12), (pick1 - 1));			// Month, Year
			
			// STREET NAME
			streetName(pick2 % 10 + 11);
			
			// CITY AND STATE
			cityState(pick1 % 20);
			
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