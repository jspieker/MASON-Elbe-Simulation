package de.uni_oldenburg.simulation.ship;

import static java.lang.Math.*;

import java.util.ArrayList;
import java.util.List;

import Jama.Matrix;
import de.uni_oldenburg.simulation.elbe.Elbe;
import sim.engine.*;
import sim.field.*;
import sim.field.grid.IntGrid2D;
import sim.field.grid.SparseGrid2D;
import sim.util.*;

@SuppressWarnings("serial")
public abstract class  Ship implements Steppable {
	
	//Properties - physical
		private String mmsi;
		
		private String name;
		
		private double weight;
		
		private double length;
		
		private double width;
		
		//init with 1 m/s = 3.6 km/std ~ 1.94384 kn 
		private double speed;
		
		private boolean directionHamburg;
		
		private double radar;
		
		private int legDist = 10;
		//---Simulation---
		private Bag route;
		
		//---Movement---
		Matrix M, D, C;
	public Ship(String mmsi, String name, double weight, double length, double width, double speed) {
		super();
		this.mmsi = mmsi;
		this.name = name;
		this.weight = weight;
		this.length = length;
		this.width = width;
		this.speed = speed;
	}
	
	public Ship(String mmsi, String name, double weight, double length, double width, double speed, boolean directionHamburg) {
		super();
		this.mmsi = mmsi;
		this.name = name;
		this.weight = weight;
		this.length = length;
		this.width = width;
		this.speed = speed;
		this.directionHamburg = directionHamburg;
	}
	
	public double getSpeed() {
		return speed;
	}

	public Object getDirection() {
		return directionHamburg;
	}

	public void setDirection(boolean directionHamburg) {
		this.directionHamburg = directionHamburg;
	}

	public Bag getRoute() {
		return route;
	}

	public void setRoute(Bag route) {
		this.route = route;
	}

	public String getMmsi() {
		return mmsi;
	}

	public String getName() {
		return name;
	}

	public double getWeight() {
		return weight;
	}

	public double getLength() {
		return length;
	}
	public double getWidth() {
		return width;
	}
	
	@Override
	public void step(SimState state) {
		
		System.out.println("Huhu");
		
		double yaw = 0;
		
		Elbe elbe = (Elbe) state;
		
		Int2D myPosition, myCourse, prePosition; 
		
		List<Ship> foundShip;
		
		boolean rejectCourse = true;
		
		myPosition = elbe.getVesselGrid().getObjectLocation(this);

		double distanceToCoast = distanceToCoast(elbe, myPosition);
		
		foundShip = Observation(elbe, myPosition);
		
		int tries = 0;
		
		do{
			
			prePosition = predictionPosition(myPosition, yaw);
		
			if (Colregs(elbe.getVesselGrid(), prePosition, foundShip, distanceToCoast) || tries == 10) {
				
				rejectCourse = false;
			}
			
			tries ++;
			
			//yaw new define
					
			if (tries % 2 == 0) {

				yaw = tries * 5;
				
			} else {
				
				yaw = tries * (-1) * 5;
			}
		
		}while(rejectCourse);		
		
		myCourse = prePosition;
		
		elbe.getVesselGrid().setObjectLocation(this, myCourse);
		
		
	}
	
	private boolean Colregs(SparseGrid2D vesselGrid, Int2D prePosition, List<Ship> foundShip, double distanceToCoast){
		
		boolean acpt = false;
		
		Int2D obsPosition;
		
		if(!foundShip.isEmpty()){
			
			for (Ship ship : foundShip) {
				
				obsPosition = vesselGrid.getObjectLocation(ship);
				
			 	double distToShip = pow(pow(abs(obsPosition.getX() - prePosition.getX()), 2) + 	pow(abs(obsPosition.getY() - prePosition.getY()), 2), 0.5); 
				
			 	if (legDist >= distToShip) {
					
			 		acpt = true;
			 		
				}
				
			}
			
		}
		return acpt;
	}

	private double distanceToCoast(SimState state, Int2D myPosition){
		
		double distance;
		
		//TODO replace SparseGrid to Fairway
		//IntGrid2D fairWay = new IntGrid2D(1, 1); //state.getVesselGrid();
		
		//GetY derivation from GetX
		
		distance = 50 - myPosition.getY();
		
		return distance;
		
	}



	private  List<Ship> Observation(Elbe elbe, Int2D myPosition){
		
		
		SparseGrid2D vesselGrid = elbe.getVesselGrid();
		
		List<Ship> foundShip = new ArrayList<Ship>();
		
		//all Vessel 
		Bag vesselBag = vesselGrid.getAllObjects();
		
		MutableDouble2D position;
		
		for (int i = 0; i < vesselBag.size(); i++) {
			
			position = new MutableDouble2D(vesselGrid.getObjectLocation(vesselBag.get(i)));
			
			if(abs(position.getX() - myPosition.getX()) <= radar ){
				
				foundShip.add((Ship) vesselBag.get(i));
				
			}	
		}
		
		return foundShip;
	}
	
	private Int2D predictionPosition(Int2D myPosition, double yaw){
		
		double x = myPosition.getX();
		double y = myPosition.getY();
		
		double xNew;
		double yNew;
		
		Int2D positionNew;
		
		if (!directionHamburg) {
			
			yaw += 180;
			
		}
		
		//sin(a) * Hypo = GegenK
		
		yNew = sin(yaw) * speed + y;
		
		xNew = cos(yaw) * speed + x;		
		
		positionNew = new Int2D((int) xNew, (int) yNew);
		
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
		
			
			
		//Tranform ship dynamic
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
