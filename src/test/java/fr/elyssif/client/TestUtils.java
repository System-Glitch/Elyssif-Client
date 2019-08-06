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
