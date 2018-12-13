package tx;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Analyse {
	
	public static int start (Connection conn, int delta){
	try {
		PreparedStatement stmt = conn.prepareStatement("SELECT = FROM history WHERE delta =" +delta+ ";");
		ResultSet result = stmt.executeQuery();
		int Anzahl = 0;
		while(result.next())
			Anzahl++;
		
		return Anzahl;
		}
	catch (Exception e){
		e.printStackTrace();
		return 0;
		}
	
	}
}
