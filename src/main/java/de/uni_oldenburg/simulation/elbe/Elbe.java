package de.uni_oldenburg.simulation.elbe;

import sim.engine.Schedule;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.grid.IntGrid2D;
import sim.field.grid.SparseGrid2D;

import java.lang.reflect.Array;

public class Elbe extends SimState {

	IntGrid2D elbeMap;
	SparseGrid2D vesselGrid;

	private final int[] FAIRWAY_LENGTH = 				{507, 230, 230, 200, 48}; // TODO find exact values in relation
	private final int[] FAIRWAY_WIDTH_NOT_EXTENDED =	{400, 300, 250, 250, 230}; // TODO find exact values for #2 #3 #4 (#0 and #1 are correct, others mostly)
	private final int[] FAIRWAY_WIDTH_EXTENDED = 		{400, 320, 380, 270, 250};
	private int[] FAIRWAY_WIDTH = FAIRWAY_WIDTH_NOT_EXTENDED;
	private final int MARGIN = 25;

	private int depthOfWaterBelowCD = 15; // sample value
	private int depthOfWaterAboveCD = 2; // TODO use this for tide function
	private int depthOfWaterTotal = depthOfWaterBelowCD + depthOfWaterAboveCD;

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

	public Elbe(long seed) {
		super(seed);

		calculateInitialValues();
	}

	/**
	 * Start the simulation
	 */
	public void start() {
		super.start(); // clear out the schedule

		// Initialize grids
		vesselGrid = new SparseGrid2D(gridWidth, gridHeight);
		elbeMap = new IntGrid2D(gridWidth, gridHeight, 0);

		// Draw Elbe, spawn, dockyard
		drawObjects();

		// TODO Create Vessels

		schedule.scheduleRepeating(Schedule.EPOCH, 1, (Steppable) (SimState state) -> {
			depthOfWaterTotal = depthOfWaterBelowCD + depthOfWaterAboveCD;


		}, 1);
	}

	/**
	 * Draws the Elbe, the boat spawn area and the Hamburg dockyard onto the simulation map
	 */
	private void drawObjects() {
		// Draw Elbe
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

		// Draw spawn
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

	public int getDepthOfWaterAboveCD() {
		return depthOfWaterAboveCD;
	}

	public void setDepthOfWaterAboveCD(int newDepth) {
		depthOfWaterAboveCD = newDepth;
	}

	public int getDepthOfWaterTotal() {
		return depthOfWaterTotal;
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

}