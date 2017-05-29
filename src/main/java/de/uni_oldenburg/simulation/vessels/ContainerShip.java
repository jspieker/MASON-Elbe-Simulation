/*
 * 
 * 
 */
package de.uni_oldenburg.simulation.vessels;

import sim.engine.SimState;
import sim.util.*;
import static java.lang.Math.*;

import Jama.Matrix;

/*
 * A container ship
 */
public class ContainerShip extends AbstractVessel {
	
	//---Movement---
	Matrix M, D, C;

	public ContainerShip(boolean directionHamburg) {
		super(100, 200, 30, 20, directionHamburg);
		Matrices();
	}

	protected MutableDouble2D Movement(MutableDouble2D position, double yaw) {
	
		// TODO physikalische/mathematische Modell für die Fortbewegung
	
		double x, xNew;
		double y, yNew;
		
		double epsilon = M.get(2,3)/M.get(2,2);
		
		x = position.getX();
		y = position.getY();
		
		double [][] j =	{{cos(yaw), -sin(yaw), 0},
				 		 {sin(yaw), cos(yaw) , 0},
				 		 {0		  ,	0		 , 1}};
		
		//Tranform vessels dynamic
		xNew = x + epsilon * cos(yaw);
		yNew = y + epsilon * sin(yaw);
		
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

		// Values?!
		double v = 1;
		double u = 1;
		double r = 1;
		
		double [][] m =	{{(this.getWeight() - xu), 0	   	, 0			 },
						 {0		, this.getWeight() - yv	, this.getWeight() * xg - yr},
						 {0		, this.getWeight() - yv	, iz - nr}
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
