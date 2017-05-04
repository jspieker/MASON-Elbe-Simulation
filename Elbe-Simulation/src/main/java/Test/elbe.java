package Test;

import sim.engine.*;
import sim.engine.SimState.*;

public class elbe extends SimState {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public elbe(long seed) {
		super(seed);
		// TODO Auto-generated constructor stub
	}
	
	public static void main(String[] args) {
		
		doLoop(elbe.class, args);
		
		SimState elbe = new elbe(System.currentTimeMillis());
		
	
		
		System.exit(0);
		
	}

}
