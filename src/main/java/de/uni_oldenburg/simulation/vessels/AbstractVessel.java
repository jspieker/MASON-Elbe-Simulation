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
import sim.util.*;

import java.util.ArrayList;

/**
 * The AbstractVessel combines the properties of all vessels
 */
@SuppressWarnings("serial")
public abstract class AbstractVessel implements Steppable {
	
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
	
	//---Movement---
	Matrix M, D, C;


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
		
		myPosition = elbe.getVesselGrid().getObjectLocation(this);
		
		//create /change distance Network
		observNearSpace(elbe, myPosition);
		
			yaw = computeYaw(elbe, myPosition);
			
			prePosition = predictPosition(myPosition, yaw);
			
			adaptSpeed(elbe, prePosition, yaw);			
		
		myCourse = prePosition;
		
		//System.out.println(" Schiff: "+this.toString() + " Kurs: " +myCourse.toString());
		//System.out.println("Step: "+ elbe.schedule.getSteps() +  " time: "+elbe.schedule.getTime());
		
		elbe.getVesselGrid().setObjectLocation(this, myCourse);
		
		observer.update(this);
		
		
	}
	
	private void adaptSpeed(Elbe elbe, Double2D prePosition, double yaw){
		
		Bag vesselBag = observationField.getAllNodes();
		
		vesselBag.remove(this);
		
		for (Object vessel : vesselBag) {
			
			Double2D d = elbe.getVesselGrid().getObjectLocation(vessel);
			
			if (((AbstractVessel) vessel).getDirectionHamburg() == this.getDirectionHamburg()) {
				
				Edge e = observationField.getEdge(this, vessel);
				
				MutableDouble2D otherPos = new MutableDouble2D(elbe.getVesselGrid().getObjectLocation(vessel));
				
				Double2D myPosition = elbe.getVesselGrid().getObjectLocation(this);
				
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
			Bag vesselBag = elbe.getVesselGrid().getAllObjects();//getNeighborsExactlyWithinDistance(myPosition, distance, true);
			
			vesselBag.remove(this);	
			
			for (Object vessel : vesselBag) {
				
				MutableDouble2D otherPos = new MutableDouble2D(elbe.getVesselGrid().getObjectLocation(vessel));
			
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
					
					MutableDouble2D otherPos = new MutableDouble2D(elbe.getVesselGrid().getObjectLocation(vessel));
					
					//delete vessel from Observation Network
					if(otherPos.distance(myPosition) > distance){	
						observationField.removeEdge(observationField.getEdge(this, vessel));
						observationField.removeNode(vessel);
					}
				}
				
				//add vessel to Observation Network
				if(isNew){
					
					MutableDouble2D otherPos = new MutableDouble2D(elbe.getVesselGrid().getObjectLocation(newVessel));
					
					observationField.addNode(newVessel);
					
					observationField.addEdge(this, newVessel, otherPos.distance(myPosition) );
				}
			}
			
			//update already known distances
			for (Object vessel : observationField.getAllNodes()) {
				
				if(!this.equals(vessel)){
					Edge e = observationField.getEdge(this, vessel);
					
					MutableDouble2D otherPos = new MutableDouble2D(elbe.getVesselGrid().getObjectLocation(vessel));
					
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

	

	
	
	
/*
 * TODO improve physical model for the movement
 * 
	private  MutableDouble2D Movement(MutableDouble2D position, double yaw){
		
		// TODO physikalische/mathematische Modell für die Fortbewegung
		
		
		double x, xNew;
		double y, yNew;
		double v = 0, vNew;
		
		double epsilon = M.get(2,3)/M.get(2,2);
		
		x = position.getX();
		y = position.getY();
		
		double [][] j =	{{cos(yaw), -sin(yaw), 0},
				 		 {sin(yaw), cos(yaw) , 0},
				 		 {0		  ,	0		 , 1}};			
		
			
			
		//Tranform vessels dynamic
		xNew = x + epsilon * cos(yaw);
		yNew = y + epsilon * sin(yaw);
		double r = 0;
		vNew = v + epsilon * r;
		
		MutableDouble2D positionNew = new MutableDouble2D(xNew, yNew);
		
		return positionNew;
	}
	
 
	protected void Matrices(){
		
		double xu = -226.5 * 10e-5;
		
		double yv = -725 * 10e-5;
		double xg = -0.46 * 10e-5;
		double yr = 118.2 * 10e-5;
		double iz = 43.25 * 10e-5;
		double nr = 0;
		double xuu = -64.4 * 10e-5;
		double yvv = -5801.5 * 10e-5;
		double yrv = -1192.7 * 10e-5;
		double nrv = -174.4 * 10e-5;
		double yvr = -409.4 * 10e-5;
		double nvr = -778.8 * 10e-5;
		double nvv = -712.9 * 10e-5;
		double nv = -300 * 10e-5;
		double nrr = 0;
		double yrr = 0;
		
		double v;
		double u;
		double r;
		
		double [][] m =	{{(weight - xu), 0	   	, 0			 },
						 {0		, weight - yv	, weight * xg - yr},
						 {0		, weight - yv	, iz - nr}
						};
		
		double [][] c =	{{0							, 0				, -m[2][2] * v - m[2][3] * r},
				 		 {0							, 0			 	, m[1][1] * u},
				 		 {m[2][2] * v + m[2][3] * r	,-m[1][1] * u	, 0}
				 		};
		
		double [][] d =	{{-(xu + xuu * abs(u))	, 0									 , 0},
				  		 {0						, -(yv + yvv * abs(v) + yrv * abs(r)), -(yr + yvr * abs(v) + yrr * abs(r)) },
				  		 {0						, -(nv + nvv * abs(v) + nrv * abs(r)), -(nr + nvr * abs(v) + nrr * abs(r)) }
				  		 };
		
		M = new Matrix(m);
		C = new Matrix(c);
		D = new Matrix(d);
		
		
	}
*/
	
	//abstract void CreateRoute();
	
	//abstract void ChangeRoute();
	
}
