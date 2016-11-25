import java.util.Scanner;
import java.util.Random;
import java.util.*;
import java.io.*;

public class Reservation_Detail_Generator
{
	private final int DATA_COUNT = 300;
	
	private PrintWriter output;
	private String[] dates = {"12-04-2016", "12-05-2016", "12-06-2016", "12-07-2016", "12-08-2016", "12-09-2016", "12-10-2016", "12-11-2016"};
	public String[][] tripEndpoints = new String[DATA_COUNT][2];
	public String[] reservationPrices = new String[DATA_COUNT];
	public String[] reservationStarts = new String[DATA_COUNT];
	public int counter;
	public Reservation_Detail_Generator(String[] depCity, String[] arrCity, String[] flightNum, String[] dTime, String[] aTime, int[][] prices)
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
		Random numGen = new Random();
		int numLegs;						// Random Number
		int pick2;						// Random Number
		int customer;			//Customer Generator
		counter = 1;				//Counter
		
		for(int i = 0; i < 10; i++) //Create Some Reservations For Each Airline
		{
			for(int j = 0; j < 30; j++) //Create 30 Reservations For Each Flight
			{
				customer = numGen.nextInt(200);
				numLegs = numGen.nextInt(4) + 1; //Number of Legs On Trip
				String[] legDest = new String[1+numLegs]; //To Keep Track Of Where We Are Going
				int legDestIndex = 0; //Index For Our Destinations
				int leg = 0; //Index for Our Leg Number
				pick2 = (numGen.nextInt(999) % 56); //To Get One of 56 Flights Per Airline
				output.print("INSERT INTO RESERVATION_DETAIL VALUES("); //First Leg
				output.printf("'%05d', ", ((i*30) + j)+1);
				output.printf("'%s', ", flightNum[(pick2 + (i * 56))]); //Get That Flight
				if(numLegs == 1)
				{
					output.printf("to_date('%s', 'MM-DD-YYYY'), ", dates[pick2%7]); //First Flight Takes Off Anyday
					reservationStarts[(i*30) + j] = dates[pick2%7];
				}
				else
				{
					output.printf("to_date('%s', 'MM-DD-YYYY'), ", dates[pick2%2]); //First Flight Takes Off Sun, Mon
					reservationStarts[(i*30) + j] = dates[pick2%2];
				}
				output.printf("%d", (leg++));
				output.print(");\n");
				legDest[legDestIndex++] = depCity[pick2];
				tripEndpoints[(i*30) + j][0] = depCity[pick2];
				legDest[legDestIndex++] = arrCity[pick2];
				if(numLegs == 1)
				{
					tripEndpoints[(i*30) + j][1] = arrCity[pick2];
					if((dTime[(pick2 + (i * 56))].compareTo(aTime[(pick2 + (i * 56))]) < 0)) //Same Day, High Price
						reservationPrices[(i*30) + j] = Integer.toString(prices[(pick2 + (i * 56))][1]);
					else //Different Day, Low Price
						reservationPrices[(i*30) + j] = Integer.toString(prices[(pick2 + (i * 56))][0]);
				}
				
				while(leg < numLegs) //Additional Legs
				{
					output.print("INSERT INTO RESERVATION_DETAIL VALUES(");
					output.printf("'%05d', ", ((i*30) + j)+1);
					pick2 = (numGen.nextInt(999) % 56); //To Get One of 56 Flights Per Airline 
					
					if(leg == numLegs-1)
					{
						while(!alreadyVisited(arrCity[(pick2 + (i * 56))], legDest, 1, legDestIndex-1) && !(legDest[legDestIndex-1].equals(depCity[(pick2 + (i * 56))])))
						{
							pick2 = (numGen.nextInt(999) % 56); //To Get One of 56 Flights Per Airline 
						}
					}
					
					else
					{
						while(!alreadyVisited(arrCity[(pick2 + (i * 56))], legDest, 0, legDestIndex-1)  && !(legDest[legDestIndex-1].equals(depCity[(pick2 + (i * 56))])))
						{
							pick2 = (numGen.nextInt(999) % 56); //To Get One of 56 Flights Per Airline 
						}
					}
					
					output.printf("'%s', ", flightNum[(pick2 + (i * 56))]); //Get That Flight
					output.printf("to_date('%s', 'MM-DD-YYYY'), ", dates[(pick2%2) + leg]); //First Flight Takes Off Tues-Sat
					if(leg == numLegs-1)
						tripEndpoints[(i*30) + j][1] = arrCity[(pick2 + (i * 56))];
					output.printf("%d", leg++);
					output.print(");\n");
					legDest[legDestIndex++] = arrCity[(pick2 + (i * 56))];
					//Pricing Issues
					if(leg == numLegs)
					{
						if(legDest[0].equals(legDest[legDestIndex-1])) //Roundtrip
						{
							//Price Will Be Sum of Two Prices
							int priceIndex1 = 0, priceIndex2 = 0;
							priceIndex1 = getPriceIndex(legDest[0], legDest[numLegs/2], i, depCity, arrCity);
							priceIndex2 = getPriceIndex(legDest[numLegs/2], legDest[legDestIndex-1], i, depCity, arrCity);
							
							//Since Flights Are On Separate Days, Add 2 Low Prices
							reservationPrices[(i*30) + j] = Integer.toString((prices[priceIndex1][0]) + prices[priceIndex2][0]);
						}
						
						else //Not Roundtrip, Find Direct and Give Price
						{
							int priceIndex = 0;
							priceIndex = getPriceIndex(legDest[0], legDest[legDestIndex-1], i, depCity, arrCity);
							
							//Connections Happen On Different Days, Low Price
							reservationPrices[(i*30) + j] = Integer.toString(prices[priceIndex][0]);
						}
					}
				}
			}
		}
		output.close();
		System.out.println("Reservation Details Created");
	}
	
	private boolean alreadyVisited(String city, String[] destinations, int startIndex, int endIndex)
	{
		for(int i = startIndex; i < endIndex; i++)
		{
			if(city.equals(destinations[i])) 
				return false;
		}
		return false;
	}
	
	private int getPriceIndex(String startCity, String endCity, int airline, String[] depCity, String[] arrCity)
	{
		int startIndex = airline*56;
		int returnIndex = 0;
		for(int i = startIndex; i < (airline+1)*56; i++)
		{
			if(startCity.equals(depCity[i]) && endCity.equals(arrCity[i]))
			{
				returnIndex = i;
				break;
			}
		}
		
		return returnIndex;
	}
}