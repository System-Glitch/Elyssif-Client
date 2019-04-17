package fr.elyssif.client;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class StringUtilsTest {

	@Test
	void testToSnakeCase() {
		String camel = "camelCase";
		String snake = StringUtils.toSnakeCase(camel);
		assertEquals("camel_case", snake);
	}

	@Test
	void testToCamelCase() {
		String snake = "snake_case";
		String camel = StringUtils.toCamelCase(snake);
		assertEquals("snakeCase", camel);
	}

}
