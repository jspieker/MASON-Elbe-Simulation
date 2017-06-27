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

public class Elbe extends SimState {

	public IntGrid2D elbeMap;
	public DoubleGrid2D tidesMap;
	public Continuous2D vesselGrid;

	private final int[] FAIRWAY_LENGTH = {507, 230, 230, 200, 48}; // TODO find exact values in relation
	private final int[] FAIRWAY_WIDTH_NOT_EXTENDED = {400, 300, 250, 250, 230}; // TODO find exact values for #2 #3 #4 (#0 and #1 are correct, others mostly)
	private final int[] FAIRWAY_WIDTH_EXTENDED = {400, 320, 380, 270, 250};
	private int[] FAIRWAY_WIDTH = FAIRWAY_WIDTH_NOT_EXTENDED;
	private final int MARGIN = 25;

	private DynamicWaterLevel dynamicWaterLevel;
	private final double DEPTH_REGULAR = 16.98;
	private final double DEPTH_DEEPENED = 17.4;
	private double depthOfWaterBelowCD = DEPTH_REGULAR;
	private boolean isTideActive = true;


	private boolean isExtended = false;
	private boolean isDeepened = false;
	private int fairwayLengthTotal;
	private int fairwayWidthMax;
	private int spawnPositionX;
	private int dockyardPositionX;
	private int gridHeight;
	private int gridWidth;
	private final int FAIRWAY_ID = 1;
	private final int SEA_POINT_ID = 2;
	private final int DOCKYARD_POINT_ID = 3;

	private int numContainerShip = 0;
	private int numContainerShipSinceLastMeasurement = 0;
	private int numTankerShip = 0;
	private int numTankerShipSinceLastMeasurement = 0;
	private int numOtherShip = 0;
	private int numOtherShipSinceLastMeasurement = 0;
	private int collisionCount = 0;


	// weka
	private WaterLevelWeka waterLevelWEKA;
	private CollisionWeka collisionWEKA;

	private final long HIGHT_TIDE_PERIOD = 19670 / 60;
	private final long LOW_TIDE_PERIOD = 24505 / 60;

	public Elbe(long seed) {
		super(seed);

		calculateInitialValues();

		// Initialize empty grids
		elbeMap = new IntGrid2D(gridWidth, gridHeight, 0);
		tidesMap = new DoubleGrid2D(gridWidth, gridHeight, 0.0);
		vesselGrid = new Continuous2D(1, gridWidth, gridHeight);

		// Draw Elbe, spawn area and dockyard to the map
		drawObjects();
	}

