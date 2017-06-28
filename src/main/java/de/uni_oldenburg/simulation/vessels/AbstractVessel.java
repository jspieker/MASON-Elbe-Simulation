package de.uni_oldenburg.simulation.vessels;

import de.uni_oldenburg.simulation.elbe.Elbe;
import sim.engine.*;
import sim.portrayal.inspector.StableDouble2D;
import sim.portrayal.simple.ShapePortrayal2D;
import sim.util.*;

import java.awt.*;

/**
 * The AbstractVessel combines the properties of all vessels
 */
public abstract class AbstractVessel extends ShapePortrayal2D implements Steppable {

	// Properties
	final private double draught;
	final private double length;
	final private double width;
	final private boolean directionHamburg;

	private double targetSpeed;
	private double currentSpeed;
	public double currentYaw;
	private Double2D currentPosition;
	private Elbe elbe;

	/**
	 * Constructor
	 *
	 * @param draught           Height of the vessel
	 * @param length           Length of the vessel
	 * @param width            Width of the vessel
	 * @param targetSpeed      Target speed of the vessel
	 * @param directionHamburg True if moving towards docks, else false
	 */
	public AbstractVessel(double draught, double length, double width, double targetSpeed, boolean directionHamburg) {

		super(new double[]{-length / 2.0 / 100.0, length / 4.0 / 100.0, length / 2.0 / 100.0, length / 4.0 / 100.0, -length / 2.0 / 100.0}, new double[]{-width / 2, -width / 2, 0, width / 2, width / 2}, new Color(255, 255, 0), 1, true);

		this.draught = draught;
		this.length = length;
		this.width = width;
		this.targetSpeed = targetSpeed;
		this.directionHamburg = directionHamburg;
	}

	public double getDraught() {
		return draught;
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

	/**
	 * Move one simulation step (1 min)
	 * @param state The current sim state
	 */
	@Override
	public void step(SimState state) {

		elbe = (Elbe) state;
		currentPosition = elbe.vesselGrid.getObjectLocation(this);

		// Quit if vessel not on the map
		if (currentPosition == null) return;

		// Remove vessel when arrived at destination
		if ((directionHamburg && elbe.elbeMap.get((int) currentPosition.x, (int) currentPosition.y) == 3) ||
				(!directionHamburg && elbe.elbeMap.get((int) currentPosition.x, (int) currentPosition.y) == 2)) {
			elbe.vesselGrid.remove(this);
			// remove from the counter
			elbe.decreaseShipCount(this);
			return;
		}

		// TODO: dynamically adopt speed (with respect to vessel weight)
		currentSpeed = getTargetSpeed();
		currentYaw = getTargetYaw();

		elbe.vesselGrid.setObjectLocation(this, getTargetPosition());
	}


	/**
	 * Returns the predicted position within 1 step (=1 minute) with the current speed and yaw
	 * @return The calculated position
	 */
	public Double2D getTargetPosition() {

		// Transform km/h to 100m/min, calculate new position
		Double2D forwardMotion = new Double2D(0, -currentSpeed / 6.0); // course north (0 deg)
		forwardMotion = forwardMotion.rotate(currentYaw);
		return currentPosition.add(forwardMotion);
	}

	/**
	 * Returns true if another vessels is in sight, so that this vessel has to overtake
	 * @return true if overtaking needed
	 */
	public boolean vesselInFront() {

		Bag neighbors = elbe.vesselGrid.getAllObjects();
		double observationRadius = getLength()/100*15;

		for (int neighborId = 0; neighborId < neighbors.size(); neighborId++) {
			AbstractVessel nearVessel = (AbstractVessel) neighbors.get(neighborId);
			if (nearVessel != this && nearVessel != null && nearVessel.currentPosition != null) {
				if (directionHamburg) {
					if (nearVessel.currentPosition.x - currentPosition.x < observationRadius
							&& nearVessel.currentPosition.x - currentPosition.x > 0
							&& nearVessel.currentPosition.y - currentPosition.y < nearVessel.getWidth() + getWidth()
							&& currentPosition.y - nearVessel.currentPosition.y < nearVessel.getWidth() + getWidth()) {
						return true;
					}
				} else {
					if (currentPosition.x - nearVessel.currentPosition.x < observationRadius
							&& currentPosition.x - nearVessel.currentPosition.x> 0
							&& currentPosition.y - nearVessel.currentPosition.y < nearVessel.getWidth() + getWidth()
							&& nearVessel.currentPosition.y - currentPosition.y < nearVessel.getWidth() + getWidth()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Returns true if another vessel is to the right, so that this vessel can't go there
	 * @return true if starboard course is currently not possible
	 */
	public boolean vesselToTheRight() {

		Bag neighbors = elbe.vesselGrid.getAllObjects();
		double observationRadiusY = getWidth()*2;
		double observationRadiusX = getLength()/100*15;

		for (int neighborId = 0; neighborId < neighbors.size(); neighborId++) {
			AbstractVessel nearVessel = (AbstractVessel) neighbors.get(neighborId);
			if (nearVessel != this && nearVessel != null && nearVessel.currentPosition != null) {
				if (directionHamburg) {
					if (nearVessel.currentPosition.y - currentPosition.y > 0
							&& nearVessel.currentPosition.y - currentPosition.y < observationRadiusY
							&& Math.abs(nearVessel.currentPosition.x - currentPosition.x) < observationRadiusX) {
						return true;
					}
				} else {
					if (currentPosition.y - nearVessel.currentPosition.y > 0
							&& currentPosition.y - nearVessel.currentPosition.y < observationRadiusY
							&& Math.abs(currentPosition.x - nearVessel.currentPosition.x) < observationRadiusX) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Get the best yaw according to the destination and the current environment
	 * @return yaw in rad from the normal yaw
	 */
	public double getTargetYaw() {

		double yaw;

		// Normal yaw (without obstructions)
		if (directionHamburg) {
			yaw = 1.5708; // 90 deg
		} else {
			yaw = 4.71239; // 270 deg
		}

		// Look for land (half a ship width)
		Double2D minRefPoint = currentPosition.add(new Double2D(0, -getWidth()).rotate(yaw).rotate(1.5708));
		Double2D maxRefPoint = currentPosition.add(new Double2D(0, -getWidth() * 1.1).rotate(yaw).rotate(1.5708));
		if (elbe.elbeMap.get((int) Math.round(minRefPoint.x), (int) Math.round(minRefPoint.y)) == 0 || vesselInFront()) {
			// Too near to land
			yaw -= 0.785398; // 45 deg, turn left
		} else if (elbe.elbeMap.get((int) Math.round(maxRefPoint.x), (int) Math.round(maxRefPoint.y)) == 1 && !vesselToTheRight()) {
			// No land in sight
			yaw += 0.785398; // 45 deg, turn right
		}

		return yaw;
	}
}
