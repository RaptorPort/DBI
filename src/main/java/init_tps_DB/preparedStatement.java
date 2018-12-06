package init_tps_DB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

public class preparedStatement {
	public static void init_tps_DB(Connection conn, int n) throws SQLException {	 
		Random zufall = new Random(); // neues Random Objekt, namens zufall
		 
		final String ADDRESS68 = "ADRESSE6812345678912345678912345678912345678912345678912345678912345";
		final String ADDRESS72 = "ADRESSE72123456789123456789123456789123456789123456789123456789123456789";
		final String NAME20 = "NAME2001234567890123";
		
		PreparedStatement stmt = conn.prepareStatement( 
				"insert into branches values (?, " + NAME20 + ", 0, " + ADDRESS72 + ")"
				);
		//Branches Relation
		for (int i = 1; i <= n; i++) {
			stmt.setInt(1, i);
			stmt.executeUpdate();
		}
		conn.commit();
		System.out.println("Branches DONE");
	
		//Account Relation
		stmt = conn.prepareStatement( 
				"insert into accounts values (?, ?, 0, ?, ?)"
				);
		for (int i = 1; i <= n*100000; i++) {
			stmt.setInt(1, i);								//accid
			stmt.setString(2, NAME20);						//name
			stmt.setInt(3, (int)(zufall.nextDouble()*n+1));	//branchid
			stmt.setString(4, ADDRESS68);					//address
			stmt.executeUpdate();
		}
		conn.commit(); 
		System.out.println("Accounts DONE");
		//Teller Relation
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
