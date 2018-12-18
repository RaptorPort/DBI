import java.sql.Connection;

import java.sql.SQLException;

import java.util.Random;


import tx.*;

public class load_driver extends Thread {
	public enum State {
		EINSCHWINGPHASE, MESSPHASE, AUSSCHWINGPHASE
	}
	static final State EINSCHWINGPHASE = State.EINSCHWINGPHASE;
	static final State MESSPHASE = State.MESSPHASE;
	static final State AUSSCHWINGPHASE = State.AUSSCHWINGPHASE;
	
	Connection conn;
	StoredStatement stmt;
	int rndmInit;
	int opCounter = 0;
	long messzeit = 0;
	State state = EINSCHWINGPHASE;
	
	public load_driver(Connection conn, StoredStatement stmt, int rndmInit) {
		this.conn = conn;
		this.rndmInit = rndmInit;
		this.stmt = stmt;
	}
	
	public void run() {
		Random rand = new Random(rndmInit);	
		long startTime = 0;
		long endEinschwingphase = 0;
		long endMessphase = 0;
		long endAusschwingphase = 0;
		
		try {
		System.out.println("Starting Benchmark!");
			startTime = System.currentTimeMillis(); //start measuring time
			
			//Load-Driver Schleife 10min = 600000ms
			while (System.currentTimeMillis()-startTime <= 600000) {
				//Einschwingphase
				if (state == EINSCHWINGPHASE && System.currentTimeMillis()-startTime >= 240000) {
					endEinschwingphase = System.currentTimeMillis();
					state = MESSPHASE;
				}
					
				int rndm = rand.nextInt(100) + 1;
				if (rndm <= 35) {
					//Kontostand
					tx.Kontostand.start(conn, stmt, rand.nextInt(10000000));
				} else if (rndm <= 85) {
					//Einzahlung
					tx.Einzahlung.start(conn, stmt, rand.nextInt(10000000)+1, rand.nextInt(1000)+1, rand.nextInt(100)+1, rand.nextInt(10000)+1);
					
				} else {
					//Analyse
					tx.Analyse.start(conn, stmt, rand.nextInt(10000)+1);
				}
				
				//Messphase
				if(state == MESSPHASE) {
					opCounter++;
					if (System.currentTimeMillis()-endEinschwingphase >= 300000) {
						state = AUSSCHWINGPHASE;
						endMessphase = System.currentTimeMillis();
					}
				}
				//wait 50ms
				Thread.sleep(50);
			}
			endAusschwingphase = System.currentTimeMillis();
			//Auswertung
			messzeit = endMessphase-endEinschwingphase;
			System.out.println("Thread: " + Thread.currentThread().getId() + "Messzeit: " + messzeit 
					+ "ms\tOps: " + opCounter + "\tOps/sec: " + opCounter / (messzeit / 1000));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return;			
	}
}
