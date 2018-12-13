import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;
import java.sql.ResultSet;

import init_tps_DB.*;

public class Benchmark {

	private static final String USERNAME = "dbi";
	private static final String PASSWORD = "dbi_pass";
	private static final String CONN_STRING = "jdbc:mysql://192.168.122.117/test?allowLoadLocalInfile=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC"
			+"&sessionVariables=unique_checks=0,foreign_key_checks=0&useCompression=true"; //Session Variablen setzen
	
	public static void main(String[] args) throws SQLException, InterruptedException {
		Connection conn = null; 
		try {
			conn = DriverManager.getConnection(CONN_STRING, USERNAME, PASSWORD);
			conn.setAutoCommit(false);
			System.out.println("Connected!");
			/*
			System.out.println("Initializing DB-Schema");
			initDBschema(conn);
			System.out.println("Wait for DB to finish...");
			TimeUnit.SECONDS.sleep(10);		//wait incase DB has to perform clean up tasks
			
				TimeUnit.SECONDS.sleep(10);
				Statement stmt1 = conn.createStatement();	//set options for better performance
				//stmt1.executeUpdate("SET sql_log_bin = 0;");
				//stmt1.executeUpdate("SET sql_log_off = 1;");
				
				System.out.println("Starting Benchmark!");
				long startTime = System.currentTimeMillis(); //start measuring time
				
				Infile.init_tps_DB(conn, 100);	//perform selected procedure to insert data
				if(check_db(conn) == true)
				{
					System.out.println("Datenbank vollständig erstellt.");
				long endTime = System.currentTimeMillis();	//stop measuring time
				long timeElapsed = endTime - startTime;
				System.out.println("Execution time in Seconds: " + (double)(timeElapsed/1000.00));
				}
				else
				{
					System.out.println("Nicht alle Datensätze vollständig erstellt!");
				}
				deletecsv();	//delete all the created .csv files, if present			
			*/
			clear_history_tbl(conn);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			if (conn != null) {
				conn.close();
				System.out.println("Connection Closed!");
			}
		}		
	}

	public static void initDBschema(Connection conn) throws SQLException
	{
		Statement stmt = conn.createStatement();
		stmt.executeUpdate(
				"drop table if exists "
				+ "test.branches, "
				+ "test.accounts, "
				+ "test.history, "
				+ "test.tellers; "
				);
		stmt.executeUpdate(
		"create table branches"
		+ "( branchid int not null, "
		+ "branchname char(20) not null, "
		+ "balance int not null, "
		+ "address char(72) not null, "
		+ "primary key (branchid) ); "
		);
		stmt.executeUpdate(
		"create table accounts"
		+ "( accid int not null, "
		+ "name char(20) not null, "
		+ "balance int not null, "
		+ "branchid int not null, "
		+ "address char(68) not null, "
		+ "primary key (accid), "
		+ "foreign key (branchid) references branches (branchid)); "
		);
		stmt.executeUpdate(
		"create table tellers"
		+ "( tellerid int not null, "
		+ "tellername char(20) not null, "
		+ "balance int not null, "
		+ "branchid int not null, "
		+ "address char(68) not null, "
		+ "primary key (tellerid), "
		+ "foreign key (branchid) references branches (branchid)); "
		);
		stmt.executeUpdate(
		"create table history"
		+ "( accid int not null, "
		+ "tellerid int not null, "
		+ "delta int not null, "
		+ "branchid int not null, "
		+ "accbalance int not null, "
		+ "cmmnt char(30) not null, "
		+ "foreign key (accid) references accounts (accid), "
		+ "foreign key (tellerid) references tellers (tellerid), "
		+ "foreign key (branchid) references branches (branchid));"
		 ); 
		conn.commit();
	}
	
	public static void deletecsv() 
	{		
		// Lists all files in folder
		File folder = new File("./");
		File fList[] = folder.listFiles();
		// Searchs .lck
		
		for (int i = 0; i < fList.length; i++) {
		    String pes = fList[i].toString();
		    if (pes.endsWith(".csv")) {
		        // and deletes
		        boolean success = (fList[i].delete());
		    }
		}
	}
	
	public static boolean check_db(Connection conn) throws SQLException
	{
		int anz;
		Statement statemnt = conn.createStatement();
		
		ResultSet rs;
		rs = statemnt.executeQuery("Select Count(accid) from test.accounts;");
		rs.next();
		anz=rs.getInt(1);
		System.out.println(anz);
		if(anz==10000000)
		{
			return true;
		}
		{
			return false;
		}
		
		
	}
	
	public static void clear_history_tbl(Connection conn) throws SQLException
	{
		Statement stmtcl = conn.createStatement();
		stmtcl.execute("TRUNCATE test.history;");
	}
}
