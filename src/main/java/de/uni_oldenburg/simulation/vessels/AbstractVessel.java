package de.uni_oldenburg.simulation.vessels;

import static java.lang.Math.*;

import Jama.Matrix;
import de.uni_oldenburg.simulation.elbe.Elbe;
import de.uni_oldenburg.simulation.elbe.Observer;
import sim.engine.*;
import sim.field.continuous.Continuous2D;
import sim.field.network.Edge;
import sim.field.network.Network;
import sim.portrayal.DrawInfo2D;
import sim.portrayal.simple.RectanglePortrayal2D;
import sim.util.*;

import java.awt.*;
import java.util.ArrayList;

/**
 * The AbstractVessel combines the properties of all vessels
 */
public abstract class AbstractVessel extends RectanglePortrayal2D implements Steppable {
	
	// Properties
	final private double weight;
	final private double length;
	final private double width;
	final private boolean directionHamburg;
	final private double maxSpeed;
	
	private double targetSpeed;
	
	private Network observationField;

	//target Distance to coast
	final private double targetDistance = 50;
	//observation area
	final private double distance = 50;
	
	Observer observer;

	/**
	 * Constructor
	 *
	 * @param weight Height of the vessel
	 * @param length Length of the vessel
	 * @param width Width of the vessel
	 * @param targetSpeed Target speed of the vessel
	 * @param directionHamburg True if moving towards docks, else false
	 * @param observer
	 */
	public AbstractVessel( double weight, double length, double width, double targetSpeed, boolean directionHamburg, Observer observer) {

		this.weight = weight;
		this.length = length;
		this.width = width;
		this.targetSpeed = targetSpeed;
		this.directionHamburg = directionHamburg;
		this.observer = observer;
		maxSpeed = 20;
		
		observationField = new Network();
		observationField.addNode(this);
	}

	// Getters (no setters needed)
	public double getWeight() {
		return weight;
	}
	public double getLength() {
		return length;
	}
	public double getWidth() {
		return width;
	}
	public double getTargetSpeed() {
		return targetSpeed;
	}
	public boolean getDirectionHamburg() {
		return directionHamburg;
	}
	
	@Override
	public void step(SimState state) {

		double yaw;
		
		Elbe elbe = (Elbe) state;
		
		Double2D myPosition, myCourse, prePosition;
		
		myPosition = elbe.vesselGrid.getObjectLocation(this);
		
		//create /change distance Network
		observNearSpace(elbe, myPosition);
		
			yaw = computeYaw(elbe, myPosition);
			
			prePosition = predictPosition(myPosition, yaw);
			
			adaptSpeed(elbe, prePosition, yaw);			
		
		myCourse = prePosition;
		
		//System.out.println(" Schiff: "+this.toString() + " Kurs: " +myCourse.toString());
		//System.out.println("Step: "+ elbe.schedule.getSteps() +  " time: "+elbe.schedule.getTime());
		
		elbe.vesselGrid.setObjectLocation(this, myCourse);
		
		observer.update(this);
	}
	
	private void adaptSpeed(Elbe elbe, Double2D prePosition, double yaw){
		
		Bag vesselBag = observationField.getAllNodes();
		
		vesselBag.remove(this);
		
		for (Object vessel : vesselBag) {
			
			Double2D d = elbe.vesselGrid.getObjectLocation(vessel);
			
			if (((AbstractVessel) vessel).getDirectionHamburg() == this.getDirectionHamburg()) {
				
				Edge e = observationField.getEdge(this, vessel);
				
				MutableDouble2D otherPos = new MutableDouble2D(elbe.vesselGrid.getObjectLocation(vessel));
				
				Double2D myPosition = elbe.vesselGrid.getObjectLocation(this);
				
				//forward
				if ((this.getDirectionHamburg() && d.getX() > prePosition.getX()) ^ 
						(!this.getDirectionHamburg() && d.getX() < prePosition.x)) {
					
					if((double) e.getInfo() < otherPos.distance(prePosition)){
					//reduce speed	
						do{
							
							targetSpeed -= 1;
							
							prePosition = predictPosition(myPosition, yaw);
						
						}while((double) e.getInfo() <= otherPos.distance(prePosition));
						
					
					}else if((double) e.getInfo() > otherPos.distance(prePosition)){
						//rise speed
						do{
							
							targetSpeed += 1;
							
							prePosition = predictPosition(myPosition, yaw);
						
						}while((double) e.getInfo() > otherPos.distance(prePosition) || targetSpeed == maxSpeed);
					}
				}
			}
		}
	}

	private  void observNearSpace(Elbe elbe, Double2D myPosition){	
		
			//new Obersvation field all Vessel  
			Bag vesselBag = elbe.vesselGrid.getAllObjects();//getNeighborsExactlyWithinDistance(myPosition, distance, true);
			
			vesselBag.remove(this);	
			
			for (Object vessel : vesselBag) {
				
				MutableDouble2D otherPos = new MutableDouble2D(elbe.vesselGrid.getObjectLocation(vessel));
			
				if(otherPos.distance(myPosition) > distance){	
					
					vesselBag.remove(vessel);
				}
			}
					
			
			for (Object newVessel : vesselBag) {
				
				boolean isNew = true;
				
				for (Object vessel : observationField.getAllNodes()) {
					
					if(vessel.equals(newVessel)){
						isNew = false;
						break;
					}
					
					//if vessel vector too long delete from Network
					
					MutableDouble2D otherPos = new MutableDouble2D(elbe.vesselGrid.getObjectLocation(vessel));
					
					//delete vessel from Observation Network
					if(otherPos.distance(myPosition) > distance){	
						observationField.removeEdge(observationField.getEdge(this, vessel));
						observationField.removeNode(vessel);
					}
				}
				
				//add vessel to Observation Network
				if(isNew){
					
					MutableDouble2D otherPos = new MutableDouble2D(elbe.vesselGrid.getObjectLocation(newVessel));
					
					observationField.addNode(newVessel);
					
					observationField.addEdge(this, newVessel, otherPos.distance(myPosition) );
				}
			}
			
			//update already known distances
			for (Object vessel : observationField.getAllNodes()) {
				
				if(!this.equals(vessel)){
					Edge e = observationField.getEdge(this, vessel);
					
					MutableDouble2D otherPos = new MutableDouble2D(elbe.vesselGrid.getObjectLocation(vessel));
					
					e.setInfo(otherPos.distance(myPosition));
				}
			}
		
		
	}
	
	private Double2D predictPosition(Double2D myPosition, double yaw){
		
		double x = myPosition.getX();
		double y = myPosition.getY();
		
		double xNew;
		double yNew;
		
		Double2D positionNew;
		
		if (!directionHamburg) {
			yaw += 180;
		}
		
		// compute position in coordiante
		yNew = sin(toRadians(yaw)) * targetSpeed + y;
		xNew = cos(toRadians(yaw)) * targetSpeed + x;
		
		positionNew = new Double2D(xNew, yNew);
		
		return positionNew;
	}

	private double computeYaw(Elbe elbe, Double2D myPosition){
		
		double yaw = 0;
		
		if (directionHamburg) {
			
			
			
		}else{
			
		}
		
		
		return yaw;
	}

	public void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
		graphics.setColor(Color.black);

		int x = (int) (info.draw.x - info.draw.width / 2.0);
		int y = (int) (info.draw.y - info.draw.height / 2.0);
		int width = (int) (info.draw.width);
		int height = (int) (info.draw.height);
		graphics.fillRect(x, y, 50, 50);
	}
}
