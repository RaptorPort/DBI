import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class PingTest {

	private static final String USERNAME = "dbi";
	private static final String PASSWORD = "dbi_pass";
	private static final String CONN_STRING = "jdbc:mysql://192.168.122.117/test?useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
	
	public static void main(String[] args) throws SQLException, InterruptedException {
		
		Connection conn = null; 
		try {
			conn = DriverManager.getConnection(CONN_STRING, USERNAME, PASSWORD);
			conn.setAutoCommit(false);
			System.out.println("Connected!");
			initDBschema(conn);
			TimeUnit.SECONDS.sleep(20);
			long startTime = System.currentTimeMillis()/100;
			init_tps_DBString(conn, 1);
			long endTime = System.currentTimeMillis()/100;
			long timeElapsed = endTime - startTime;
			System.out.println("Execution time in Seconds: " + timeElapsed);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			if (conn != null) {
				conn.close();
				System.out.println("Connection Closed!");
			}
		}	
		
	}
	
	public static void sqlQuery(Connection conn) throws SQLException
	{
		Statement stmt = conn.createStatement();
		ResultSet rlt = stmt.executeQuery("select * from customers, products");
		
		while (rlt.next())
		{
			System.out.printf("%10s | ",rlt.getString("cid"));
			System.out.printf("%10s | ",rlt.getString("pid"));
			System.out.printf("%10s | ",rlt.getString("cname"));
			System.out.printf("%10s | ",rlt.getString("city")+" | ");
			System.out.printf("%10s |\n",rlt.getString("discnt"));				
		}
	}
	public static void initDBschema(Connection conn) throws SQLException
	{
		Statement stmt = conn.createStatement();
		/*stmt.executeUpdate(
				"drop table "
				+ "test.branches, "
				+ "test.accounts, "
				+ "test.history, "
				+ "test.tellers; "
				);*/
		stmt.executeUpdate(
		"create table branches"
		+ "( branchid int not null, "
		+ "branchname char(20) not null, "
		+ "balance int not null, "
		+ "address char(72) not null, "
		+ "primary key (branchid) ); "
		);
		stmt.executeUpdate(
		"create table accounts"
		+ "( accid int not null, "
		+ "name char(20) not null, "
		+ "balance int not null, "
		+ "branchid int not null, "
		+ "address char(68) not null, "
		+ "primary key (accid), "
		+ "foreign key (branchid) references branches (branchid)); "
		);
		stmt.executeUpdate(
		"create table tellers"
		+ "( tellerid int not null, "
		+ "tellername char(20) not null, "
		+ "balance int not null, "
		+ "branchid int not null, "
		+ "address char(68) not null, "
		+ "primary key (tellerid), "
		+ "foreign key (branchid) references branches (branchid)); "
		);
		stmt.executeUpdate(
		"create table history"
		+ "( accid int not null, "
		+ "tellerid int not null, "
		+ "delta int not null, "
		+ "branchid int not null, "
		+ "accbalance int not null, "
		+ "cmmnt char(30) not null, "
		+ "foreign key (accid) references accounts (accid), "
		+ "foreign key (tellerid) references tellers (tellerid), "
		+ "foreign key (branchid) references branches (branchid));"
		 ); 
		conn.commit();
	}
	
	public static void init_tps_DB(Connection conn, int n) throws SQLException {	 
		Random zufall = new Random(); // neues Random Objekt, namens zufall
		 
		final String ADDRESS68 = "ADRESSE6812345678912345678912345678912345678912345678912345678912345";
		final String ADDRESS72 = "ADRESSE72123456789123456789123456789123456789123456789123456789123456789";
		final String NAME20 = "NAME2001234567890123";
		final String CMMNT30 = "CMMNT1234567891234567891234567";
		
		PreparedStatement stmt = conn.prepareStatement( 
				"insert into branches values (?, 'BRANCHNAME', 0, 'ADDRESS')"
				);
		//n Tupel in der BRANCH-Relation mit fortlaufender BRANCHID (1 bis n), der
		//BALANCE 0 und Strings der richtigen Länge für BRANCHNAME und ADDRESS
		for (int i = 1; i <= n; i++) {
			stmt.setInt(1, i);
			stmt.executeUpdate();
		}
		conn.commit();
		System.out.println("Branches DONE");
	
		//n * 100000 Tupel in der ACCOUNTS-Relation mit fortlaufender ACCID (1 bis
		//n * 100000), dem Kontostand (BALANCE) 0, einer zufälligen BRANCHID (1 bis n) und
		//wieder beliebigen Strings der richtigen Länge für NAME und ADDRESS
		stmt = conn.prepareStatement( 
				"insert into accounts values (?, ?, 0, ?, ?)"
				);
		for (int i = 1; i <= n*100000; i++) {
			stmt.setInt(1, i);
			stmt.setString(2, NAME20);
			stmt.setInt(3, (int)(zufall.nextDouble()*n+1));
			stmt.setString(4, ADDRESS68);
			stmt.executeUpdate();
		}
		conn.commit(); 
		System.out.println("Accounts DONE");
		//n * 10 Tupel in der TELLER-Relation mit fortlaufender TELLERID (1 bis n * 10), der
		//BALANCE 0, einer zufälligen BRANCHID (1 bis n) und wieder beliebigen Strings der
		//richtigen Länge für TELLERNAME und ADDRESS
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
	
	
	public static void init_tps_DBString(Connection conn, int n) throws SQLException {	 
		Random zufall = new Random(); // neues Random Objekt, namens zufall
		 
		final String ADDRESS68 = "ADRESSE6812345678912345678912345678912345678912345678912345678912345";
		final String ADDRESS72 = "ADRESSE72123456789123456789123456789123456789123456789123456789123456789";
		final String NAME20 = "NAME2001234567890123";
		final String CMMNT30 = "CMMNT1234567891234567891234567";
		
		PreparedStatement stmt = conn.prepareStatement( 
				"insert into branches values (?, 'BRANCHNAME', 0, 'ADDRESS')"
				);
		//n Tupel in der BRANCH-Relation mit fortlaufender BRANCHID (1 bis n), der
		//BALANCE 0 und Strings der richtigen Länge für BRANCHNAME und ADDRESS
		for (int i = 1; i <= n; i++) {
			stmt.setInt(1, i);
			stmt.executeUpdate();
		}
		conn.commit();
		System.out.println("Branches DONE");
	
		//n * 100000 Tupel in der ACCOUNTS-Relation mit fortlaufender ACCID (1 bis
		//n * 100000), dem Kontostand (BALANCE) 0, einer zufälligen BRANCHID (1 bis n) und
		//wieder beliebigen Strings der richtigen Länge für NAME und ADDRESS
		
		Statement stmt2 = conn.createStatement();
		for (int i = 1; i <= n*100000; i++) {
			stmt2.addBatch( "insert into accounts values (" + i + ", " + NAME20 + ", " + (int)(zufall.nextDouble()*n+1) + ", " + ADDRESS68 + ")");
		}
		
		stmt2.executeBatch();
		conn.commit(); 
		System.out.println("Accounts DONE");
		//n * 10 Tupel in der TELLER-Relation mit fortlaufender TELLERID (1 bis n * 10), der
		//BALANCE 0, einer zufälligen BRANCHID (1 bis n) und wieder beliebigen Strings der
		//richtigen Länge für TELLERNAME und ADDRESS
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
