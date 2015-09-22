package ese.gnomemsr2009;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;


public class IOFormatter 
{
	private String dbN, mysqlUserName, mysqlPass;
	private String product, startDate, endDate, directoryPath;
	private Scanner user_input = new Scanner( System.in );
	
	
	public IOFormatter()
	{
		dbN = "";
		mysqlUserName = "";
		mysqlPass = "";
		product = "";
		startDate = "";
		endDate = "";
		directoryPath = "";
	}
	
	public String getDBN()
	{
		return dbN;
	}
	
	public String getMysqlUserName()
	{
		return mysqlUserName;
	}
	
	public String getMysqlPass()
	{
		return mysqlPass;
	}
	
	public String getProduct()
	{
		return product;
	}
	
	public String getStartDate()
	{
		return startDate;
	}
	
	public String getEndDate()
	{
		return endDate;
	}
	
	public String getDirectoryPath()
	{
		return directoryPath;
	}
	
	
	/*
	 * Method Name: inputConString
	 * Input: void
	 * Output: void
	 * Function: 
	 * Takes user input for database name, user name and password and store it to the respective variable.
	 */
	public void inputConString()
	{
		System.out.print("Please Enter Database Name(gnome_msr2009 or github_msr2014): ");
		
		dbN = user_input.nextLine();
		
		System.out.print("Please Enter User Name: ");
		mysqlUserName = user_input.nextLine();
		
		System.out.print("Please Enter User Password: ");
		mysqlPass = user_input.nextLine();
	}
	
	
	/*
	 * Method Name: inputData
	 * Input: Void
	 * Output: Void
	 * Function: 
	 * Takes keyboard input from user and stores it to the respective variable.
	 * If no input for start date, default it to "0000-00-00"
	 * If no input for end date, default it to "9999-12-31"
	 */
	public void inputData()
	{
		while(product.trim().isEmpty())
		{
			System.out.print("Please Input Product Name:");
			product = user_input.nextLine();
		}
		
		System.out.print("Enter Start Date(e.g 2002-05-28):");
		startDate = user_input.nextLine();
		
		System.out.print("Enter End Date(e.g 2002-06-28):");
		endDate = user_input.nextLine();
		
		user_input.close();
		
		if(startDate.trim().isEmpty())
		{
			startDate = "0000-00-00";
		}
		if(endDate.trim().isEmpty())
		{
			endDate = "9999-12-31";
		}
	}
	
	public void batchInput()
	{
		System.out.print("Enter Directory of Product-Names.csv:");
		directoryPath = user_input.nextLine();
		
		user_input.close();
	}
	
	public int inputChoiceGnome()
	{
		int choice = 0;
		
		do
		{
			System.out.println("Available Services for Gnome Data Set");
			System.out.println("1. Generate Developer Communication Network (DCN) in PAJEK Format for a Given Set of Products (product-names.csv)");
			System.out.println("2. Generate Developer Activity Network (DAN) in PAJEK Format for a Given Set of Products (product-names.csv)");
			System.out.println("3. Generate the DCN and/or DAN Metrics for a Given Set of Product and PAJEK Files (product-names.csv)");
			System.out.println("4. Generate Bugs-By-Developer Matrix in CSV Format for a Given Set of Products (DCN) (product-names.csv)");
			System.out.println("5. Generate Devs-By-Devs Matrix in CSV Format for a Given Set of Products (DCN) (product-names.csv)");
			System.out.println("6. Generate Bugs-By-Developer Matrix in CSV Format for a Given Set of Products (DAN) (product-names.csv)");
			System.out.println("7. Generate Devs-By-Devs Matrix in CSV Format for a Given Set of Products (DAN) (product-names.csv)");
			System.out.println("8. Generate Project Data Summary in CSV Format for a Given Set of Products (product-names.csv, and PAJEK for DCN and DAN)");
			System.out.println("9. Generate Project Data Summary in CSV Format for a Given Set of Products With Socio-Tech-Congruence (product-names.csv, and PAJEK and dev-by-dev matrix for DCN and DAN)");
			System.out.println("10. Generate Bug-Details in CSV Format for a Given Set of Products (product-names.csv)");
			System.out.println("11. Generate Dev-Details in CSV Format for a Given Set of Products (product-names.csv)");
			System.out.println("12. Generate Descriptive Statistics and Correlations for a Given Set of Products (product-names.csv, and PAJEK for DCN and DAN)");
			System.out.println("13. Generate 1, 2, 3, 8, 10, 11 for a Given Set of Products (product-names.csv)");
			System.out.println("14. Generate Descriptive Statistics, Correlations and Regression Output for a Given Set of Products (product-names.csv, dependent.csv, independent.csv and model-type.csv)");
			System.out.println("15. Generate Correlations, EigenValues, and Perform Factor Analysis (product-names.csv, variables.csv, parameters.csv and model-type.csv)");
			System.out.println("16. New Menu");
			System.out.print  ("Please Enter Your Choice (1 to 11): ");
			
			try
			{
				choice = user_input.nextInt();
			} catch(InputMismatchException e)
			{
				System.out.println("Error! Only Integers Are Accepted.");
			}
			
			System.out.println("");
			
			user_input.nextLine();
			return choice;
		} while((choice<1)||(choice>16));
	}
	
