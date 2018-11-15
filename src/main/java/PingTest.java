import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PingTest {

	private static final String USERNAME = "dbi";
	private static final String PASSWORD = "dbi_pass";
	private static final String CONN_STRING = "jdbc:mysql://192.168.122.117/cap?useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
	
	public static void main(String[] args) throws SQLException {
		
		Connection conn = null; 
		try {
			conn = DriverManager.getConnection(CONN_STRING, USERNAME, PASSWORD);
			System.out.println("Connected!");
			
			sqlQuery(conn);
			
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
			System.out.printf("%6s | ",rlt.getString("cid"));
			System.out.printf("%5s | ",rlt.getString("pid"));
			System.out.printf("%8s | ",rlt.getString("cname"));
			System.out.printf("%8s | ",rlt.getString("city"));
			System.out.printf("%4s |\n",rlt.getString("discnt"));				
		}
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
