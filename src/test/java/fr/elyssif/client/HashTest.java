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

import static fr.elyssif.client.TestUtils.asyncFail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;

import fr.elyssif.client.security.Hash;

class HashTest {

	private File createInputFile() {
		final String inputContent = "Hello world!";
		var inputFile = new File("input.txt");

		try {
			var bw = new BufferedWriter(new FileWriter(inputFile));
			bw.write(inputContent);
			bw.close();
		} catch (IOException e) {
			fail(e);
			return null;
		}

		return inputFile;
	}

	@Test
	void testSha256() {

		File inputFile = createInputFile();

		CountDownLatch latch = new CountDownLatch(1);
		AtomicReference<AssertionError> failure = new AtomicReference<>();

		Hash.sha256(inputFile, digest -> {
			try {
				assertEquals("c0535e4be2b79ffd93291305436bf889314e4a3faec05ecffcbb7df31ad9e51a", Hash.toHex(digest));
			} catch (AssertionError e) {
				failure.set(e);
			}
			latch.countDown();
		}, exception -> {
			asyncFail("SHA-256 failure.", failure);
			latch.countDown();
		});

		try {
			latch.await();

			inputFile.delete();

			if (failure.get() != null)
				throw failure.get();
		} catch (InterruptedException e) {
			fail(e);
		}
	}

	@Test
	void testFileError() {
		File inputFile = new File("doesntexist.bin");

		CountDownLatch latch = new CountDownLatch(1);
		AtomicReference<AssertionError> failure = new AtomicReference<>();

		Hash.sha256(inputFile, digest -> {
			try {
				fail("Success callback on non-existing file");
			} catch (AssertionError e) {
				failure.set(e);
			}
			latch.countDown();
		}, exception -> {
			assertTrue(exception instanceof IOException);
			latch.countDown();
		});

		try {
			latch.await();

			inputFile.setReadable(true, true);
			inputFile.delete();

			if (failure.get() != null)
				throw failure.get();
		} catch (InterruptedException e) {
			fail(e);
		}
	}
}
