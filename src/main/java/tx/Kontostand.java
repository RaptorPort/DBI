package tx;

import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class Kontostand {
	
	public static int start (Connection conn, PreparedStatement stmt, int accid) {
		try {
			//"SELECT balance FROM accounts WHERE accid = ?;"
			stmt.setInt(1, accid);
			ResultSet result = stmt.executeQuery();
			result.next();
			return result.getInt(1);
		}
		catch(Exception e){
			e.printStackTrace();
			return 0;
		}
	}
}
