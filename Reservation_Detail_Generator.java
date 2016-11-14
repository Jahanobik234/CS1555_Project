import java.util.Scanner;
import java.util.Random;
import java.util.*;
import java.io.*;

public class Reservation_Detail_Generator
{
	private final int DATA_COUNT = 300;
	
	private PrintWriter output;
	public int[][] flightPrices;
	private String[] dates = {"12-04-2016", "12-05-2016", "12-06-2016", "12-07-2016", "12-08-2016", "12-09-2016", "12-10-2016", "12-11-2016"};
	public Reservation_Detail_Generator(String[] depCity, String[] arrCity, String[] flightNum, String[] dTime, String[] aTime, String[] schedule, int[][] prices)
	{
		try
		{
			output = new PrintWriter("reservation_detail_data.sql");
		}
		catch(IOException excep)
		{
			System.out.println("Did not create file.");
		}
		
		// Data Titles for Easier Readability to User Looking at Insert Statements
		output.println("-- reservation_number, flight_number, flight_date, leg --");
		
		flightPrices = new int[DATA_COUNT][3];
		int[][] flightDays = new int[DATA_COUNT][3];
		int[][] tripNums = new int[DATA_COUNT][3];
		Random numGen = new Random();
		int numLegs;						// Random Number
		int pick2;						// Random Number
		int customer;			//Customer Generator
		int counter = 1;				// User Counter
		
		while(counter < DATA_COUNT)
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
			tripNums[counter][0] = pick2;
			char[] sched = schedule[pick2].toCharArray();
			for(int i = 0; i < 7; i++)
			{
				if(!(Character.toString(sched[i]).equals("-")))
				{
					output.printf("to_date('<%s>'), 'MM-DD-YYYY'), ", dates[i]);
					flightDays[counter][0] = i;
					if(Integer.parseInt(dTime[pick2]) < Integer.parseInt(aTime[pick2])) //Flight On Same Day
						flightPrices[counter][0] = prices[pick2][1]; //High Value
					else
						flightPrices[counter][0] = prices[pick2][0]; //Low Value
						
					System.out.println(flightPrices[counter][0]);
					break;
				}
			}
			
			output.printf("%d ", ++leg);
			output.print(");\n");
			legDest[legDestIndex++] = depCity[pick2];
			legDest[legDestIndex++] = arrCity[pick2];
			
			while(leg < numLegs) //Additional Legs
			{
				do {
					pick2 = (numGen.nextInt(999) % 100);
				}while((depCity[pick2].equals(legDest[legDestIndex-1]) || (alreadyVisited(arrCity[pick2], legDest))));
				
				output.print("INSERT INTO RESERVATION_DETAIL(");
				output.printf("'%05d', ", counter);
				output.printf("'%s', ", flightNum[pick2]);
				tripNums[counter][leg] = pick2;
				for(int i = flightDays[counter][leg-1]; i < 7; i++)
				{
					if(!(Character.toString(sched[i]).equals("-")))
					{
						if((i != flightDays[counter][leg-1]) || ((i == flightDays[counter][leg-1]) && (Integer.parseInt(dTime[pick2]) > Integer.parseInt(aTime[tripNums[counter][leg-1]]))))
						{
							output.printf("to_date('<%s>'), 'MM-DD-YYYY'), ", dates[i]);
							if(Integer.parseInt(dTime[pick2]) < Integer.parseInt(aTime[pick2]))
							{
								if(flightDays[leg].equals(flightDays[leg-1]) && depCity[0].equals(arrCity[numLegs-1])) //Same-Day Round Trip
									flightPrices[counter][leg] = prices[pick2][1]; //High Value
								else if(flightDays[leg].equals(flightDays[leg-1]) && !(depCity[0].equals(arrCity[numLegs-1]))) //Connection
									flightPrices[counter][leg] = prices[pick2][0]; //Low Value
							}
							else
								flightPrices[counter][0] = prices[pick2][0]; //Low Value
							break;
						}
					}
				}
				output.printf("%d ", ++leg);			
				output.print(");\n");
				legDest[legDestIndex++] = arrCity[pick2];
			}
			counter++;
		}
		output.close();
		System.out.println("Reservation Details Created");
	}
	
	private boolean alreadyVisited(String city, String[] destinations)
	{
		for(int i = 0; i < destinations.length; i++)
		{
			if(city.equals(destinations[i]))
				return true;
		}
		return false;
	}
}