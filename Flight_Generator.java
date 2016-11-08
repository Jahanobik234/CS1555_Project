import java.util.Scanner;
import java.util.Random;
import java.util.*;
import java.io.*;

public class Flight_Generator
{
	public static Plane_Generator pg = new Plane_Generator();
	private static final int DATA_COUNT = 100;
	private static String[] cities = {"PIT", "IND", "NYC", "MCO", "LAS", "JFK", "MIA", "HOU", "ATL", "DAL", "HON", "SEA", "DET", "CHI", "PHX", "CLT", "COL", "BOS", "DEN", "ANC"};
	private static String[] days = {"S", "M", "T", "W", "T", "F", "S", "-"};
	public String[] dep = new String[DATA_COUNT];
	public String[] arr = new String[DATA_COUNT];
	public String[] flightNum = new String[DATA_COUNT];
	private static PrintWriter output;
	
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
		
		Random numGen = new Random();
		int counter = 0;				// User Counter
		
		while(counter < DATA_COUNT)
		{
			output.print("INSERT INTO FLIGHT VALUES (");
			
			// NUMBER
			String number = String.format("%03d", counter + 1);
			flightNum[counter] = number;
			output.printf("'%s', ", number);
			
			// AIRLINE ID
			output.printf("'%05d', ", (numGen.nextInt(10) + 1));
			
			// PLANE TYPE
			output.printf("'%s', ", pg.planeType[numGen.nextInt(30)]);
			
			// DEPARTURE CITY
			int depCity = numGen.nextInt(cities.length);			
			dep[counter] = cities[depCity];
			output.printf("'%s', ", dep[counter]);
			
			// ARRIVAL CITY
			int arrCity = numGen.nextInt(cities.length);
			while(depCity == arrCity) {arrCity = numGen.nextInt(cities.length);}
			arr[counter] = cities[arrCity];
			output.printf("'%s', ", arr[counter]);
			
			// DEPARTURE TIME
			int h1 = numGen.nextInt(24) + 1;
			int m1 = numGen.nextInt(4) * 15;
			String hour1 = String.format("%02d", h1);
			String minute1 = String.format("%02d", m1);
			String dTime = hour1.concat(minute1);
			
			output.printf("'%s', ", dTime);
			
			// ARRIVAL TIME
			int h2 = (numGen.nextInt(24) + 1 + h1) % 24;
			int m2 = numGen.nextInt(4) * 15;
			String hour2 = String.format("%02d", h2);
			String minute2 = String.format("%02d", m2);
			dTime = hour2.concat(minute2);
			
			output.printf("'%s', ", dTime);
			
			// WEEKLY SCHEDULE
			output.print("'");
			for(int i = 0; i < 7; i++)
			{
				output.printf("%s", getDay(i, numGen.nextInt(2)));
			}
			output.print("'");
			
			output.print(");\n");
			counter ++;
		}
		output.close();
		System.out.println(counter + " Flights Created");
	}
	
	private String getDay(int i, int j)
	{
		if(j == 1) {return days[i];}
		else {return days[7];}
	}
}