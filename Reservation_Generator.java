import java.util.Scanner;
import java.util.Random;
import java.util.*;
import java.io.*;

public class Reservation_Generator
{
	private static final int DATA_COUNT = 300;

	private static PrintWriter output1;
	private static PrintWriter output2;
	
	public Reservation_Generator()
	{
		Random gen = new Random();
		
		try
		{
			output1 = new PrintWriter("reservation_data.sql");
			output2 = new PrintWriter("reservation_detail.sql");
		}
		catch(IOException excep)
		{
			System.out.println("Did not create file.");
		}
		
		Random numGen = new Random();
		int counter = 0;				// Counter for while loop
		
		while(counter < DATA_COUNT)
		{
			output1.print("INSERT INTO RESERVATION VALUES (");
			output2.print("INSERT INTO RESERVATION_DETAIL VALUES (");
			
			// RERVATION NUMBER
			output1.printf("'%05d', \n", (counter + 1));
			output2.printf("'%05d', \n", (counter + 1));
			counter++;
		}
		output1.close();
		output2.close();
		System.out.println("Reservations Started");
	}
}