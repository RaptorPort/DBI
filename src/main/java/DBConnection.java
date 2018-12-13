import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
	private static final String USERNAME = "dbi";
	private static final String PASSWORD = "dbi_pass";
	private static final String CONN_STRING = "jdbc:mysql://192.168.122.117/test?allowLoadLocalInfile=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC"
			+"&sessionVariables=unique_checks=0,foreign_key_checks=0&useCompression=true"; //Session Variablen setzen
	
	public static void main(String[] args) throws SQLException, InterruptedException {
		Connection conn = null; 
		try {
			conn = DriverManager.getConnection(CONN_STRING, USERNAME, PASSWORD);
			conn.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			if (conn != null) {
				conn.close();
			}
		}		
	}
	
}
