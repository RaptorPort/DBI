package tx;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StoredStatement {
	Connection conn;
	PreparedStatement einzahlung_tellers;
	PreparedStatement einzahlung_accounts;
	PreparedStatement einzahlung_balance;
	PreparedStatement einzahlung_history;
	PreparedStatement einzahlung_branches;
	
	PreparedStatement analyse;
	PreparedStatement kontostand;
	
	public StoredStatement() {
		System.out.println("Creation of PreparedStatements");
	}
	
	public void initAll(Connection conn) {
		this.conn = conn;
		initEinzahlung();
		initKontostand();
		initAnalyse();
	}
	
	public void initEinzahlung() {
		try {
		//Einzahlung
		einzahlung_tellers = conn.prepareStatement("UPDATE tellers SET balance = balance + ? WHERE tellerid = ?;");
		einzahlung_accounts = conn.prepareStatement("UPDATE accounts SET balance = balance + ? WHERE accid = ? ;");
		einzahlung_balance = conn.prepareStatement("SELECT balance FROM accounts WHERE accid = ?;");		
		einzahlung_history = conn.prepareStatement("INSERT INTO history VALUES (?, ?, ? , ? , ?, ?);");
		einzahlung_branches = conn.prepareStatement("UPDATE branches SET balance = balance + ? WHERE branchid = ?;");
		} catch (SQLException e) {
			System.out.println("Initialisation of PreparedStatements faulty! (initEinzahlung)");
			e.printStackTrace();
		}
	}
	public void initKontostand() {
		try {
		//Kontostand
		kontostand = conn.prepareStatement("SELECT balance FROM accounts WHERE accid = ?;");
		} catch (SQLException e) {
			System.out.println("Initialisation of PreparedStatements faulty! (initKontostand)");
			e.printStackTrace();
		}
	}
	
	public void initAnalyse() {
		try {
		//Analyse
		analyse = conn.prepareStatement("SELECT COUNT(accid) FROM history WHERE delta = ?");
		} catch (SQLException e) {
			System.out.println("Initialisation of PreparedStatements faulty! (initAnalyse)");
			e.printStackTrace();
		}
	}
	
	public int analyse (int delta){
		try {
			//"SELECT COUNT(accid) FROM history WHERE delta = ?"
			analyse.setInt(1, delta);
			ResultSet result = analyse.executeQuery();
			result.next();
			return result.getInt(1);
			}
		catch (Exception e){
			e.printStackTrace();
			return 0;
			}
		
		}
	
	public int einzahlung (int accid, int tellerid, int branchid, int delta) {
		try
		{			
			//"UPDATE branches SET balance = balance + ? WHERE branchid = ?;"
			einzahlung_branches.setInt(1, delta);
			einzahlung_branches.setInt(2, branchid);
			einzahlung_branches.executeUpdate();
			
			//"UPDATE tellers SET balance = balance + ? WHERE tellerid = ?;"
			einzahlung_tellers.setInt(1, delta);
			einzahlung_tellers.setInt(2, tellerid);
			einzahlung_tellers.executeUpdate();
			
			//"UPDATE accounts SET balance = balance + ? WHERE accid = ? ;"
			einzahlung_accounts.setInt(1, delta);
			einzahlung_accounts.setInt(2, accid);
			einzahlung_accounts.executeUpdate();
			
			//"SELECT balance FROM accounts WHERE accid = ?;"	
			einzahlung_balance.setInt(1, accid);
			ResultSet result = einzahlung_balance.executeQuery();
			result.next();
			int endresult=result.getInt(1);
			
			//"INSERT INTO history VALUES (?, ?, ? , ? , ?, ?);"
			einzahlung_history.setInt(1, accid);
			einzahlung_history.setInt(2, tellerid);
			einzahlung_history.setInt(3, delta);
			einzahlung_history.setInt(4, branchid);
			einzahlung_history.setInt(5, result.getInt(1));
			einzahlung_history.setString(6, "abcdefghijklmnopqrstuvwxvzabcd");
			einzahlung_history.executeUpdate();
			
			conn.commit();
			return endresult;
		}
		catch (Exception e) {
			e.printStackTrace();
		return 0;
		}
	}
	
	public int kontostand (int accid) {
		try {
			//"SELECT balance FROM accounts WHERE accid = ?;"
			kontostand.setInt(1, accid);
			ResultSet result = kontostand.executeQuery();
			result.next();
			return result.getInt(1);
		}
		catch(Exception e){
			e.printStackTrace();
			return 0;
		}
	}
}
