import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;

import init_tps_DB.Infile;

public class load_driver {
	
	public static void start(Connection conn) throws SQLException, InterruptedException {
			System.out.println("Starting Benchmark!");
			long startTime = System.currentTimeMillis(); //start measuring time
			
			while (System.TimeMillis)
			
			
			long endTime = System.currentTimeMillis();	//stop measuring time
			long timeElapsed = endTime - startTime;
			System.out.println("Execution time in Seconds: " + (double)(timeElapsed/1000.00));
				
	}
}
