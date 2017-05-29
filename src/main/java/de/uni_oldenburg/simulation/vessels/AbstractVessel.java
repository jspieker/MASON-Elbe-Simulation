package de.uni_oldenburg.simulation.vessels;

import static java.lang.Math.*;

import Jama.Matrix;
import de.uni_oldenburg.simulation.elbe.Elbe;
import sim.engine.*;
import sim.field.continuous.Continuous2D;
import sim.util.*;

import java.util.ArrayList;

/**
 * The AbstractVessel combines the properties of all vessels
 */
public abstract class AbstractVessel implements Steppable {
	
	// Properties
	final private double weight;
	final private double length;
	final private double width;
	final private double targetSpeed;
	final private boolean directionHamburg;

	//---Movement---
	Matrix M, D, C;

	public AbstractVessel(double weight, double length, double width, double targetSpeed, boolean directionHamburg) {
		this.weight = weight;
		this.length = length;
		this.width = width;
		this.targetSpeed = targetSpeed;
		this.directionHamburg = directionHamburg;
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

		double yaw = 0;
		Elbe elbe = (Elbe) state;
		Double2D myPosition, myCourse, prePosition;
		ArrayList<AbstractVessel> foundShip;
		boolean rejectCourse = true;
		myPosition = elbe.vesselGrid.getObjectLocation(this);
		double distanceToCoast = distanceToCoast(elbe, myPosition);
		foundShip = Observation(elbe, myPosition);
		
		int tries = 0;
		do {
			prePosition = predictionPosition(myPosition, yaw);
			if (Colregs(elbe.vesselGrid, prePosition, foundShip, distanceToCoast) || tries == 10) {
				rejectCourse = false;
			}
			tries ++;
			
			//yaw new define
			if (tries % 2 == 0) {
				yaw = tries * 5;
			} else {
				yaw = tries * (-1) * 5;
			}
		
		} while (rejectCourse);
		
		myCourse = prePosition;
		elbe.vesselGrid.setObjectLocation(this, myCourse);
	}
	
	private boolean Colregs(Continuous2D vesselGrid, Double2D prePosition, ArrayList<AbstractVessel> foundShip, double distanceToCoast) {
		
		boolean acpt = false;
		
		Double2D obsPosition;
		if(!foundShip.isEmpty()){
			for (AbstractVessel ship : foundShip) {
				obsPosition = vesselGrid.getObjectLocation(ship);
			 	double distToShip = pow(pow(abs(obsPosition.getX() - prePosition.getX()), 2) + 	pow(abs(obsPosition.getY() - prePosition.getY()), 2), 0.5);
			 	if (10 >= distToShip) {
			 		acpt = true;
				}
			}
		}
		return acpt;
	}

	private double distanceToCoast(SimState state, Double2D myPosition){
		
		double distance;
		
		//TODO replace SparseGrid to Fairway
		//IntGrid2D fairWay = new IntGrid2D(1, 1); //state.getVesselGrid();
		//GetY derivation from GetX
		
		distance = 50 - myPosition.getY();
		return distance;
	}



	private  ArrayList<AbstractVessel> Observation(Elbe elbe, Double2D myPosition){

		Continuous2D vesselGrid = elbe.vesselGrid;
		ArrayList<AbstractVessel> foundShip = new ArrayList<AbstractVessel>();

		//all Vessel
		Bag vesselBag = vesselGrid.getAllObjects();
		MutableDouble2D position;
		
		for (int i = 0; i < vesselBag.size(); i++) {
			position = new MutableDouble2D(vesselGrid.getObjectLocation(vesselBag.get(i)));
			if(abs(position.getX() - myPosition.getX()) <= 10 ){
				foundShip.add((AbstractVessel) vesselBag.get(i));
				
			}	
		}
		
		return foundShip;
	}
	
	private Double2D predictionPosition(Double2D myPosition, double yaw){
		
		double x = myPosition.getX();
		double y = myPosition.getY();
		
		double xNew;
		double yNew;
		
		Double2D positionNew;
		
		if (!directionHamburg) {
			yaw += 180;
		}
		
		// sin(a) * Hypo = GegenK
		yNew = sin(yaw) * targetSpeed + y;
		xNew = cos(yaw) * targetSpeed + x;
		positionNew = new Double2D(xNew, yNew);
		return positionNew;
	}

/*
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