	public int inputChoiceGithub()
	{
		int choice = 0;
		
		boolean loop = true;
		do
		{
			System.out.println("Available Services for Github Data Set");
			System.out.println("1. Generate Developer Communication Network (DCN) in PAJEK Format for a Given Set of Products");
			System.out.println("2. Generate the DCN and/or DAN Metrics for a Given Set of Product and PAJEK Files");
			System.out.println("3. Generate Bugs-By-Developer Matrix in CSV Format for a Given Set of Products (DCN)");
			System.out.println("4. Generate Devs-By-Devs Matrix in CSV Format for a Given Set of Products (DCN)");
			System.out.println("5. Generate Project Data Summary in CSV Format for a Given Set of Products");
			System.out.print  ("Please Enter Your Choice (1 to 5): ");
			
			
			try
			{
				choice = user_input.nextInt();
			} catch(InputMismatchException e)
			{
				System.out.println("Error! Only Integers Are Accepted.");
			}
			
			System.out.println("");
			
			user_input.nextLine();
		} while((choice<1)||(choice>5));
		
		switch(choice)
		{
		case 1: choice = 1; break;
		case 2: choice = 3; break;
		case 3: choice = 4; break;
		case 4: choice = 5; break;
		case 5: choice = 8; break;
		default:System.out.println("This Shouldn't Happen"); break;
		}
		
		return choice;
	}
	
	public int inputType()
	{
		int choice = 0;
		
		do
		{
			System.out.println("Choose:");
			System.out.println("1. Include Developers Who Comment on at Least One Bug in RESOLVED Status.");
			System.out.println("2. Include Developers Who Own at Least One Bug in RESOLVED Status..");
			System.out.print  ("Please Enter Your Choice (1 or 2): ");
			
			try
			{
				choice = user_input.nextInt();
			} catch(InputMismatchException e)
			{
				System.out.println("Error! Only Integers Are Accepted.");
			}
			
			System.out.println("");
			
			if(choice==1)
			{
				choice = 1;
			}
			if(choice==2)
			{
				choice = 100;
			}
			
			user_input.nextLine();
			return choice;
		} while((choice<1)||(choice>2));
	}
	
	
	
	
	/*
	 * Method Name: writeFile
	 * Input: File Name and It's Content
	 * Output: If it succeeds in writing a file, return true, else it will return false
	 * Function: Creates a file.
	 */
	public boolean writeFile(String text, String fileName)
	{
		File file = new File(fileName);
		
		try{
		    FileWriter fileWriter = new FileWriter(file);

		    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		    bufferedWriter.write(text);
		    bufferedWriter.close();
		    
		    return true;
		} catch(IOException e) {
		    return false;
		}
	}
	
}
