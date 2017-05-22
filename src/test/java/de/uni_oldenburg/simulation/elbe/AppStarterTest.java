package de.uni_oldenburg.simulation.elbe;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests {@link AppStarter}
 */
class AppStarterTest {

	private AppStarter appStarter;

	@Before
	public void setup() {
		appStarter = new AppStarter();
	}

	@Test
	void main() {
		AppStarter.main(new String[0]);
	}
}