package de.uni_oldenburg.simulation.elbe;

import de.uni_oldenburg.simulation.elbe.models.Tides;
import de.uni_oldenburg.simulation.vessels.*;
import sim.engine.*;
import sim.field.continuous.Continuous2D;
import sim.field.grid.DoubleGrid2D;
import sim.field.grid.IntGrid2D;
import sim.util.Double2D;
import sim.util.MutableDouble2D;

import java.lang.reflect.Array;

public class Elbe extends SimState {

	IntGrid2D elbeMap;
	DoubleGrid2D tidesMap;
	public Continuous2D vesselGrid;

	private final int[] FAIRWAY_LENGTH = 				{507, 230, 230, 200, 48}; // TODO find exact values in relation
	private final int[] FAIRWAY_WIDTH_NOT_EXTENDED =	{400, 300, 250, 250, 230}; // TODO find exact values for #2 #3 #4 (#0 and #1 are correct, others mostly)
	private final int[] FAIRWAY_WIDTH_EXTENDED = 		{400, 320, 380, 270, 250};
	private int[] FAIRWAY_WIDTH = FAIRWAY_WIDTH_NOT_EXTENDED;
	private final int MARGIN = 25;

	private Tides tides;
	private int depthOfWaterBelowCD = 15; // sample value

	private boolean isExtended = false;
	private int fairwayLengthTotal;
	private int fairwayWidthMax;
	private int spawnPositionX;
	private int dockyardPositionX;
	private int gridHeight;
	private int gridWidth;
	private final int FAIRWAY_ID = 1;
	private final int SPAWN_POINT_ID = 2;
	private final int DOCKYARD_POINT_ID = 3;

	private long stepCount = 0;

	public Elbe(long seed) {
		super(seed);

		calculateInitialValues();
	}
	
	public static void main(String[] args) {
		
		doLoop(Elbe.class, args);
		
		System.exit(0);
		
	}

	/**
	 * Start the simulation
	 */
	public void start() {
		super.start(); // clear out the schedule

		// Initialize grids
		elbeMap = new IntGrid2D(gridWidth, gridHeight, 0);
		tidesMap = new DoubleGrid2D(gridWidth, gridHeight, 0.0);
		vesselGrid = new Continuous2D(0.01, gridWidth, gridHeight);

		// Get some water
		tides = new Tides(25000/60, 20000/60, true, gridWidth);

		// Draw Elbe, spawn area and dockyard to the map
		drawObjects();
		
		
		// TODO Create Vessels
		
		vesselGrid.clear();

		
		for (int i = 0; i < 10; i++) {
			
			System.out.println("Vessel "+i+" created");
			
			ContainerShip vessel;
			
			if(i % 2 == 0){
				vessel = new ContainerShip(true);
			}else{
				vessel = new ContainerShip(false);
			}
			
			//Double2D location = new Double2D(vesselGrid.getHeight() * random.nextDouble() ,  vesselGrid.getWidth() * random.nextDouble());
			
			Double2D location = new Double2D(vesselGrid.getHeight() / 2, vesselGrid.getWidth() * random.nextDouble());			
			
			vesselGrid.setObjectLocation(vessel, location);
			
			schedule.scheduleRepeating(vessel);
			
		} 
		
		
		

		schedule.scheduleRepeating(Schedule.EPOCH, 1, (Steppable) (SimState state) -> {

			stepCount++;
			for (int x = 0; x < gridWidth; x++) {
				double waterLevel = depthOfWaterBelowCD + tides.computeWaterLevel(stepCount, depthOfWaterBelowCD, x);
				for (int y = 0; y < gridHeight; y++) {
					tidesMap.set(x, y, waterLevel);
				}
			}
		}, 1);
	}

	/**
	 * Draws the Elbe, the boat spawn area and the Hamburg dockyard onto the simulation map
	 */
	private void drawObjects() {
		// Draw Elbe area
		int tempLengthHelper = 0;
		int tempWidthHelper = 0;
		for (int elbeSection = 0; elbeSection < FAIRWAY_LENGTH.length; elbeSection++) {
			try {
				tempWidthHelper = (FAIRWAY_WIDTH[elbeSection] - FAIRWAY_WIDTH[elbeSection + 1]) / 2;
			} catch (ArrayIndexOutOfBoundsException ignored) {}

			// Draw blocks for elbe sections
			for (int i = MARGIN + tempLengthHelper; i < (MARGIN + tempLengthHelper + FAIRWAY_LENGTH[elbeSection]) - tempWidthHelper; i++) { // from left to right
				for (int j = ((fairwayWidthMax - FAIRWAY_WIDTH[elbeSection]) / 2) + MARGIN; j < (((fairwayWidthMax - FAIRWAY_WIDTH[elbeSection]) / 2) + MARGIN + FAIRWAY_WIDTH[elbeSection]); j++) { // from top to bottom
					elbeMap.field[i][j] = FAIRWAY_ID;
				}
			}

			// Draw transitions
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
				} else if (tempWidthHelper < 0){
					// Draw transitions if the following elbeSection is wider than the previous one
					System.out.println("Das hier wird nicht ausgeführt, da MASON mit veränderten Variablen nicht klarkommt"); // TODO
				}
			}
				tempLengthHelper += FAIRWAY_LENGTH[elbeSection];
		}

		// Draw spawn area
		for (int i = ((fairwayWidthMax - FAIRWAY_WIDTH[0]) / 2) + MARGIN; i < (((fairwayWidthMax - FAIRWAY_WIDTH[0]) / 2 ) + MARGIN + FAIRWAY_WIDTH[0]); i++) {
			for (int j = 0; j < MARGIN; j++) {
				elbeMap.field[spawnPositionX - (j + 1)][i] = SPAWN_POINT_ID;
			}
		}

		// Draw dockyard
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

	public int getDepthOfWaterBelowCD() {
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
		} else {
			FAIRWAY_WIDTH = FAIRWAY_WIDTH_NOT_EXTENDED;
		}
	}
	
	public Continuous2D getVesselGrid(){
		return vesselGrid;
	}

}