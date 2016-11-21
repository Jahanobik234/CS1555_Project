//Jonathan Hanobik and Seth Stayer
//CS1555 - Fall 2016, Term Project
//Administrator/User Interface

public class PittToursInterface
{
	public static void main(String[] args)
	{
		System.out.println("Welcome to PittTours by Stayer/Hanobik!");
		Scanner reader = new Scanner(System.in);
		System.out.println("Please select either Administrator or User By Entering Corresponding Number:");
		System.out.printf("\t%-20s\n\t%-20s", "(1) Administrator", ")2) User");
		String userSelection = reader.nextLine();
		
		while(!userSelection.equals("1") || !userSelection.equals("2"))
		{
				System.out.println("Invalid Input! Please Try Again!");
				System.out.println("Please select either Administrator or User By Entering Corresponding Number:");
				System.out.printf("\t%-20s\n\t%-20s", "(1) Administrator", ")2) User");
				userSelection = reader.nextLine();
		}
		
		if(userSelection.equals("1"))
		{
			administratorInterface();
		}
		
		else
		{
			userInterface();
		}
	}
	
	public static void administratorInterface() //Interface Methods/Functions for Administrator
	{
	
	}
	
	public static void userInterface() //Interface Methods/Functions for User
	{
	
	}
}