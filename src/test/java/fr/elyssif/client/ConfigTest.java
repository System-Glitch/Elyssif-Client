package fr.elyssif.client;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import fr.elyssif.client.Config;

class ConfigTest {
	
	@Test
	void test() {
		//Load
		assertTrue(Config.getInstance().load());
		
		//Check
		assertEquals(Config.getInstance().get("Environment"), "testing");
		assertEquals(Config.getInstance().get("Host"), "http://localhost:4567");
	}


}