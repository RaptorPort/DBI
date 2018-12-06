package init_tps_DB;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

import com.opencsv.CSVWriter;

public class VariableInfile {
	final static String ADDRESS68 = "ADRESSE6812345678912345678912345678912345678912345678912345678912345";
	final static String ADDRESS72 = "ADRESSE72123456789123456789123456789123456789123456789123456789123456789";
	final static String NAME20 = "NAME2001234567890123";
	final static String CMMNT30 = "CMMNT1234567891234567891234567";
	
	private static void createINFILEcsvAccounts(int n, int filenumber, int start, int length) {
		Random zufall = new Random(); // neues Random Objekt, namens zufall		
		
		try (
	            Writer writer = Files.newBufferedWriter(Paths.get("C:\\ProgramData\\MySQL\\MySQL Server 8.0\\Data\\test\\INFILEaccounts" + filenumber + ".csv"));
				
	            CSVWriter csvWriter = new CSVWriter(writer,
	                    CSVWriter.DEFAULT_SEPARATOR,
	                    CSVWriter.NO_QUOTE_CHARACTER,
	                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
	                    CSVWriter.DEFAULT_LINE_END);
	        ) {
	            String[] headerRecord = {"accid", "name", "balance", "branchID", "address"};
	            csvWriter.writeNext(headerRecord);
	            for (int i = start; i < start + length; i++) {
	            	csvWriter.writeNext(new String[]{i + "", NAME20, "0", (int)(zufall.nextDouble()*n+1) + "", ADDRESS68});
	            }
	        } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}
	
	public static void init_tps_DB(Connection conn, int n) throws SQLException {
		Random zufall = new Random(); // neues Random Objekt, namens zufall
		int initialsize = 10000;
		int size = initialsize;
		int filenumber = 1;
		PreparedStatement stmt = conn.prepareStatement( 
				"insert into branches values (?, " + NAME20 + ", 0, " + ADDRESS72 + ")"
				);
		//Branches Relation
		for (int i = 1; i <= n; i++) {
			stmt.setInt(1, i);
			stmt.executeUpdate();
		}
		conn.commit();
		System.out.println("Branches DONE");
	
		//Account Relation
		
		Statement stmt2 = conn.createStatement();
		System.out.println("Start INLINE insert");
		size = initialsize;
		filenumber = 1;
		for (int i = 1; i <= n*100000; i += size) {
			createINFILEcsvAccounts(n, filenumber, i, size);
			stmt2.executeUpdate("LOAD DATA INFILE 'INFILEaccounts" + filenumber + ".csv' INTO TABLE test.accounts FIELDS TERMINATED BY ',' LINES TERMINATED BY '\n' IGNORE 1 LINES;");
			filenumber++;
		}
		conn.commit();
		
		stmt2.close();
		System.out.println("Commit check");
		
		System.out.println("Accounts DONE");
		//Teller Relation
		stmt = conn.prepareStatement( 
				"insert into tellers values (?, ?, 0, ?, ?)"
				);
		for (int i = 1; i <= n*10; i++) {
			stmt.setInt(1, i);
			stmt.setString(2, NAME20);
			stmt.setInt(3, (int)(zufall.nextDouble()*n+1));
			stmt.setString(4, ADDRESS68);
			stmt.executeUpdate();
		}
		conn.commit(); 
		System.out.println("Tellers DONE");
		//0 Tupel in der HISTORY-Relation.
		
	}
}
