package tx;
import java.sql.ResultSet;
import java.sql.Connection;

public class Einzahlung {
	public static int start (Connection conn, StoredStatement stmt, int accid, int tellerid, int branchid, int delta) {
		try
		{			
			//"UPDATE branches SET balance = balance + ? WHERE branchid = ?;"
			stmt.einzahlung_branches.setInt(1, delta);
			stmt.einzahlung_branches.setInt(2, branchid);
			stmt.einzahlung_branches.executeUpdate();
			
			//"UPDATE tellers SET balance = balance + ? WHERE tellerid = ?;"
			stmt.einzahlung_tellers.setInt(1, delta);
			stmt.einzahlung_tellers.setInt(2, tellerid);
			stmt.einzahlung_tellers.executeUpdate();
			
			//"UPDATE accounts SET balance = balance + ? WHERE accid = ? ;"
			stmt.einzahlung_accounts.setInt(1, delta);
			stmt.einzahlung_tellers.setInt(2, accid);
			stmt.einzahlung_tellers.executeUpdate();
			
			//"SELECT balance FROM accounts WHERE accid = ?;"	
			stmt.einzahlung_balance.setInt(1, accid);
			ResultSet result = stmt.einzahlung_balance.executeQuery();
			result.next();
			int endresult=result.getInt(1);
			
			//"INSERT INTO history VALUES (?, ?, ? , ? , ?, ?);"
			stmt.einzahlung_history.setInt(1, accid);
			stmt.einzahlung_history.setInt(2, tellerid);
			stmt.einzahlung_history.setInt(3, delta);
			stmt.einzahlung_history.setInt(4, branchid);
			stmt.einzahlung_history.setInt(5, result.getInt(1));
			stmt.einzahlung_history.setString(6, "abcdefghijklmnopqrstuvwxvzabcd");
			stmt.einzahlung_history.executeUpdate();
			
			return endresult;
		}
		catch (Exception e) {
		e.printStackTrace();
		return 0;
		}
	}
	
}
