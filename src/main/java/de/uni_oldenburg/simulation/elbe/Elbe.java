package de.uni_oldenburg.simulation.elbe;

import de.uni_oldenburg.simulation.weka.CollisionWeka;
import de.uni_oldenburg.simulation.weka.WaterLevelWeka;
import de.uni_oldenburg.simulation.elbe.models.DynamicWaterLevel;
import de.uni_oldenburg.simulation.vessels.*;
import sim.field.continuous.Continuous2D;
import sim.engine.Schedule;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.grid.DoubleGrid2D;
import sim.field.grid.IntGrid2D;
import sim.util.Double2D;

import java.util.ArrayList;

public class Elbe extends SimState {

	public IntGrid2D elbeMap;
	public DoubleGrid2D tidesMap;
	public Continuous2D vesselGrid;

	private final int[] FAIRWAY_LENGTH = {507, 230, 230, 200, 48}; // TODO find exact values in relation
	private final int[] FAIRWAY_WIDTH_NOT_EXTENDED = {400, 300, 250, 250, 230}; // TODO find exact values for #2 #3 #4 (#0 and #1 are correct, others mostly)
	private final int[] FAIRWAY_WIDTH_EXTENDED = {400, 320, 380, 250, 250};
	private int[] FAIRWAY_WIDTH = FAIRWAY_WIDTH_NOT_EXTENDED;
	private final int MARGIN = 25;

	private DynamicWaterLevel dynamicWaterLevel;
	private final double DEPTH_REGULAR = 16.98;
	private final double DEPTH_DEEPENED = 17.4;
	private double depthOfWaterBelowCD = DEPTH_REGULAR;
	private boolean isTideActive = true;


	private boolean isExtended = false;
	private int fairwayLengthTotal;
	private int fairwayWidthMax;
	private int spawnPositionX;
	private int dockyardPositionX;
	private int gridHeight;
	private int gridWidth;
	private final int FAIRWAY_ID = 1;
	private final int SEA_POINT_ID = 2;
	private final int DOCKYARD_POINT_ID = 3;

	private double humanErrorInShipLength = 15;
	private double securityLevelGroundToDraught = 2.5; // TODO is that correct?

	// WEKA
	private WaterLevelWeka waterLevelWEKA;
	private CollisionWeka collisionWEKA;
	private boolean evaluate = false;

	private int numLargeContainerShips;
	private int numLargeContainerShipsSinceLastMeasurement;
	private int numSmallContainerShips;
	private int numSmallContainerShipsSinceLastMeasurement;
	private int numLargeTanker;
	private int numLargeTankerSinceLastMeasurement;
	private int numSmallTanker;
	private int numSmallTankerSinceLastMeasurement;

	private int collisionCount;
	private int waitingShipsCount;
	// Tide
	private final long HIGHT_TIDE_PERIOD = 19670 / 60;
	private final long LOW_TIDE_PERIOD = 24505 / 60;

	// Auxiliary properties
	private boolean ranAlready = false;
	private ElbeWithUI elbeWithUI;
	private final double elbeLengthToHamburg = 84900; // in meter
	private double scale = 50;
	private double minimumShips = 10;

	public Elbe(long seed) {
		super(seed);
		renderElbe();
	}

