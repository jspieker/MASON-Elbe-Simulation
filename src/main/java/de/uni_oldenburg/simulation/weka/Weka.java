package de.uni_oldenburg.simulation.weka;

import de.uni_oldenburg.simulation.weka.plot.Plot;
import weka.core.Instances;
import weka.core.converters.ArffSaver;

import java.io.File;
import java.io.IOException;

/**
 * {@link weka.Run} wrapper.
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

	public Weka(String path, String WEKAname) {
		arffSaver = new ArffSaver();
		prepareWEKAEntry();
		arffSaver.setInstances(instances);
		this.path = path;
		this.wekaName = WEKAname;
		fileNumberCounter = 0;
	}

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

	public abstract boolean addWEKAEntry(Object[] wekaEntry);

	abstract void prepareWEKAEntry();

	public abstract void plotWEKAEntries();

	public void resetWEKA() {
		prepareWEKAEntry();
		arffSaver = new ArffSaver();
		arffSaver.setInstances(instances);
	}

}
