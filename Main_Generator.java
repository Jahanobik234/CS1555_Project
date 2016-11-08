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
		Reservation_Generator rg = new Reservation_Generator();
		
		System.out.println("Most Data Generated");
	}
}