	/**
	 * Start the simulation
	 */
	public void start() {
		numLargeContainerShips = 0;
		numLargeContainerShipsSinceLastMeasurement = 0;
		numSmallContainerShips = 0;
		numSmallContainerShipsSinceLastMeasurement = 0;
		numLargeTanker = 0;
		numLargeTankerSinceLastMeasurement = 0;
		numSmallTanker = 0;
		numSmallTankerSinceLastMeasurement = 0;
		collisionCount = 0;
		waitingShipsCount = 0;
		if (ranAlready) {
			elbeWithUI.setupPortrayals();
		} else {
			ranAlready = true;
		}
		super.start(); // clear out the schedule

		// Schedule Tides
		schedule.scheduleRepeating(Schedule.EPOCH, 1, (Steppable) (SimState state) -> {
			double[] currentWaterLevels = dynamicWaterLevel.getCurrentWaterLevels(schedule.getSteps());
			for (int x = 0; x < gridWidth; x++) {
				double waterLevel = depthOfWaterBelowCD + currentWaterLevels[x];
				for (int y = 0; y < gridHeight; y++) {
					tidesMap.set(x, y, waterLevel);
				}
				// weka entries
				if (x % 10 == 0)
					waterLevelWEKA.addWEKAEntry(new Object[]{schedule.getSteps(), x, waterLevel});
			}
			// weka entries
			if (schedule.getSteps() % (HIGHT_TIDE_PERIOD + LOW_TIDE_PERIOD) == 0 && schedule.getSteps() != 0) {
				collisionWEKA.addWEKAEntry(new Object[]{schedule.getSteps(), isTideActive(), getIsExtended(),
						numLargeContainerShips + numLargeContainerShipsSinceLastMeasurement, numSmallContainerShips + numSmallContainerShipsSinceLastMeasurement, numLargeTanker + numLargeTankerSinceLastMeasurement,
						numSmallTanker + numSmallTankerSinceLastMeasurement, collisionCount, humanErrorInShipLength, waitingShipsCount});
				numLargeContainerShipsSinceLastMeasurement = 0;
				numSmallContainerShipsSinceLastMeasurement = 0;
				numLargeTankerSinceLastMeasurement = 0;
				numSmallTankerSinceLastMeasurement = 0;
				collisionCount = 0; // reset collisions
				waitingShipsCount = 0; // reset waitingShips
			}
		}, 1);

		vesselGrid.clear();

		// Dynamically spawn new vessels
		schedule.scheduleRepeating(Schedule.EPOCH, 1, (Steppable) (SimState state) -> {

			// Spawn vessels coming from sea
			if (newShipArrivedFromSea()) {
				AbstractVessel newVessel = getNewVessel(true);
				if (!vesselNeedsToWait(newVessel)) {
					vesselGrid.setObjectLocation(newVessel, new Double2D(0, 380));
					schedule.scheduleRepeating(newVessel, 1);
					increaseShipCount(newVessel);
				}
			}

			// Spawn vessels coming from docks
			if (newShipArrivedFromDocks()) {
				AbstractVessel newVessel = getNewVessel(false);
				if (!vesselNeedsToWait(newVessel)) {
					vesselGrid.setObjectLocation(newVessel, new Double2D(gridWidth - 1, 170));
					schedule.scheduleRepeating(newVessel, 1);
					increaseShipCount(newVessel);
				}
			}

			checkForCollision();

		}, 1);
	}

	private boolean vesselNeedsToWait(AbstractVessel newVessel) {

		// determine worstCaseTime
		double averageDistanzAtStep = newVessel.getTargetSpeed() / scale;
		double averageTimeNeeded = (double) gridWidth / averageDistanzAtStep;

		for (double timeStep = 0; timeStep <= averageTimeNeeded; timeStep++) {
			double expetedWaterLevelAtXAndTime = depthOfWaterBelowCD + dynamicWaterLevel.getFutureWaterLevelAtTimePosition((long) (timeStep + schedule.getSteps()), (int) (averageDistanzAtStep * timeStep));
			if (expetedWaterLevelAtXAndTime - securityLevelGroundToDraught < newVessel.getDraught()) {
				//System.out.println(newVessel.getClass().getSimpleName() + " needs to wait: ExpectedWaterLevel at (" + averageDistanzAtStep * timeStep + ", " + (timeStep + schedule.getSteps()) + ") (x,t) is " + expetedWaterLevelAtXAndTime + " with SCN " + depthOfWaterBelowCD + " and draught " + newVessel.getdraught() + " and securityLevel " + securityLevelGroundToDraught + " meters.");
				waitingShipsCount++;
				return true;
			}
		}

		return false;
	}

