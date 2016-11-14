import java.util.Scanner;
import java.util.Random;
import java.util.*;
import java.io.*;

public class Price_Generator
{
	private static final int DATA_COUNT = 100;
	private static PrintWriter output;
	private static String[] cities = {"PIT", "IND", "NYC", "MCO", "LAS", "JFK", "MIA", "HOU", "ATL", "DAL", "HON", "SEA", "DET", "CHI", "PHX", "CLT", "COL", "BOS", "DEN", "ANC"};	
	private static String[] airlineID = {"10001", "20001", "30001", "40001", "50001", "60001", "70001", "80001", "90001", "10010"};
	public String[] arrCity;
	public String[] depCity;
	public int[][] prices;
	private int counter = 0;				// Counter for while loop
	public Price_Generator()
	{
		try
		{
			output = new PrintWriter("price_data.sql");
		}
		catch(IOException excep)
		{
			System.out.println("Did not create file.");
		}	
		
		arrCity = new String[DATA_COUNT];
		depCity = new String[DATA_COUNT];
		prices = new int[DATA_COUNT][2];
	
		// Data Titles for Easier Readability to User Looking at Insert Statements
		output.println("-- departure_city, arrival_city, airline_id, high_price, low_price --");
		
		Random numGen = new Random();
		int counter = 0;				// Counter for while loop
		
		while(counter < DATA_COUNT)
		{	
			// BEGIN
			output.print("INSERT INTO PRICE VALUES (");

			// DEPARTURE AND ARRIVAL CITY
			departure_arrival_city(numGen, counter);
			
			// AIRLINE ID
			airlineID(numGen);
			
			// HIGH AND LOW PRICE
			high_low_price(numGen);
			
			output.println(");");
			counter++;
		}
		output.close();
		System.out.println(counter + " Prices Created");
	}
	
	// Generate Departure and Arrival Cities
	private void departure_arrival_city(Random gen, int index)
	{
		int num1 = gen.nextInt(999) + 1;
		int num2;
		String depart_code = cities[num1 % 20];
		String arrival_code;
		do {
			num2 = gen.nextInt(999) + 1;
		}while((cities[num1 % 20].equals(cities[num2 % 20])) || (checkDup(num1, num2, index)));

		arrival_code = cities[num2 % 20];
		
		arrCity[index] = arrival_code;
		depCity[index] = depart_code;
		
		output.printf("'%s', ", depart_code);
		output.printf("'%s', ", arrival_code);
	}
	
	// Check Duplicate Flights
	private boolean checkDup(int num1, int num2, int index)
	{
		for(int i = 0; i < index; i++)
		{
			if(arrCity[i].equals(cities[num2%20]) && depCity[i].equals(cities[num1%20]))
			{
				return true;
			}
		}
		return false;
	}
	
	// Generate Airline Number
	private void airlineID(Random gen)
	{
		int num = (gen.nextInt(10));	
		output.printf("'%s', ", airlineID[num]);
	}
	
	// Year
	private void high_low_price(Random gen)
	{
		int highPrice, lowPrice;
		do{
		highPrice = gen.nextInt(151) + 150; //High Price Somewhere Between 150-300
		lowPrice = gen.nextInt(101) + 50; //Low Price Between 50-150
		}while(!(highPrice - lowPrice > 30));
		
		output.printf("'%d', ", highPrice);
		output.printf("'%d'", lowPrice);
		prices[counter][0] = lowPrice;
		prices[counter][1] = highPrice;
	}
}