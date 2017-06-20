package de.uni_oldenburg.simulation.elbe;

import de.uni_oldenburg.simulation.vessels.*;
import sim.field.continuous.Continuous2D;
import sim.util.Bag;
import sim.util.Double2D;

public  class Observer {

	private int almostCollision = 0;
	private int collision = 0;

	private Continuous2D vesselGrid;
	
	
	
	public Observer(Elbe elbe) {
		super();
		this.vesselGrid = elbe.vesselGrid;
	}

	public int getAlmostCollision() {
		return almostCollision;
	}

	public int getCollision() {
		return collision;
	}
	
	public AbstractVessel addVessel(boolean directionHamburg){
		
		AbstractVessel newVessel = new ContainerShip(directionHamburg, this);
		
		return newVessel;		
	}
	
	public void update(AbstractVessel vessel){
		
		//
		
		Double2D vesselPosition = vesselGrid.getObjectLocation(vessel);
		
		//wenn das Schiff am ausgang ist rufe removeVessel auf
		
		if(vesselPosition.getX() < 20 ^ vesselPosition.getX() > (vesselGrid.getWidth() - 20)){
		//	removeVessel(vessel);
		}
		
		//zähle die Kollisionen
		
		Bag vesselBag = vesselGrid.getAllObjects();
		
		vesselBag.remove(vessel);
		
		for (Object other : vesselBag) {
		
			Double2D otherPosition = vesselGrid.getObjectLocation(other);
			
			if(otherPosition.distance(vesselPosition) < 20){
				//almost collision
				countAlmostCollision();
			}else if(otherPosition.distance(vesselPosition) < 3){
				//collision
				countCollision();
			}
		}

		// TODO remove vessel
		
	}
	
	private void countCollision(){		
		collision++;		
	}
	
	private void countAlmostCollision(){
		almostCollision++;		
	}
	
	private boolean removeVessel(AbstractVessel vessel){
		
		
		boolean isRemove = false;
		
		//TODO entferne Verbindungen in anderen Networks
		
		vesselGrid.remove(vessel);
		
		return isRemove;
	}
	

}
