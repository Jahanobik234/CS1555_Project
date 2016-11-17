import java.util.Scanner;
import java.util.Random;
import java.util.*;
import java.io.*;

public class Flight_Generator
{
	public Plane_Generator pg = new Plane_Generator();
	private final int DATA_COUNT = 560; //MAX Number of Flights
	private String[] cities = {"PIT", "IND", "SAN", "MCO", "LAS", "JFK", "MIA", "BWI"}; 
	private String[] days = {"S", "M", "T", "W", "T", "F", "S", "-"};
	public String[] dep = new String[DATA_COUNT];
	public String[] arr = new String[DATA_COUNT];
	public String[] flightNumbers = new String[DATA_COUNT];
	public String[] depTime = new String[DATA_COUNT];
	public String[] arrTime = new String[DATA_COUNT];
	private PrintWriter output;
	
	public Flight_Generator()
	{
		try
		{
			output = new PrintWriter("flight_data.sql");
		}
		catch(IOException excep)
		{
			System.out.println("Did not create file.");
		}
		
		// Data Titles for Easier Readability to User Looking at Insert Statements
		output.println("-- flight_number, airline_id, plane_type, departure_city, arrival_city, departure_time, arrival_time, weekly_schedule --");
		
		Random numGen = new Random();
		int counter = 0;				// User Counter
		int cityNum = 0;
		int flightNum = 0;
		for(int i = 1; i <= 10; i++) //For Each Airline
		{
			for(int j = 0; j < 8; j++)//For Each Starting City
			{
				String departureCity = cities[j];
				for(int m = 0; m < 8; m++) //Go Through Each of the Other Destinations
				{
					if(!(departureCity.equals(cities[m])))
					{
						output.print("INSERT INTO FLIGHT VALUES (");
						
						// NUMBER
						String number = String.format("%03d", counter + 1);
						flightNumbers[counter] = number;
						output.printf("'%s', ", number);
						
						// AIRLINE ID
						output.printf("'%05d', ", (i));
						
						// PLANE TYPE
						output.printf("'%s', ", pg.planeType[numGen.nextInt(30)]);
						
						// DEPARTURE CITY			
						dep[flightNum] = departureCity;
						output.printf("'%s', ", departureCity);
						
						// ARRIVAL CITY
						arr[flightNum] = cities[m];
						output.printf("'%s', ", arr[flightNum]);
						
						// DEPARTURE TIME
						int h1 = numGen.nextInt(24);
						int m1 = numGen.nextInt(4) * 15;
						String hour1 = String.format("%02d", h1);
						String minute1 = String.format("%02d", m1);
						String dTime = hour1.concat(minute1);
						
						output.printf("'%s', ", dTime);
						depTime[counter] = dTime;
						
						// ARRIVAL TIME
						int h2;
						h2 = (numGen.nextInt(12) + 1);
						
						int m2 = numGen.nextInt(4) * 15;
						String hour2 = String.format("%02d", ((h1+h2) % 24));
						String minute2 = String.format("%02d", m2);
						String aTime = hour2.concat(minute2);
						
						output.printf("'%s', ", aTime);
						arrTime[counter] = aTime;
						
						// WEEKLY SCHEDULE
						output.print("'SMTWTFS");
						output.print("'");
						output.print(");\n");
						counter++;
						flightNum++;
					}
				}
			}
		}
			
		output.close();
		System.out.println(counter + " Flights Created");
	}
}