package tx;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Analyse {
	static Connection con = null;
	static PreparedStatement stmt = null;
	
	static int analyse (int delta){
	try {
		stmt = con.prepareStatement("SELCET = FROM histroy WHERE delta =" +delta+ ";");
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
