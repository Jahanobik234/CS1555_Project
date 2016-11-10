import java.util.Scanner;
import java.util.Random;
import java.util.*;
import java.io.*;

public class Plane_Generator
{
	private final int DATA_COUNT = 30;
	private String[] manuf = {"Boeing", "Airbus", "Embraer", "Bombardier", "Learjet"};	
	private String[] alpha = {"B", "A", "E", "M", "L"};
	private String[] airlineID = {"10001", "20001", "30001", "40001", "50001", "60001", "70001", "80001", "90001", "10010"};
	
	public String[] planeType = new String[DATA_COUNT];
	private PrintWriter output;
	
	public Plane_Generator()
	{
		try
		{
			output = new PrintWriter("plane_data.sql");
		}
		catch(IOException excep)
		{
			System.out.println("Did not create file.");
		}
		
		// Data Titles for Easier Readability to User Looking at Insert Statements
		output.println("-- plane_type, manufacturer, plane_capacity, last_service_date, year, owner_id -- ");
		
		Random numGen = new Random();
		int counter = 0;				// Counter for while loop
		
		while(counter < DATA_COUNT)
		{
			int tmp;
			
			// BEGIN
			output.print("INSERT INTO PLANE VALUES (");

			// TYPE (RETURNS LETTER CODE FOR MANUFACTURER)
			tmp = type(numGen, counter);
			
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
		System.out.println("Planes Created");
	}
	
	// Generate Plane Type '<char><3-digit num>'
	public int type(Random gen, int index)
	{
		int num;
		String let;
		String t;
		
		do
		{
			num = gen.nextInt(999) + 1;
			let = alpha[num % 5];
			t = let.concat(String.format("%03d", num));
		} while (duplicate(t, index));
		
		planeType[index] = t;
		output.printf("'%s', ", t);
		return num % 5;
	}
	
	// Check for plane type duplicates
	private boolean duplicate(String test, int numValues)
	{
		for(int i = 0; i < numValues; i++)
		{
			String used = planeType[i];
			if(test.equals(used))
			{
				return true;
			}
		}
		return false;
	}
	
	// Generate Manufacturer
	public void manufacturer(int num)
	{
		String man = manuf[num];
		
		output.printf("'%s', ", man);
	}
	
	// Generate Plane Capacity
	public void capacity(Random gen)
	{
		int num = (gen.nextInt(40) + 10) * 5;				// 50 - 245
		output.printf("'%d', ", num);
	}
	
	// Last Service Date
	public int service_date(Random gen)
	{
		int mon = gen.nextInt(12) + 1;
		int day = gen.nextInt(30) + 1;
		int year = gen.nextInt(16) + 2000;
		
		output.printf("to_date('%02d/%02d/%04d', 'MM/DD/YYYY'), ", mon, day, year);
		return year;
	}
	
	// Year
	public void yearCreated(Random gen, int sYear)
	{
		int cDate = sYear - (gen.nextInt(10) + 1);
		output.printf("%d, ", cDate);
	}
	
	// Owner ID
	public void owner(Random gen)
	{
		int num = gen.nextInt(10);
		output.printf("'%s'", airlineID[num]);
	}
}