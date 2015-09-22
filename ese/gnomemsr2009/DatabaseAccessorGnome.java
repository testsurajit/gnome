package ese.gnomemsr2009;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import au.com.bytecode.opencsv.CSVReader;


public class DatabaseAccessorGnome 
{
	Connection con;
	ResultSet rs, rs2;
	Statement s ;
	Statement s2;
	
	
	private NetworkBuilder nb = new NetworkBuilder();
	
	
	private String fileContent;
	private String fileName;
	private String dbName;
	
	private int num;

	public DatabaseAccessorGnome()
	{
		fileContent = "";
		fileName = "";
	}
	
	public String getFileContent()
	{
		return fileContent;
	}
	
	public String getFileName()
	{
		return fileName;
	}
	
	public String getDBName()
	{
		return dbName;
	}
	
	
	public boolean openConnection(String databaseName, String mysqlUser, String password) throws Exception
	{
		dbName = databaseName;
		System.out.println("test...");
		Class.forName("com.mysql.jdbc.Driver"); //load mysql driver
		System.out.println("test...11");
		try
		{
			
			con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/" + databaseName + "?user=" + mysqlUser + "&password=" + password); //set-up connection with database
			s = con.createStatement(); //Statements to issue sql queries
			s2 = con.createStatement();
		} catch (SQLException e) 
		{
			System.out.println("test...11222");
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public void generateDCN(String product, String startDate, String endDate) throws Exception
	{
		ArrayList<String> developers = new ArrayList<String>();
		ArrayList<String> developers2= new ArrayList<String>();
		ArrayList<String> developers3= new ArrayList<String>();
		ArrayList<Integer> edges     = new ArrayList<Integer>();
		
		
		System.out.println("");
		System.out.println("Calculating the Total Number of Distinct Developers...");

		rs = s.executeQuery(
				"select count(distinct(b.who)) "+
				"from bugs c, comment b " +
				"where c.bug_id = b.bugid " +
				"and (STR_TO_DATE(b.bug_when, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"' ) "+
				"and trim(' ' from c.product) like '%"+product+"\n'" +
				"and trim(' ' from c.bug_status) like '%RESOLVED\n';"
				); //ResultSet gets Query results. Query to find out the total number of distinct developers commenting on the bugs of a specific product
		
		while(rs.next())
		{
			num = rs.getInt("count(distinct(b.who))");
		}
		
		System.out.println("Retrieving the Developer's E-Mail Addresses...");
		
		rs = s.executeQuery(
				"select distinct(trim(' ' from replace(b.who, '\n', ''))) \"who\""+
				"from bugs c, comment b " +
				"where c.bug_id = b.bugid " +
				"and (STR_TO_DATE(b.bug_when, '%Y-%m-%d %H:%i:%s')) between '"+startDate+"' and '"+endDate+"' "  +
				"and trim(' ' from c.product) like '%"+product+"\n' " +
				"and trim(' ' from c.bug_status) like '%RESOLVED\n' " +
				"order by who;"
				); //Query to find the distinct developers working on the bugs
		
		while(rs.next())
		{
			developers.add(rs.getString("who"));
		}
		
		System.out.println("Building the Developer Communication Network...");
		
		rs = s.executeQuery(
				"select trim(' ' from replace(a.who, '\n', '')), count(distinct(a.bugid)), trim(' ' from replace(b.who, '\n', '')) " +
						"from comment a, comment b " +
						"where a.bugid IN " +
						"(" +
							"select b.bugid " +
							"from bugs c, comment b " +
							"where c.bug_id = b.bugid " +
							"and trim(' ' from c.product) like '%"+product+"\n' "+
							"and trim(' ' from c.bug_status) like '%RESOLVED\n' " +
						") " +
						"and a.who <> b.who " +
						"and a.bugid = b.bugid "+
						"and (STR_TO_DATE(b.bug_when, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " +
						"group by a.who, b.who " +
						"order by trim(' ' from replace(a.who, '\n', ''));"
						//Query to find how many times a developer work with another developer on the bugs of a particular component
				);
		
		while(rs.next())
		{
			developers2.add(rs.getString("trim(' ' from replace(a.who, '\n', ''))"));
			developers3.add(rs.getString("trim(' ' from replace(b.who, '\n', ''))"));
			edges.add((rs.getInt("count(distinct(a.bugid))")));
		}
		
		fileContent = nb.networkBuilder(developers, developers2, developers3, edges, num);
		
	}
	
	public void generateDAN(String product, String startDate, String endDate) throws Exception
	{
		ArrayList<String> developers = new ArrayList<String>();
		ArrayList<String> developers2= new ArrayList<String>();
		ArrayList<String> developers3= new ArrayList<String>();
		ArrayList<Integer> edges     = new ArrayList<Integer>();
		
		
		System.out.println("");
		System.out.println("Calculating the Total Number of Distinct Developers...");

		rs = s.executeQuery(
				"select count(distinct(b.who)) "+
				"from bugs c, comment b " +
				"where c.bug_id = b.bugid " +
				"and (STR_TO_DATE(b.bug_when, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"' ) "+
				"and trim(' ' from c.bug_status) like '%RESOLVED\n' " +
				"and trim(' ' from c.product) like '%"+product+"\n';"
				); //ResultSet gets Query results. Query to find out the total number of distinct developers commenting on the bugs of a specific product
		
		while(rs.next())
		{
			num = rs.getInt("count(distinct(b.who))");
		}
		
		System.out.println("Retrieving the Developer's E-Mail Addresses...");
		
		rs = s.executeQuery(
				"select distinct(trim(' ' from replace(b.who, '\n', ''))) \"who\""+
				"from bugs c, comment b " +
				"where c.bug_id = b.bugid " +
				"and (STR_TO_DATE(b.bug_when, '%Y-%m-%d %H:%i:%s')) between '"+startDate+"' and '"+endDate+"' "  +
				"and trim(' ' from c.product) like '%"+product+"\n' " +
				"and trim(' ' from c.bug_status) like '%RESOLVED\n' " +
				"order by who;"
				); //Query to find the distinct developers working on the bugs
		
		while(rs.next())
		{
			developers.add(rs.getString("who"));
		}
		
		System.out.println("Building the Developer Communication Network...");
		
		rs = s.executeQuery(
				"select trim(' ' from replace(a.who, '\n', '')), count(distinct(a.bugid)), trim(' ' from replace(b.who, '\n', '')) " +
						"from activity a, activity b " +
						"where a.bugid IN " +
						"(" +
							"select b.bugid " +
							"from bugs c, activity b " +
							"where c.bug_id = b.bugid " +
							"and trim(' ' from c.product) like '%"+product+"\n' "+
							"and trim(' ' from c.bug_status) like '%RESOLVED\n' " +
						") " +
						"and a.who <> b.who " +
						"and a.bugid = b.bugid "+
						"and (STR_TO_DATE(b.bug_when, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " +
						"group by a.who, b.who " +
						"order by trim(' ' from replace(a.who, '\n', ''));"
						//Query to find how many times a developer work with another developer on the bugs of a particular component
				);
		
		while(rs.next())
		{
			developers2.add(rs.getString("trim(' ' from replace(a.who, '\n', ''))"));
			developers3.add(rs.getString("trim(' ' from replace(b.who, '\n', ''))"));
			edges.add((rs.getInt("count(distinct(a.bugid))")));
		}

		fileContent = nb.networkBuilder(developers, developers2, developers3, edges, num);
		
	}
	
	public void generateOwnersDCN(String product, String startDate, String endDate) throws Exception
	{
		ArrayList<String> developers = new ArrayList<String>();
		ArrayList<String> developers2= new ArrayList<String>();
		ArrayList<String> developers3= new ArrayList<String>();
		ArrayList<Integer> edges     = new ArrayList<Integer>();
		
		
		System.out.println("");
		System.out.println("Calculating the Total Number of Distinct Owners...");

		rs = s.executeQuery(
				"select count(distinct(assigned_to)) 'vertices' "
						+  "from bugs "
						+  "where trim(' ' from replace(product, '\\n', '')) like '" + product + "' "
						+  "and trim(' ' from replace(bug_status, '\\n', '')) like 'RESOLVED' "
						+  "and (STR_TO_DATE(creation_ts, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " 
						+  "and (STR_TO_DATE(delta_ts, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"'); " 
				); //ResultSet gets Query results. Query to find out the total number of distinct developers commenting on the bugs of a specific product
		
		while(rs.next())
		{
			num = rs.getInt("vertices");
		}
		//test
		System.out.println("Vertices :-"+num);
		//end		
		System.out.println("Retrieving the Developer's E-Mail Addresses...");
		
		rs = s.executeQuery(
				"select distinct(assigned_to) 'who' "
						+  "from bugs "
						+  "where trim(' ' from replace(product, '\\n', '')) like '" + product + "' "
						+  "and trim(' ' from replace(bug_status, '\\n', '')) like 'RESOLVED' "
						+  "and (STR_TO_DATE(creation_ts, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " 
						+  "and (STR_TO_DATE(delta_ts, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"'); " 
				); //Query to find the distinct developers working on the bugs
		
		while(rs.next())
		{
			developers.add(rs.getString("who").trim());
		}
		
		String in = "(";
		for (int i = 0; i < developers.size(); i++)
		{
			if(i==developers.size()-1)
				in = in +"'" +developers.get(i)+ "') ";
			else
				in = in +"'" +developers.get(i)+ "', ";
		}
		//System.out.println(in);
		System.out.println("Building the Developer Communication Network...");
		
		rs = s.executeQuery(
				"select trim(' ' from replace(a.who, '\n', '')), count(distinct(a.bugid)), trim(' ' from replace(b.who, '\n', '')) " +
						"from comment a, comment b, bugs c " +
						"where trim(' ' from replace(c.product, '\n', '')) like '"+product+"' " +
						"and a.who <> b.who " +
						"and a.bugid = b.bugid "+
						"and a.bugid = c.bug_id "+
						"and trim(' ' from replace(c.bug_status, '\\n', '')) like 'RESOLVED' "+
						"and (STR_TO_DATE(b.bug_when, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " +
						"and trim(' ' from replace(a.who, '\n', '')) IN " + in +
						"and trim(' ' from replace(b.who, '\n', '')) IN " + in +
						"group by a.who, b.who " +
						"order by trim(' ' from replace(a.who, '\n', ''));"
						//Query to find how many times a developer work with another developer on the bugs of a particular component
				);
		
		while(rs.next())
		{
			developers2.add(rs.getString("trim(' ' from replace(a.who, '\n', ''))"));
			developers3.add(rs.getString("trim(' ' from replace(b.who, '\n', ''))"));
			edges.add((rs.getInt("count(distinct(a.bugid))")));
		}
		
		fileContent = nb.networkBuilder(developers, developers2, developers3, edges, num);
		
	}
	
	public void generateOwnersDAN(String product, String startDate, String endDate) throws Exception
	{
		ArrayList<String> developers = new ArrayList<String>();
		ArrayList<String> developers2= new ArrayList<String>();
		ArrayList<String> developers3= new ArrayList<String>();
		ArrayList<Integer> edges     = new ArrayList<Integer>();
		
		
		System.out.println("");
		System.out.println("Calculating the Total Number of Distinct Developers...");
		
		
		rs = s.executeQuery(
				"select count(distinct(assigned_to)) 'vertices' "
						+  "from bugs "
						+  "where trim(' ' from replace(product, '\\n', '')) like '" + product + "' "
						+  "and trim(' ' from replace(bug_status, '\\n', '')) like 'RESOLVED' "
						+  "and (STR_TO_DATE(creation_ts, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " 
						+  "and (STR_TO_DATE(delta_ts, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"'); "
				); //ResultSet gets Query results. Query to find out the total number of distinct developers commenting on the bugs of a specific product
		
		while(rs.next())
		{
			num = rs.getInt("vertices");
		}
		
		System.out.println("Retrieving the Developer's E-Mail Addresses...");
		
		rs = s.executeQuery(
				"select distinct(assigned_to) 'who' "
						+  "from bugs "
						+  "where trim(' ' from replace(product, '\\n', '')) like '" + product + "' "
						+  "and trim(' ' from replace(bug_status, '\\n', '')) like 'RESOLVED' "
						+  "and (STR_TO_DATE(creation_ts, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " 
						+  "and (STR_TO_DATE(delta_ts, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"'); " 
				); //Query to find the distinct developers working on the bugs
		
		while(rs.next())
		{
			developers.add(rs.getString("who").trim());
		}
		
		String in = "(";
		for (int i = 0; i < developers.size(); i++)
		{
			if(i==developers.size()-1)
				in = in +"'" +developers.get(i)+ "') ";
			else
				in = in +"'" +developers.get(i)+ "', ";
		}
		//System.out.println(in);
		System.out.println("Building the Developer Communication Network...");
		
		rs = s.executeQuery(
				"select trim(' ' from replace(a.who, '\n', '')), count(distinct(a.bugid)), trim(' ' from replace(b.who, '\n', '')) " +
						"from activity a, activity b, bugs c " +
						"where trim(' ' from replace(c.product, '\n', '')) like '"+product+"' " +
						"and a.who <> b.who " +
						"and a.bugid = b.bugid "+
						"and a.bugid = c.bug_id "+
						"and trim(' ' from replace(c.bug_status, '\\n', '')) like 'RESOLVED' "+
						"and (STR_TO_DATE(b.bug_when, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " +
						"and trim(' ' from replace(a.who, '\n', '')) IN " + in +
						"and trim(' ' from replace(b.who, '\n', '')) IN " + in +
						"group by a.who, b.who " +
						"order by trim(' ' from replace(a.who, '\n', ''));"
						//Query to find how many times a developer work with another developer on the bugs of a particular component
				);
		
		while(rs.next())
		{
			developers2.add(rs.getString("trim(' ' from replace(a.who, '\n', ''))"));
			developers3.add(rs.getString("trim(' ' from replace(b.who, '\n', ''))"));
			edges.add((rs.getInt("count(distinct(a.bugid))")));
		}
		
		fileContent = nb.networkBuilder(developers, developers2, developers3, edges, num);
		
	}
	
	public void projectSummary(ArrayList<String> productName, String dirName, boolean congruenceOrNot) throws Exception
	{
		ArrayList<String> elapsedHours 		= new ArrayList<String>();
		ArrayList<String> firstComment		= new ArrayList<String>();
		ArrayList<String> lastComment		= new ArrayList<String>();
		ArrayList<String> resolvedAge		= new ArrayList<String>();
		ArrayList<String> productName2 		= new ArrayList<String>();
		ArrayList<String> numOfBugs 		= new ArrayList<String>();
		ArrayList<String> resolvedOwners    = new ArrayList<String>();
		ArrayList<String> resolvedCommenters= new ArrayList<String>();
		ArrayList<String> resolvedComments  = new ArrayList<String>();
		ArrayList<String> avgResolvedComments	= new ArrayList<String>();
		ArrayList<String> medianResolvedComments= new ArrayList<String>();
		ArrayList<String> medianResolvedActivity= new ArrayList<String>();
		ArrayList<String> medianCommentSpan	= new ArrayList<String>();
		ArrayList<String> avgCommentSpan	= new ArrayList<String>();
		ArrayList<String> avgPriority	= new ArrayList<String>();
		ArrayList<String> resolvedActivities	= new ArrayList<String>();
		ArrayList<String> avgResolvedActivities	= new ArrayList<String>();
		ArrayList<String> medianPriority	= new ArrayList<String>();
		ArrayList<String> medianElapsedTime	= new ArrayList<String>();
		ArrayList<String> avgElapsedTime	= new ArrayList<String>();
		ArrayList<String> allBugStatus		= new ArrayList<String>();
		
		ArrayList<String> everythingElse	= new ArrayList<String>();
		//everythingElse includes cent.Degree, cent.Betweenness, cent.closeness, cent.EVcent, transitivity.global, assortativity, diameter, density, modularity, avg.PathLength, and avg.Degree
		
		String inStatement = "";
		
		RFunctions rf = Controller.rf;
		
		for(int i = 0; i < productName.size(); i++)
		{
			if(i == 0)
				inStatement = inStatement + "AND (trim(' ' from replace(a.product, '\n', '')) like '"+productName.get(i).trim()+"'";
			if(i == productName.size() - 1)
				inStatement = inStatement + " OR trim(' ' from replace(a.product, '\n', '')) like '"+productName.get(i).trim()+"') ";
			else
				inStatement = inStatement + " OR trim(' ' from replace(a.product, '\n', '')) like '"+productName.get(i).trim()+"'";
		}
		
		System.out.println("\nExtracting Data from Database...");
		
		rs = s.executeQuery("select distinct(trim(' ' from replace(a.product, '\n', ''))), count(distinct(b.bugid)) "
							+"from bugs a, comment b "
							+"where a.bug_id = b.bugid "
							+ inStatement
							+"group by a.product "
							);
		
		
		while(rs.next())
		{
			String product = rs.getString("(trim(' ' from replace(a.product, '\n', '')))");
			productName2.add(product);
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d H:mm:ss", Locale.ENGLISH);
			String allStatus = "";
			int resolvedBugs = 0;
			int totalBug = 0;
			for(int i = 0; i < 8; i++)
			{
				String bugStatus = "";
				
				switch(i)
				{
				case 0: bugStatus = "assigned"; break;
				case 1: bugStatus = "closed"; break;
				case 2: bugStatus = "needinfo"; break;
				case 3: bugStatus = "new"; break;
				case 4: bugStatus = "reopened"; break;
				case 5: bugStatus = "unconfirmed"; break;
				case 6: bugStatus = "verified"; break;
				case 7: bugStatus = "resolved"; break;
				default: break;
				}
				
				rs2 = s2.executeQuery(
						"select product, count(bug_id) "
						+ "from bugs "
						+ "where trim(' ' from replace(product, '\n', '')) like '"+product+"' "
						+ "and trim(' ' from replace(bug_status, '\n', '')) like '"+bugStatus+"' "
						+ "group by product "
						); //Query to find the distinct developers working on the bugs
				
				String curStatus = "";
				while(rs2.next())
				{
					curStatus = rs2.getString("count(bug_id)");
				}
				
				if(curStatus.isEmpty()) curStatus = "0";
				if(i == 7) 
				{
					allStatus = allStatus + curStatus;
					resolvedBugs = Integer.parseInt(curStatus);
				}
				else allStatus = allStatus + curStatus + ", ";
				
				totalBug = totalBug + Integer.parseInt(curStatus);
			}
			
			allBugStatus.add(allStatus);
			numOfBugs.add("" + totalBug);
			System.out.println("\nFinding First and Last Comment of: " + product + "\n");
			
			rs2 = s2.executeQuery(
					"select MIN(b.bug_when), MAX(b.bug_when), MIN(a.creation_ts), MAX(a.creation_ts) " +
					"from bugs a, comment b " +
					"where trim(' ' from replace(a.product, '\n', '')) like '"+product+"' " +
					"and trim(' ' from replace(a.bug_status, '\n', '')) like 'resolved' " +
					"and a.bug_id = b.bugid " +
					"group by a.product; "
					); //Query to find the distinct developers working on the bugs
			
			while(rs2.next())
			{
				String minDate = rs2.getString("MIN(b.bug_when)").trim();
				String maxDate = rs2.getString("MAX(b.bug_when)").trim();
				
				Date dateMin = sdf.parse(minDate);
				Date dateMax = sdf.parse(maxDate);
				
				firstComment.add("\""+minDate+"\"");
				lastComment.add("\""+maxDate+"\"");
				
				float differenceInTime = dateMax.getTime() - dateMin.getTime(); //elapsed time in millisecond
				
				elapsedHours.add("" + (float)((differenceInTime/1000)/3600)); //elapsed time in hours
				
				minDate = rs2.getString("MIN(a.creation_ts)").trim();
				maxDate = rs2.getString("MAX(a.creation_ts)").trim();
				
				dateMin = sdf.parse(minDate);
				dateMax = sdf.parse(maxDate);
				
				differenceInTime = dateMax.getTime() - dateMin.getTime(); //elapsed time in millisecond
				
				resolvedAge.add("" + (float)((differenceInTime/1000)/3600));
			}
			
			
			System.out.println("\nCalculating Median Elapsed Time of: " + product + "\n");
			
			rs2 = s2.executeQuery(
							"select timestampdiff(second, a.creation_ts, a.delta_ts)/3600 'elapsed_time' " +
							"from bugs a " +
							"where trim(' ' from replace(a.product, '\n', '')) like '"+product+"' " +
							"and trim(' ' from replace(a.bug_status, '\n', '')) like 'RESOLVED' " +
							"order by timestampdiff(second, a.creation_ts, a.delta_ts)/3600; "
								);
			
			float elTime = 0.0f;
			ArrayList<Float> elapsedTime 			= new ArrayList<Float>();
			
			while(rs2.next())
			{
				elTime = rs2.getFloat("elapsed_time");
				elapsedTime.add(elTime);
			}
			
			if(elTime == 0.0f)
			{
				elapsedTime.add(elTime);
			}
			
			//find the median of the elapsed time
			int mid = elapsedTime.size()/2; 
			float median = elapsedTime.get(mid); 
			
			if (elapsedTime.size()%2 == 0) 
			{ 
				median = (median + elapsedTime.get(mid-1))/2; 
			}
			
			medianElapsedTime.add(""+median);
			
			System.out.println("\nCalculating Median Resolved Activities of: " + product + "\n");
			
			rs2 = s2.executeQuery(
							"select a.bug_id, count(b.who) " +
							"from bugs a, activity b " +
							"where trim(' ' from replace(a.product, '\n', '')) like '"+product+"' " +
							"and a.bug_id = b.bugid " +
							"and trim(' ' from replace(a.bug_status, '\n', '')) like 'RESOLVED' " +
							"group by a.bug_id "
						);
	
			float activities = 0.0f;
			ArrayList<Float> activityList = new ArrayList<Float>();
			
			while(rs2.next())
			{
				activities = rs2.getFloat("count(b.who)");
				
				activityList.add(activities);
			}
			
			if(activities == 0.0f)
			{
				activityList.add(activities);
			}
			
			//find the median of the elapsed time
			mid = activityList.size()/2; 
			median = activityList.get(mid); 
			
			if (activityList.size()%2 == 0) 
			{ 
				median = (median + activityList.get(mid-1))/2; 
			}
			
			medianResolvedActivity.add(""+median);
			
			System.out.println("\nCalculating Median Priority of: " + product + "\n");
			
			rs2 = s2.executeQuery(
					"select REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(trim(' ' from replace(priority, '\n', '')), 'Low', 1), 'Normal', 2), 'High', 3), 'Urgent', 4), 'Immediate', 5) as priority "
					+ "from bugs "
					+ "where trim(' ' from replace(product, '\n', '')) like '"+product+"' "
					+ "and trim(' ' from replace(bug_status, '\n', '')) like 'resolved'; "
						);
	
			float priority = 0.0f;
			ArrayList<Float> priorityList = new ArrayList<Float>();
			
			while(rs2.next())
			{
				priority = rs2.getFloat("priority");
				priorityList.add(priority);
			}
			
			if(priority == 0.0f)
			{
				priorityList.add(priority);
			}
			
			//find the median of the elapsed time
			mid = priorityList.size()/2; 
			median = priorityList.get(mid); 
			
			if (priorityList.size()%2 == 0) 
			{ 
				median = (median + priorityList.get(mid-1))/2; 
			}
			
			medianPriority.add(""+median);

			
			System.out.println("\nCalculating Median Resolved Comments of: " + product + "\n");
			
			rs2 = s2.executeQuery(
							"select a.bug_id, count(b.who) " +
							"from bugs a, comment b " +
							"where trim(' ' from replace(a.product, '\n', '')) like '"+product+"' " +
							"and a.bug_id = b.bugid " +
							"and trim(' ' from replace(a.bug_status, '\n', '')) like 'RESOLVED' " +
							"group by a.bug_id "
								);
			
			float comments = 0.0f;
			ArrayList<Float> commentsArray		= new ArrayList<Float>();
			
			while(rs2.next())
			{
				comments = rs2.getFloat("count(b.who)");
				
				commentsArray.add(comments);
			}
			
			if(comments == 0.0f)
			{
				commentsArray.add(comments);
			}
			
			//find the median of the elapsed time
			mid = commentsArray.size()/2; 
			median = commentsArray.get(mid); 
			
			if (commentsArray.size()%2 == 0) 
			{ 
				median = (median + commentsArray.get(mid-1))/2; 
			}
			
			medianResolvedComments.add(""+median);
			
			System.out.println("\nCalculating Median Resolved Comments of: " + product + "\n");
			
			rs2 = s2.executeQuery(
							"select a.comment_span "
							+ "from ( "
							+ "select b.who, timestampdiff(second, MIN(bug_when), MAX(bug_when))/3600 AS comment_span " 
							+ "from bugs a, comment b " 
							+ "where trim(' ' from replace(a.product, '\n', '')) like '"+product+"' "
							+ "and trim(' ' from replace(a.bug_status, '\n', '')) like 'resolved' "
							+ "and a.bug_id = b.bugid " 
							+ "group by who "  
							+ "order by who) a "
								);
			
			float commentSpan = 0.0f;
			ArrayList<Float> commentSpanArray		= new ArrayList<Float>();
			
			while(rs2.next())
			{
				commentSpan = rs2.getFloat("a.comment_span");
				commentSpanArray.add(commentSpan);
			}
			
			if(commentSpan == 0.0f)
			{
				commentSpanArray.add(commentSpan);
			}
			
			//find the median of the elapsed time
			mid = commentSpanArray.size()/2; 
			median = commentSpanArray.get(mid); 
			
			if (commentSpanArray.size()%2 == 0) 
			{ 
				median = (median + commentSpanArray.get(mid-1))/2; 
			}
			
			medianCommentSpan.add(""+median);
			
			
			System.out.println("\nCalculating Average Elapsed Time of: " + product + "\n");
			
			rs2 = s2.executeQuery(
					"select a.product,  avg(timestampdiff(second, a.creation_ts, a.delta_ts)/3600) as avgElapsedTime, avg(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(trim(' ' from replace(priority, '\n', '')), 'Low', 1), 'Normal', 2), 'High', 3), 'Urgent', 4), 'Immediate', 5)) as avgPriority " +
					"from bugs a " +
					"where trim(' ' from replace(a.product, '\n', '')) like '"+product+"' " +
					"and trim(' ' from replace(a.bug_status, '\n', '')) like 'RESOLVED' " +
					"group by a.product " +
					"order by a.product; "
					); //Query to find the distinct developers working on the bugs
			
			while(rs2.next())
			{
				avgElapsedTime.add(rs2.getString("avgElapsedTime"));
				avgPriority.add(rs2.getString("avgPriority"));
			}
			
			System.out.println("\nCalculating the Number of Owners with Resolved Bugs in: " + product + "\n");
			
			rs2 = s2.executeQuery(
					"select a.product,  count(distinct(a.assigned_to)) as noOfOwners " +
					"from bugs a " +
					"where trim(' ' from replace(a.product, '\n', '')) like '"+product+"' " +
					"and trim(' ' from replace(a.bug_status, '\n', '')) like 'RESOLVED' " +
					"group by a.product " +
					"order by a.product; "
					); //Query to find the distinct developers working on the bugs
			
			while(rs2.next())
			{
				resolvedOwners.add(rs2.getString("noOfOwners"));
			}
			
			System.out.println("\nCalculating the Number of Commenters on Resolved Bugs in: " + product + "\n");
			
			rs2 = s2.executeQuery(
					"select a.product,  count(distinct(b.who)) as noOfCommenters, count(b.bug_when) as noOfComments " +
					"from bugs a, comment b " +
					"where trim(' ' from replace(a.product, '\n', '')) like '"+product+"' " +
					"and trim(' ' from replace(a.bug_status, '\n', '')) like 'RESOLVED' " +
					"and a.bug_id = b.bugid " +
					"group by a.product " +
					"order by a.product; "
					); //Query to find the distinct developers working on the bugs
			
			while(rs2.next())
			{
				int commentsOnResolved = rs2.getInt("noOfComments");
				resolvedCommenters.add(rs2.getString("noOfCommenters"));
				resolvedComments.add("" + commentsOnResolved);
				avgResolvedComments.add("" + (float)commentsOnResolved/resolvedBugs);
			}
			
			System.out.println("\nCalculating the Average Comment Span of Resolved Bugs in: " + product + "\n");
			
			rs2 = s2.executeQuery(
					"select avg(a.comment_span) "
					+ "from ( "
					+ "select b.who, timestampdiff(second, MIN(bug_when), MAX(bug_when))/3600 AS comment_span " 
					+ "from bugs a, comment b " 
					+ "where trim(' ' from replace(a.product, '\n', '')) like '"+product+"' "
					+ "and trim(' ' from replace(a.bug_status, '\n', '')) like 'resolved' "
					+ "and a.bug_id = b.bugid " 
					+ "group by who "  
					+ "order by who) a; "
					); //Query to find the distinct developers working on the bugs
			
			while(rs2.next())
			{
				avgCommentSpan.add(rs2.getString("avg(a.comment_span)"));
			}
			
			
			System.out.println("\nCalculating the Number of Activities on Resolved Bugs in: " + product + "\n");
			
			rs2 = s2.executeQuery(
					"select a.product,  count(distinct(b.who)) as noOfCommenters, count(b.bug_when) as noOfActivities " +
					"from bugs a, activity b " +
					"where trim(' ' from replace(a.product, '\n', '')) like '"+product+"' " +
					"and trim(' ' from replace(a.bug_status, '\n', '')) like 'RESOLVED' " +
					"and a.bug_id = b.bugid " +
					"group by a.product " +
					"order by a.product; "
					); //Query to find the distinct developers working on the bugs
			
			while(rs2.next())
			{
				int activitiesOnResolved = rs2.getInt("noOfActivities");
				//resolvedCommenters.add(rs2.getString("noOfCommenters"));
				resolvedActivities.add("" + activitiesOnResolved);
				avgResolvedActivities.add("" + (float)activitiesOnResolved/resolvedBugs);
			}
			
			System.out.println("\nCalculating Network Metrics of: " + product + "\n");
			
			everythingElse.add(rf.summaryMetrics(dirName, product, true) + ", " + rf.summaryMetrics(dirName, product, false));
		}
		
		//StringBuilder start
		StringBuilder csv = new StringBuilder();
		csv.append("product-name, noof-bugs-total, "
				+ "noof-bugs-assigned, noof-bugs-closed, noof-bugs-needinfo, noof-bugs-new, noof-bugs-reopened, noof-bugs-unconfirmed, noof-bugs-verified, noof-bugs-resolved, "
				+ "avg-elapsed-time-resolved, median-elapsed-time-resolved, "
				+ "noof-developers-owning-resolved, noof-developers-commenting-resolved, noof-comments-resolved, avg-comments-resolved, "
				+ "median-comments-resolved, avg-comment-span-resolved, median-comment-span-resolved, first-comment-ts-resolved, last-comment-ts-resolved, "
				+ "elapsed-time-hours-resolved, noof-activities-resolved, avg-activities-resolved, median-activities-resolved, product-age-resolved, "
				+ "avg-bug-priority-resolved, median-bug-priority-resolved, ");
		if(congruenceOrNot) csv.append("socio-tech-congruence, ");	
		csv.append("dcn-degree-centralization, dcn-betweenness-centralization, dcn-closeness, dcn-evcent, dcn-transitivity-global, "
				+ "dcn-assortativity, dcn-diameter, dcn-density, dcn-modularity, dcn-avg-path-length, dcn-avg-degree, "
				+ "dan-degree-centralization, dan-betweenness-centralization, dan-closeness, dan-evcent, dan-transitivity-global, "
				+ "dan-assortativity, dan-diameter, dan-density, dan-modularity, dan-avg-path-length, dan-avg-degree\n");
				
		
		System.out.println("Generating .CSV File");
		
		for(int i = 0; i < productName2.size(); i++)
		{
			csv.append(productName2.get(i) + ", ");
			csv.append(numOfBugs.get(i) + ", ");
			csv.append(allBugStatus.get(i) + ", ");
			csv.append(avgElapsedTime.get(i) + ", ");
			csv.append(medianElapsedTime.get(i) + ", ");
			csv.append(resolvedOwners.get(i) + ", ");
			csv.append(resolvedCommenters.get(i) + ", ");
			csv.append(resolvedComments.get(i) + ", ");
			csv.append(avgResolvedComments.get(i) + ", ");
			csv.append(medianResolvedComments.get(i) + ", ");
			csv.append(avgCommentSpan.get(i) + ", ");
			csv.append(medianCommentSpan.get(i) + ", ");
			csv.append(firstComment.get(i) + ", ");
			csv.append(lastComment.get(i) + ", ");
			csv.append(elapsedHours.get(i) + ", ");
			csv.append(resolvedActivities.get(i) + ", ");
			csv.append(avgResolvedActivities.get(i) + ", ");
			csv.append(medianResolvedActivity.get(i) + ", ");
			csv.append(resolvedAge.get(i) + ", ");
			csv.append(avgPriority.get(i) + ", ");
			csv.append(medianPriority.get(i) + ", ");
			if(congruenceOrNot) csv.append(socioTechCongruence(dirName, productName2.get(i)) + ", ");
			csv.append(everythingElse.get(i) + "\n");
		}
		
		fileContent = csv.toString();
	}
	
	
	public void generateBugsByOwners(String product, String startDate, String endDate) throws Exception
	{
		ArrayList<String> distinctBug_id 			= new ArrayList<String>();
		ArrayList<String> distinctDev_email 		= new ArrayList<String>();
		ArrayList<String> bug_id 			= new ArrayList<String>();
		ArrayList<String> dev_email 		= new ArrayList<String>();
		ArrayList<Integer> numOfComments 	= new ArrayList<Integer>();
		
		System.out.println("\nExtracting Data from Database...");
		
		rs = s.executeQuery("select distinct(trim(' ' from replace(bug_id, '\n', ''))) " +
							"from bugs "+
							"where trim(' ' from replace(product, '\n', '')) like '"+product+"' " +
							"and trim(' ' from replace(bug_status, '\\n', '')) like 'RESOLVED' "+
							"and (STR_TO_DATE(creation_ts, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " + 
							"and (STR_TO_DATE(delta_ts, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " +
							"order by bug_id;"	
							);
		
		while(rs.next())
		{
			distinctBug_id.add(rs.getString("(trim(' ' from replace(bug_id, '\n', '')))"));
		}
		
		rs = s.executeQuery(
							"select distinct(trim(' ' from replace(assigned_to, '\n', ''))) \"who\" "+
							"from bugs " +
							"where (STR_TO_DATE(creation_ts, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " +
							"and (STR_TO_DATE(delta_ts, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " +
							"and trim(' ' from product) like '%"+product+"\n' " +
							"and trim(' ' from replace(bug_status, '\\n', '')) like 'RESOLVED' "+
							"order by who;"
							);
		
		while(rs.next())
		{
			distinctDev_email.add(rs.getString("who"));
		}
		
		rs = s.executeQuery(
							"select distinct(trim(' ' from replace(a.bug_id, '\n', ''))), trim(' ' from replace(b.who, '\n', '')), count(b.bug_when) "+
							"from bugs a, comment b " +
							"where a.bug_id = b.bugid " +
							"and trim(' ' from replace(a.product, '\n', '')) = '"+product+"' " +
							"and (STR_TO_DATE(b.bug_when, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " +
							"and trim(' ' from replace(a.bug_status, '\\n', '')) like 'RESOLVED' "+
							"group by a.bug_id, b.who " +
							"order by b.who, a.bug_id;"
							);
		
		while(rs.next())
		{
			bug_id.add(rs.getString("(trim(' ' from replace(a.bug_id, '\n', '')))"));
			dev_email.add(rs.getString("trim(' ' from replace(b.who, '\n', ''))"));
			numOfComments.add(rs.getInt("count(b.bug_when)"));
		}
		
		fileContent = nb.bugsByDevs(distinctDev_email, distinctBug_id, dev_email, bug_id, numOfComments);
	}
	
	public void generateBugsByDev(String product, String startDate, String endDate) throws Exception
	{
		ArrayList<String> distinctBug_id 			= new ArrayList<String>();
		ArrayList<String> distinctDev_email 		= new ArrayList<String>();
		ArrayList<String> bug_id 			= new ArrayList<String>();
		ArrayList<String> dev_email 		= new ArrayList<String>();
		ArrayList<Integer> numOfComments 	= new ArrayList<Integer>();
		
		System.out.println("\nExtracting Data from Database...");
		
		rs = s.executeQuery("select distinct(trim(' ' from replace(bug_id, '\n', ''))) " +
							"from bugs "+
							"where trim(' ' from replace(product, '\n', '')) like '"+product+"' " +
							"and trim(' ' from replace(bug_status, '\\n', '')) like 'RESOLVED' "+
							"and (STR_TO_DATE(creation_ts, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " + 
							"and (STR_TO_DATE(delta_ts, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " +
							"order by bug_id;"	
							);
		
		while(rs.next())
		{
			distinctBug_id.add(rs.getString("(trim(' ' from replace(a.bug_id, '\n', '')))"));
		}
		
		rs = s.executeQuery(
							"select distinct(trim(' ' from replace(b.who, '\n', ''))) " +
							"from bugs a, comment b " +
							"where a.bug_id = b.bugid " +
							"and trim(' ' from replace(a.product, '\n', '')) = '"+product+"' " +
							"and (STR_TO_DATE(b.bug_when, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " +
							"and trim(' ' from replace(a.bug_status, '\\n', '')) like 'RESOLVED' "+
							"order by b.who;"
							);
		
		while(rs.next())
		{
			distinctDev_email.add(rs.getString("(trim(' ' from replace(b.who, '\n', '')))"));
		}
		
		rs = s.executeQuery(
							"select distinct(trim(' ' from replace(a.bug_id, '\n', ''))), trim(' ' from replace(b.who, '\n', '')), count(b.bug_when) "+
							"from bugs a, comment b " +
							"where a.bug_id = b.bugid " +
							"and trim(' ' from replace(a.product, '\n', '')) = '"+product+"' " +
							"and (STR_TO_DATE(b.bug_when, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " +
							"and trim(' ' from replace(a.bug_status, '\\n', '')) like 'RESOLVED' "+
							"group by a.bug_id, b.who " +
							"order by b.who, a.bug_id;"
							);
		
		while(rs.next())
		{
			bug_id.add(rs.getString("(trim(' ' from replace(a.bug_id, '\n', '')))"));
			dev_email.add(rs.getString("trim(' ' from replace(b.who, '\n', ''))"));
			numOfComments.add(rs.getInt("count(b.bug_when)"));
		}
		
		fileContent = nb.bugsByDevs(distinctDev_email, distinctBug_id, dev_email, bug_id, numOfComments);
	}
	
	public void generateActivityByOwners(String product, String startDate, String endDate) throws Exception
	{
		ArrayList<String> distinctBug_id 			= new ArrayList<String>();
		ArrayList<String> distinctDev_email 		= new ArrayList<String>();
		ArrayList<String> bug_id 			= new ArrayList<String>();
		ArrayList<String> dev_email 		= new ArrayList<String>();
		ArrayList<Integer> numOfComments 	= new ArrayList<Integer>();
		
		System.out.println("\nExtracting Data from Database...");
		
		rs = s.executeQuery("select distinct(trim(' ' from replace(bug_id, '\n', ''))) " +
							"from bugs "+
							"where trim(' ' from replace(product, '\n', '')) like '"+product+"' " +
							"and trim(' ' from replace(bug_status, '\\n', '')) like 'RESOLVED' "+
							"and (STR_TO_DATE(creation_ts, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " + 
							"and (STR_TO_DATE(delta_ts, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " +
							"order by bug_id;"		
							);
		
		while(rs.next())
		{
			distinctBug_id.add(rs.getString("(trim(' ' from replace(a.bug_id, '\n', '')))"));
		}
		
		rs = s.executeQuery(
							"select distinct(trim(' ' from replace(assigned_to, '\n', ''))) \"who\" "+
							"from bugs " +
							"where (STR_TO_DATE(creation_ts, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " +
							"and (STR_TO_DATE(delta_ts, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " +
							"and trim(' ' from product) like '%"+product+"\n' " +
							"and trim(' ' from replace(bug_status, '\\n', '')) like 'RESOLVED' "+
							"order by who;"
							);
		
		while(rs.next())
		{
			distinctDev_email.add(rs.getString("who"));
		}
		
		rs = s.executeQuery(
							"select distinct(trim(' ' from replace(a.bug_id, '\n', ''))), trim(' ' from replace(b.who, '\n', '')), count(b.bug_when) "+
							"from bugs a, activity b " +
							"where a.bug_id = b.bugid " +
							"and trim(' ' from replace(a.product, '\n', '')) = '"+product+"' " +
							"and (STR_TO_DATE(b.bug_when, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " +
							"and trim(' ' from replace(a.bug_status, '\\n', '')) like 'RESOLVED' "+
							"group by a.bug_id, b.who " +
							"order by b.who, a.bug_id;"
							);
		
		while(rs.next())
		{
			bug_id.add(rs.getString("(trim(' ' from replace(a.bug_id, '\n', '')))"));
			dev_email.add(rs.getString("trim(' ' from replace(b.who, '\n', ''))"));
			numOfComments.add(rs.getInt("count(b.bug_when)"));
		}
		
		fileContent = nb.bugsByDevs(distinctDev_email, distinctBug_id, dev_email, bug_id, numOfComments);
	}
	
	public void generateActivityByDev(String product, String startDate, String endDate) throws Exception
	{
		ArrayList<String> distinctBug_id 			= new ArrayList<String>();
		ArrayList<String> distinctDev_email 		= new ArrayList<String>();
		ArrayList<String> bug_id 			= new ArrayList<String>();
		ArrayList<String> dev_email 		= new ArrayList<String>();
		ArrayList<Integer> numOfComments 	= new ArrayList<Integer>();
		
		System.out.println("\nExtracting Data from Database...");
		
		rs = s.executeQuery("select distinct(trim(' ' from replace(bug_id, '\n', ''))) " +
							"from bugs "+
							"where trim(' ' from replace(product, '\n', '')) like '"+product+"' " +
							"and trim(' ' from replace(bug_status, '\\n', '')) like 'RESOLVED' "+
							"and (STR_TO_DATE(creation_ts, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " + 
							"and (STR_TO_DATE(delta_ts, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " +
							"order by bug_id;"		
							);
		
		while(rs.next())
		{
			distinctBug_id.add(rs.getString("(trim(' ' from replace(a.bug_id, '\n', '')))"));
		}
		
		rs = s.executeQuery(
							"select distinct(trim(' ' from replace(b.who, '\n', ''))) " +
							"from bugs a, activity b " +
							"where a.bug_id = b.bugid " +
							"and trim(' ' from replace(a.product, '\n', '')) = '"+product+"' " +
							"and (STR_TO_DATE(b.bug_when, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " +
							"and trim(' ' from replace(bug_status, '\\n', '')) like 'RESOLVED' "+
							"order by b.who;"
							);
		
		while(rs.next())
		{
			distinctDev_email.add(rs.getString("(trim(' ' from replace(b.who, '\n', '')))"));
		}
		
		rs = s.executeQuery(
							"select distinct(trim(' ' from replace(a.bug_id, '\n', ''))), trim(' ' from replace(b.who, '\n', '')), count(b.bug_when) "+
							"from bugs a, activity b " +
							"where a.bug_id = b.bugid " +
							"and trim(' ' from replace(a.product, '\n', '')) = '"+product+"' " +
							"and (STR_TO_DATE(b.bug_when, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " +
							"and trim(' ' from replace(a.bug_status, '\\n', '')) like 'RESOLVED' "+
							"group by a.bug_id, b.who " +
							"order by b.who, a.bug_id;"
							);
		
		while(rs.next())
		{
			bug_id.add(rs.getString("(trim(' ' from replace(a.bug_id, '\n', '')))"));
			dev_email.add(rs.getString("trim(' ' from replace(b.who, '\n', ''))"));
			numOfComments.add(rs.getInt("count(b.bug_when)"));
		}
		
		fileContent = nb.bugsByDevs(distinctDev_email, distinctBug_id, dev_email, bug_id, numOfComments);
	}
	
	public void generateDevsByDevs(String product, String startDate, String endDate) throws Exception
	{
		ArrayList<String> developers = new ArrayList<String>();
		ArrayList<String> developers2= new ArrayList<String>();
		ArrayList<String> developers3= new ArrayList<String>();
		ArrayList<Integer> edges     = new ArrayList<Integer>();
			
		
		System.out.println("");
		System.out.println("Retrieving the Developer's E-Mail Addresses...");
		
		rs = s.executeQuery(
				"select distinct(trim(' ' from replace(b.who, '\n', ''))) \"who\""+
				"from bugs c, comment b " +
				"where c.bug_id = b.bugid " +
				"and (STR_TO_DATE(b.bug_when, '%Y-%m-%d %H:%i:%s')) between '"+startDate+"' and '"+endDate+"' "  +
				"and trim(' ' from product) like '%"+product+"\n' " +
				"and trim(' ' from replace(c.bug_status, '\\n', '')) like 'RESOLVED' "+
				"order by who;"
				); //Query to find the distinct developers working on the bugs
		
		while(rs.next())
		{
			developers.add(rs.getString("who"));
		}
		
		System.out.println("Building the Developer Communication Network...");
		
		rs = s.executeQuery(
				"select trim(' ' from replace(a.who, '\n', '')), count(distinct(a.bugid)), trim(' ' from replace(b.who, '\n', '')) " +
						"from comment a, comment b " +
						"where a.bugid IN " +
						"(" +
							"select b.bugid " +
							"from bugs c, comment b " +
							"where c.bug_id = b.bugid " +
							"and trim(' ' from c.product) like '%"+product+"\n' "+
							"and trim(' ' from replace(c.bug_status, '\\n', '')) like 'RESOLVED' "+
						") " +
						"and a.who <> b.who " +
						"and a.bugid = b.bugid "+
						"and (STR_TO_DATE(b.bug_when, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " +
						"group by a.who, b.who " +
						"order by trim(' ' from replace(a.who, '\n', ''));"
						//Query to find how many times a developer work with another developer on the bugs of a particular component
				);
		
		while(rs.next())
		{
			developers2.add(rs.getString("trim(' ' from replace(a.who, '\n', ''))"));
			developers3.add(rs.getString("trim(' ' from replace(b.who, '\n', ''))"));
			edges.add((rs.getInt("count(distinct(a.bugid))")));
		}
	
		fileContent = nb.devsByDevs(developers, developers2, developers3, edges);
		
	}
	
	public void generateOwnersByOwners(String product, String startDate, String endDate) throws Exception
	{
		ArrayList<String> developers = new ArrayList<String>();
		ArrayList<String> developers2= new ArrayList<String>();
		ArrayList<String> developers3= new ArrayList<String>();
		ArrayList<Integer> edges     = new ArrayList<Integer>();
		
		
		System.out.println("");
		System.out.println("Retrieving the Developer's E-Mail Addresses...");
		
		rs = s.executeQuery(
				"select distinct(trim(' ' from replace(assigned_to, '\n', ''))) \"who\" "+
				"from bugs " +
				"where (STR_TO_DATE(creation_ts, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " +
				"and (STR_TO_DATE(delta_ts, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " +
				"and trim(' ' from product) like '%"+product+"\n' " +
				"and trim(' ' from replace(bug_status, '\\n', '')) like 'RESOLVED' "+
				"order by who;"
				); //Query to find the distinct developers working on the bugs
		
		while(rs.next())
		{
			developers.add(rs.getString("who"));
		}
		
		System.out.println("Building the Developer Communication Network...");
		
		rs = s.executeQuery(
				"select trim(' ' from replace(a.who, '\n', '')), count(distinct(a.bugid)), trim(' ' from replace(b.who, '\n', '')) " +
						"from comment a, comment b " +
						"where a.bugid IN " +
						"(" +
							"select b.bugid " +
							"from bugs c, comment b " +
							"where c.bug_id = b.bugid " +
							"and trim(' ' from c.product) like '%"+product+"\n' "+
							"and trim(' ' from replace(c.bug_status, '\\n', '')) like 'RESOLVED' "+
						") " +
						"and a.who <> b.who " +
						"and a.bugid = b.bugid "+
						"and (STR_TO_DATE(b.bug_when, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " +
						"group by a.who, b.who " +
						"order by trim(' ' from replace(a.who, '\n', ''));"
						//Query to find how many times a developer work with another developer on the bugs of a particular component
				);
		
		while(rs.next())
		{
			developers2.add(rs.getString("trim(' ' from replace(a.who, '\n', ''))"));
			developers3.add(rs.getString("trim(' ' from replace(b.who, '\n', ''))"));
			edges.add((rs.getInt("count(distinct(a.bugid))")));
		}
		
		fileContent = nb.devsByDevs(developers, developers2, developers3, edges);
		
	}
	
	public void generateDevsByDevsActivity(String product, String startDate, String endDate) throws Exception
	{
		ArrayList<String> developers = new ArrayList<String>();
		ArrayList<String> developers2= new ArrayList<String>();
		ArrayList<String> developers3= new ArrayList<String>();
		ArrayList<Integer> edges     = new ArrayList<Integer>();
		
		
		System.out.println("");
		System.out.println("Retrieving the Developer's E-Mail Addresses...");
		
		rs = s.executeQuery(
				"select distinct(trim(' ' from replace(b.who, '\n', ''))) \"who\""+
				"from bugs c, activity b " +
				"where c.bug_id = b.bugid " +
				"and (STR_TO_DATE(b.bug_when, '%Y-%m-%d %H:%i:%s')) between '"+startDate+"' and '"+endDate+"' "  +
				"and trim(' ' from c.product) like '%"+product+"\n' " +
				"and trim(' ' from replace(c.bug_status, '\\n', '')) like 'RESOLVED' "+
				"order by who;"
				); //Query to find the distinct developers working on the bugs
		
		while(rs.next())
		{
			developers.add(rs.getString("who"));
		}
		
		System.out.println("Building the Developer Communication Network...");
		
		rs = s.executeQuery(
				"select trim(' ' from replace(a.who, '\n', '')), count(distinct(a.bugid)), trim(' ' from replace(b.who, '\n', '')) " +
						"from activity a, activity b " +
						"where a.bugid IN " +
						"(" +
							"select b.bugid " +
							"from bugs c, activity b " +
							"where c.bug_id = b.bugid " +
							"and trim(' ' from c.product) like '%"+product+"\n' "+
							"and trim(' ' from replace(c.bug_status, '\\n', '')) like 'RESOLVED' "+
						") " +
						"and a.who <> b.who " +
						"and a.bugid = b.bugid "+
						"and (STR_TO_DATE(b.bug_when, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " +
						"group by a.who, b.who " +
						"order by trim(' ' from replace(a.who, '\n', ''));"
						//Query to find how many times a developer work with another developer on the bugs of a particular component
				);
		
		while(rs.next())
		{
			developers2.add(rs.getString("trim(' ' from replace(a.who, '\n', ''))"));
			developers3.add(rs.getString("trim(' ' from replace(b.who, '\n', ''))"));
			edges.add((rs.getInt("count(distinct(a.bugid))")));
		}
		
		fileContent = nb.devsByDevs(developers, developers2, developers3, edges);
		
	}
	
	public void generateOwnersByOwnersActivity(String product, String startDate, String endDate) throws Exception
	{
		ArrayList<String> developers = new ArrayList<String>();
		ArrayList<String> developers2= new ArrayList<String>();
		ArrayList<String> developers3= new ArrayList<String>();
		ArrayList<Integer> edges     = new ArrayList<Integer>();
		
		
		System.out.println("");
		System.out.println("Retrieving the Developer's E-Mail Addresses...");
		
		rs = s.executeQuery(
				"select distinct(trim(' ' from replace(assigned_to, '\n', ''))) \"who\" "+
				"from bugs " +
				"where (STR_TO_DATE(creation_ts, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " +
				"and (STR_TO_DATE(delta_ts, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " +
				"and trim(' ' from product) like '%"+product+"\n' " +
				"and trim(' ' from replace(bug_status, '\\n', '')) like 'RESOLVED' "+
				"order by who;"
				); //Query to find the distinct developers working on the bugs
		
		while(rs.next())
		{
			developers.add(rs.getString("who"));
		}
		
		System.out.println("Building the Developer Communication Network...");
		
		rs = s.executeQuery(
				"select trim(' ' from replace(a.who, '\n', '')), count(distinct(a.bugid)), trim(' ' from replace(b.who, '\n', '')) " +
						"from activity a, activity b " +
						"where a.bugid IN " +
						"(" +
							"select b.bugid " +
							"from bugs c, activity b " +
							"where c.bug_id = b.bugid " +
							"and trim(' ' from c.product) like '%"+product+"\n' "+
							"and trim(' ' from replace(c.bug_status, '\\n', '')) like 'RESOLVED' "+
						") " +
						"and a.who <> b.who " +
						"and a.bugid = b.bugid "+
						"and (STR_TO_DATE(b.bug_when, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " +
						"group by a.who, b.who " +
						"order by trim(' ' from replace(a.who, '\n', ''));"
						//Query to find how many times a developer work with another developer on the bugs of a particular component
				);
		
		while(rs.next())
		{
			developers2.add(rs.getString("trim(' ' from replace(a.who, '\n', ''))"));
			developers3.add(rs.getString("trim(' ' from replace(b.who, '\n', ''))"));
			edges.add((rs.getInt("count(distinct(a.bugid))")));
		}
		
		fileContent = nb.devsByDevs(developers, developers2, developers3, edges);
	}
	
	
	public void generateBugModel(String product, String startDate, String endDate) throws Exception
	{
		ArrayList<String> bug_id	 	= new ArrayList<String>();
		ArrayList<String> owner		 	= new ArrayList<String>();
		ArrayList<String> elapsedTime	= new ArrayList<String>();
		ArrayList<String> component	 	= new ArrayList<String>();
		ArrayList<String> version	 	= new ArrayList<String>();
		ArrayList<String> repPlatform	= new ArrayList<String>();
		ArrayList<String> op_sys	 	= new ArrayList<String>();
		ArrayList<String> bug_status 	= new ArrayList<String>();
		ArrayList<String> resolution 	= new ArrayList<String>();
		ArrayList<String> priority   	= new ArrayList<String>();
		ArrayList<String> bugSeverity	= new ArrayList<String>();
		ArrayList<String> tgtMilestone	= new ArrayList<String>();
		ArrayList<String> dupeOf		= new ArrayList<String>();
		
		ArrayList<String> bug_id2		= new ArrayList<String>();
		ArrayList<String> activityLevel	= new ArrayList<String>();
		
		ArrayList<String> bug_id3		= new ArrayList<String>();
		ArrayList<String> totalComments	= new ArrayList<String>();
		ArrayList<String> numOfDevs		= new ArrayList<String>();
		ArrayList<String> interestSpan	= new ArrayList<String>();
		
		ArrayList<String> bug_id4		= new ArrayList<String>();
		ArrayList<String> ownerComments	= new ArrayList<String>();
		ArrayList<String> owner2		= new ArrayList<String>();
		
		ArrayList<String> owner4		= new ArrayList<String>();
		ArrayList<String> ownerWorkload	= new ArrayList<String>();
		
		ArrayList<String> ownerComArc	= new ArrayList<String>();
		ArrayList<String> owner3		= new ArrayList<String>();
		
		
		System.out.println("");
		System.out.println("Retrieving Data from 'Bugs' Table...");
		
		rs = s.executeQuery(
				"select a.bug_id, trim(' ' from replace(a.assigned_to, '\n', '')) \"owner\", timestampdiff(second, a.creation_ts, a.delta_ts)/3600 \"ElapsedTime\", trim(' ' from replace(a.Component, '\n', '')), trim(' ' from replace(a.Version, '\n', '')), trim(' ' from replace(a.Rep_Platform, '\n', '')), trim(' ' from replace(a.Op_Sys, '\n', '')), trim(' ' from replace(a.Bug_Status, '\n', '')), trim(' ' from replace(a.Resolution, '\n', '')), trim(' ' from replace(a.Priority, '\n', '')), trim(' ' from replace(a.Bug_Severity, '\n', '')), trim(' ' from replace(a.Target_Milestone, '\n', '')), trim(' ' from replace(a.duplicate_Of, '\n', '')) " +
				"from bugs a " +
				"where trim(' ' from replace(a.product, '\n', '')) like \""+product+"\" " +
				"and (STR_TO_DATE(a.creation_ts, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " +
				"and (STR_TO_DATE(a.delta_ts, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " +
				"and trim(' ' from replace(a.bug_status, '\\n', '')) like 'RESOLVED' "+
				"group by a.bug_id " +
				"order by a.bug_id asc;"
				); //Query to find the distinct developers working on the bugs
		
		while(rs.next())
		{
			bug_id.add(rs.getString("a.bug_id"));
			owner.add(rs.getString("owner"));
			elapsedTime.add(rs.getString("ElapsedTime"));
			component.add(rs.getString("trim(' ' from replace(a.Component, '\n', ''))"));
			version.add(rs.getString("trim(' ' from replace(a.version, '\n', ''))"));
			repPlatform.add(rs.getString("trim(' ' from replace(a.Rep_Platform, '\n', ''))"));
			op_sys.add(rs.getString("trim(' ' from replace(a.Op_Sys, '\n', ''))"));
			bug_status.add(rs.getString("trim(' ' from replace(a.bug_status, '\n', ''))"));
			resolution.add(rs.getString("trim(' ' from replace(a.resolution, '\n', ''))"));
			priority.add(rs.getString("trim(' ' from replace(a.priority, '\n', ''))"));
			bugSeverity.add(rs.getString("trim(' ' from replace(a.bug_severity, '\n', ''))"));
			tgtMilestone.add(rs.getString("trim(' ' from replace(a.target_milestone, '\n', ''))"));
			dupeOf.add(rs.getString("trim(' ' from replace(a.duplicate_Of, '\n', ''))"));
		}
		
		System.out.println("Retrieving Data from 'Activity' and 'Bugs' Table...");
		
		rs = s.executeQuery(
				"select COUNT(c.bug_when), a.bug_id " +
				"from activity c, bugs a " + 
				"where a.bug_id = c.bugid " +
				"and (STR_TO_DATE(a.creation_ts, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " +
				"and (STR_TO_DATE(a.delta_ts, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " +
				"and trim(' ' from replace(a.bug_status, '\\n', '')) like 'RESOLVED' "+
				"and c.bugid in " +
				"( select bug_id from bugs where trim(' ' from replace(a.product, '\n', '')) like \"" + product + "\"  )" +
				"group by a.bug_id " +
				"order by a.bug_id asc; "
				);
		
		while(rs.next())
		{
			bug_id2.add(rs.getString("a.bug_id"));
			activityLevel.add(rs.getString("COUNT(c.bug_when)"));
		}
		
		System.out.println("Retrieving Data from 'Comment' and 'Bugs' Table...");
		
		rs = s.executeQuery(
				"select b.bugid, count(b.text), count(distinct(b.who)), timestampdiff(second, MIN(b.bug_when), MAX(b.bug_when))/3600 " +
				"from bugs a, comment b " +
				"where a.bug_id = b.bugid " +
				"and (STR_TO_DATE(a.creation_ts, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " +
				"and (STR_TO_DATE(a.delta_ts, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " +
				"and trim(' ' from replace(a.bug_status, '\\n', '')) like 'RESOLVED' "+
				"and b.bugid in " +
				"(	select bug_id from bugs where trim(' ' from replace(a.product, '\n', '')) like \"" + product + "\"	)" +
				"group by b.bugid " +
				"order by b.bugid asc; " 
				);
		
		while(rs.next())
		{
			bug_id3.add(rs.getString("b.bugid"));
			totalComments.add(rs.getString("count(b.text)"));
			numOfDevs.add(rs.getString("count(distinct(b.who))"));
			interestSpan.add(rs.getString("timestampdiff(second, MIN(b.bug_when), MAX(b.bug_when))/3600"));
		}
		
		System.out.println("Retrieving Owner Informations...");
		
		rs = s.executeQuery(
				"select count(text), trim(' ' from replace(who, '\n', '')), bugid " +
				"from comment " +
				"where bugid in "+
				"(	select bug_id from bugs where trim(' ' from replace(product, '\n', '')) like '" + product + "' and trim(' ' from replace(bug_status, '\\n', '')) like 'RESOLVED' ) " +
				"and trim(' ' from replace(who, '\n', '')) in " +
				"( select trim(' ' from replace(assigned_to, '\n', '')) from bugs where trim(' ' from replace(product, '\n', '')) like '" + product + "' and trim(' ' from replace(bug_status, '\\n', '')) like 'RESOLVED' ) " +
				"and (STR_TO_DATE(bug_when, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " +	
				"group by bugid, who " +
				"order by bugid;"
				);
		
		while(rs.next())
		{
			ownerComments.add(rs.getString("count(text)"));
			owner2.add(rs.getString("trim(' ' from replace(who, '\n', ''))"));
			bug_id4.add(rs.getString("bugid"));
		}
		
		rs = s.executeQuery(
				"select count(bug_id), trim(' ' from replace(assigned_to, '\n', '')) " + 
				"from bugs " +
				"where trim(' ' from replace(product, '\n', '')) like \"" + product + "\" " +
				"and (STR_TO_DATE(creation_ts, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " +
				"and (STR_TO_DATE(delta_ts, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " +
				"and trim(' ' from replace(bug_status, '\\n', '')) like 'RESOLVED' "+
				"group by assigned_to;"
				);
		
		while(rs.next())
		{
			owner4.add(rs.getString("trim(' ' from replace(assigned_to, '\n', ''))"));
			ownerWorkload.add(rs.getString("count(bug_id)"));
		}
		
		rs = s.executeQuery(
				"select count(distinct(b.bugid)), trim(' ' from replace(b.who, '\n', '')) " +
				"from comment b " +
				"where b.bugid in " + 
				"(	select bug_id from bugs where trim(' ' from replace(product, '\n', '')) like '" + product + "' and trim(' ' from replace(bug_status, '\\n', '')) like 'RESOLVED' ) " +
				"and trim(' ' from replace(b.who, '\n', '')) in " + 
				"( select trim(' ' from replace(assigned_to, '\n', '')) from bugs where trim(' ' from replace(product, '\n', '')) like '" + product + "' and trim(' ' from replace(bug_status, '\\n', '')) like 'RESOLVED' ) " +
				"and (STR_TO_DATE(b.bug_when, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " +
				"group by who "
				);
		
		while(rs.next())
		{
			ownerComArc.add(rs.getString("count(distinct(b.bugid))"));
			owner3.add(rs.getString("trim(' ' from replace(b.who, '\n', ''))"));
		}
		
		
		StringBuilder matrix = new StringBuilder();
		
		//RFunctions rf = Controller.rf;
		
		//ArrayList<String> degNBetweenness = rf.rScript(fileContent, owner);
		//Column Headers
		//matrix.append("bug_id, owner, elapsed-time, component, version, rep-platform, op-sys, bug-status, resolution, priority, severity, target-milestone, duplicate, activity-level, number-of-comments, number-of-commenters, interest-span, number-of-comments-by-owner, owner-workload, owner-comment-arc, degree, betweenness, closeness, clustcoeff, eigencentrality, pagerank");
		matrix.append("bug_id, owner, elapsed-time, component, version, rep-platform, op-sys, bug-status, resolution, priority, severity, target-milestone, duplicate, activity-level, number-of-comments, number-of-commenters, interest-span, number-of-comments-by-owner, owner-workload, owner-comment-arc");
		matrix.append("\n");
		for(int i = 0; i < bug_id.size(); i++)
		{
			
			matrix.append(bug_id.get(i) + ", ");
			matrix.append(owner.get(i) + ", ");
			matrix.append(elapsedTime.get(i) + ", ");
			matrix.append(component.get(i) + ", ");
			matrix.append(version.get(i) + ", ");
			matrix.append(repPlatform.get(i) + ", ");
			matrix.append(op_sys.get(i) + ", ");
			matrix.append(bug_status.get(i) + ", ");
			matrix.append(resolution.get(i) + ", ");
			matrix.append(priority.get(i) + ", ");
			matrix.append(bugSeverity.get(i) + ", ");
			matrix.append(tgtMilestone.get(i) + ", ");
			matrix.append(dupeOf.get(i) + ", ");
			
			for(int j = 0; j < bug_id2.size(); j++)
			{
				if(bug_id.get(i).equals(bug_id2.get(j)))
				{
					matrix.append(activityLevel.get(j));
				}
			}
			matrix.append(", ");
			
			for(int j = 0; j < bug_id3.size(); j++)
			{
				if(bug_id.get(i).equals(bug_id3.get(j)))
				{
					matrix.append(totalComments.get(j));
				}
			}
			matrix.append(", ");
			
			for(int j = 0; j < bug_id3.size(); j++)
			{
				if(bug_id.get(i).equals(bug_id3.get(j)))
				{
					matrix.append(numOfDevs.get(j));
				}
			}
			matrix.append(", ");
			
			for(int j = 0; j < bug_id3.size(); j++)
			{
				if(bug_id.get(i).equals(bug_id3.get(j)))
				{
					matrix.append(interestSpan.get(j));
				}
			}
			matrix.append(", ");
			
			for(int j = 0; j < owner2.size(); j++)
			{
				
				if(	(owner.get(i).equals(owner2.get(j)))	&& (bug_id.get(i).equals(bug_id4.get(j))))
				{
					matrix.append(ownerComments.get(j));
				}	
			}
			matrix.append(", ");
			
			for(int j = 0; j < owner4.size(); j++)
			{
				if(owner.get(i).equals(owner4.get(j)))
				{
					matrix.append(ownerWorkload.get(j));
				}
			}
			matrix.append(", ");
			
			for(int j = 0; j < owner3.size(); j++)
			{
				if(owner.get(i).equals(owner3.get(j)))
				{
					matrix.append(ownerComArc.get(j));
				}
			}
			//matrix.append(", ");
			
			//matrix.append(degNBetweenness.get(i));
			matrix.append("\n");
		}
		
		fileContent = matrix.toString();
		System.out.println("");
		System.out.println("Generating .CSV File");
	}
	
	public void generateOwnersModel(String product, String startDate, String endDate, String dirName) throws Exception
	{
		ArrayList<String> owners		 		= new ArrayList<String>();
		ArrayList<String> bugsOwned		 		= new ArrayList<String>();
		ArrayList<String> assignedTo			= new ArrayList<String>();
		
		ArrayList<String> bugsCommented		 	= new ArrayList<String>();
		ArrayList<String> bugsCommentSpan	 	= new ArrayList<String>();
		
		ArrayList<String> commentsOnOwned		= new ArrayList<String>();
		ArrayList<String> commentsOffOwned	 	= new ArrayList<String>();
		
		ArrayList<String> noOfActivities	 	= new ArrayList<String>();
		
		ArrayList<String> avgElapsedTime	 	= new ArrayList<String>();
		ArrayList<String> medianElapsedTime		= new ArrayList<String>();
		
		ArrayList<String> avgInterestSpan	 	= new ArrayList<String>();
		ArrayList<String> medianInterestSpan	= new ArrayList<String>();
		
		ArrayList<String> congruency			= new ArrayList<String>();
		
		System.out.println("");
		System.out.println("Finding the Number of Bugs Owned by Each Developers...");
		
		
		rs = s.executeQuery(
				"select distinct(trim(' ' from replace(a.assigned_to, '\n', ''))), count(a.bug_id) " +
				"from bugs a " +
				"where trim(' ' from replace(a.product, '\n', '')) like '"+product+"' " +
				"and (STR_TO_DATE(a.creation_ts, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " +
				"and (STR_TO_DATE(a.delta_ts, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " +
				"and trim(' ' from replace(a.bug_status, '\\n', '')) like 'RESOLVED' "+
				"group by assigned_to " +
				"order by assigned_to;"
				); //Query to find the distinct developers working on the bugs
		//test
		System.out.println("Finding end..");
		int t=0;
		//test
		
		while(rs.next())
		{
			t=t+1;
			String owner = rs.getString("(trim(' ' from replace(a.assigned_to, '\n', '')))");
			owners.add(owner);
			bugsOwned.add(rs.getString("count(a.bug_id)"));
			
			ArrayList<String> developersSetDCN = new ArrayList<String>(); 
			ArrayList<String> developersSetDAN = new ArrayList<String>();
			
			//test
			System.out.println(" owner "+owner);
			System.out.println(" start1..");
			//test
			
		/*	rs2 = s2.executeQuery(
					"select trim(' ' from replace(a.who, '\n', '')),  trim(' ' from replace(b.who, '\n', '')) 'who' "
					+ "from comment a, comment b, bugs c "
					+ "where a.who <> b.who "
					+ "and a.bugid = b.bugid "
					+ "and a.bugid = c.bug_id "
					+ "and trim(' ' from replace(c.product, '\n', '')) like '"+product+"' "
					+ "and trim(' ' from replace(c.bug_status, '\n', '')) like 'RESOLVED' "
					+ "and trim(' ' from replace(a.who, '\n', '')) like '"+owner+"' "
					+ "and trim(' ' from replace(b.who, '\n', '')) IN "
					+ "(select distinct(trim(' ' from replace(assigned_to, '\n', ''))) from bugs where trim(' ' from replace(product, '\n', '')) like 'gnome-vfs') "
					+ "group by a.who, b.who "
					+ "order by a.who;");*/
			
			rs2 = s2.executeQuery(
					"select a.who,b.who 'who' "
					+" from comment_temp  a,comment_temp  b, bugs_temp1 c "
					+" where a.who <> b.who "
					+" and a.bugid = b.bugid"
					+" and a.bugid = c.bug_id"
					+" and c.product like '"+product+"' "
					+" and c.bug_status like 'RESOLVED' "
					+" and a.who like '"+owner+"' "
					+" and b.who IN "
					+" ( SELECT assigned_to FROM bugs_temp2) "
					+ " group by a.who, b.who "
					+ " order by a.who;");
			int co=0;
			
			while(rs2.next())
			{
				co++;
				developersSetDCN.add(rs2.getString("who"));
			}
			//test
			System.out.println("Total no of record :-"+co);
			System.out.println("end 1..");
			//test
			//test
			System.out.println(" start2..");
			//test
			
			/*rs2 = s2.executeQuery(
					"select trim(' ' from replace(a.who, '\n', '')),  trim(' ' from replace(b.who, '\n', '')) 'who' "
					+ "from activity a, activity b, bugs c "
					+ "where a.who <> b.who "
					+ "and a.bugid = b.bugid "
					+ "and a.bugid = c.bug_id "
					+ "and trim(' ' from replace(c.product, '\n', '')) like '"+product+"' "
					+ "and trim(' ' from replace(c.bug_status, '\n', '')) like 'RESOLVED' "
					+ "and trim(' ' from replace(a.who, '\n', '')) like '"+owner+"' "
					+ "and trim(' ' from replace(b.who, '\n', '')) IN "
					+ "(select distinct(trim(' ' from replace(assigned_to, '\n', ''))) from bugs where trim(' ' from replace(product, '\n', '')) like 'gnome-vfs') "
					+ "group by a.who, b.who "
					+ "order by a.who;");*/
			
			rs2= s2.executeQuery(
					"select a.who,b.who 'who'"
					+ " from activity_temp a,activity_temp b,bugs_temp1 c "
					+ " where a.who <> b.who "
					+ " and a.bugid = b.bugid "
					+ " and a.bugid = c.bug_id "
					+ " and c.product like '"+product+"' "
					+ " and c.bug_status like 'RESOLVED' "
					+ " and a.who like '"+owner+"' "
					+ " and b.who IN (select assigned_to from bugs_temp2 where product like 'gnome-vfs') "
					+ " group by a.who, b.who "
					+ "order by a.who;");
					
			//test
			System.out.println("end 2..");
			//test
			
			while(rs2.next())
			{
				developersSetDAN.add(rs2.getString("who"));
			}
			
			if(!(developersSetDCN.isEmpty()&&developersSetDAN.isEmpty()))
			{
				float unionCardinality = 0;
				float intersectionCardinality = 0;
				
				for(int countDCN = 0; countDCN < developersSetDCN.size(); countDCN++)
				{
					for(int countDAN = 0; countDAN < developersSetDAN.size(); countDAN++)
					{
						if(developersSetDCN.get(countDCN).equals(developersSetDAN.get(countDAN)))
						{
							intersectionCardinality = intersectionCardinality + 1;
						}
					}
				}
				
				for(int countDCN = 0; countDCN < developersSetDCN.size(); countDCN++)
				{
					unionCardinality = unionCardinality + 1;
				}
				
				for(int countDAN = 0; countDAN < developersSetDAN.size(); countDAN++)
				{
					int comparator = 0;
					for(int countDCN = 0; countDCN < developersSetDCN.size(); countDCN++)
					{
						if(developersSetDCN.get(countDCN).equals(developersSetDAN.get(countDAN)))
						{
							comparator = comparator + 1;
						}
					}
					
					if(comparator == 0)
					{
						unionCardinality = unionCardinality + 1;
					}
				}
				
				float congruence = intersectionCardinality/unionCardinality;
				
				congruency.add(congruence + "");
			} else
			{
				congruency.add("0");
			}
			//test
			System.out.println("Data record ="+t);
			//test
		}
		
		System.out.println("Calculating the Number of Comments and the Timespan by the Developers...");
		
		//test
		System.out.println("start 3..");
		System.out.println("owner size =>"+owners.size());
		//test
		
		for(int i = 0; i < owners.size(); i++)
		{
			rs = s.executeQuery(
					"select count(text), timestampdiff(second, MIN(bug_when), MAX(bug_when))/3600 AS comment_span "+
					"from comment " +
					"where bugid in " +
					"(	select bug_id from bugs where trim(' ' from replace(product, '\n', '')) like '"+product+"'	) " +
					"and trim(' ' from replace(who, '\n', '')) like '"+owners.get(i) + "' " +
					"and (STR_TO_DATE(bug_when, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " +
					"group by who " +
					"order by who;"
					); //Query to find the distinct developers working on the bugs
			
				
			String comment = "";
			String commentSpan = "";
			//test
			int y=0;
			System.out.println("start 3a..");
			//test
			while(rs.next())
			{	
				//test
				y=y+1;
				//test
				
				comment = rs.getString("count(text)");
				commentSpan = rs.getString("comment_span");
				
				
				bugsCommented.add(comment);
				bugsCommentSpan.add(commentSpan);
			}
			
			//test
			System.out.println("end 3a..");
			System.out.println("total record "+y);
			//test
			
			if(comment.isEmpty())
			{
				comment = "0";
				bugsCommented.add(comment);
			}
			
			if(commentSpan.isEmpty())
			{
				commentSpan = "0";
				bugsCommentSpan.add(commentSpan);
			}
				
		}
		
		//test
		System.out.println("end 3..");
		//test
		
		System.out.println("Calculating Activity Level of Each Developer...");
		
		//test
		System.out.println("start 4..");
		System.out.println("owner size =>"+owners.size());
		//test
		
		for(int i = 0; i < owners.size(); i++)
		{
			//test
		     t=0;
			System.out.println("start 4a..");
			//test
			
			rs = s.executeQuery(
					"select who, count(bug_when) " +
					"from activity " +
					"where bugid in " +
					"(	select bug_id from bugs where trim(' ' from replace(product, '\n', '')) like '"+product+"'	) " +
					"and trim(' ' from replace(who, '\n', '')) like '"+owners.get(i)+"' " +
					"and (STR_TO_DATE(bug_when, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " +
					"group by who " +
					"order by who;"
					); 
			
			String numOfAct = "";
			while(rs.next())
			{
				//test
				t=t+1;
				//test
				numOfAct = rs.getString("count(bug_when)");
				
				noOfActivities.add(numOfAct);
			}
			
			//test
			System.out.println("end 4a..");
			System.out.println("total record "+t);
			//test
			
			if(numOfAct.isEmpty())
			{
				numOfAct = "0";
				noOfActivities.add(numOfAct);
			}
			
		}
		
		//test
		System.out.println("end 4..");
		//test
		
		System.out.println("Calculating Average Elapsed Time...");
		
		//test
		System.out.println("start 5..");
		t=0;
		//test
		
		rs = s.executeQuery(
				"select a.assigned_to,  avg(timestampdiff(second, a.creation_ts, a.delta_ts)/3600) as avgElapsedTime " +
				"from bugs a " +
				"where trim(' ' from replace(a.product, '\\n', '')) like '"+product+"' " +
				"and (STR_TO_DATE(a.creation_ts, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " +
				"and (STR_TO_DATE(a.delta_ts, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " +
				"and trim(' ' from replace(a.bug_status, '\\n', '')) like 'RESOLVED' "+
				"group by a.assigned_to " +
				"order by a.assigned_to; "
				); //Query to find the distinct developers working on the bugs
		//test
		System.out.println("end 5..");
		//test
		
		while(rs.next())
		{
			//test
			t=t+1;
			//test
			
			avgElapsedTime.add(rs.getString("avgElapsedTime"));
		}
		
		System.out.println("Calculating Average Interest Span...");
		
		//test
		System.out.println("total record =>"+t);
		System.out.println("start 6..");
		//test
		
		rs = s.executeQuery(
				"select a.ownerz, avg(a.interest_span) " +
				"from " +
				"(select trim(' ' from replace(a.assigned_to, '\n', '')) as ownerz,  timestampdiff(second, MIN(b.bug_when), MAX(b.bug_when))/3600 as interest_span " +
				"from bugs a, comment b " +
				"where a.bug_id = b.bugid " +
				"and (STR_TO_DATE(creation_ts, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " +
				"and (STR_TO_DATE(delta_ts, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " +
				"and (STR_TO_DATE(b.bug_when, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " +
				"and trim(' ' from replace(a.bug_status, '\\n', '')) like 'RESOLVED' "+
				"and b.bugid in " +
				"(	select bug_id from bugs where trim(' ' from replace(a.product, '\n', '')) like '"+product+"' 	) " +
				"group by b.bugid " +
				"order by b.bugid asc) a " +
				"group by ownerz " +
				"order by ownerz; " 
				); //Query to find the distinct developers working on the bugs
		//test
		System.out.println("end 6..");
		t=0;
		//test
		
		while(rs.next())
		{
			//test
			t=t+1;
			//test
			assignedTo.add(rs.getString("ownerz"));
			avgInterestSpan.add(rs.getString("avg(a.interest_span)"));
		}
		
		//test
		System.out.println("total record =>"+t);
		System.out.println("start 7..");
		System.out.println("owner size =>"+owners.size());
		//test
				
		System.out.println("Retrieving Specific Owners' Data and Median Elapsed and Interest Span...");
		/* A product can have many bugs, and not every bugs are owned by a single developer
		 * The next few queries require it to be repeated N times.
		 * N is the number of distinct developers that owns the bugs in the specified product.
		 */
		for(int i = 0; i < owners.size(); i++)
		{
			//test
			System.out.println("start 7a..");
			t=0;
			//test
			
			ArrayList<Float> elapsedTime 			= new ArrayList<Float>();
			ArrayList<Float> interestSpan 			= new ArrayList<Float>();
			//find the number of times the owner has commented on their own bugs
			rs = s.executeQuery(
					"select who, count(text) " +
					"from comment " +
					"where bugid in " +
					"(	select bug_id from bugs where trim(' ' from replace(product, '\n', '')) like '"+product+"' and assigned_to like '%" +owners.get(i) + "%' ) " +
					"and (STR_TO_DATE(bug_when, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " +
					"and who like '%" +owners.get(i) + "%' ; " 
					); //Query to find the distinct developers working on the bugs
			
			String comOnOwned = "";
			
			while(rs.next())
			{
				t=t+1;
				comOnOwned = rs.getString("count(text)");
				commentsOnOwned.add(comOnOwned);
			}
			//test
			System.out.println("end 7a..");
			System.out.println("total record "+t);
			//test
			
			if(comOnOwned.isEmpty())
			{
				comOnOwned = "0";
				commentsOnOwned.add(comOnOwned);
			}
			
			//find the number of times the owner has commented on bugs that they don't own
			rs = s.executeQuery(
					"select who, count(text) " +
					"from comment " +
					"where bugid in " +
					"(	select bug_id from bugs where trim(' ' from replace(product, '\n', '')) like '"+product+"' and assigned_to not like '%" +owners.get(i) + "%' ) " +
					"and who like '%" +owners.get(i) + "%' " +
					"and (STR_TO_DATE(bug_when, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"');"
					);
			
			String comOffOwned = "";
			
			while(rs.next())
			{
				comOffOwned = rs.getString("count(text)");
				commentsOffOwned.add(comOffOwned);
			}
			
			if(comOffOwned.isEmpty())
			{
				comOffOwned = "0";
				commentsOffOwned.add(comOffOwned);
			}
			
			//find the elapsed time for every bug the owner has
			rs = s.executeQuery(
					"select timestampdiff(second, a.creation_ts, a.delta_ts)/3600 as elapsed_time " +
					"from bugs a " +
					"where trim(' ' from replace(a.product, '\n', '')) like '"+product+"' " +
					"and a.assigned_to like '%" +owners.get(i) + "%' " +
					"and (STR_TO_DATE(a.creation_ts, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " +
					"and (STR_TO_DATE(a.delta_ts, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " +
					"and trim(' ' from replace(a.bug_status, '\\n', '')) like 'RESOLVED' "+
					"order by timestampdiff(second, a.creation_ts, a.delta_ts)/3600; "
					); //Query to find the distinct developers working on the bugs
			
			float elTime = 0.0f;
			
			while(rs.next())
			{
				elTime = elTime + rs.getFloat("elapsed_time");
				
				elapsedTime.add(elTime);
			}
			
			if(elTime == 0.0f)
			{
				elapsedTime.add(elTime);
			}
			
			//find the median of the elapsed time
			int mid = elapsedTime.size()/2; 
			float median = elapsedTime.get(mid); 
			if (elapsedTime.size()%2 == 0) 
			{ 
				median = (median + elapsedTime.get(mid-1))/2; 
			}
			
			medianElapsedTime.add(""+median);
			
			//find median interest spans for every bug the owner has
			rs = s.executeQuery(
					"select timestampdiff(second, MIN(b.bug_when), MAX(b.bug_when))/3600 as interest_span " +
					"from bugs a, comment b " +
					"where a.bug_id = b.bugid " +
					"and b.bugid in " +
					"(	select bug_id from bugs where trim(' ' from replace(a.product, '\n', '')) like '"+product+"'	) " +
					"AND a.assigned_to like '%" +owners.get(i) + "%' " +
					"and (STR_TO_DATE(a.creation_ts, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " +
					"and (STR_TO_DATE(a.delta_ts, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " +
					"and trim(' ' from replace(a.bug_status, '\\n', '')) like 'RESOLVED' "+
					"and (STR_TO_DATE(b.bug_when, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " +
					"group by b.bugid " +
					"order by timestampdiff(second, MIN(b.bug_when), MAX(b.bug_when))/3600 asc " 
					);
			Float intSpan = 0.0f;
			
			while(rs.next())
			{
				intSpan = intSpan + rs.getFloat("interest_span");
				interestSpan.add(intSpan);
			}
			
			if(intSpan == 0.0f)
			{
				interestSpan.add(intSpan);
			}
			//find the median of the interest span
			int mid2 = interestSpan.size()/2; 
			float median2 = interestSpan.get(mid2); 
			if (interestSpan.size()%2 == 0) 
			{ 
				median2 = (median2 + interestSpan.get(mid2-1))/2; 
			}
		
			medianInterestSpan.add(""+median2);
			
		}
		
		ArrayList<String> mostParameters = new ArrayList<String>();
		ArrayList<String> otherParameters = new ArrayList<String>();
		for(int i = 0; i < owners.size(); i++)
		{
			mostParameters.add(bugsOwned.get(i) + ", "
								+ bugsCommented.get(i) + ", "
								+ bugsCommentSpan.get(i) + ", "
								+ commentsOnOwned.get(i) + ", "
								+ commentsOffOwned.get(i) + ", "
								+ noOfActivities.get(i) + ", "
								+ avgElapsedTime.get(i) + ", "
								+ medianElapsedTime.get(i) + ", "
								);
			otherParameters.add(medianInterestSpan.get(i) + ", "
								+ congruency.get(i));
		}
		
		fileContent = nb.ownersModelGnome(owners, assignedTo, avgInterestSpan, mostParameters, otherParameters);
		
		System.out.println("");
		System.out.println("Generating .CSV File.");
	}
	
	public void generateCommenterModel(String product, String startDate, String endDate, String dirName) throws Exception
	{
		ArrayList<String> commenters 			= new ArrayList<String>();
		ArrayList<String> owners2		 		= new ArrayList<String>();
		
		ArrayList<String> owners		 		= new ArrayList<String>();
		ArrayList<String> bugsOwned		 		= new ArrayList<String>();
		ArrayList<String> assignedTo			= new ArrayList<String>();
		
		ArrayList<String> bugsCommented		 	= new ArrayList<String>();
		ArrayList<String> bugsCommentSpan	 	= new ArrayList<String>();
		
		ArrayList<String> commentsOnOwned		= new ArrayList<String>();
		ArrayList<String> commentsOffOwned	 	= new ArrayList<String>();
		
		ArrayList<String> noOfActivities	 	= new ArrayList<String>();
		
		ArrayList<String> avgElapsedTime	 	= new ArrayList<String>();
		ArrayList<String> medianElapsedTime		= new ArrayList<String>();
		
		ArrayList<String> avgInterestSpan	 	= new ArrayList<String>();
		ArrayList<String> medianInterestSpan	= new ArrayList<String>();
		
		ArrayList<String> congruency 			= new ArrayList<String>();
		
		System.out.println("");
		System.out.println("Finding the Distinct Commenters...");
		
		rs = s.executeQuery(
				"select distinct(trim(' ' from replace(b.who, '/n', ''))) 'who' "
				+ "from bugs a, comment b "
				+ "where a.bug_id = b.bugid "
				+ "and trim(' ' from replace(a.product, '\n', '')) like '"+product+"' "
				+ "and (STR_TO_DATE(b.bug_when, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " 
				+ "and trim(' ' from replace(a.bug_status, '\\n', '')) like 'RESOLVED' "
				+ "order by assigned_to;"
				);
		
		while(rs.next())
		{
			String commenter = rs.getString("who").trim();
			commenters.add(commenter);
			
			ArrayList<String> developersSetDCN = new ArrayList<String>(); 
			ArrayList<String> developersSetDAN = new ArrayList<String>();
			
			rs2 = s2.executeQuery(
					"select trim(' ' from replace(a.who, '\n', '')),  trim(' ' from replace(b.who, '\n', '')) 'who' "
					+ "from comment a, comment b, bugs c "
					+ "where a.who <> b.who "
					+ "and a.bugid = b.bugid "
					+ "and a.bugid = c.bug_id "
					+ "and trim(' ' from replace(c.product, '\n', '')) like '"+product+"' "
					+ "and trim(' ' from replace(c.bug_status, '\n', '')) like 'RESOLVED' "
					+ "and trim(' ' from replace(a.who, '\n', '')) like '"+commenter+"' "
					+ "and trim(' ' from replace(b.who, '\n', '')) IN "
					+ "(select distinct(trim(' ' from replace(assigned_to, '\n', ''))) from bugs where trim(' ' from replace(product, '\n', '')) like 'gnome-vfs') "
					+ "group by a.who, b.who "
					+ "order by a.who;");
			
			while(rs2.next())
			{
				developersSetDCN.add(rs2.getString("who"));
			}
			
			rs2 = s2.executeQuery(
					"select trim(' ' from replace(a.who, '\n', '')),  trim(' ' from replace(b.who, '\n', '')) 'who' "
					+ "from activity a, activity b, bugs c "
					+ "where a.who <> b.who "
					+ "and a.bugid = b.bugid "
					+ "and a.bugid = c.bug_id "
					+ "and trim(' ' from replace(c.product, '\n', '')) like '"+product+"' "
					+ "and trim(' ' from replace(c.bug_status, '\n', '')) like 'RESOLVED' "
					+ "and trim(' ' from replace(a.who, '\n', '')) like '"+commenter+"' "
					+ "and trim(' ' from replace(b.who, '\n', '')) IN "
					+ "(select distinct(trim(' ' from replace(assigned_to, '\n', ''))) from bugs where trim(' ' from replace(product, '\n', '')) like 'gnome-vfs') "
					+ "group by a.who, b.who "
					+ "order by a.who;");
			
			while(rs2.next())
			{
				developersSetDAN.add(rs2.getString("who"));
			}
			
			if(!(developersSetDCN.isEmpty()&&developersSetDAN.isEmpty()))
			{
				float unionCardinality = 0;
				float intersectionCardinality = 0;
				
				for(int countDCN = 0; countDCN < developersSetDCN.size(); countDCN++)
				{
					for(int countDAN = 0; countDAN < developersSetDAN.size(); countDAN++)
					{
						if(developersSetDCN.get(countDCN).equals(developersSetDAN.get(countDAN)))
						{
							intersectionCardinality = intersectionCardinality + 1;
						}
					}
				}
				
				for(int countDCN = 0; countDCN < developersSetDCN.size(); countDCN++)
				{
					unionCardinality = unionCardinality + 1;
				}
				
				for(int countDAN = 0; countDAN < developersSetDAN.size(); countDAN++)
				{
					int comparator = 0;
					for(int countDCN = 0; countDCN < developersSetDCN.size(); countDCN++)
					{
						if(developersSetDCN.get(countDCN).equals(developersSetDAN.get(countDAN)))
						{
							comparator = comparator + 1;
						}
					}
					
					if(comparator == 0)
					{
						unionCardinality = unionCardinality + 1;
					}
				}
				
				float congruence = intersectionCardinality/unionCardinality;
				
				congruency.add(congruence + "");
			} else
			{
				congruency.add("0");
			}
		}
		
		System.out.println("");
		System.out.println("Finding the Number of Bugs Owned by Each Developers...");
		
		rs = s.executeQuery(
				"select distinct(trim(' ' from replace(a.assigned_to, '\n', ''))) 'who', count(a.bug_id) " +
				"from bugs a " +
				"where trim(' ' from replace(product, '\n', '')) like '"+product+"' " +
				"and (STR_TO_DATE(creation_ts, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " +
				"and (STR_TO_DATE(delta_ts, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " +
				"and trim(' ' from replace(a.bug_status, '\\n', '')) like 'RESOLVED' " +
				"group by assigned_to " +
				"order by assigned_to;"
				); //Query to find the distinct developers working on the bugs
		
		while(rs.next())
		{
			owners.add(rs.getString("who"));
			bugsOwned.add(rs.getString("count(a.bug_id)"));
		}
		
		System.out.println("Calculating the Number of Comments and the Timespan by the Developers...");

		for(int i = 0; i < commenters.size(); i++)
		{
			rs = s.executeQuery(
					"select count(text), timestampdiff(second, MIN(bug_when), MAX(bug_when))/3600 AS comment_span "+
					"from comment " +
					"where bugid in " +
					"(	select bug_id from bugs where trim(' ' from replace(product, '\n', '')) like '"+product+"'	) " +
					"and trim(' ' from replace(who, '\n', '')) like '"+commenters.get(i) + "' " +
					"and (STR_TO_DATE(bug_when, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " +
					"group by who " +
					"order by who;"
					); //Query to find the distinct developers working on the bugs
			
			String comment = "";
			String commentSpan = "";
			
			while(rs.next())
			{	
				comment = rs.getString("count(text)");
				commentSpan = rs.getString("comment_span");
				
				
				bugsCommented.add(comment);
				bugsCommentSpan.add(commentSpan);
			}
			
			if(comment.isEmpty())
			{
				comment = "0";
				bugsCommented.add(comment);
			}
			
			if(commentSpan.isEmpty())
			{
				commentSpan = "0";
				bugsCommentSpan.add(commentSpan);
			}
				
		}
		
		System.out.println("Calculating Activity Level of Each Developer...");
		
		for(int i = 0; i < commenters.size(); i++)
		{
			rs = s.executeQuery(
					"select who, count(bug_when) " +
					"from activity " +
					"where bugid in " +
					"(	select bug_id from bugs where trim(' ' from replace(product, '\n', '')) like '"+product+"'	) " +
					"and trim(' ' from replace(who, '\n', '')) like '"+commenters.get(i)+"' " +
					"and (STR_TO_DATE(bug_when, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " +
					"group by who " +
					"order by who;"
					); 
			
			String numOfAct = "";
			
			while(rs.next())
			{
				numOfAct = rs.getString("count(bug_when)");
				
				noOfActivities.add(numOfAct);
			}
			
			if(numOfAct.isEmpty())
			{
				numOfAct = "0";
				noOfActivities.add(numOfAct);
			}
			
		}
		
		System.out.println("Calculating Average Elapsed Time...");
		
		rs = s.executeQuery(
				"select a.assigned_to,  avg(timestampdiff(second, a.creation_ts, a.delta_ts)/3600) as avgElapsedTime " +
				"from bugs a " +
				"where trim(' ' from replace(a.product, '\n', '')) like '"+product+"' " +
				"and (STR_TO_DATE(a.creation_ts, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " +
				"and (STR_TO_DATE(a.delta_ts, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " +
				"and trim(' ' from replace(a.bug_status, '\\n', '')) like 'RESOLVED' " +
				"group by a.assigned_to " +
				"order by a.assigned_to; "
				); //Query to find the distinct developers working on the bugs
		
		while(rs.next())
		{
			String owner = rs.getString("a.assigned_to");
			owners.add(owner);
			avgElapsedTime.add(rs.getString("avgElapsedTime"));
		}
		
		System.out.println("Calculating Average Interest Span...");
		
		rs = s.executeQuery(
				"select a.ownerz, avg(a.interest_span) " +
				"from " +
				"(select trim(' ' from replace(a.assigned_to, '\n', '')) as ownerz,  timestampdiff(second, MIN(b.bug_when), MAX(b.bug_when))/3600 as interest_span " +
				"from bugs a, comment b " +
				"where a.bug_id = b.bugid " +
				"and (STR_TO_DATE(creation_ts, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " +
				"and (STR_TO_DATE(delta_ts, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " +
				"and (STR_TO_DATE(b.bug_when, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " +
				"and trim(' ' from replace(a.bug_status, '\\n', '')) like 'RESOLVED' " +
				"and b.bugid in " +
				"(	select bug_id from bugs where trim(' ' from replace(a.product, '\n', '')) like '"+product+"' 	) " +
				"group by b.bugid " +
				"order by b.bugid asc) a " +
				"group by ownerz " +
				"order by ownerz; " 
				); //Query to find the distinct developers working on the bugs
		
		while(rs.next())
		{
			assignedTo.add(rs.getString("ownerz"));
			avgInterestSpan.add(rs.getString("avg(a.interest_span)"));
		}
		
		
		System.out.println("Retrieving Specific Owners' Data and Median Elapsed and Interest Span...");
		/* A product can have many bugs, and not every bugs are owned by a single developer
		 * The next few queries require it to be repeated N times.
		 * N is the number of distinct developers that owns the bugs in the specified product.
		 */
		for(int i = 0; i < commenters.size(); i++)
		{
			ArrayList<Float> elapsedTime 			= new ArrayList<Float>();
			ArrayList<Float> interestSpan 			= new ArrayList<Float>();
			//find the number of times the owner has commented on their own bugs
			rs = s.executeQuery(
					"select who, count(text) " +
					"from comment " +
					"where bugid in " +
					"(	select bug_id from bugs where trim(' ' from replace(product, '\n', '')) like '"+product+"' and assigned_to like '%" +commenters.get(i) + "%' ) " +
					"and (STR_TO_DATE(bug_when, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " +
					"and who like '%" +commenters.get(i) + "%' ; " 
					); //Query to find the distinct developers working on the bugs
			
			String comOnOwned = "";
			
			while(rs.next())
			{
				comOnOwned = rs.getString("count(text)");
				commentsOnOwned.add(comOnOwned);
			}
			
			if(comOnOwned.isEmpty())
			{
				comOnOwned = "0";
				commentsOnOwned.add(comOnOwned);
			}
			
			//find the number of times the owner has commented on bugs that they don't own
			rs = s.executeQuery(
					"select who, count(text) " +
					"from comment " +
					"where bugid in " +
					"(	select bug_id from bugs where trim(' ' from replace(product, '\n', '')) like '"+product+"' and assigned_to not like '%" +commenters.get(i) + "%' ) " +
					"and who like '%" +commenters.get(i) + "%' " +
					"and (STR_TO_DATE(bug_when, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"');"
					);
			
			String comOffOwned = "";
			
			while(rs.next())
			{
				comOffOwned = rs.getString("count(text)");
				commentsOffOwned.add(comOffOwned);
			}
			
			if(comOffOwned.isEmpty())
			{
				comOffOwned = "0";
				commentsOffOwned.add(comOffOwned);
			}
			
			//find the elapsed time for every bug the owner has
			rs = s.executeQuery(
					"select timestampdiff(second, a.creation_ts, a.delta_ts)/3600 as elapsed_time " +
					"from bugs a " +
					"where trim(' ' from replace(a.product, '\n', '')) like '"+product+"' " +
					"and a.assigned_to like '%" +commenters.get(i) + "%' " +
					"and (STR_TO_DATE(a.creation_ts, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " +
					"and (STR_TO_DATE(a.delta_ts, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " +
					"and trim(' ' from replace(a.bug_status, '\\n', '')) like 'RESOLVED' " +
					"order by timestampdiff(second, a.creation_ts, a.delta_ts)/3600; "
					); //Query to find the distinct developers working on the bugs
			
			float elTime = 0.0f;
			
			while(rs.next())
			{
				elTime = elTime + rs.getFloat("elapsed_time");
				
				elapsedTime.add(elTime);
			}
			
			if(elTime == 0.0f)
			{
				elapsedTime.add(elTime);
			}
			
			//find the median of the elapsed time
			int mid = elapsedTime.size()/2; 
			float median = elapsedTime.get(mid); 
			if (elapsedTime.size()%2 == 0) 
			{ 
				median = (median + elapsedTime.get(mid-1))/2; 
			}
			
			medianElapsedTime.add(""+median);
				
			
			
			//find median interest spans for every bug the owner has
			rs = s.executeQuery(
					"select timestampdiff(second, MIN(b.bug_when), MAX(b.bug_when))/3600 as interest_span " +
					"from bugs a, comment b " +
					"where a.bug_id = b.bugid " +
					"and b.bugid in " +
					"(	select bug_id from bugs where trim(' ' from replace(a.product, '\n', '')) like '"+product+"'	) " +
					"AND a.assigned_to like '%" +commenters.get(i) + "%' " +
					"and (STR_TO_DATE(a.creation_ts, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " +
					"and (STR_TO_DATE(a.delta_ts, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " +
					"and (STR_TO_DATE(b.bug_when, '%Y-%m-%d %H:%i:%s') between '"+startDate+"' and '"+endDate+"') " +
					"and trim(' ' from replace(a.bug_status, '\\n', '')) like 'RESOLVED' " +
					"group by b.bugid " +
					"order by timestampdiff(second, MIN(b.bug_when), MAX(b.bug_when))/3600 asc " 
					);
			float intSpan = 0.0f;
			
			while(rs.next())
			{
				intSpan = intSpan + rs.getFloat("interest_span");
				interestSpan.add(intSpan);
			}
			
			if(intSpan == 0.0f)
			{
				interestSpan.add(intSpan);
			}
			//find the median of the interest span
			int mid2 = interestSpan.size()/2; 
			float median2 = interestSpan.get(mid2); 
			if (interestSpan.size()%2 == 0) 
			{ 
				median2 = (median2 + interestSpan.get(mid2-1))/2; 
			}
		
			medianInterestSpan.add(""+median2);
			
		}
		
		/*RFunctions rf = Controller.rf;
		ArrayList<String> dcnParameters = rf.nwParameters(dirName, product, owners, true);
		
		ArrayList<String> danParameters = rf.nwParameters(dirName, product, owners, true);*/
		
		
		StringBuilder matrix = new StringBuilder();
		
		matrix.append("developer, bugs-owned, bugs-commented, comment-span, comments-on-owned, "
				+ "comments-on-nonowned, noof-activities, average-elapsed-time, median-elapsed-time, "
				+ "average-interest-span, median-interest-span, congruence");
				//+ "dcn.degree, dcn.betweenness, dcn.clustcoeff, dcn.eigencentrality, dcn.closeness, dcn.pagerank, "
				//+ "dan.degree, dan.betweenness, dan.clustcoeff, dan.eigencentrality, dan.closeness, dan.pagerank");
		matrix.append("\n");
		
		
		String tempString = "0";
		String tempString2= "0";
		for(int i = 0; i < commenters.size(); i++)
		{
			matrix.append(commenters.get(i).trim() + ", ");
			for(int j = 0; j < owners.size(); j++)
			{
				if(owners.get(j).equalsIgnoreCase(commenters.get(i)))
				{
					matrix.append(bugsOwned.get(j) + ", ");
				}else
				{
					matrix.append("0, ");
				}
			}
			
			matrix.append(bugsCommented.get(i) + ", ");
			matrix.append(bugsCommentSpan.get(i) + ", ");
			matrix.append(commentsOnOwned.get(i) + ", ");
			matrix.append(commentsOffOwned.get(i) + ", ");
			matrix.append(noOfActivities.get(i) + ", ");
			
			for(int j = 0; j < assignedTo.size(); j++)
			{
				if(commenters.get(i).equals(assignedTo.get(j)))
				{
					tempString = avgInterestSpan.get(j);
				}
			}
			//matrix.append(avgElapsedTime.get(i) + ", ");
			
			for(int j = 0; j < owners2.size(); j++)
			{
				if(commenters.get(i).equals(owners2.get(j)))
				{
					tempString2 = avgElapsedTime.get(j);
				}
			}
			matrix.append(tempString2 + ", ");
			matrix.append(medianElapsedTime.get(i) + ", ");
			matrix.append(tempString + ", ");
			matrix.append(medianInterestSpan.get(i) + ", ");
			matrix.append(congruency.get(i));
			//matrix.append(dcnParameters.get(i) + ", ");
			//matrix.append(danParameters.get(i));
			
			matrix.append("\n");
			tempString = "0";
			tempString2= "0";
		}
		
		fileContent = matrix.toString();
	}
	
	public float socioTechCongruence(String directoryName, String productName) throws IOException 
	{
		ArrayList<Boolean> dcnMatrix = new ArrayList<Boolean>();
		ArrayList<Boolean> danMatrix = new ArrayList<Boolean>();
		
		File dcnFile = new File(directoryName+"/"+productName+"/"+productName+"-DCN-dev-by-devs.csv");
		File danFile = new File(directoryName+"/"+productName+"/"+productName+"-DAN-dev-by-devs.csv");
		if(!dcnFile.exists()||!danFile.exists())
			return Float.NaN;
		
		int dandcnIntersection 	= 0;
		int danTrue 			= 0;
		
		String [] nextLine;
		
		directoryName = directoryName.replaceAll("\\\\", "/");
		CSVReader reader = new CSVReader(new FileReader(directoryName + "/" + productName + "/" + productName + "-DCN-dev-by-devs.csv"), ',', '\"', 1);
		
		while ((nextLine = reader.readNext()) != null) 
		{
			for(int rowLength = 1; rowLength < nextLine.length; rowLength++) 
			{
				
				if(nextLine[rowLength].trim().isEmpty()) nextLine[rowLength] = "0";
				
				if(Integer.parseInt(nextLine[rowLength].trim())==0) 
				{
					dcnMatrix.add(false);
				} else {
					dcnMatrix.add(true);
				}
			}
		}
		
		reader = new CSVReader(new FileReader(directoryName + "/" + productName + "/" + productName + "-DAN-dev-by-devs.csv"), ',', '\"', 1);
		
		while ((nextLine = reader.readNext()) != null) 
		{
			for(int rowLength = 1; rowLength < nextLine.length; rowLength++) 
			{
				
				if(nextLine[rowLength].trim().isEmpty()) nextLine[rowLength] = "0";
				
				if(Integer.parseInt(nextLine[rowLength].trim())==0) 
				{
					danMatrix.add(false);
				} else {
					danMatrix.add(true);
				}
			}
		}
		
		for(int i = 0; i < danMatrix.size(); i++) 
		{
			if(danMatrix.get(i)) 
			{
				danTrue++;
			}
			try 
			{
				if(danMatrix.get(i)||dcnMatrix.get(i)) 
				{
					dandcnIntersection++;
				}
			} catch (Exception e) 
			{
				System.out.println("ERROR: Vertex Length Mismatch!");
				return Float.NaN;
			}
		}
		
		float congruence = 0;
		if(danTrue == 0) congruence = 0;
		else congruence = (float)dandcnIntersection/danTrue;
		
		return (float)dandcnIntersection/danTrue;
	}
}