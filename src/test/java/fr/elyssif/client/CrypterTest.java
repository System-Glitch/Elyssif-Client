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
import static org.junit.jupiter.api.Assertions.fail;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;

import fr.elyssif.client.security.Crypter;

class CrypterTest {

	@Test
	public void testEncrypt() {

		final String inputContent = "Hello world!";

		var inputFile = new File("input.txt");
		var destination = new File("encrypted.txt");
		var decryptDestination = new File("decrypted.txt");


		try {
			var bw = new BufferedWriter(new FileWriter(inputFile));
			bw.write(inputContent);
			bw.close();
		} catch (IOException e) {
			fail(e);
		}

		Crypter encrypter = new Crypter(inputFile);

		String pubKey = "0484f064445a8161501803290970d5c8b65f639d606fe603ef1d67ba290606a13c0057be062ca25dfd7d919f57ae1e95848fa228e6976b6cb8daab79eba25751af";
		String privKey = "61a224feb41188062da2e205498f4a97a3ddd71f591956518e808fe3657fb0ce";

		CountDownLatch latch = new CountDownLatch(1);
		AtomicReference<AssertionError> failure = new AtomicReference<>();
		encrypter.encrypt(pubKey, destination, progress -> {}, () -> {

			var decrypter = new Crypter(destination);
			decrypter.decrypt(privKey, decryptDestination, progress -> {}, () -> {

				try {
					var br = new BufferedReader(new FileReader(decryptDestination));
					String content = br.readLine();
					br.close();
					try {
						assertEquals(inputContent, content);
					} catch (AssertionError e) {
						failure.set(e);
					}
				} catch (IOException e) {
					asyncFail(e, failure);
				}
				latch.countDown();
			}, exception -> {
				asyncFail("Decryption has failed!", failure);
				latch.countDown();
			});
		}, exception -> {
			asyncFail("Encryption has failed!", failure);
			latch.countDown();
		});

		try {
			latch.await();

			inputFile.delete();
			destination.delete();
			decryptDestination.delete();

			if (failure.get() != null)
				throw failure.get();
		} catch (InterruptedException e) {
			fail(e);
		}
	}

}
