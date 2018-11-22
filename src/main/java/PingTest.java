import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PingTest {

	private static final String USERNAME = "dbi";
	private static final String PASSWORD = "dbi_pass";
	private static final String CONN_STRING = "jdbc:mysql://192.168.122.117/test?useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
	
	public static void main(String[] args) throws SQLException {
		
		Connection conn = null; 
		try {
			conn = DriverManager.getConnection(CONN_STRING, USERNAME, PASSWORD);
			System.out.println("Connected!");
			initDBschema(conn);
			init_tps_DB(conn, 10);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			if (conn != null) {
				conn.close();
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
		stmt.executeUpdate(
				"drop table "
				+ "test.branches, "
				+ "test.accounts, "
				+ "test.history, "
				+ "test.tellers; "
				);
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
	}
	
	public static void init_tps_DB(Connection conn, int n) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement( 
				"insert into branches values (?, 'BRANCHNAME', 0, 'ADDRESS')"
				);
		//n Tupel in der BRANCH-Relation mit fortlaufender BRANCHID (1 bis n), der
		//BALANCE 0 und Strings der richtigen Länge für BRANCHNAME und ADDRESS
		for (int i = 1; i <= n; i++) {
			stmt.setInt(1, i);
			stmt.executeUpdate();
		}
	
		//n * 100000 Tupel in der ACCOUNTS-Relation mit fortlaufender ACCID (1 bis
		//n * 100000), dem Kontostand (BALANCE) 0, einer zufälligen BRANCHID (1 bis n) und
		//wieder beliebigen Strings der richtigen Länge für NAME und ADDRESS
		
		//n * 10 Tupel in der TELLER-Relation mit fortlaufender TELLERID (1 bis n * 10), der
		//BALANCE 0, einer zufälligen BRANCHID (1 bis n) und wieder beliebigen Strings der
		//richtigen Länge für TELLERNAME und ADDRESS
		
		//0 Tupel in der HISTORY-Relation.
	}
	
	
/*
	static void initClasspath() {
		String homeDirectory = System.getProperty("user.home");
		try {
			System.out.println(homeDirectory + "\\eclipse-workspace\\DBI\\ressources");
			Runtime.getRuntime().exec(String.format(homeDirectory + "/git/repository/DBI/ressources/setCLASSPATH.bat"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	*/
}
