package tx;

import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class Kontostand {
	static Connection con = null;
	static PreparedStatement stmt = null;
	
	public static int kontostand (int accid) {
		try {
			stmt = con.prepareStatement("SELECT balance FROM accounts WHERE accid = "+accid+";");
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