	private void checkForCollision() {
		ArrayList<AbstractVessel> vessels = new ArrayList<>();
		ArrayList<AbstractVessel> toRemove = new ArrayList<>();
		for (Object object : vesselGrid.getAllObjects()) {
			AbstractVessel abstractVessel = (AbstractVessel) object;
			if (shipIsAshore(abstractVessel)) {
				toRemove.add(abstractVessel);
				collisionCount++;
			} else {
				vessels.add(abstractVessel);
			}
		}

		for (AbstractVessel vessel1 : vessels) {
			for (AbstractVessel vessel2 : vessels) {
				if (!toRemove.contains(vessel1) || !toRemove.contains(vessel2)) { // must be or connection if three or mor hips collide
					double widthFromCenter1 = vessel1.getWidth() / 2;
					double lengthFromCenter1 = vessel1.getLength() / 2 / scale;
					double widthFromCenter2 = vessel2.getWidth() / 2;
					double lengthFromCenter2 = vessel2.getLength() / 2 / scale;

					if (vessel1.getCurrentPosition() != null && vessel2.getCurrentPosition() != null && !vessel1.equals(vessel2)) {
						double x1 = vessel1.getCurrentPosition().getX();
						double x2 = vessel2.getCurrentPosition().getX();
						double y1 = vessel1.getCurrentPosition().getY();
						double y2 = vessel2.getCurrentPosition().getY();

						double x1LesserBound = x1 - lengthFromCenter1;
						double x1UpperBound = x1 + lengthFromCenter1;
						double x2LesserBound = x2 - lengthFromCenter2;
						double x2UpperBound = x2 + lengthFromCenter2;

						double y1LesserBound = y1 - widthFromCenter1;
						double y1UpperBound = y1 + widthFromCenter1;
						double y2LesserBound = y2 - widthFromCenter2;
						double y2UpperBound = y2 + widthFromCenter2;

						if (x1UpperBound >= x2LesserBound && x1UpperBound <= x2UpperBound
								&& ((y2LesserBound <= y1LesserBound && y1LesserBound <= y2UpperBound)
								|| (y2LesserBound <= y1UpperBound && y1UpperBound <= y2UpperBound)
								|| (y1LesserBound <= y2LesserBound && y2LesserBound <= y1UpperBound)
								|| (y1LesserBound <= y2UpperBound && y2UpperBound <= y1UpperBound))) { // rear-end collision from wilhelmshaven
							//System.out.println("Collision with:\n" +
							//		"x1_less: " + x1LesserBound + ", x1_upper: " + x1UpperBound + " to x2_less: " + x2LesserBound + ", x2_upper: " + x2UpperBound + "\n" +
							//		"y1_less: " + y1LesserBound + ", y1_upper: " + y1UpperBound + " to y2_less: " + y2LesserBound + ", y2_upper: " + y2UpperBound + "\n");
							toRemove.add(vessel1);
							toRemove.add(vessel2);
							collisionCount++;
						} else if (x1LesserBound <= x2UpperBound && x1LesserBound >= x2LesserBound
								&& ((y2LesserBound <= y1LesserBound && y1LesserBound <= y2UpperBound)
								|| (y2LesserBound <= y1UpperBound && y1UpperBound <= y2UpperBound)
								|| (y1LesserBound <= y2LesserBound && y2LesserBound <= y1UpperBound)
								|| (y1LesserBound <= y2UpperBound && y2UpperBound <= y1UpperBound))) { // frontal collision1
							//System.out.println("Collision with:\n" +
							//		"x1_less: " + x1LesserBound + ", x1_upper: " + x1UpperBound + " to x2_less: " + x2LesserBound + ", x2_upper: " + x2UpperBound + "\n" +
							//		"y1_less: " + y1LesserBound + ", y1_upper: " + y1UpperBound + " to y2_less: " + y2LesserBound + ", y2_upper: " + y2UpperBound + "\n");
							toRemove.add(vessel1);
							toRemove.add(vessel2);
							collisionCount++;
						}
					}
				}
			}
		}
		for (AbstractVessel vessel : toRemove) {
			vesselGrid.remove(vessel);
			decreaseShipCount(vessel); // decrease from the counter
			//System.out.println(collisionCount);
		}
	}

	private boolean shipIsAshore(AbstractVessel abstractVessel) {

		Double2D double2D = abstractVessel.getCurrentPosition();
		if (double2D != null) {
			// System.out.println("Pos: " + double2D.getX() + "," +
			double x = double2D.getX();
			double y = double2D.getY();

			double widthFromShipsCenter = abstractVessel.getWidth() / 2;
			double lengthFromShipsCenter = abstractVessel.getLength() / 2;

			if (elbeMap.get((int) x, (int) Math.ceil(y + widthFromShipsCenter)) == 0 || elbeMap.get((int) x, (int) Math.floor(y - widthFromShipsCenter)) == 0) // 0 is ashore
				return true;
		}
		return false;
	}

	private boolean newShipArrivedFromSea() {
		return !(vesselGrid.getAllObjects().size() >= minimumShips) || random.nextBoolean(0.02);
	}

	private boolean newShipArrivedFromDocks() {
		return !(vesselGrid.getAllObjects().size() >= minimumShips) || random.nextBoolean(0.02);
	}

