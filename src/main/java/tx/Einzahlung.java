package tx;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class Einzahlung {
	static Connection con = null;
	static PreparedStatement stmt = null;
	

	public static int einzahlung (int accid, int tellerid, int branchid, int delta) {
		try
		{
			stmt = con.prepareStatement("UPDATE brancches SET balance = balance + "+delta+"WHERE branchid ="+branchid+";");
			stmt.executeQuery();
			
			stmt = con.prepareStatement("UPDATE tellers SET balance = balance + "+delta+"WHERE tellerid ="+tellerid+";");
			stmt.executeQuery();
			
			stmt = con.prepareStatement("UPDATE accounts SET balance = balance + "+delta+"WHERE accid ="+accid+";");
			stmt.executeQuery();
			stmt = con.prepareStatement("SELECT blance FROM accounts WHERE accid ="+accid+";");
			ResultSet result = stmt.executeQuery();
			result.next();
			
			stmt = con.prepareStatement("INSERT INTo history VALUES ("+accid+", "+tellerid+", "+branchid+" , " +result.getInt(1)+","
					+ "'abcdefghijklmnopqrstuvwxvzabcd');");
			
			stmt.executeQuery();
			return result.getInt(1);
		}
		
		catch (Exception e) {
		e.printStackTrace();
		return 0;
		}
	}
	
}
