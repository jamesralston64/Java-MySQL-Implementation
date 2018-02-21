/*****************************
Query the Crime Database
*****************************/
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.CallableStatement;
import java.util.Date;
import java.lang.String;

public class MyQuery {

   private Connection conn = null;
	private Statement statement = null;
	private ResultSet resultSet = null;
    
    public MyQuery(Connection c)throws SQLException
    {
        conn = c;
        statement = conn.createStatement();
    }
    
	 //Query 0: this is a sample query provided by Dr. Li
	 public void findChargeDate() throws SQLException
    {
        String query  = "SELECT crime_id, date_charged " +
                            "FROM crimes " + 
                            "WHERE date_charged <= \'2008-10-22\';";

        resultSet = statement.executeQuery(query);
    }
    
    public void printChargeDate() throws IOException, SQLException
    {
		System.out.println("******** Query 0 ********");
		System.out.println("Crime_ID\t"+"Charge_Date");

        while (resultSet.next()) {
			// It is possible to get the columns via name
			// also possible to get the columns via the column number
			// which starts at 1
			// e.g. resultSet.getString(2);
			int crime_id = resultSet.getInt(1);
			Date charge_date = resultSet.getDate("date_charged");
			System.out.println(crime_id+"\t\t"+charge_date);
		}        
    }

	 //Query 1
    public void findBestOfficer() throws SQLException
    {
        String queryOne = "SELECT first, last, count( crime_id ) cnt " +
                "FROM officers join crime_officers using(officer_id) " +
                "GROUP BY officers.officer_id " +
                "HAVING count(crime_id)>(SELECT avg(cnt) " +
                "FROM(SELECT count(crime_id) cnt " + 
                "FROM crime_officers GROUP BY officer_id) temp );";

        resultSet = statement.executeQuery( queryOne );
	 }//end findBestOfficer
    
    public void printBestOfficer() throws IOException, SQLException
    {
		System.out.println("******** Query 1 ********");
			System.out.println( "Last, First, count" );
	      while( resultSet.next() )
	      {
	         String result = resultSet.getString("Last") + ", " + 
	                         resultSet.getString("First" ) + ", " + resultSet.getInt( "cnt" );
	         System.out.println( result );
	      }//end while
	 }//end print best officer


	 //Query 2
    public void findCrimeCharge() throws SQLException
    {
        String query = "SELECT charge_id " +
                "FROM crime_charges " +
                "WHERE fine_amount > (SELECT avg(fine_amount) FROM crime_charges) " +
                "and amount_paid < (SELECT avg(amount_paid) FROM crime_charges);";
                   
        resultSet = statement.executeQuery(query);
    }

    public void printCrimeCharge() throws IOException, SQLException
    {
		System.out.println("******** Query 2 ********");
	      System.out.println("Charge_ID");
	      
	      while (resultSet.next()) 
	      {	      
	         String result = resultSet.getString("charge_id");
	         System.out.println(result);
	      }//end while
    }//end printCrimeCharged


	 //Query 3
    public void findCriminal() throws SQLException
    {
        String query = "SELECT distinct first, last " +
                "FROM criminals join crimes using(criminal_id) " +
                "WHERE criminal_id in (SELECT criminal_id FROM crimes join crime_charges using(crime_id) " +
                "WHERE crime_code in (SELECT crime_code FROM crime_charges WHERE crime_id = 10089));";
                   
        resultSet = statement.executeQuery(query);
    }//end findCriminal

    public void printCriminal() throws IOException, SQLException
    {
		System.out.println("******** Query 3 ********");
		System.out.println("First, Last");
	      while (resultSet.next()) 
	      {	        
	           String result = resultSet.getString("first") + ", " + resultSet.getString("last");
	           System.out.println(result);
	      }//end while
    }//end printCriminal


	 //Query 4
    public void findCriminalSentence() throws SQLException
    {
        String query = "SELECT criminal_id, last, first, count(sentence_id) cnt_sentence " +
                "FROM criminals join sentences using(criminal_id) " +
                "GROUP BY criminal_id " +
                "HAVING count(sentence_id) > 1;";
                  
        resultSet = statement.executeQuery(query); 
    }//end findCriminalSentence

    public void printCriminalSentence() throws IOException, SQLException
    {
		System.out.println("******** Query 4 ********");
	    System.out.println("Criminal_ID, Last,   First, Count_Sentence");
	      
	       while (resultSet.next()) 
	       {	      
	          String result = resultSet.getString("criminal_id") + ",        " + resultSet.getString("last") + ", " +
	                          resultSet.getString("first") + ",   " + resultSet.getString("cnt_sentence");
	          System.out.println(result);
	       }//end while 
       
    }//end printCriminalSentence


	 //Query 5
    public void findChargeCount() throws SQLException
    {
	    String query = "SELECT precinct, count(charge_id) charge_cnt " +
                "FROM officers join crime_officers using(officer_id) join crime_charges using(crime_id) " +
                "WHERE charge_status = 'GL' " +
                "GROUP BY precinct " +
                "HAVING count(charge_id) >= 7; ";
                  
	    resultSet = statement.executeQuery(query);
    }//end findChargeCount

    public void printChargeCount() throws IOException, SQLException
    {
		System.out.println("******** Query 5 ********");
	       System.out.println("Precinct, Charge_Count");
	      
	       while (resultSet.next()) {
	          String result = resultSet.getString("precinct") + ",     " + resultSet.getString("charge_cnt");
	          System.out.println(result);
	       } //end while
    }//end printChargeCount
	 
	 
	 //Query 6
	  public void findEarliestLatest() throws SQLException
    {
		   String query = "SELECT criminal_id, first, last, min(start_date) as 'earliest_start_date', max(end_date) as 'latest_end_date' " +
                   "FROM criminals join sentences using(criminal_id) " +
                   "GROUP BY criminals.criminal_id;";
		   resultSet = statement.executeQuery( query );
    }//end findEarliestLatest

    public void printEarliestLatest() throws IOException, SQLException
    {
			System.out.println("******** Query 6 ********");
		    System.out.println( "Criminal_ID, First, Last, Earliest_Start_Date, Latest_End_Date" );
		    while( resultSet.next() )
		    {
		         String result = resultSet.getInt( "criminal_id" ) + ", " + resultSet.getString("first" ) +
		                         ", " + resultSet.getString( "last" ) + ", " + 
		                         resultSet.getDate( "earliest_start_date" ) +
		                         ", " + resultSet.getDate( "latest_end_date" );
		         System.out.println( result ); 
		    }//end while
    }//end printEarliestLatest
	

	 //Query 7
    public void findCrimeCounts() throws SQLException
    {
		System.out.println("******** Query 7 ********");	
		InputStreamReader istream = new InputStreamReader(System.in) ;
      BufferedReader bufRead = new BufferedReader(istream) ;
      int id;
      int cnt;
      CallableStatement stmt;
      String query = "{call getNumber(?, ?)}";
		
		try{
            System.out.println("Please enter the officer_id for the query: ");
            //fill in this portion
			
            id = Integer.parseInt(bufRead.readLine());
            
            stmt = conn.prepareCall(query);
            stmt.setInt(1, id);
            
            stmt.registerOutParameter(2, java.sql.Types.INTEGER);
            
            stmt.execute();
            
            cnt = stmt.getInt(2);
	   	   System.out.println("Officer " +id+" has reported "+cnt+" crimes.");
	   }			
		catch (IOException err) {
            System.out.println("Error reading line");
           }
    }
}
