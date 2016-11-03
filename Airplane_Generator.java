import java.util.Scanner;
import java.util.Random;
import java.util.*;
import java.io.*;

public class Airplane_Generator
{
	private static PrintWriter output;
	private static String[] manuf = {"Boeing", "Airbus", "Embraer", "Bombardier", "Learjet"};	
	private static char[] alpha = {'B', 'A', 'E', 'M', 'L'};
	private static String[] airlineID = {"10001", "20001", "30001", "40001", "50001", "60001", "70001", "80001", "90001", "10010"};
	
	public static void main(String[] args)
	{
		try
		{
			output = new PrintWriter("airplane_data.sql");
		}
		catch(IOException excep)
		{
			System.out.println("Did not create file.");
		}
		
		Random numGen = new Random();
		int counter = 0;				// Counter for while loop
		
		while(counter < 30)
		{
			int tmp;
			
			// BEGIN
			output.print("INSERT INTO AIRPLANE VALUES (");

			// TYPE
			tmp = type(numGen);
			
			// MANUFACTURER
			manufacturer(tmp);
			
			// CAPACITY
			capacity(numGen);
			
			// LAST SERVICE DATE
			tmp = service_date(numGen);
			
			// YEAR
			yearCreated(numGen, tmp);
					
			// OWNER ID
			owner(numGen);
			
			output.println(");");
			counter ++;
		}
		output.close();
		System.out.println("Airplanes Created");
	}
	
	// Generate Plane Type '<char><3-digit num>'
	public static int type(Random gen)
	{
		int num = gen.nextInt(999) + 1;
		char let = alpha[num % 5];
		
		output.printf("'%c%03d', ", let, num);
		return num % 5;
	}
	
	// Generate Manufacturer
	public static void manufacturer(int num)
	{
		String man = manuf[num];
		
		output.printf("'%s', ", man);
	}
	
	// Generate Plane Capacity
	public static void capacity(Random gen)
	{
		int num = (gen.nextInt(40) + 10) * 5;				// 50 - 245
		output.printf("'%d', ", num);
	}
	
	// Last Service Date
	public static int service_date(Random gen)
	{
		int mon = gen.nextInt(12) + 1;
		int day = gen.nextInt(30) + 1;
		int year = gen.nextInt(16) + 2000;
		
		output.printf("to_date('%02d/%02d/%04d', 'MM/DD/YYYY'), ", mon, day, year);
		return year;
	}
	
	// Year
	public static void yearCreated(Random gen, int sYear)
	{
		int cDate = sYear - (gen.nextInt(10) + 1);
		output.printf("%d, ", cDate);
	}
	
	// Owner ID
	public static void owner(Random gen)
	{
		int num = gen.nextInt(10);
		output.printf("'%s'", airlineID[num]);
	}
}