import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Random;

import init_tps_DB.*;
import tx.StoredProcedure;
import tx.StoredStatement;

public class Benchmark {
	static ArrayList<Connection> conns;

	private static final String USERNAME = "dbi";
	private static final String PASSWORD = "dbi_pass";
	private static final String CONN_STRING = "jdbc:mysql://192.168.122.54/test?allowLoadLocalInfile=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC"
			+"&sessionVariables=unique_checks=0,foreign_key_checks=0&useCompression=true"; //Session Variablen setzen
	
	public static void main(String[] args) throws SQLException, InterruptedException {
		//DBConnection conn = new DBConnection();
		Connection conn = null; 
		try {
			conn = DriverManager.getConnection(CONN_STRING, USERNAME, PASSWORD);
			conn.setAutoCommit(false);
			System.out.println("Connected main...");
						
			clear_history_tbl(conn);
			//StoredProcedure tempstmt=new StoredProcedure();
			//tempstmt.createProc(conn);
			
			if (conn != null) {
				conn.close();
				System.out.println("Closed main...");
			}
				
			int sumOps = 0;
			long sumTime = 0;
			Random rand = new Random();	
			
			ArrayList<load_driver> threads = new ArrayList<load_driver>();
			ArrayList<StoredProcedure> procstats = new ArrayList<StoredProcedure>();
			conns = new ArrayList<Connection>();
			for (int i = 0; i < 5; i++) {
				Connection connt = null; 
			
				connt = DriverManager.getConnection(CONN_STRING, USERNAME, PASSWORD);
				connt.setAutoCommit(false);
				System.out.println("Connected!");
				StoredProcedure stmt=new StoredProcedure();
				stmt.init(connt);
				load_driver temp = new load_driver(connt, stmt, rand.nextInt());
				temp.start();
				threads.add(temp);
				procstats.add(stmt);
				conns.add(connt);
			}
			for (load_driver ld : threads) {
				ld.join();
				while (ld.state != load_driver.AUSSCHWINGPHASE)
					Thread.sleep(100);
				sumOps += ld.opCounter;
				
			}
			
			int opEin = 0;
			long timeEin = 0;
			int opKonto = 0;
			long timeKonto = 0;
			int opAn = 0;
			long timeAn = 0;
			for (StoredProcedure ld : procstats) {
				opEin += ld.opEin;
				timeEin += ld.timeEin;
				opKonto += ld.opKonto;
				timeKonto += ld.timeKonto;
				opAn += ld.opAn;
				timeAn += ld.timeAn;
			}
			System.out.println("Analysis of each Statement: ");
			System.out.println("Kontostand #Ops: " + opKonto + "\tsec: " + timeKonto/1000 + "\tops/sec: " + (double)opKonto/(double)timeKonto);
			System.out.println("Einzahlung #Ops: " + opEin + "\tsec: " + timeEin/1000 + "\tops/sec: " + (double)opEin/(double)timeEin);
			System.out.println("Einzahlung #Ops: " + opAn + "\tsec: " + timeAn/1000 + "\tops/sec: " + (double)opAn/(double)timeAn);
			
			System.out.println("End Result #Operations: " + sumOps);
			System.out.println("Ops/sec: " + (double)sumOps/(double)300);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			if (conn != null) {
				conn.close();
				for (Connection t : conns) {
					if (t != null) {
						t.close();
						System.out.println("Connection Closed!");
					}
				}
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
