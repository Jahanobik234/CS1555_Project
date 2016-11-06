/*
* The main generator activates the other data generators. Once the program has been run,
|  all data should be contained in .sql files.
*/
import java.util.Scanner;
import java.util.Random;
import java.util.*;
import java.io.*;

public class Main_Generator
{
	public static void main(String[] args)
	{
		Flight_Generator fg = new Flight_Generator();
		Price_Generator pg  = new Price_Generator();
		Customer_Generator cg = new Customer_Generator();
		// Cannot function enough right now to figure out the best way to implement the last
		// two generators. Either they need to be created in a single generator, or not.
		
		//Reservation_Detail_Generator rdg = new Reservation_Detail_Generator();
		//Reservation_Generator rg = new Reservation_Generator(cg.ccNumbers, cg.cids);
		
		System.out.println("All Data Generated");
	}
}