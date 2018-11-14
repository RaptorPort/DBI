import java.io.IOException;

public class PingTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		initClasspath();

	}

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
}
