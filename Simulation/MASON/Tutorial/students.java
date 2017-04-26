package Tutorial;

import sim.engine.*;

public class students extends SimState {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4613919932312173669L;

	public students(long seed) {
		
		super(seed);
		
	}

	public void main(String[] args){
		doLoop(students.class, args);
		System.exit(0);
	}
	
}
