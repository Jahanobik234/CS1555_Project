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
		Price_Generator pg  = new Price_Generator(fg.dep, fg.arr, fg.flightNum);
		Customer_Generator cg = new Customer_Generator();
		Reservation_Detail_Generator rg = new Reservation_Detail_Generator(fg.dep, fg.arr, fg.flightNum, fg.depTime, fg.arrTime, fg.schedule, pg.prices);
		Reservation_Generator rg1 = new Reservation_Generator(cg.ccNumbers, cg.cids, rg.flightPrices);
		
		System.out.println("Most Data Generated");
	}
}