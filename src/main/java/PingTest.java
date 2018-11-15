import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PingTest {

	private static final String USERNAME = "dbi";
	private static final String PASSWORD = "dbi_pass";
	private static final String CONN_STRING = "jdbc:mysql://192.168.122.117/cap?useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
	
	public static void main(String[] args) throws SQLException {
		
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(CONN_STRING, USERNAME, PASSWORD);
			System.out.println("Connected!");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				conn.close();
			}
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