	private AbstractVessel getNewVessel(boolean directionHamburg) {

		double randomValue = random.nextDouble();
		double randomVesselType = random.nextDouble(false, true) * 100;

		if (randomValue < 0.79) { // 79% of Vessels are Cargos
			//Tanker or Cargo
			/* Cargo
			 * 3x 	16x400x60 	22kn 3%
			 * 10x 	15.5x365x50 24kn 10%
			 * 16x 	15x300x30	24kn 17%
			 * 18x 	15x210x30	14kn 19%
			 * 21x	10x155x25	 	 22%
			 * 24x	6x110x17 	13kn 26%
			 */
			if (randomVesselType <= 3) {
				return new LargeContainer(directionHamburg, humanErrorInShipLength, scale, 16, 400, 60, 22);
			} else if (3 < randomVesselType & randomVesselType <= 13) {
				return new LargeContainer(directionHamburg, humanErrorInShipLength, scale, 15.5, 365, 50, 24);
			} else if (13 < randomVesselType & randomVesselType <= 30) {
				return new LargeContainer(directionHamburg, humanErrorInShipLength, scale, 15, 300, 30, 24);
			} else if (30 < randomVesselType & randomVesselType <= 49) {
				return new SmallContainer(directionHamburg, humanErrorInShipLength, scale, 15, 210, 30, 14);
			} else if (49 < randomVesselType & randomVesselType <= 71) {
				return new SmallContainer(directionHamburg, humanErrorInShipLength, scale, 10, 155, 25, 14);
			} else {
				return new SmallContainer(directionHamburg, humanErrorInShipLength, scale, 6, 110, 17, 13);
			}
		} else {
			/*
			 * Tanker
			 * 1x 8x250x45		13kn 4%
			 * 3x 12x180x29 	15kn 12%
			 * 3x 6x138x21 		10kn 12%
			 * 7x 6x117x17 		15kn 28%
			 * 6x 7x100x16 		13kn 24%
			 * 5x 4x70x12 		12kn 20%
			 */
			if (randomVesselType <= 4) {
				return new LargeTanker(directionHamburg, humanErrorInShipLength, scale, 8, 250, 45, 13);
			} else if (4 < randomVesselType & randomVesselType <= 16) {
				return new LargeTanker(directionHamburg, humanErrorInShipLength, scale, 12, 180, 29, 15);
			} else if (16 < randomVesselType & randomVesselType <= 28) {
				return new LargeTanker(directionHamburg, humanErrorInShipLength, scale, 6, 138, 21, 10);
			} else if (28 < randomVesselType & randomVesselType <= 56) {
				return new LargeTanker(directionHamburg, humanErrorInShipLength, scale, 6, 117, 17, 15);
			} else if (56 < randomVesselType & randomVesselType <= 80) {
				return new SmallTanker(directionHamburg, humanErrorInShipLength, scale, 7, 100, 16, 13);
			} else {
				return new SmallTanker(directionHamburg, humanErrorInShipLength, scale, 4, 70, 12, 12);
			}
		}
	}

	@Override
	public void finish() {
		if (evaluate) {
			waterLevelWEKA.writeWEKAEntries();
			collisionWEKA.writeWEKAEntries();
			waterLevelWEKA.plotWEKAEntries();
			collisionWEKA.plotWEKAEntries();
		}
		super.finish();
		resetWEKA();
	}

