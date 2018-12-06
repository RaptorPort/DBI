package init_tps_DB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

public class VariableSizeStrings {
	public static void init_tps_DB(Connection conn, int n) throws SQLException {	 
		Random zufall = new Random(); // neues Random Objekt, namens zufall
		 
		final String ADDRESS68 = "ADRESSE6812345678912345678912345678912345678912345678912345678912345";
		final String ADDRESS72 = "ADRESSE72123456789123456789123456789123456789123456789123456789123456789";
		final String NAME20 = "NAME2001234567890123";
		final String CMMNT30 = "CMMNT1234567891234567891234567";
		
		PreparedStatement stmt = conn.prepareStatement( 
				"insert into branches values (?, 'BRANCHNAME', 0, 'ADDRESS')"
				);
		//Branches Relationen
		for (int i = 1; i <= n; i++) {
			stmt.setInt(1, i);
			stmt.executeUpdate();
		}
		conn.commit();
		System.out.println("Branches DONE");
	
		//Account Relationen	
		String query;
		Statement stmt2 = conn.createStatement();
		int size = 100;
		for (int s = 1; s < n*100000; s += size) {
			query = "insert into test.accounts (accid, name, balance, branchid, address) values";
			query += "(" + s + ",'" + NAME20 + "',0," + (int)(zufall.nextDouble()*n+1) + ",'" + ADDRESS68 + "')";
			for (int i = s+1; i < s + size && i <= n*100000; i++) {
				query += ",(" + i + ",'" + NAME20 + "',0," + (int)(zufall.nextDouble()*n+1) + ",'" + ADDRESS68 + "')";
			}
			query += ";";
			stmt2.executeUpdate(query);
		}		
		conn.commit(); 
		System.out.println("Accounts DONE");
		
		//Teller Relationen
		stmt = conn.prepareStatement( 
				"insert into tellers values (?, ?, 0, ?, ?)"
				);
		for (int i = 1; i <= n*10; i++) {
			stmt.setInt(1, i);
			stmt.setString(2, NAME20);
			stmt.setInt(3, (int)(zufall.nextDouble()*n+1));
			stmt.setString(4, ADDRESS68);
			stmt.executeUpdate();
		}
		conn.commit(); 
		System.out.println("Tellers DONE");
		//0 Tupel in der HISTORY-Relation.
	}
}
