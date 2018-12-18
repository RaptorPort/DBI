import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Random;

import init_tps_DB.*;
import tx.StoredStatement;

public class Benchmark {

	private static final String USERNAME = "dbi";
	private static final String PASSWORD = "dbi_pass";
	private static final String CONN_STRING = "jdbc:mysql://192.168.122.117/test?allowLoadLocalInfile=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC"
			+"&sessionVariables=unique_checks=0,foreign_key_checks=0&useCompression=true"; //Session Variablen setzen
	
	public static void main(String[] args) throws SQLException, InterruptedException {
		//DBConnection conn = new DBConnection();
		Connection conn = null; 
		try {
			conn = DriverManager.getConnection(CONN_STRING, USERNAME, PASSWORD);
			conn.setAutoCommit(false);
			System.out.println("Connected!");
			
						
			clear_history_tbl(conn);
			
			int sumOps = 0;
			long sumTime = 0;
			Random rand = new Random();	
			
			ArrayList<load_driver> threads = new ArrayList<load_driver>();
			for (int i = 0; i < 5; i++) {
				StoredStatement stmt=new StoredStatement();
				stmt.initAll(conn);
				load_driver temp = new load_driver(conn, stmt, rand.nextInt());
				temp.start();
				threads.add(temp);
			}
			for (load_driver ld : threads) {
				ld.join();
				while (ld.state != load_driver.AUSSCHWINGPHASE)
					Thread.sleep(100);
				sumOps += ld.opCounter;
				
			}
			System.out.println("End Result #Operations: " + sumOps);
			
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
