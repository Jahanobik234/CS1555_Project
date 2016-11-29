import java.util.Scanner;
import java.util.Random;
import java.util.*;
import java.io.*;

public class Reservation_Generator
{
	private final int DATA_COUNT = 300;

	private PrintWriter output;
	
	public Reservation_Generator(String[] ccNums, String[] cids, String[] reservationPrices, int counter, String[][] tripEndpoints, String[] reservationStarts)
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
		int count = 0;				// Counter for while loop
		
		while(count < DATA_COUNT)
		{
			int custID = gen.nextInt(200);
			output.print("INSERT INTO RESERVATION VALUES (");
			
			// RERVATION NUMBER
			output.printf("'%05d', ", (count + 1));
			
			// CID
			output.printf("%s, ", cids[custID]);
			
			//Start City
			output.printf("'%s', ", tripEndpoints[count][0]);
			
			//End City
			output.printf("'%s', ", tripEndpoints[count][1]);
			
			// COST
			output.printf("'%s', ", reservationPrices[count]);
			
			// CREDIT CARD NUMBER
			output.printf("'%s', ", ccNums[custID]);
			
			// RESERVATION DATE
			output.printf("to_date('%s', 'MM-DD-YYYY'), ", reservationStarts[count]);
			
			// TICKETED
			if (numGen.nextInt(2) == 0)
				output.print("'Y');\n");
			else				
				output.print("'N');\n");

				
			count++;
		}
		output.close();
		System.out.println("Reservations Started");
	}
}