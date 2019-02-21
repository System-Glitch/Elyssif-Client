package fr.elyssif.client;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;

class ConfigTest {
	
	@Test
	void testLoad() {
		// Load
		Config config = Config.getInstance();
		config.setExport(false);
		assertFalse(config.isExport());
		assertTrue(config.load());
		
		// Check
		assertEquals("testing", config.get("Environment"));
		assertEquals("http://localhost:4567", config.get("Host"));
		assertTrue(config.isVerbose());
		assertNull(config.get("Verbose"));
	}
	
	@Test
	void testSetValue() {
		Config config = Config.getInstance();
		
		config.set("TestKey", "TestValue");
		assertEquals("TestValue", config.get("TestKey"));
		
		config.set("TestKey", null);
		assertNull(config.get("TestKey"));
	}
	
	@Test
	void testSave() {
		Config config = Config.getInstance();
		
		// Temporarily change export path
		changeConstant("CONFIG_FILE_PATH", "./test_config/test_config.xml");
		changeConstant("PROGRAM_DIRECTORY_PATH", "./test_config");
		
		config.setExport(true);
		assertTrue(config.save());
		
		File configDir = new File("./test_config");
		assertTrue(configDir.exists());
		File configFile = new File("./test_config/test_config.xml");
		assertTrue(configFile.exists());
		
		// Delete exported file
		configFile.delete();
		configDir.delete();
		config.setExport(false);
	}
	
	private void changeConstant(String fieldName, String value) {
		try {
			Field field = Config.class.getDeclaredField(fieldName);
			field.setAccessible(true);
			Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt( field, field.getModifiers() & ~Modifier.FINAL);
			field.set(null, value);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			return;
		}
	}


}