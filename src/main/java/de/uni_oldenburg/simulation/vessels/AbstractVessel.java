package de.uni_oldenburg.simulation.vessels;

import static java.lang.Math.*;

import de.uni_oldenburg.simulation.elbe.Elbe;
import de.uni_oldenburg.simulation.elbe.Observer;
import sim.engine.*;
import sim.field.network.Edge;
import sim.field.network.Network;
import sim.portrayal.simple.ShapePortrayal2D;
import sim.util.*;

import java.awt.*;

/**
 * The AbstractVessel combines the properties of all vessels
 */
public abstract class AbstractVessel extends ShapePortrayal2D implements Steppable {

	// Properties
	final private double weight;
	final private double length;
	final private double width;
	final private boolean directionHamburg;
	final private double maxSpeed;

	private double targetSpeed;
	private double currentSpeed;
	private Double2D currentPosition;

	Elbe elbe;

	private Network observationField;

	//observation area
	final private double distance = 50;

	Observer observer;

	/**
	 * Constructor
	 *
	 * @param weight           Height of the vessel
	 * @param length           Length of the vessel
	 * @param width            Width of the vessel
	 * @param targetSpeed      Target speed of the vessel
	 * @param directionHamburg True if moving towards docks, else false
	 * @param observer
	 */
	public AbstractVessel(double weight, double length, double width, double targetSpeed, boolean directionHamburg, Observer observer) {

		super(new double[]{-length / 2 / 100, length / 4 / 100, length / 2 / 100, length / 4 / 100, -length / 2 / 100}, new double[]{-width / 2, -width / 2, 0, width / 2, width / 2}, new Color(255, 255, 0), 1, true);

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

	/**
	 * Move one simulation step (1 min)
	 *
	 * @param state
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

		// Transform km/h to 100m/min, calculate new position
		Double2D forwardMotion = new Double2D(0, -currentSpeed / 6.0); // course north (0 deg)
		forwardMotion = forwardMotion.rotate(getTargetYaw());
		Double2D newPosition = currentPosition.add(forwardMotion);

		elbe.vesselGrid.setObjectLocation(this, newPosition);
	}


	private void adaptSpeed(Elbe elbe, Double2D prePosition, double yaw) {

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
					if ((double) e.getInfo() < otherPos.distance(prePosition)) {
						//reduce speed
						do {
							targetSpeed -= 1;
							prePosition = predictPosition(myPosition, yaw);
						} while ((double) e.getInfo() <= otherPos.distance(prePosition));

					} else if ((double) e.getInfo() > otherPos.distance(prePosition)) {
						//rise speed
						do {
							targetSpeed += 1;
							prePosition = predictPosition(myPosition, yaw);
						} while ((double) e.getInfo() > otherPos.distance(prePosition) || targetSpeed == maxSpeed);
					}
				}
			}
		}
	}

	private void observNearSpace(Elbe elbe, Double2D myPosition) {
		//new Obersvation field all Vessel
		Bag vesselBag = elbe.vesselGrid.getAllObjects();//getNeighborsExactlyWithinDistance(myPosition, distance, true);
		vesselBag.remove(this);

		for (Object vessel : vesselBag) {

			MutableDouble2D otherPos = new MutableDouble2D(elbe.vesselGrid.getObjectLocation(vessel));

			if (otherPos.distance(myPosition) > distance) {

				vesselBag.remove(vessel);
			}
		}

		for (Object newVessel : vesselBag) {
			boolean isNew = true;
			for (Object vessel : observationField.getAllNodes()) {
				if (vessel.equals(newVessel)) {
					isNew = false;
					break;
				}

				//if vessel vector too long delete from Network
				MutableDouble2D otherPos = new MutableDouble2D(elbe.vesselGrid.getObjectLocation(vessel));

				//delete vessel from Observation Network
				if (otherPos.distance(myPosition) > distance) {
					observationField.removeEdge(observationField.getEdge(this, vessel));
					observationField.removeNode(vessel);
				}
			}

			//add vessel to Observation Network
			if (isNew) {
				MutableDouble2D otherPos = new MutableDouble2D(elbe.vesselGrid.getObjectLocation(newVessel));
				observationField.addNode(newVessel);
				observationField.addEdge(this, newVessel, otherPos.distance(myPosition));
			}
		}

		//update already known distances
		for (Object vessel : observationField.getAllNodes()) {

			if (!this.equals(vessel)) {
				Edge e = observationField.getEdge(this, vessel);
				MutableDouble2D otherPos = new MutableDouble2D(elbe.vesselGrid.getObjectLocation(vessel));
				e.setInfo(otherPos.distance(myPosition));
			}
		}
	}

	private Double2D predictPosition(Double2D myPosition, double yaw) {

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

	/**
	 * Get the best yaw according to the destination and the current environment
	 *
	 * @return yaw in rad from the normal yaw
	 */
	public double getTargetYaw() {

		double yaw;

		Bag neighbors = elbe.vesselGrid.getNeighborsWithinDistance(currentPosition, getLength());

		// Check neighbors
		for (int neighborId = 0; neighborId < neighbors.size(); neighborId++) {
			AbstractVessel vessel = (AbstractVessel) neighbors.get(neighborId);
			//if (vessel)
		}

		if (directionHamburg) {
			// look forward one ship length
			if (elbe.elbeMap.get((int) Math.ceil(currentPosition.x + getLength() / 10), (int) Math.ceil(currentPosition.y + getWidth())) == 0) {
				// near to coast (< half ship width)
				yaw = 0.785398; // 45 deg, turn left
			} else if (elbe.elbeMap.get((int) Math.ceil(currentPosition.x + getLength() / 10), (int) Math.ceil(currentPosition.y + getWidth() * 1.5)) == 0) {
				// just about right
				yaw = 1.5708; // 90 deg
			} else {
				// off coast (> 1 ship width), turn right
				yaw = 2.35619; // 135 deg
			}
			return yaw;
		} else {
			// look forward one ship length
			if (elbe.elbeMap.get((int) Math.ceil(currentPosition.x - getLength() / 10), (int) Math.ceil(currentPosition.y - getWidth())) == 0) {
				// near to coast (< half ship width), turn left
				yaw = 3.92699; // 225 deg
			} else if (elbe.elbeMap.get((int) Math.ceil(currentPosition.x - getLength() / 10), (int) Math.ceil(currentPosition.y - getWidth() * 1.5)) == 0) {
				// just about right
				yaw = 4.71239; // 270 deg
			} else {
				// off coast (> 1 ship width), turn right
				yaw = 5.49779; // 135 deg
			}
			return yaw;
		}

	}
}
