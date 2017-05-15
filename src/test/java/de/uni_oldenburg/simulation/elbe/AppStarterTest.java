package de.uni_oldenburg.simulation.elbe;

import org.junit.Before;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by adrian-jagusch on 16.05.17.
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