/*
 * Elyssif-Client
 * Copyright (C) 2019 Jérémy LAMBERT (System-Glitch)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
 package fr.elyssif.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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