	/**
	 * Draws the Elbe, the boat spawn area and the Hamburg dockyard onto the simulation map
	 */
	private void drawObjects() {
		// Init Elbe area
		int tempLengthHelper = 0;
		int tempWidthHelper = 0;
		for (int elbeSection = 0; elbeSection < FAIRWAY_LENGTH.length; elbeSection++) {
			try {
				tempWidthHelper = (FAIRWAY_WIDTH[elbeSection] - FAIRWAY_WIDTH[elbeSection + 1]) / 2;
			} catch (ArrayIndexOutOfBoundsException ignored) {
			}

			// Init blocks for elbe sections
			for (int i = MARGIN + tempLengthHelper; i < (MARGIN + tempLengthHelper + FAIRWAY_LENGTH[elbeSection]) - tempWidthHelper; i++) { // from left to right
				for (int j = ((fairwayWidthMax - FAIRWAY_WIDTH[elbeSection]) / 2) + MARGIN; j < (((fairwayWidthMax - FAIRWAY_WIDTH[elbeSection]) / 2) + MARGIN + FAIRWAY_WIDTH[elbeSection]); j++) { // from top to bottom
					elbeMap.field[i][j] = FAIRWAY_ID;
				}
			}

			// Calculate width transitions
			int tempTopIndex = ((fairwayWidthMax - FAIRWAY_WIDTH[elbeSection]) / 2) + MARGIN;
			int tempBottomIndex = FAIRWAY_WIDTH[elbeSection] - tempWidthHelper;
			for (int i = (MARGIN + tempLengthHelper + FAIRWAY_LENGTH[elbeSection]) - tempWidthHelper; i < (MARGIN + tempLengthHelper + FAIRWAY_LENGTH[elbeSection]); i++) {
				if (tempWidthHelper > 0) {
					// Draw transitions if the following elbeSection is narrower than the previous one
					for (int j = tempTopIndex; j < tempTopIndex + tempWidthHelper + tempBottomIndex; j++) {
						elbeMap.field[i][j] = FAIRWAY_ID;
					}
					tempTopIndex++;
					tempBottomIndex -= 2;
				}
			}
			tempLengthHelper += FAIRWAY_LENGTH[elbeSection];
		}

		// Init spawn area
		for (int i = ((fairwayWidthMax - FAIRWAY_WIDTH[0]) / 2) + MARGIN; i < (((fairwayWidthMax - FAIRWAY_WIDTH[0]) / 2) + MARGIN + FAIRWAY_WIDTH[0]); i++) {
			for (int j = 0; j < MARGIN; j++) {
				elbeMap.field[spawnPositionX - (j + 1)][i] = SEA_POINT_ID;
			}
		}

		// Init dockyard
		int lastTransitionWidth = (FAIRWAY_WIDTH[FAIRWAY_WIDTH.length - 2] - FAIRWAY_WIDTH[FAIRWAY_WIDTH.length - 1]) / 2;
		for (int i = ((fairwayWidthMax - FAIRWAY_WIDTH[(FAIRWAY_WIDTH.length - 1)]) / 2) + MARGIN + lastTransitionWidth; i < (((fairwayWidthMax - FAIRWAY_WIDTH[(FAIRWAY_WIDTH.length - 1)]) / 2) + MARGIN + FAIRWAY_WIDTH[(FAIRWAY_WIDTH.length - 1)]) - lastTransitionWidth; i++) {
			for (int j = 0; j < MARGIN; j++) {
				elbeMap.field[dockyardPositionX + j][i] = DOCKYARD_POINT_ID;
			}
		}
	}

	/**
	 * Calculates the initial distances, the grid dimensions and the source/target point based on their relation and the given values
	 */
	private void calculateInitialValues() {
		// fairwayWidthMax
		for (int width : FAIRWAY_WIDTH) {
			if (width > fairwayWidthMax) {
				fairwayWidthMax = width;
			}
		}

		// fairwayLengthTotal
		for (int length : FAIRWAY_LENGTH) {
			fairwayLengthTotal += length;
		}

		gridHeight = fairwayWidthMax + (MARGIN * 2);
		gridWidth = fairwayLengthTotal + (MARGIN * 2);

		spawnPositionX = MARGIN;
		dockyardPositionX = fairwayLengthTotal + MARGIN;
	}

	public void initWEKA(final String WEKAPath) {
		waterLevelWEKA = new WaterLevelWeka(WEKAPath);
		collisionWEKA = new CollisionWeka(WEKAPath);
	}

	public void resetWEKA() {
		waterLevelWEKA.resetWEKA();
		collisionWEKA.resetWEKA();
	}

	public void increaseShipCount(AbstractVessel vessel) {
		if (vessel instanceof LargeContainer) { // TODO add
			numLargeContainerShips++;
		} else if (vessel instanceof SmallContainer) {
			numSmallContainerShips++;
		} else if (vessel instanceof LargeTanker) {
			numLargeTanker++;
		} else {
			numSmallTanker++;
		}
	}

	public void decreaseShipCount(AbstractVessel vessel) {
		if (vessel instanceof LargeContainer) { // TODO add
			numLargeContainerShips--;
			numLargeContainerShipsSinceLastMeasurement++;
		} else if (vessel instanceof SmallContainer) {
			numSmallContainerShips--;
			numSmallContainerShipsSinceLastMeasurement++;
		} else if (vessel instanceof LargeTanker) {
			numLargeTanker--;
			numLargeTankerSinceLastMeasurement++;
		} else {
			numSmallTanker--;
			numSmallTankerSinceLastMeasurement++;
		}
	}

	private void renderElbe() {
		calculateInitialValues();
		dynamicWaterLevel = new DynamicWaterLevel(gridWidth, HIGHT_TIDE_PERIOD, LOW_TIDE_PERIOD, true, isTideActive);

		// Initialize empty grids
		elbeMap = new IntGrid2D(gridWidth, gridHeight, 0);
		tidesMap = new DoubleGrid2D(gridWidth, gridHeight, 0.0);
		vesselGrid = new Continuous2D(1, gridWidth, gridHeight);

		// Draw Elbe, spawn area and dockyard to the map
		drawObjects();
	}

