/*
 * 
 * 
 */
package de.uni_oldenburg.simulation.ship;

import sim.engine.SimState;
import sim.util.*;
import static java.lang.Math.*;

import Jama.Matrix;

/*
 * Description 
 * 
 */
public class Transport extends Ship {
	/* Description implement*/
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3323072242122972608L;

	//Properties - physical
	private String mmsi;
	
	private String name;
	
	private double weight;
	
	private double length;
	
	private double width;
	
	//init with zero
	private double speed = 0;
	
	private Object direction;
	
	//---Simulation---
	private Bag route;
	
	//---Movement---
	Matrix M, D, C;
	
	
	/*
	 * 
	 */
	public Transport(String mmsi, String name, double weight, double length, double width) {
		super();
		this.mmsi = mmsi;
		this.name = name;
		this.weight = weight;
		this.length = length;
		this.width = width;
		
		Matrices();
	}

	/*
	 *
	 */
	public Transport(String mmsi, String name, double weight, double length, double width,  Object direction) {
		super();
		this.mmsi = mmsi;
		this.name = name;
		this.weight = weight;
		this.length = length;
		this.width = width;
		this.direction = direction;
		
		Matrices();
	}

	public double getSpeed() {
		return speed;
	}
	
	public Object getDirection() {
		return direction;
	}

	@Override
	public void step(SimState state) {
		/* TODO Erhöhung der Geschwindigkeit 
		 * TODO Beobachtung durchführen
		*/
		
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.uni_oldenburg.simulation.ships.Ships#Obersvation()
	 */
	@Override
	public void Observation() {
		
		// TODO Beobachtung der Umgebung
		// TODO Colregs aufrufen bei bevorstehender Kollision
		// TODO gegensteuern beim verlassen der Fahrrinne
		
	}

	@Override 
	protected MutableDouble2D Movement(MutableDouble2D position, double yaw) {
	
		// TODO physikalische/mathematische Modell für die Fortbewegung
	
		double x, xNew;
		double y, yNew;
		double v, vNew;
		
		double epsilon = M.get(2,3)/M.get(2,2);
		
		x = position.getX();
		y = position.getY();
		
		double [][] j =	{{cos(yaw), -sin(yaw), 0},
				 		 {sin(yaw), cos(yaw) , 0},
				 		 {0		  ,	0		 , 1}};
		
		
		
		//Tranform ship dynamic
		xNew = x + epsilon * cos(yaw);
		yNew = y + epsilon * sin(yaw);
		vNew = v + epsilon * r;
		
		
		
		MutableDouble2D positionNew = new MutableDouble2D(xNew, yNew);
		
		return positionNew;
	}

	@Override
	protected void Colregs() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void CreateRoute() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void ChangeRoute() {
		// TODO Auto-generated method stub
		
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
}
