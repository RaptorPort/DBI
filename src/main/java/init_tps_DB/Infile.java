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

public class Infile {
	final static String ADDRESS68 = "ADRESSE6812345678912345678912345678912345678912345678912345678912345";
	final static String ADDRESS72 = "ADRESSE72123456789123456789123456789123456789123456789123456789123456789";
	final static String NAME20 = "NAME2001234567890123";
	final static String CMMNT30 = "CMMNT1234567891234567891234567";
	
	private static void createINFILEtxtAccounts(int n) {
		Random zufall = new Random(); // neues Random Objekt, namens zufall
		 
		
		
		try (Writer txt = new BufferedWriter(new OutputStreamWriter(
	              new FileOutputStream("INFILEaccounts.txt"), "ASCII"))) {
			for (int i = 1; i <= n*10000; i++) {
				txt.write(i + "\t" + NAME20 + "\t0\t" + (int)(zufall.nextDouble()*n+1) + "\t" + ADDRESS68 + "\n");
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void createINFILEcsvAccounts(int n) {
		Random zufall = new Random(); // neues Random Objekt, namens zufall		
		
		try (
	            Writer writer = Files.newBufferedWriter(Paths.get("C:\\ProgramData\\MySQL\\MySQL Server 8.0\\Data\\test\\INFILEaccounts.csv"));

	            CSVWriter csvWriter = new CSVWriter(writer,
	                    CSVWriter.DEFAULT_SEPARATOR,
	                    CSVWriter.NO_QUOTE_CHARACTER,
	                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
	                    CSVWriter.DEFAULT_LINE_END);
	        ) {
	            String[] headerRecord = {"accid", "name", "balance", "branchID", "address"};
	            csvWriter.writeNext(headerRecord);
	            for (int i = 1; i <= n*100000; i++) {
	            	csvWriter.writeNext(new String[]{i + "", NAME20, "0", (int)(zufall.nextDouble()*n+1) + "", ADDRESS68});
	            }
	        } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}
	
	public static void init_tps_DB(Connection conn, int n) throws SQLException {
		Random zufall = new Random(); // neues Random Objekt, namens zufall
		
		System.out.print("Generating INFILEaccounts.csv...");
		createINFILEcsvAccounts(n);
		System.out.println("\tdone!");
		
		PreparedStatement stmt = conn.prepareStatement( 
				"insert into branches values (?, 'BRANCHNAME', 0, 'ADDRESS')"
				);
		//n Tupel in der BRANCH-Relation mit fortlaufender BRANCHID (1 bis n), der
		//BALANCE 0 und Strings der richtigen Länge für BRANCHNAME und ADDRESS
		for (int i = 1; i <= n; i++) {
			stmt.setInt(1, i);
			stmt.executeUpdate();
		}
		conn.commit();
		System.out.println("Branches DONE");
	
		//n * 100000 Tupel in der ACCOUNTS-Relation mit fortlaufender ACCID (1 bis
		//n * 100000), dem Kontostand (BALANCE) 0, einer zufälligen BRANCHID (1 bis n) und
		//wieder beliebigen Strings der richtigen Länge für NAME und ADDRESS
		
		Statement stmt2 = conn.createStatement();
		System.out.println("Start INLINE insert");
		stmt2.executeUpdate("LOAD DATA INFILE 'INFILEaccounts.csv' INTO TABLE test.accounts FIELDS TERMINATED BY ',' LINES TERMINATED BY '\n' IGNORE 1 LINES;");
		conn.commit();
		System.out.println("Commit check");
		
		System.out.println("Accounts DONE");
		//n * 10 Tupel in der TELLER-Relation mit fortlaufender TELLERID (1 bis n * 10), der
		//BALANCE 0, einer zufälligen BRANCHID (1 bis n) und wieder beliebigen Strings der
		//richtigen Länge für TELLERNAME und ADDRESS
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
	
	public static void x() {
		try {
			Path po = Paths.get("./bin/src/INFILEaccounts");
			//Path folder = p.getParent();
			Process p = Runtime.getRuntime().exec("cmd /c start " + po);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
