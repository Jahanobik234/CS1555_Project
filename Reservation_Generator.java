import java.util.Scanner;
import java.util.Random;
import java.util.*;
import java.io.*;

public class Reservation_Generator
{
	private final int DATA_COUNT = 300;

	private PrintWriter output;
	
	public Reservation_Generator(String[] ccNums, String[] cids, int[][] flightPrices)
	{
		Random gen = new Random();
		
		try
		{
			output = new PrintWriter("reservation_data.sql");
		}
		catch(IOException excep)
		{
			System.out.println("Did not create file.");
		}
		
		// Data Titles for Easier Readability to User Looking at Insert Statements
		output.println("-- reservation_number, cid, cost, credit_card_num, reservation_date, ticketed --");
				
		Random numGen = new Random();
		int counter = 0;				// Counter for while loop
		
		while(counter < DATA_COUNT)
		{
			int custID = gen.nextInt(200);
			output.print("INSERT INTO RESERVATION VALUES (");
			
			// RERVATION NUMBER
			output.printf("'%05d', ", (counter + 1));
			
			// CID
			output.printf("'%s', ", cids[custID]);
			
			// COST
			for(int i = 0; i < 300; i++) {
				for(int j = 0; j < 3; j++) {
					System.out.print(flightPrices[i][j]); }
				System.out.println(); }
			int sum = 0;
			for(int i = 0; i < 3; i++)
			{
				sum += flightPrices[counter][i];
			}
			output.printf("'%s', ", sum);
			
			// CREDIT CARD NUMBER
			output.printf("'%s, ', ", ccNums[custID]);
			
			// RESERVATION DATE
			output.print("to_date('<date>','MM-DD-YYYY'), ");
			
			// TICKETED
			output.print("'<Y/N>');\n");
			counter++;
		}
		output.close();
		System.out.println("Reservations Started");
	}
}