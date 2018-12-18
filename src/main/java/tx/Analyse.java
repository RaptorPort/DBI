package tx;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Analyse {
	
	public static int start (Connection conn, StoredStatement stmt, int delta){
	try {
		//"SELECT * FROM history WHERE delta = ?;"
		stmt.analyse.setInt(1, delta);
		ResultSet result = stmt.analyse.executeQuery();
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
