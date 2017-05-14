package de.uni_oldenburg.simulation.elbe;

import sim.engine.Schedule;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.grid.IntGrid2D;
import sim.field.grid.SparseGrid2D;

public class Elbe extends SimState {

	public IntGrid2D elbeMap;
	public SparseGrid2D vesselGrid;

	private final int FAIRWAY_LENGTH_TOTAL = 1000;
	private final int FAIRWAY_WIDTH_TOTAL = 600;
	private final int FAIRWAY_LENGTH_1 = 400;
	private final int FAIRWAY_LENGTH_2 = 350;
	private final int FAIRWAY_LENGTH_3 = 250;

	private int fairwayWidth1 = 400;
	private int fairwayWidth2 = 320;
	private int fairwayWidth3 = 250;

	private final int MARGIN = 25;
	private final int WIDTH_TRANSITION = 25;

	private final int GRID_HEIGHT = FAIRWAY_WIDTH_TOTAL + (MARGIN * 2);
	private final int GRID_WIDTH = FAIRWAY_LENGTH_TOTAL + (MARGIN * 2);

	private final int SPAWN_POSITION_X = MARGIN;
	private final int DOCKYARD_POSITION_X = FAIRWAY_LENGTH_TOTAL + MARGIN;

	private final int FAIRWAY_ID = 1;
	private final int SPAWN_POINT_ID = 2;
	private final int DOCKYARD_POINT_ID = 3;

	public Elbe(long seed) {
		super(seed);
	}

	/**
	 * Start the simulation
	 */
	public void start() {
		super.start();  // clear out the schedule

		// Initialize grids
		vesselGrid = new SparseGrid2D(GRID_WIDTH, GRID_HEIGHT);
		elbeMap = new IntGrid2D(GRID_WIDTH, GRID_HEIGHT, 0);

		// Draw Elbe section 1
		for (int i = MARGIN; i < (MARGIN + FAIRWAY_LENGTH_1); i++) { // from left to right
		    for (int j = ((FAIRWAY_WIDTH_TOTAL - fairwayWidth1) / 2) + MARGIN; j < (((FAIRWAY_WIDTH_TOTAL - fairwayWidth1) / 2) + MARGIN + fairwayWidth1); j++) { // from top to bottom
				elbeMap.field[i][j] = FAIRWAY_ID;
			}
		}

		// Draw Elbe section 2
		for (int i = (MARGIN + FAIRWAY_LENGTH_1); i < (MARGIN + FAIRWAY_LENGTH_1 + FAIRWAY_LENGTH_2); i++) { // from left to right
			for (int j = ((FAIRWAY_WIDTH_TOTAL - fairwayWidth2) / 2) + MARGIN; j < (((FAIRWAY_WIDTH_TOTAL - fairwayWidth2) / 2) + MARGIN + fairwayWidth2); j++) { // from top to bottom
				elbeMap.field[i][j] = FAIRWAY_ID;
			}
		}

		// Draw Elbe section 3
		for (int i = (MARGIN + FAIRWAY_LENGTH_1 + FAIRWAY_LENGTH_2); i < (MARGIN + FAIRWAY_LENGTH_1 + FAIRWAY_LENGTH_2 + FAIRWAY_LENGTH_3); i++) { // from left to right
			for (int j = ((FAIRWAY_WIDTH_TOTAL - fairwayWidth3) / 2) + MARGIN; j < (((FAIRWAY_WIDTH_TOTAL - fairwayWidth3) / 2) + MARGIN + fairwayWidth3); j++) { // from top to bottom
				elbeMap.field[i][j] = FAIRWAY_ID;
			}
		}

		// Draw spawn
		for (int i = ((FAIRWAY_WIDTH_TOTAL - fairwayWidth1) / 2) + MARGIN; i < (((FAIRWAY_WIDTH_TOTAL - fairwayWidth1) / 2 ) + MARGIN + fairwayWidth1); i++) {
			for (int j = 0; j < MARGIN; j++) {
				elbeMap.field[SPAWN_POSITION_X - (j + 1)][i] = SPAWN_POINT_ID;
			}
		}

		// Draw dockyard
		for (int i = ((FAIRWAY_WIDTH_TOTAL - fairwayWidth3) / 2) + MARGIN; i < (((FAIRWAY_WIDTH_TOTAL - fairwayWidth3) / 2) + MARGIN + fairwayWidth3); i++) {
			for (int j = 0; j < MARGIN; j++) {
				elbeMap.field[DOCKYARD_POSITION_X + j][i] = DOCKYARD_POINT_ID;
			}
		}



		// TODO Spawn Vessels

		schedule.scheduleRepeating(Schedule.EPOCH, 1, (Steppable) (SimState state) -> {

		}, 1);
	}

	public int getFairwayWidth1() {
		return fairwayWidth1;
	}

	public void setFairwayWidth1(int newWidth) {
		fairwayWidth1 = newWidth;
	}

	public int getFairwayWidth2() {
		return fairwayWidth2;
	}

	public void setFairwayWidth2(int newWidth) {
		fairwayWidth2 = newWidth;
	}

	public int getFairwayWidth3() {
		return fairwayWidth3;
	}

	public void setFairwayWidth3(int newWidth) {
		fairwayWidth3 = newWidth;
	}

}