package tx;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class Einzahlung {
	public static int start (Connection conn, int accid, int tellerid, int branchid, int delta) {
		try
		{
			
			PreparedStatement stmt = conn.prepareStatement("UPDATE branches SET balance = balance + "+delta+"WHERE branchid ="+branchid+";");
			stmt.executeQuery();
			
			stmt = conn.prepareStatement("UPDATE tellers SET balance = balance + "+delta+"WHERE tellerid ="+tellerid+";");
			stmt.executeUpdate();
			
			stmt = conn.prepareStatement("UPDATE accounts SET balance = balance + "+delta+"WHERE accid ="+accid+";");
			stmt.executeUpdate();
			stmt = conn.prepareStatement("SELECT balance FROM accounts WHERE accid ="+accid+";");
			ResultSet result = stmt.executeQuery();
			result.next();
			
			stmt = conn.prepareStatement("INSERT INTO history VALUES ("+accid+", "+tellerid+", "+branchid+" , " +result.getInt(1)+","
					+ "'abcdefghijklmnopqrstuvwxvzabcd');");
			
			stmt.executeUpdate();
			return result.getInt(1);
		}
		
		catch (Exception e) {
		e.printStackTrace();
		return 0;
		}
	}
	
}