	private void renderElbeWithoutInit() {
		dynamicWaterLevel = new DynamicWaterLevel(gridWidth, HIGHT_TIDE_PERIOD, LOW_TIDE_PERIOD, true, isTideActive);

		// Initialize empty grids
		elbeMap = new IntGrid2D(gridWidth, gridHeight, 0);
		tidesMap = new DoubleGrid2D(gridWidth, gridHeight, 0.0);
		//vesselGrid = new Continuous2D(1, gridWidth, gridHeight);

		// Draw Elbe, spawn area and dockyard to the map
		drawObjects();
	}

	// Geter and Setter

	public double getDepthOfWaterBelowCD() {
		return depthOfWaterBelowCD;
	}

	public void setDepthOfWaterBelowCD(int newDepth) {
		depthOfWaterBelowCD = newDepth;
	}

	public boolean getIsExtended() {
		return isExtended;
	}

	public void setIsExtended(boolean newValue) {
		isExtended = newValue;
		if (isExtended) {
			FAIRWAY_WIDTH = FAIRWAY_WIDTH_EXTENDED;
			depthOfWaterBelowCD = DEPTH_DEEPENED;
		} else {
			FAIRWAY_WIDTH = FAIRWAY_WIDTH_NOT_EXTENDED;
			depthOfWaterBelowCD = DEPTH_REGULAR;
		}
		renderElbeWithoutInit();
		elbeWithUI.setupPortrayals();
	}

	/**
	 * Calculates the upper border (y-value) of the fairway.
	 *
	 * @param x-value of position to be calculated
	 * @return y-value in relation to given x-value
	 */
	public int getTopBorderOfElbe(int x) {
		int tempLengthHelper = 0;
		int currentElbeSection = 0;
		for (int i = 0; i < FAIRWAY_LENGTH.length; i++) {
			tempLengthHelper += FAIRWAY_LENGTH[i];
			if (tempLengthHelper > x) {
				currentElbeSection = i;
				break;
			}
		}
		return ((fairwayWidthMax - FAIRWAY_WIDTH[currentElbeSection]) / 2);
	}

	/**
	 * Calculates the lower border (y-value) of the fairway.
	 *
	 * @param x-value of position to be calculated
	 * @return y-value in relation to given x-value
	 */
	public int getBottomBorderOfElbe(int x) {
		int tempLengthHelper = 0;
		int currentElbeSection = 0;
		for (int i = 0; i < FAIRWAY_LENGTH.length; i++) {
			tempLengthHelper += FAIRWAY_LENGTH[i];
			if (tempLengthHelper > x) {
				currentElbeSection = i;
				break;
			}
		}

		return (((fairwayWidthMax - FAIRWAY_WIDTH[currentElbeSection]) / 2) + MARGIN + FAIRWAY_WIDTH[currentElbeSection]);
	}

	public int getFairwayLengthTotal() {
		return fairwayLengthTotal;
	}

	public int getFairwayWidthMax() {
		return fairwayWidthMax;
	}

	public boolean executeStep() {
		return super.schedule.step(this);
	}

	public boolean isTideActive() {
		return isTideActive;
	}

	public void setTideActive(boolean tideActive) {
		isTideActive = tideActive;
		dynamicWaterLevel.setTideActive(this.isTideActive);
	}

	public boolean isEvaluate() {
		return evaluate;
	}

	public void setEvaluate(boolean evaluate) {
		this.evaluate = evaluate;
	}

	public void setElbeWithUI(ElbeWithUI elbeWithUI) {
		this.elbeWithUI = elbeWithUI;
	}

	public double getHumanErrorInShipLength() {
		return humanErrorInShipLength;
	}

	public void setHumanErrorInShipLength(double humanErrorInShipLength) {
		this.humanErrorInShipLength = humanErrorInShipLength;
	}

	public double getSecurityLevelGroundToDraught() {
		return securityLevelGroundToDraught;
	}

	public void setSecurityLevelGroundToDraught(double securityLevelGroundToDraught) {
		this.securityLevelGroundToDraught = securityLevelGroundToDraught;
	}

	public double getMinimumShips() {
		return minimumShips;
	}

	public void setMinimumShips(double minimumShips) {
		this.minimumShips = minimumShips;
	}
}