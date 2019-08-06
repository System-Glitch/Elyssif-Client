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

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map.Entry;

import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ReflectionUtils;

import fr.elyssif.client.gui.model.User;
import fr.elyssif.client.gui.repository.UserRepository;
import fr.elyssif.client.model.TestModel;
import fr.elyssif.client.repository.TestRepository;

class RepositoryTest {

	@SuppressWarnings("unchecked")
	@Test
	void testAttributes() {
		TestRepository repo = new TestRepository();
		TestModel model = new TestModel();
		model.setId(4);
		model.setName("John Doe");
		model.setInteger(666);

		Method method = ReflectionUtils.findMethod(UserRepository.class, "getAttributes", User.class).get();
		HashMap<String, Object> attributes = (HashMap<String, Object>) ReflectionUtils.invokeMethod(method, repo, model);
		
		for(Entry<String, Object> entry : attributes.entrySet())
			System.out.println(entry.getKey() + " " + entry.getValue());

		assertEquals(4, attributes.get("id"));
		assertEquals("John Doe", attributes.get("name"));
		assertEquals(666, attributes.get("integer"));
	}

}
