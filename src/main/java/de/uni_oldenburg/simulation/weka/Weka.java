package de.uni_oldenburg.simulation.weka;

import de.uni_oldenburg.simulation.weka.plot.Plot;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.converters.ArffSaver;

import java.io.File;
import java.io.IOException;

/**
 * Wrapper to encapsulate the Weka API.
 *
 * @see CollisionWeka
 * @see WaterLevelWeka
 */
public abstract class Weka {

	Instances instances;

	String path;
	String wekaName;

	ArffSaver arffSaver;
	Plot plot;

	int fileNumberCounter;

	/**
	 * Initializes the this class by setting the *.arff writer, the path, the files name and counter. Calls {@link #prepareWEKAEntry()} to set the attributes and others.
	 *
	 * @param path     The path to store the generated *.arff files at.
	 * @param WEKAname The name of the file.
	 */
	public Weka(String path, String WEKAname) {
		arffSaver = new ArffSaver();
		prepareWEKAEntry();
		arffSaver.setInstances(instances);
		this.path = path;
		this.wekaName = WEKAname;
		fileNumberCounter = 0;
	}

	/**
	 * Writes the written {@link Instances} values to a *.arff file using the path, the name and the counter. The method checks for already existing files to prevent data losses.
	 */
	public void writeWEKAEntries() {
		File file;
		String pathCopy = path + wekaName + String.valueOf(fileNumberCounter) + ".arff";
		while ((file = new File(pathCopy)).isFile()) { // prevent overriding
			pathCopy = path + wekaName + String.valueOf(++fileNumberCounter) + ".arff";
		}

		try {
			arffSaver.setFile(file);
			arffSaver.writeBatch();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adds a Weka entry to the instances. As every entry can have a different type the passes argument is an Object array.
	 *
	 * @param wekaEntry An Object array which contains the new entries. The Array size must meet the set size of attributes at {@link #prepareWEKAEntry()}.
	 * @return A boolean value whether the adding succeeded (true) or not.
	 */
	public abstract boolean addWEKAEntry(Object[] wekaEntry);

	/**
	 * Prepares the {@link Instances} by setting its {@link Attribute}s the name and the type of the attributes as well as its their order.
	 */
	abstract void prepareWEKAEntry();

	/**
	 * Polots the Weka entries using a {@link Plot} implementation.
	 */
	public abstract void plotWEKAEntries();

	/**
	 * Resets the Weka *.arff saver and instances to start a new measurement.
	 */
	public void resetWEKA() {
		prepareWEKAEntry();
		arffSaver = new ArffSaver();
		arffSaver.setInstances(instances);
	}

}
