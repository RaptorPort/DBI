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
	
	public void init(Connection conn) {
		try {
			Statement stmt;
			//Kontostand
			stmt = conn.createStatement();
			stmt.execute("DROP PROCEDURE IF EXISTS `Kontostand_tx`;");
			
			stmt.execute("CREATE PROCEDURE `Kontostand_tx` (IN id int, OUT bala int) " + 
					"BEGIN " + 
					"SELECT balance INTO bala FROM accounts WHERE accid = id; " + 
					"END");
			
			cstmt_Kontostand = conn.prepareCall("call Kontostand_tx (?,?)");
			//Einzahlung
			stmt = conn.createStatement();
			stmt.execute("DROP PROCEDURE IF EXISTS `Einzahlung_tx`;");
			stmt.execute("CREATE PROCEDURE `Einzahlung_tx` "
					+ "(IN accIN int, IN tellerIN int, IN branchIN int, IN delta int, IN cmnt char(30), OUT balance_out int)\n"
					+ "BEGIN\n"
					+ "UPDATE tellers SET balance = balance + delta WHERE tellerid = tellerIN;\n"
					+ "UPDATE accounts SET balance = balance + delta WHERE accid = accIN ;\n"
					+ "SELECT balance INTO balance_out FROM accounts WHERE accid = accIN;\n"
					+ "INSERT INTO history VALUES (accIN, tellerIN, delta , branchIN , balance_out, cmnt);\n" 
					+ "UPDATE branches SET balance = balance + delta WHERE branchid = branchIN;\n"
					+ "END");
			
			cstmt_Einzahlung = conn.prepareCall("call Einzahlung_tx (?,?,?,?,?,?)");
			//Analyse
			stmt = conn.createStatement();
			stmt.execute("DROP PROCEDURE IF EXISTS `Analyse_tx`;");
			
			stmt.execute("CREATE PROCEDURE `Analyse_tx` (IN deltaIN int, OUT anz int)\n"
					+ "BEGIN\n"
					+ "SELECT COUNT(accid) INTO anz FROM history WHERE delta = deltaIN;\n"
					+ "END");
			
			cstmt_Analyse = conn.prepareCall("call Analyse_tx (?,?)");
		} catch (SQLException e) {
			e.printStackTrace();
		}	
	}
	
	public int Kontostand_tx(Connection conn, int accid) throws SQLException {
		cstmt_Kontostand.setInt(1, 127);
		cstmt_Kontostand.registerOutParameter(2, Types.INTEGER);
		cstmt_Kontostand.execute();
		
		return cstmt_Kontostand.getInt(2);
	}
	
	public int Einzahlung_tx(Connection conn, int accid, int tellerid, int branchid, int delta) throws SQLException {
		cstmt_Einzahlung.setInt(1, accid); 
		cstmt_Einzahlung.setInt(2, tellerid); 
		cstmt_Einzahlung.setInt(3, branchid);
		cstmt_Einzahlung.setInt(4, delta);
		cstmt_Einzahlung.setString(5, CMMNT30);
		cstmt_Einzahlung.registerOutParameter(6, Types.INTEGER);
		cstmt_Einzahlung.execute();
		conn.commit();
		return cstmt_Einzahlung.getInt(6); //<<--balance 
	}
	
	public int Analyse_tx(Connection conn, int accid) throws SQLException {
		cstmt_Analyse.setInt(1, 101);
		cstmt_Analyse.registerOutParameter(2, Types.INTEGER);
		cstmt_Analyse.execute();
		conn.commit();
		return cstmt_Analyse.getInt(2);
	}
}

