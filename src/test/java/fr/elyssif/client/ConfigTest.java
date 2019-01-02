package fr.elyssif.client;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import fr.elyssif.client.Config;

class ConfigTest {
	
	@Test
	void test() {
		//Load
		Config.getInstance().setExport(false);
		assertTrue(Config.getInstance().load());
		
		//Check
		assertEquals("testing", Config.getInstance().get("Environment"));
		assertEquals("http://localhost:4567", Config.getInstance().get("Host"));
	}


}