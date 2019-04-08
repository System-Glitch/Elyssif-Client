package fr.elyssif.client;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.concurrent.atomic.AtomicReference;

abstract class TestUtils {
	
	static void asyncFail(String message, AtomicReference<AssertionError> failure) {
		try {
			fail(message);
		} catch (AssertionError e) {
			failure.set(e);
		}
	}

	static void asyncFail(Throwable throwable, AtomicReference<AssertionError> failure) {
		try {
			fail(throwable);
		} catch (AssertionError e) {
			failure.set(e);
		}
	}

}
