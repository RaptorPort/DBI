import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Random;

import init_tps_DB.*;
import tx.*;


public class Benchmark {
	
	private static final String USERNAME = "dbi";
	private static final String PASSWORD = "dbi_pass";	//192.168.122.54
	private static final String CONN_STRING = "jdbc:mysql://192.168.188.59"
			+"/test?allowLoadLocalInfile=true&useJDBCCompliantTimezoneShift=true"
			+"&useLegacyDatetimeCode=false&serverTimezone=UTC"
			+"&sessionVariables=unique_checks=0,foreign_key_checks=0&useCompression=true"; //Session Variablen setzen
	
	public static void main(String[] args) throws SQLException, InterruptedException {
		prepareDB();
			
		ArrayList<load_driver> threads = new ArrayList<load_driver>();
		ArrayList<Connection> conns = new ArrayList<Connection>();
		
		try {				
			int sumOps = 0;
			//new randomizer with current time as seed
			Random rand = new Random(System.currentTimeMillis());	
			
			for (int i = 0; i < 5; i++) {
				Connection connt = null; 
				connt = DriverManager.getConnection(CONN_STRING, USERNAME, PASSWORD);
				connt.setAutoCommit(false);
				
				StoredProcedure stmt = new StoredProcedure();
				stmt.init(connt);	//init Procedures for connection
				//create load_driver with connection, procedures and a random number as seed
				load_driver temp = new load_driver(stmt, rand.nextInt());
				temp.start();
				System.out.println("Connected and started thread " + temp.getId() + "!");
				threads.add(temp);
				conns.add(connt);
			}
			for (load_driver ld : threads) {
				ld.join();	//wait for threads to finish
				while (ld.state != load_driver.AUSSCHWINGPHASE) //doublecheck if they are done
					Thread.sleep(100);	//->wait until thread is done
				sumOps += ld.opCounter;
			}
			analyseTX(threads);
			System.out.println("End Result #Operations: " + sumOps);
			System.out.println("Ops/sec: " + (double)sumOps/(double)300);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			for (Connection t : conns) {
				if (t != null) {
					t.close();
					System.out.println("Connection Closed!");
				}
			}
		}			
	}
	
	public static void prepareDB() throws SQLException {
		Connection conn = null; 
		try {
			conn = DriverManager.getConnection(CONN_STRING, USERNAME, PASSWORD);
			conn.setAutoCommit(false);
			System.out.println("Connected main...");	
			clear_history_tbl(conn);
			System.out.println("Wiped history!");
			
			//updating of Storedprocedures
			//StoredProcedure tempstmt=new StoredProcedure();
			//tempstmt.createProcedure(conn);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			if (conn != null) {
				conn.close();
				System.out.println("Closed main...");
			}
		}
	}
	
	//delete all tuples in the history table
	public static void clear_history_tbl(Connection conn) throws SQLException
	{
		Statement stmtcl = conn.createStatement();
		stmtcl.execute("TRUNCATE test.history;");
	}
	
	public static void analyseTX(ArrayList<load_driver> threads) {
		int opEin = 0;
		long timeEin = 0;
		int opKonto = 0;
		long timeKonto = 0;
		int opAn = 0;
		long timeAn = 0;
		for (load_driver ld : threads) {
			opEin += ld.stmt.opEin;
			timeEin += ld.stmt.timeEin;
			opKonto += ld.stmt.opKonto;
			timeKonto += ld.stmt.timeKonto;
			opAn += ld.stmt.opAn;
			timeAn += ld.stmt.timeAn;
		}
		System.out.println("Analysis of each Statement: ");
		System.out.println("Kontostand #Ops: " + opKonto + "\tsec: " + timeKonto/1000 
						 + "\tops/sec: " + (double)opKonto/(double)timeKonto*1000);
		System.out.println("Einzahlung #Ops: " + opEin + "\tsec: " + timeEin/1000 
						 + "\tops/sec: " + (double)opEin/(double)timeEin*1000);
		System.out.println("Analyse #Ops: " + opAn + "\tsec: " + timeAn/1000 
						 + "\tops/sec: " + (double)opAn/(double)timeAn*1000);
	}
	
	//check if we have 10.000.000 tuples in accounts
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
}
