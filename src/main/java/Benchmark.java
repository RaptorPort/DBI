import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;

import init_tps_DB.*;

public class Benchmark {

	private static final String USERNAME = "dbi";
	private static final String PASSWORD = "dbi_pass";
	private static final String CONN_STRING = "jdbc:mysql://192.168.188.59/test?allowLoadLocalInfile=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
	
	public static void main(String[] args) throws SQLException, InterruptedException {
		Connection conn = null; 
		try {
			conn = DriverManager.getConnection(CONN_STRING, USERNAME, PASSWORD);
			conn.setAutoCommit(false);
			System.out.println("Connected!");
			
			System.out.println("Initializing DB-Schema");
			initDBschema(conn);
			System.out.println("Wait for DB to finish...");
			TimeUnit.SECONDS.sleep(3);
			
			System.out.println("Starting Benchmark!");
			long startTime = System.currentTimeMillis();
			
			// Initialize Database - INSERT
			Batch.init_tps_DB(conn, 1);
			
			long endTime = System.currentTimeMillis();
			long timeElapsed = endTime - startTime;
			System.out.println("Execution time in Seconds: " + timeElapsed/1000);
			
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
	
	public static void sqlQuery(Connection conn) throws SQLException
	{
		Statement stmt = conn.createStatement();
		ResultSet rlt = stmt.executeQuery("select * from customers, products");
		
		while (rlt.next())
		{
			System.out.printf("%10s | ",rlt.getString("cid"));
			System.out.printf("%10s | ",rlt.getString("pid"));
			System.out.printf("%10s | ",rlt.getString("cname"));
			System.out.printf("%10s | ",rlt.getString("city")+" | ");
			System.out.printf("%10s |\n",rlt.getString("discnt"));				
		}
	}
}
