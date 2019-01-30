package fr.elyssif.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import fr.elyssif.client.model.TestModel;

class ModelTest {

	private static final int STRING_LIST_LENGTH = 5;
	private static final int INT_LIST_LENGTH = 10;

	private static JsonObject json;

	@BeforeAll
	static void setUp() {
		json = new JsonObject();
		json.addProperty("string", "lorem ipsum");
		json.addProperty("bool", true);
		json.addProperty("integer", 42);
		json.addProperty("lng", 9999L);
		json.addProperty("flt", 2.4f);
		json.addProperty("dbl", 4.2);
		json.addProperty("date_at", "2019-01-05 14:15:16");

		JsonArray stringList = new JsonArray();
		for(int i = 0 ; i < STRING_LIST_LENGTH ; i++) 
			stringList.add("string list item " + i);

		json.add("string_list", stringList);

		JsonArray intList = new JsonArray();
		for(int i = 0 ; i < INT_LIST_LENGTH ; i++) 
			intList.add(i);

		json.add("int_list", intList);
	}
	
	@Test
	void testLoadFromJson() {
		TestModel model = new TestModel(json);
		assertModel(model);
	}

	@Test
	void testUpdate() {
		TestModel model = new TestModel(json);
		JsonObject updateJson = new JsonObject();

		updateJson.addProperty("string", "updated string");
		updateJson.add("flt", JsonNull.INSTANCE);
		updateJson.add("dbl", JsonNull.INSTANCE);

		model.loadFromJsonObject(updateJson);

		// Check the rest have not changed
		assertTrue(model.getBool().get());
		assertEquals(42, model.getInteger().get());
		assertEquals(9999L, model.getLng().get());
		assertEquals("Sat Jan 05 14:15:16 CET 2019", model.getDateAt().get().toString());

		assertEquals(STRING_LIST_LENGTH, model.getStringList().size());
		assertEquals(INT_LIST_LENGTH, model.getIntList().size());

		for(int i = 0 ; i < INT_LIST_LENGTH ; i++)
			assertEquals(i, model.getIntList().get(i).intValue());

		for(int i = 0 ; i < STRING_LIST_LENGTH ; i++)
			assertEquals("string list item " + i, model.getStringList().get(i));

		// Check the changed values have changed
		assertEquals("updated string", model.getString().get());
		assertEquals(Double.NaN, model.getDbl().get(), 0.001);
		assertEquals(Float.NaN, model.getFlt().get(), 0.001);
		
		updateJson = new JsonObject();
		updateJson.add("bool", JsonNull.INSTANCE);
		updateJson.add("integer", JsonNull.INSTANCE);
		updateJson.add("lng", JsonNull.INSTANCE);
		updateJson.add("string", JsonNull.INSTANCE);
		
		model.loadFromJsonObject(updateJson);
		
		assertEquals(0, model.getInteger().get());
		assertEquals(0, model.getLng().get());
		assertFalse(model.getBool().get());
		assertNull(model.getString().get());
	}
	
	@Test
	void testNaming() {
		JsonObject json = new JsonObject();
		json.addProperty("name_", "value");
		
		TestModel model = new TestModel(json);
		
		assertEquals("value", model.getName().get());
	}
	
	@Test
	void testResourceName() {
		assertEquals("testmodels", new TestModel().getResourceName());
	}
	
	@Test
	void testNotProperty() {
		JsonObject json = new JsonObject();
		TestModel model = new TestModel();
		
		json.addProperty("not_property", "test");
		
		assertThrows(RuntimeException.class, () -> model.loadFromJsonObject(json), "Field \"notProperty\" in model \"TestModel\" is not a property.");
		assertNull(model.getNotProperty());
	}
	
	@Test
	void testNoField() {
		JsonObject json = new JsonObject();
		
		json.addProperty("doesntexist", "test");
		new TestModel(json);
		// Exception should have been thrown
	}
	
	@Test
	void testWrongDate() {
		JsonObject json = new JsonObject();
		json.addProperty("date_at", "wrong date");
		
		TestModel model = new TestModel(json);
		
		assertNull(model.getDateAt().get());
	}
	
	@Test
	void testNestedObject() {
		JsonObject json = new JsonObject();
		json.add("nested", ModelTest.json);
		
		TestModel model = new TestModel(json);
		
		assertNotNull(model.getNested().get());
		assertModel(model.getNested().get());
	}

	private void assertModel(TestModel model) {
		assertEquals("lorem ipsum", model.getString().get());
		assertTrue(model.getBool().get());
		assertEquals(42, model.getInteger().get());
		assertEquals(9999L, model.getLng().get());
		assertEquals(2.4f, model.getFlt().get(), 0.001);
		assertEquals(4.2, model.getDbl().get(), 0.001);
		assertEquals("Sat Jan 05 14:15:16 CET 2019", model.getDateAt().get().toString());

		assertEquals(STRING_LIST_LENGTH, model.getStringList().size());
		assertEquals(INT_LIST_LENGTH, model.getIntList().size());

		for(int i = 0 ; i < INT_LIST_LENGTH ; i++)
			assertEquals(i, model.getIntList().get(i).intValue());

		for(int i = 0 ; i < STRING_LIST_LENGTH ; i++)
			assertEquals("string list item " + i, model.getStringList().get(i));
	}

}