	/**
	 * Start the simulation
	 */
	public void start() {
		super.start(); // clear out the schedule

		// Get some water
		dynamicWaterLevel = new DynamicWaterLevel(gridWidth, HIGHT_TIDE_PERIOD, LOW_TIDE_PERIOD, true, isTideActive);

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
			if (schedule.getSteps() == 0 || schedule.getSteps() % (HIGHT_TIDE_PERIOD + LOW_TIDE_PERIOD) == 0) {
				collisionWEKA.addWEKAEntry(new Object[]{schedule.getSteps(), isTideActive(), getIsExtended(), isDeepened(),
						numContainerShip + numContainerShipSinceLastMeasurement, numTankerShip + numTankerShipSinceLastMeasurement,
						numOtherShip + numOtherShipSinceLastMeasurement, collisionCount, collisionCount});
				numContainerShipSinceLastMeasurement = 0;
				numTankerShipSinceLastMeasurement = 0;
				numOtherShipSinceLastMeasurement = 0;
			}
		}, 1);

		vesselGrid.clear();

		// Dynamically spawn new vessels
		schedule.scheduleRepeating(Schedule.EPOCH, 1, (Steppable) (SimState state) -> {
			
			// Spawn vessels coming from sea
			if (newShipArrivedFromSea()) {				
				AbstractVessel newVessel = getNewVessel(true);
				vesselGrid.setObjectLocation(newVessel, new Double2D(0, 380));
				schedule.scheduleRepeating(newVessel, 1);
				increaseShipCount(newVessel);
			}

			// Spawn vessels coming from docks
			if (newShipArrivedFromDocks()) {
				AbstractVessel newVessel = getNewVessel(false);
				vesselGrid.setObjectLocation(newVessel, new Double2D(gridWidth - 1, 170));
				schedule.scheduleRepeating(newVessel, 1);
				increaseShipCount(newVessel);
			}
		}, 1);
	}

	private boolean newShipArrivedFromSea() {
		return random.nextBoolean(0.02);
	}

	private boolean newShipArrivedFromDocks() {
		return random.nextBoolean(0.02);
	}

	private AbstractVessel getNewVessel(boolean directionHamburg) {

		double randomValue = random.nextDouble();
		double randomVesselType = random.nextDouble(false,true) * 100;
		
		double draught, length, width, targetSpeed;

		// Configure the propabilities for some vessel types
		//Tanker oder Cargo
		/* Cargo
		 * 3x 	16x400x60 	22kn 3%
		 * 10x 	15.5x365x50 24kn 10%
		 * 16x 	15x300x30	24kn 17%
		 * 18x 	15x210x30	14kn 19%
		 * 21x	10x155x25	 	 22%
		 * 24x	6x110x17 	13kn26%
		 * 
		
		 */
		
		if (randomValue < 0.79) {//79% of Vessels are Cargos
			
			draught = 16;
			
			length = 400;
			
			width = 60;
			
			targetSpeed = 35;
			
			if (3 < randomVesselType & randomVesselType <= 13) {
				draught = 15;
				
				length = 365;
				
				width = 50;
				
				targetSpeed = 39;
				
			}else if(13 < randomVesselType & randomVesselType <= 30){

				draught = 15;
				
				length = 300;
				
				width = 30;
				
				targetSpeed = 39;
			}else if(30 < randomVesselType & randomVesselType <= 49){

				draught = 15;
				
				length = 210;
				
				width = 30;
				
				targetSpeed = 24;
			}else if(49 < randomVesselType & randomVesselType <= 71 ){

				draught = 10;
				
				length = 155;
				
				width = 25;
				
				targetSpeed = 22;
			}else if(71 < randomVesselType){

				draught = 6;
				
				length = 110;
				
				width = 17;
				
				targetSpeed = 20;
			}
			
			return new ContainerShip(draught, length, width, targetSpeed,directionHamburg);
			
		} else {
			/*
			 * Tanker
			 * 1x 8x250x45		13kn 4%
			 * 3x 12x180x29 	15kn 12%
			 * 3x 6x138x21 		10kn 12%
			 * 7x 6x117x17 		15kn 28%
			 * 6x 7x100x16 		13kn 24%
			 * 5x 4x70x12 		12kn 20%
			 * */

			draught = 8;
			
			length = 250;
			
			width = 45;
			
			targetSpeed = 20;
				
			if (4 < randomVesselType & randomVesselType <= 16) {

				draught = 12;
				
				length = 180;
				
				width = 29;
				
				targetSpeed = 24;
				
			}else if(16 < randomVesselType & randomVesselType <= 28){

				draught = 6;
				
				length = 138;
				
				width = 21;
				
				targetSpeed = 16;
			}else if(28 < randomVesselType & randomVesselType <= 56){

				draught = 6;
				
				length = 117;
				
				width = 17;
				
				targetSpeed = 24;
				
			}else if(56 < randomVesselType & randomVesselType <= 80){

				draught = 7;
				
				length = 100;
				
				width = 16;
				
				targetSpeed = 20;
			}else if(80 < randomVesselType){

				draught = 4;
				
				length = 70;
				
				width = 12;
				
				targetSpeed = 19;
			}
			
			return new Tanker(draught, length, width, targetSpeed,directionHamburg);
		}
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();

		waterLevelWEKA.writeWEKAEntries();
		collisionWEKA.writeWEKAEntries();
		waterLevelWEKA.plotWEKAEntries();
		collisionWEKA.plotWEKAEntries();
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

	public void increaseShipCount(AbstractVessel vessel) {
		if (vessel instanceof ContainerShip) {
			numContainerShip++;
		} else if (vessel instanceof Tanker) {
			numTankerShip++;
		} else {
			numOtherShip++; // TODO improve if else with other ships
		}
	}

	public void decreaseShipCount(AbstractVessel vessel) {
		if (vessel instanceof ContainerShip) {
			numContainerShip--;
			numContainerShipSinceLastMeasurement++;
		} else if (vessel instanceof Tanker) {
			numTankerShip--;
			numTankerShipSinceLastMeasurement++;
		} else {
			numOtherShip--; // TODO improve if else with other ships
			numOtherShipSinceLastMeasurement++;
		}
	}


	// Geter and Setter

	public double getDepthOfWaterBelowCD() {
		return depthOfWaterBelowCD;
	}

	public void setDepthOfWaterBelowCD(int newDepth) {
		depthOfWaterBelowCD = newDepth;
	}

	public boolean isDeepened() {
		return isDeepened;
	}

	public void setDeepened(boolean deepened) {
		isDeepened = deepened;
		if (isDeepened) {
			depthOfWaterBelowCD = DEPTH_DEEPENED;
		} else {
			depthOfWaterBelowCD = DEPTH_REGULAR;
		}
	}

	public boolean getIsExtended() {
		return isExtended;
	}

	public void setIsExtended(boolean newValue) {
		isExtended = newValue;
		if (isExtended) {
			FAIRWAY_WIDTH = FAIRWAY_WIDTH_EXTENDED;
		} else {
			FAIRWAY_WIDTH = FAIRWAY_WIDTH_NOT_EXTENDED;
		}
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
}