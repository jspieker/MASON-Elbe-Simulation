package de.uni_oldenburg.simulation.elbe;

import sim.display.Controller;
import sim.display.Display2D;
import sim.display.GUIState;
import sim.engine.SimState;
import sim.portrayal.continuous.ContinuousPortrayal2D;
import sim.portrayal.grid.FastValueGridPortrayal2D;
import sim.portrayal.grid.SparseGridPortrayal2D;
import sim.util.gui.SimpleColorMap;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.OptionalDataException;

/**
 * Starts the graphical interface (GUI) of MASON by extending the {@link GUIState} and others.
 */
public class ElbeWithUI extends GUIState {

	private Display2D display;
	private JFrame displayFrame;
	private FastValueGridPortrayal2D elbePortrayal = new FastValueGridPortrayal2D("Elbe Map", true);
	private FastValueGridPortrayal2D tidesPortrayal = new FastValueGridPortrayal2D("Tides", false);
	private ContinuousPortrayal2D vesselPortrayal = new ContinuousPortrayal2D();

	private final double scale = 1; // scale of the simulation // TODO set to 0.5

	/**
	 * Start the Simulation at time 0
	 */
	public ElbeWithUI() {
		super(new Elbe(System.currentTimeMillis()));
	}

	public ElbeWithUI(Display2D display, JFrame jFrame) {
		super(new Elbe(System.currentTimeMillis()));
		this.display = display;
		this.displayFrame = jFrame;
	}

	/**
	 * Start the Simulation with a given state
	 *
	 * @param state The state to start at
	 */
	public ElbeWithUI(SimState state) {
		super(state);
	}

	/**
	 * Allow the user to inspect the model
	 */
	public Object getSimulationInspectedObject() {
		return state;
	}

	/**
	 * @return The name of the simulation
	 */
	public static String getName() {
		return "Elbe Simulation";
	}

	/**
	 * Set up our views
	 */
	private void setupPortrayals() {

		Elbe elbe = (Elbe) state;

		// Draw the tide values
		tidesPortrayal.setField(elbe.tidesMap);
		tidesPortrayal.setMap(new SimpleColorMap(10, 20, Color.WHITE, Color.BLUE));

		// Show the Elbe map
		elbePortrayal.setField(elbe.elbeMap);

		// Set the colors for the Elbe map
		Color[] colorMap = new Color[4];
		colorMap[0] = new Color(203,230,163, 255); 		// landmass
		colorMap[1] = new Color(0, 0, 0, 0);            	// transparent water (values given by tides)
		colorMap[2] = new Color(90, 164, 255, 255); 		// water (open sea spawn point)
		colorMap[3] = new Color(237, 65, 62, 255); 		// hamburg dockyard
		elbePortrayal.setMap(new SimpleColorMap(colorMap));

		// Map the vessels
		vesselPortrayal.setField(elbe.vesselGrid);

		// reschedule the display
		display.reset();

		// redraw the display
		display.repaint();
	}

	/**
	 * Start the simulation
	 */
	public void start() {
		super.start();  // set up everything but replacing the display
		// set up our portrayals
		setupPortrayals();
	}

	/**
	 * Load a specific state
	 * @param state The state to be simulated
	 */
	public void load(SimState state) {
		super.load(state);
		// we now have new grids. Set up the portrayals to reflect that
		setupPortrayals();
	}

	/**
	 * Initialize the view
	 * @param controller Initial controller
	 */
	public void init(Controller controller) {
		super.init(controller);

		// Make the Display2D. We'll have it display stuff later.
		display = new Display2D(1200, 350, this); // At 10x510, we've got 10x10 per array position
        display.setScale(scale);
		displayFrame = display.createFrame();
		controller.registerFrame(displayFrame);   // Register the frame so it appears in the "Display" list
		displayFrame.setVisible(true);

		// Attach the portrayals from bottom to top
		display.attach(tidesPortrayal, "Tides");
		display.attach(elbePortrayal, "Elbe Map");
		display.attach(vesselPortrayal, "Vessels");

		// specify the backdrop color  -- what gets painted behind the displays
		display.setBackdrop(new Color(80, 80, 80, 255)); // landmass
	}

	/**
	 * Quit the simulation
	 */
	public void quit() {
		super.quit();

		// disposing the displayFrame automatically calls quit() on the display,
		// so we don't need to do so ourselves here.
		if (displayFrame != null) displayFrame.dispose();
		displayFrame = null;  // let gc
		display = null;       // let gc
	}
}