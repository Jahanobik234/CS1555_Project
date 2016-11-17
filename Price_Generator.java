import java.util.Scanner;
import java.util.Random;
import java.util.*;
import java.io.*;

public class Price_Generator
{
	private static final int DATA_COUNT = 560;
	private static PrintWriter output;
	private static String[] airlineID = {"00001", "00002", "00003", "00004", "00005", "00006", "00007", "00008", "00009", "00010"};
	public String[] arrCity;
	public String[] depCity;
	public int[][] prices = new int[DATA_COUNT][2];;
	private int counter = 0;				// Counter for while loop
	public Price_Generator(String[] dep, String[] arr, String[] flightNum)
	{
		try
		{
			output = new PrintWriter("price_data.sql");
		}
		catch(IOException excep)
		{
			System.out.println("Did not create file.");
		}	
	
		// Data Titles for Easier Readability to User Looking at Insert Statements
		output.println("-- departure_city, arrival_city, airline_id, high_price, low_price --");
		
		Random numGen = new Random();
		int counter = 0;				// Counter for while loop
		
		for(int i = 0; i < 10; i++)
		{
			for(int j = 0; j < 56; j++)
			{
			// BEGIN
			output.print("INSERT INTO PRICE VALUES (");

			// DEPARTURE AND ARRIVAL CITY
			output.printf("'%s', ", dep[counter]);
			output.printf("'%s', ", arr[counter]);
			
			// AIRLINE ID
			output.printf("'%s', ", airlineID[i]);
			
			// HIGH AND LOW PRICE
			int highPrice, lowPrice;
			do{
			highPrice = numGen.nextInt(151) + 150; //High Price Somewhere Between 150-300
			lowPrice = numGen.nextInt(101) + 50; //Low Price Between 50-150
			}while(!(highPrice - lowPrice > 30));
			
			output.printf("'%d', ", highPrice);
			output.printf("'%d'", lowPrice);
			prices[counter][0] = lowPrice;
			prices[counter][1] = highPrice;
			
			output.println(");");
			
			counter++;
			}
		}
		output.close();
		System.out.println(counter + " Prices Created");
	}

}