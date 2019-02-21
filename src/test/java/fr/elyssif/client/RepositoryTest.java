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
