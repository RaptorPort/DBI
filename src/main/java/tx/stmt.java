package tx;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class stmt {
	static PreparedStatement einzahlung_tellers;
	static PreparedStatement einzahlung_accounts;
	static PreparedStatement einzahlung_balance;
	static PreparedStatement einzahlung_history;
	
	static PreparedStatement analyse;
	static PreparedStatement kontostand;
	
	public stmt() {
		System.out.println("Creation of PreparedStatements");
	}
	
	public void initAll(Connection conn) {
		initEinzahlung(conn);
		initKontostand(conn);
		initAnalyse(conn);
	}
	
	public void initEinzahlung(Connection conn) {
		try {
		//Einzahlung
		einzahlung_tellers = conn.prepareStatement("UPDATE tellers SET balance = balance + ? WHERE tellerid = ?;");
		einzahlung_accounts = conn.prepareStatement("UPDATE accounts SET balance = balance + ? WHERE accid = ? ;");
		einzahlung_balance = conn.prepareStatement("SELECT balance FROM accounts WHERE accid = ?;");		
		einzahlung_history       = conn.prepareStatement("INSERT INTO history VALUES (?, ?, ? , ? , ?, ?);");
		} catch (SQLException e) {
			System.out.println("Initialisation of PreparedStatements faulty! (initEinzahlung)");
			e.printStackTrace();
		}
	}
	public void initKontostand(Connection conn) {
		try {
		//Kontostand
		kontostand = conn.prepareStatement("SELECT balance FROM accounts WHERE accid = ?;");
		} catch (SQLException e) {
			System.out.println("Initialisation of PreparedStatements faulty! (initKontostand)");
			e.printStackTrace();
		}
	}
	
	public void initAnalyse(Connection conn) {
		try {
		//Analyse
		analyse = conn.prepareStatement("SELECT * FROM history WHERE delta = ?");
		} catch (SQLException e) {
			System.out.println("Initialisation of PreparedStatements faulty! (initAnalyse)");
			e.printStackTrace();
		}
	}
}
