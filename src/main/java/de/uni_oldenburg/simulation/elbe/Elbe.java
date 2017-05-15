package de.uni_oldenburg.simulation.elbe;

import sim.engine.Schedule;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.grid.IntGrid2D;
import sim.field.grid.SparseGrid2D;

public class Elbe extends SimState {

	public IntGrid2D elbeMap;
	public SparseGrid2D vesselGrid;

	private final int[] FAIRWAY_LENGTH = {283, 283, 283, 283, 283};
	private final int[] FAIRWAY_WIDTH =  {40, 32, 38, 27, 25};
	private final int MARGIN = 25;

	private int depthOfWaterBelowCD = 15; // sample value
	private int depthOfWaterAboveCD = 2; // TODO use this for tide function
	private int depthOfWaterTotal = depthOfWaterBelowCD + depthOfWaterAboveCD;

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
		for (int elbeSection = 0; elbeSection < FAIRWAY_LENGTH.length; elbeSection++) {
			for (int i = MARGIN + tempLengthHelper; i < (MARGIN + tempLengthHelper + FAIRWAY_LENGTH[elbeSection]); i++) { // from left to right
				for (int j = ((fairwayWidthMax - FAIRWAY_WIDTH[elbeSection]) / 2) + MARGIN; j < (((fairwayWidthMax - FAIRWAY_WIDTH[elbeSection]) / 2) + MARGIN + FAIRWAY_WIDTH[elbeSection]); j++) { // from top to bottom
					elbeMap.field[i][j] = FAIRWAY_ID;
				}
			}
			tempLengthHelper += FAIRWAY_LENGTH[elbeSection];
		}

		// Draw transitions between fairway length areas
		/*tempLengthHelper = 0;
        for (int elbeSection = 1; elbeSection < FAIRWAY_LENGTH.length; elbeSection++) {
		   	int diff = (FAIRWAY_WIDTH[elbeSection] - FAIRWAY_WIDTH[elbeSection - 1]) / 2;
			System.out.println(diff);
			if (diff >= 0) {
				for (int i = FAIRWAY_LENGTH[elbeSection - 1] - diff; i < FAIRWAY_LENGTH[elbeSection - 1]; i++) {
					for (int j = MARGIN; j < MARGIN + diff; j++) {
						elbeMap.field[i][j] = FAIRWAY_ID;
						System.out.println("works");
					}
				}
			}
			tempLengthHelper += FAIRWAY_LENGTH[elbeSection-1];
		}*/


		// Draw spawn
		for (int i = ((fairwayWidthMax - FAIRWAY_WIDTH[0]) / 2) + MARGIN; i < (((fairwayWidthMax - FAIRWAY_WIDTH[0]) / 2 ) + MARGIN + FAIRWAY_WIDTH[0]); i++) {
			for (int j = 0; j < MARGIN; j++) {
				elbeMap.field[spawnPositionX - (j + 1)][i] = SPAWN_POINT_ID;
			}
		}

		// Draw dockyard
		for (int i = ((fairwayWidthMax - FAIRWAY_WIDTH[(FAIRWAY_WIDTH.length - 1)]) / 2) + MARGIN; i < (((fairwayWidthMax - FAIRWAY_WIDTH[(FAIRWAY_WIDTH.length - 1)]) / 2) + MARGIN + FAIRWAY_WIDTH[(FAIRWAY_WIDTH.length - 1)]); i++) {
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

	public int getFAIRWAY_WIDTH_0() {
		return FAIRWAY_WIDTH[0];
	}

	public int getFAIRWAY_WIDTH_1() {
		return FAIRWAY_WIDTH[1];
	}

	public int getFAIRWAY_WIDTH_2() {
		return FAIRWAY_WIDTH[2];
	}

	public int getFAIRWAY_WIDTH_3() {
		return FAIRWAY_WIDTH[3];
	}

	public int getFAIRWAY_WIDTH_4() {
		return FAIRWAY_WIDTH[4];
	}

}