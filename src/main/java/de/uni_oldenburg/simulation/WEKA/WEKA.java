package de.uni_oldenburg.simulation.WEKA;

import weka.core.Instances;
import weka.core.converters.ArffSaver;

import java.io.File;
import java.io.IOException;

/**
 * {@link weka.Run} wrapper.
 */
public abstract class WEKA {

	Instances instances;

	String path;

	ArffSaver arffSaver;

	public WEKA(String path) {
		arffSaver = new ArffSaver();
		prepareWEKAEntry();
		arffSaver.setInstances(instances);
		this.path = path;
	}

	public void writeWEKAEntries() {
		File file = new File(path);
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

}
