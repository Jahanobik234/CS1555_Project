import java.util.Scanner;
import java.util.Random;
import java.util.*;
import java.io.*;

public class Reservation_Detail_Generator
{
	private final int DATA_COUNT = 500;
	
	private PrintWriter output;
	private String[] dates = {"12-04-2016", "12-05-2016", "12-06-2016", "12-07-2016", "12-08-2016", "12-09-2016", "12-10-2016", "12-11-2016"};
	public String[][] tripEndpoints;
	public String[] reservationPrices;
	public String[] reservationStarts;
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
		
		tripEndpoints = new String[DATA_COUNT];
		reservationPrices = new String[DATA_COUNT];
		reservationStarts = new String[DATA_COUNT];
		Random numGen = new Random();
		int numLegs;						// Random Number
		int pick2;						// Random Number
		int customer;			//Customer Generator
		counter = 0;				//Counter
		
		for(int i = 0; i < 10; i++) //Create Some Reservations For Each Airline
		{
			for(int j = 0; j < 30; j++) //Create 30 Reservations For Each Flight
			{
				customer = numGen.nextInt(200);
				numLegs = numGen.nextInt(4) + 1; //Number of Legs On Trip
				String[] legDest = new String[1+numLegs]; //To Keep Track Of Where We Are Going
				int legDestIndex = 0; //Index For Our Destinations
				int leg = 1; //Index for Our Leg Number
				pick2 = (numGen.nextInt(999) % 56) + 1; //To Get One of 56 Flights Per Airline
				output.print("INSERT INTO RESERVATION_DETAIL("); //First Leg
				output.printf("'%05d', ", (i+1));
				output.print("'%s', " flightNum[(pick2 + (i * 56))); //Get That Flight
				output.print("to_date('<%s>'), 'MM-DD-YYYY'), ", dates[pick2%2]); //First Flight Takes Off Sun, Mon
				output.printf("%d ", leg++);
				output.print(");\n");
				legDest[legDestIndex++] = depCity[pick2];
				tripEndpoints[counter][0] = depCity[pick2];
				reservationStarts[counter] = dates[pick2%2]
				legDest[legDestIndex++] = arrCity[pick2];
				if(numLegs == 1)
				{
					tripEndpoints[counter][1] = arrCity[pick2];
					if(dTime[(pick2 + (i * 56))].compareTo(aTime[(pick2 + (i * 56))] < 0) //Same Day, High Price
						reservationPrices[counter] = prices[(pick2 + (i * 56))][1];
					else //Different Day, Low Price
						reservationPrices[counter] = prices[(pick2 + (i * 56))][0];
				}
				counter++;
				while(leg <= numLegs) //Additional Legs
				{
					output.print("INSERT INTO RESERVATION_DETAIL(");
					output.printf("'%05d', ", (i+1));
					pick2 = (numGen.nextInt(999) % 56) + 1; //To Get One of 56 Flights Per Airline 
					
					if(leg == numLegs)
					{
						while(alreadyVisited(arrCity[(pick2 + (i * 56))], legDest, 1, legDest.length))
						{
							pick2 = (numGen.nextInt(999) % 56) + 1; //To Get One of 56 Flights Per Airline 
						}
					}
					
					else
					{
						while(alreadyVisited(arrCity[(pick2 + (i * 56))], legDest, 0, legDest.length))
						{
							pick2 = (numGen.nextInt(999) % 56) + 1; //To Get One of 56 Flights Per Airline 
						}
					}
					
					output.print("'%s', " flightNum[(pick2 + (i * 56))); //Get That Flight
					output.print("to_date('<%s>'), 'MM-DD-YYYY'), ", dates[(pick2%2) + leg]); //First Flight Takes Off Sun, Mon
					output.printf("%d ", ++leg);			
					output.print(");\n");
					legDest[legDestIndex++] = arrCity[pick2];
					//Pricing Issues
					if(leg == numLegs)
					{
						if(legDest[0].equals(legDest[numLegs + 1]) //Roundtrip
						{
							//Price Will Be Sum of Two Prices
							int priceIndex1, priceIndex2;
							for(int r = (0 + (56*i)); r < 56; r++) //Search For This Flight
							{
								if(depCity[r].equals(legDest[0]) && arrCity.equals(legDest[1])) //We Found Starting Leg
									priceIndex1 = r;
								else if(depCity[r].equals(legDest[numLegs-1]) && arrCity.equals(legDest[numLegs])
									priceIndex2 = r;
							}
							
							//Since Flights Are On Separate Days, Add 2 Low Prices
							reservationPrices[counter] = String.toString(Integer.parseInt(prices[priceIndex1][0]) + Integer.parseInt(prices[priceIndex2]));
						}
						
						else //Not Roundtrip, Find Direct and Give Price
						{
							int priceIndex;
							for(int r = (0 + (56*i)); r < 56; r++) //Search For This Flight
							{
								if(depCity[r].equals(legDest[0]) && arrCity.equals(legDest[numLegs])) //We Found Starting Leg
									priceIndex1 = r;
							}
							
							//Connections Happen On Different Days, Low Price
							reservationPrices[counter] = String.toString(Integer.parseInt(prices[priceIndex][0]));
						}
					}
					counter++;
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
				return true;
		}
		return false;
	}
}