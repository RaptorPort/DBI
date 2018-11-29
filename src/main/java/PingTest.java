import java.io.IOException;
import java.sql.Timestamp;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

//import com.mysql.cj.jdbc.StatementImpl;


public class PingTest {

	private static final String USERNAME = "dbi";
	private static final String PASSWORD = "dbi_pass";
	//DBI Prak VM DB
	private static final String CONN_STRING = "jdbc:mysql://192.168.122.117/test?useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
	//Eigene DB
	//private static final String CONN_STRING = "jdbc:mysql://192.168.178.199/test?useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
	//wh wlan: private static final String CONN_STRING = "jdbc:mysql://192.168.56.1/test?useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
	
	public static void main(String[] args) throws SQLException {
		
		Connection conn = null; 
		int n = 1000; 
		
		try {
			conn = DriverManager.getConnection(CONN_STRING, USERNAME, PASSWORD);
			System.out.println("Connected!");
			//drop_proc(conn);
			initDBschema(conn);
			
			conn.setAutoCommit(false);
			conn.commit();
			System.out.println("initDBschema - done.");
			//init_tps_DB(conn, 10);
			//create_procedure(conn); //funktioniert
			Timestamp timestamp1 = new Timestamp(System.currentTimeMillis());
			create_proc(conn);
			call_stm(conn, 10000);
			//create_proc_all(conn, 100000);
			//call_stm_all(conn);
			
			Timestamp timestamp2 = new Timestamp(System.currentTimeMillis());
			System.out.print((double)((timestamp2.getTime()-timestamp1.getTime())/1000.00));
			System.out.println("s");
			
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
		String stmt2 = 	"CREATE PROCEDURE 'Insert_data' (" +
				"OUT error VARCHAR(128), "+
				"IN branchid int)) "+
				"BEGIN ";
		
		PreparedStatement stmt = conn.prepareStatement( 
				"insert into branches values (?, 'BRANCHNAME', 0, 'ADDRESS')"
				);
		//n Tupel in der BRANCH-Relation mit fortlaufender BRANCHID (1 bis n), der
		//BALANCE 0 und Strings der richtigen Länge für BRANCHNAME und ADDRESS
		stmt.addBatch(stmt2);
		for (int i = 1; i <= n; i++) {
			stmt.setInt(1, i);
			stmt.addBatch();
		}
		stmt.addBatch(" END");
		//stmt.executeBatch().toString();
		System.out.println("Done! \n"+ stmt.executeBatch().toString() );
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
	
	//CREATE STORED PROCEDURES
	//public static void create_proc(Connection conn, int iparam, int [] oparam) throws SQLException
	public static void create_proc (Connection conn) throws SQLException
	{
		boolean done = false;
		String Strproc = "CREATE PROCEDURE `insertdata` (IN id int) " + 
				"BEGIN "+
				"insert into branches values (id, 'BRANCHNAME', 0, 'ADDRESS'); "+
				"END";
		Statement stmproc = conn.createStatement();
		
		done=stmproc.execute(Strproc);
		System.out.println("done = "+done);
		
		
		//String sProc = "insert into branches values (?, 'BRANCHNAME', 0, 'ADDRESS')";
		//PreparedStatement prpstm = conn.prepareStatement(sProc);
		//prpstm.setInt(1,iparam);
		//prpstm.setInt(1, 5);
		//System.out.println(prpstm.exe);
		//int updateRowcount = prpstm.executeUpdate();
		//oparam[0] = updateRowcount;
		
	}
	
	public static void create_proc_all (Connection conn, int n) throws SQLException
	{
		String Strproc = "CREATE PROCEDURE `insertdata` () " + 
				"BEGIN \n";
		ArrayList<String> Sqllist = new ArrayList<String>();
		String Str="";
		for (int i=0; i<n; i++)
		{
			Str="insert into branches values ("+i+", 'BRANCHNAME', 0, 'ADDRESS');\n";
			Sqllist.add(Str);
		}
		String procGes;
		procGes = Strproc;
		for (int i=0; i<n; i++)
		{
			procGes=procGes+Sqllist.get(i);
		}
		procGes=procGes+
				"END";
		
		Statement stmt = conn.createStatement();
		stmt.execute(procGes);
		System.out.println("finished (create_procedure)");
		Sqllist.clear();
		stmt.close();
	}
	
	public static void call_stm(Connection conn, int n) throws SQLException
	{
		CallableStatement cstmt = conn.prepareCall("call insertdata(?)");
		for (int i=0; i<n; i++)
		{
			cstmt.setInt(1, i);
			cstmt.addBatch();
		}
		cstmt.executeBatch();
		conn.commit();
		cstmt.clearBatch();
		cstmt.close();
		
		//cstmt.registerOutParameter(1, java.sql.Types.INTEGER);
		
				//conn.prepareCall(
				//"{call getTestData(25, ?)}");
		//cstmt.registerOutParameter(1, java.sql.Types.TINYINT);
	}
	
	public static void call_stm_all (Connection conn) throws SQLException
	{
		CallableStatement cstmt = conn.prepareCall("call insertdata()");
		cstmt.execute();	
		conn.commit();
		System.out.println("finished (call insertdata())");
		cstmt.close();
	}
	
	public static void drop_proc (Connection conn) throws SQLException
	{
		Statement stmt = conn.createStatement();
		stmt.executeUpdate("DROP PROCEDURE `test`.`insertdata`;");
	}
	
	public static void create_procedure(Connection conn) throws SQLException
	{
		
		int done;
		//String Strproc = "DELIMITER // CREATE PROCEDURE 'create_proc'() BEGIN SELECT * from branches END// DELIMITER ;";
		String Strproc = "CREATE PROCEDURE `insertdata` () " + 
				"BEGIN " + 
				"Select * from branches; " + 
				"END";
		
		Statement simpleStatement = conn.createStatement();
		simpleStatement.execute(Strproc);
		//done = simpleStatement.executeUpdate(Strproc);
		System.out.println("xxxx - ");
		
	}
}
