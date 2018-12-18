package tx;

import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class Kontostand {
	
	public static int start (Connection conn, StoredStatement stmt, int accid) {
		try {
			//"SELECT balance FROM accounts WHERE accid = ?;"
			stmt.kontostand.setInt(1, accid);
			ResultSet result = stmt.kontostand.executeQuery();
			result.next();
			return result.getInt(1);
		}
		catch(Exception e){
			e.printStackTrace();
			return 0;
		}
	}
}
