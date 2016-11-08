import java.util.Scanner;
import java.util.Random;
import java.util.*;
import java.io.*;

public class Price_Generator
{
	private static PrintWriter output;
	private static String[] cities = {"PIT", "IND", "NYC", "MCO", "LAS", "JFK", "MIA", "HOU", "ATL", "DAL", "HON", "SEA", "DET", "CHI", "PHX", "CLT", "COL", "BOS", "DEN", "ANC"};	
	private static String[] airlineID = {"10001", "20001", "30001", "40001", "50001", "60001", "70001", "80001", "90001", "10010"};

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
		
		Random numGen = new Random();
		int counter = 0;				// Counter for while loop
		
		while(counter < 100)
		{	
			// BEGIN
			output.print("INSERT INTO PRICE VALUES (");

			// DEPARTURE AND ARRIVAL CITY
			departure_arrival_city(numGen);
			
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
	private static void departure_arrival_city(Random gen)
	{
		int num1 = gen.nextInt(999) + 1;
		int num2;
		String depart_code = cities[num1 % 20];
		String arrival_code;
		do {
			num2 = gen.nextInt(999) + 1;
		}while(cities[num1 % 20].equals(cities[num2 % 20]));
		
		arrival_code = cities[num2 % 20];
		
		output.printf("'%s', ", depart_code);
		output.printf("'%s', ", arrival_code);
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
	}
}