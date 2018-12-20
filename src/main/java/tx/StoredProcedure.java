package tx;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

public class StoredProcedure {
	final static String CMMNT30 = "CMMNT1234567891234567891234567";
	
	CallableStatement cstmt_Kontostand;
	CallableStatement cstmt_Einzahlung;
	CallableStatement cstmt_Analyse;
	
	public int opEin = 0;
	public long timeEin = 0;
	public int opKonto = 0;
	public long timeKonto = 0;
	public int opAn = 0;
	public long timeAn = 0;
	
	public void init(Connection conn) {
		try {
			//Kontostand
			cstmt_Kontostand = conn.prepareCall("call Kontostand_tx (?,?)");
			//Einzahlung
			cstmt_Einzahlung = conn.prepareCall("call Einzahlung_tx (?,?,?,?,?,?)");
			//Analyse
			cstmt_Analyse = conn.prepareCall("call Analyse_tx (?,?)");
		} catch (SQLException e) {
			e.printStackTrace();
		}	
	}
	
	public void createProc(Connection conn) throws SQLException {
		Statement stmt = conn.createStatement();
	
		stmt.execute("DROP PROCEDURE IF EXISTS `Kontostand_tx`;");
		stmt.execute("CREATE PROCEDURE `Kontostand_tx` (IN id int, OUT bala int) " + 
				"BEGIN " + 
				"SELECT balance INTO bala FROM accounts WHERE accid = id; " + 
				"END");
		
		stmt.execute("DROP PROCEDURE IF EXISTS `Einzahlung_tx`;");
		stmt.execute("CREATE PROCEDURE `Einzahlung_tx` "
				+ "(IN accIN int, IN tellerIN int, IN branchIN int, IN delta int, IN cmnt char(30), OUT balance_out int)\n"
				+ "BEGIN\n"
				+ "UPDATE tellers SET balance = balance + delta WHERE tellerid = tellerIN;\n"
				+ "UPDATE accounts SET balance = balance + delta WHERE accid = accIN;\n"
				+ "SELECT balance INTO balance_out FROM accounts WHERE accid = accIN;\n"
				+ "INSERT INTO history VALUES (accIN, tellerIN, delta , branchIN , balance_out, cmnt);\n" 
				+ "UPDATE branches SET balance = balance + delta WHERE branchid = branchIN;\n"
				+ "END");

		stmt.execute("DROP PROCEDURE IF EXISTS `Analyse_tx`;");
		stmt.execute("CREATE PROCEDURE `Analyse_tx` (IN deltaIN int, OUT anz int)\n"
				+ "BEGIN\n"
				+ "SELECT COUNT(accid) INTO anz FROM history WHERE delta = deltaIN;\n"
				+ "END");
	}
	
	public int Kontostand_tx(Connection conn, int accid) throws SQLException {
		long start = System.currentTimeMillis();
		opKonto++;
		cstmt_Kontostand.setInt(1, 127);
		cstmt_Kontostand.registerOutParameter(2, Types.INTEGER);
		cstmt_Kontostand.execute();
		
		timeKonto += System.currentTimeMillis()-start;
		return cstmt_Kontostand.getInt(2);
	}
	
	public int Einzahlung_tx(Connection conn, int accid, int tellerid, int branchid, int delta) throws SQLException {
		long start = System.currentTimeMillis();
		opEin++;
		cstmt_Einzahlung.setInt(1, accid); 
		cstmt_Einzahlung.setInt(2, tellerid); 
		cstmt_Einzahlung.setInt(3, branchid);
		cstmt_Einzahlung.setInt(4, delta);
		cstmt_Einzahlung.setString(5, CMMNT30);
		cstmt_Einzahlung.registerOutParameter(6, Types.INTEGER);
		cstmt_Einzahlung.execute();
		conn.commit();
		
		timeEin += System.currentTimeMillis()-start;
		return cstmt_Einzahlung.getInt(6); //<<--balance 
	}
	
	public int Analyse_tx(Connection conn, int accid) throws SQLException {
		long start = System.currentTimeMillis();
		cstmt_Analyse.setInt(1, 101);
		cstmt_Analyse.registerOutParameter(2, Types.INTEGER);
		cstmt_Analyse.execute();
		conn.commit();
		
		timeAn += System.currentTimeMillis()-start;
		return cstmt_Analyse.getInt(2);
	}
}

