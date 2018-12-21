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
	
	State state = EINSCHWINGPHASE;
	StoredProcedure stmt;
	Random rand;
	//Benchmark result vars
	int opCounter = 0;
	int testCounter = 0;
	long messzeit = 0;
	
	public load_driver(StoredProcedure stmt, int rndmInit) {
		rand = new Random(rndmInit);	
		this.stmt = stmt;
	}
	
	public void run() {	
		long startTime = 0;
		long endEinschwingphase = 0;
		long endMessphase = 0;
		
		try {
		System.out.println("Starting Benchmark!");
			startTime = System.currentTimeMillis(); //start measuring time		
			//Load-Driver Schleife 10min = 600000ms
			while (System.currentTimeMillis()-startTime <= 600000) {
				int rndm = rand.nextInt(100) + 1;
				if (rndm <= 35) {
					stmt.kontostand_tx(rand.nextInt(10000000));
				} else if (rndm <= 85) {
					stmt.einzahlung_tx(rand.nextInt(10000000)+1, rand.nextInt(1000)+1, 
							rand.nextInt(100)+1, rand.nextInt(10000)+1);
				} else {
					stmt.analyse_tx(rand.nextInt(10000)+1);
				}
				if (state == EINSCHWINGPHASE) {
					testCounter++;	//count ops during EINSCHWINGPHASE
					if (System.currentTimeMillis()-startTime >= 240000 ) {
						endEinschwingphase = System.currentTimeMillis();
						System.out.println("Einschwing #Ops: " + testCounter + "\tper sec: " 
						+ (double)testCounter/(double)240);
						state = MESSPHASE;	//4min have passed -> start Messphase
					}
				}
				if(state == MESSPHASE) {
					opCounter++;
					if (System.currentTimeMillis()-endEinschwingphase >= 300000) {
						state = AUSSCHWINGPHASE;	//5min have passed -> end Messphase
						endMessphase = System.currentTimeMillis();
					}
				}
				Thread.sleep(50);	//wait 50ms
			}
			//Auswertung
			messzeit = endMessphase-endEinschwingphase;
			System.out.println("Thread: " + Thread.currentThread().getId() + "Messzeit: " + messzeit 
					+ "ms\tOps: " + opCounter + "\tOps/sec: " + opCounter / (messzeit / 1000));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return;	//Benchmark done -> close Thread		
	}
}
