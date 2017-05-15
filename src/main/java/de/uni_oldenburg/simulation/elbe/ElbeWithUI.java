package de.uni_oldenburg.simulation.elbe;

import sim.display.Controller;
import sim.display.Display2D;
import sim.display.GUIState;
import sim.engine.SimState;

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

		// reschedule the displayer
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
		display = new Display2D(500, 500, this); // At 10x510, we've got 10x10 per array position
		displayFrame = display.createFrame();
		controller.registerFrame(displayFrame);   // Register the frame so it appears in the "Display" list
		displayFrame.setVisible(true);

		// specify the backdrop color  -- what gets painted behind the displays
		display.setBackdrop(Color.gray);
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