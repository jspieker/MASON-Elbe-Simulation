package de.uni_oldenburg.simulation.ship;

import sim.engine.Steppable;
import sim.util.MutableDouble2D;

public abstract class  Ship implements Steppable {
	
	abstract void Observation();
	
	abstract MutableDouble2D Movement(MutableDouble2D position, double degree);
	
	abstract void Colregs();
	
	abstract void CreateRoute();
	
	abstract void ChangeRoute();
	
	abstract void Matrices();
	
	
}
