import java.util.Scanner;
import java.util.Random;
import java.util.*;
import java.io.*;

public class Reservation_Detail_Generator
{
	private static final int DATA_COUNT = 300;
	
	private static PrintWriter output;
	//private static PrintWriter output2;
	
	public Reservation_Detail_Generator(String[] depCity, String[] arrCity, String[] flightNum)//, String[] cids, String[] ccNums)
	{
		try
		{
			output = new PrintWriter("reservation_detail_data.sql");
			//output2 = new PrintWriter("reservation_data.sql");
		}
		catch(IOException excep)
		{
			System.out.println("Did not create file.");
		}
		
		Random numGen = new Random();
		int numLegs;						// Random Number
		int pick2;						// Random Number
		int customer;			//Customer Generator
		int counter = 0;				// User Counter
		
		while(counter <= DATA_COUNT)
		{
			customer = numGen.nextInt(200);
			numLegs = numGen.nextInt(3) + 1; //Number of Legs On Trip
			String[] legDest = new String[1+numLegs]; //To Keep Track Of Where We Are Going
			int legDestIndex = 0; //Index For Our Destinations
			int leg = 0; //Index for Our Leg Number
			
			pick2 = (numGen.nextInt(999) % 100);
			output.print("INSERT INTO RESERVATION_DETAIL("); //First Leg
			output.printf("'%05d', ", counter);
			output.printf("'%s', ", flightNum[pick2]);
			output.print("to_date('<date>', 'MM-DD-YYYY'), ");
			output.printf("%d ", ++leg); 
			output.print("<cost>");
			output.print(");\n");
			legDest[legDestIndex] = depCity[pick2];
			legDest[legDestIndex++] = arrCity[pick2];
			
			while(leg < numLegs) //Additional Legs
			{
				do {
					pick2 = (numGen.nextInt(999) % 100);
				}while((!depCity[pick2].equals(legDest[legDestIndex-1]) || (alreadyVisited(arrCity[pick2], legDest))));
				output.print("INSERT INTO RESERVATION_DETAIL(");
				output.printf("'%05d', ", counter);
				output.printf("'%s', ", flightNum[pick2]);
				output.print("to_date('<date>', 'MM-DD-YYYY'), ");
				output.printf("%d ", ++leg);
				output.print("<cost>");				
				output.print(");\n");
				legDest[legDestIndex++] = arrCity[pick2];
				
			}
			
			// output2.print("INSERT INTO RESERVATION(");
// 			output2.printf("'%05d', '%s', <cost>, '%s', '<date>', <ticketed>", counter, cids[customer], ccNums[customer]);
// 			output2.print(");\n");
			counter++;
		}
		output.close();
		//output2.close();
		System.out.println("Reservation Details Created");
	}
	
	private static boolean alreadyVisited(String city, String[] destinations)
	{
		for(int i = 0; i < destinations.length; i++)
		{
			if(city.equals(destinations[i]))
				return true;
		}
		
		return false;
	}

}