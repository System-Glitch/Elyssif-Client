package fr.elyssif.client.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bouncycastle.util.Arrays;

import fr.elyssif.client.callback.ErrorCallback;
import fr.elyssif.client.callback.HashCallback;

/**
 * Utility class able to hash large files.
 * @author Jérémy LAMBERT
 *
 */
public abstract class Hash {

	private static final int BUFFER_LENGTH = 2048;

	/**
	 * Asynchronous SHA-256 hash of the given source file.
	 * @param source the source file to hash
	 * @param callback the callback executed on success
	 * @param failCallback the callback executed on error
	 */
	public static final void sha256(File source, HashCallback callback, ErrorCallback failCallback) {
		hash("SHA-256", source, callback, failCallback);
	}

	private static final void hash(String method, File source, HashCallback callback, ErrorCallback failCallback) {
		new Thread(() -> { 

			FileInputStream isr = null;
			try {
				int read;
				var md = MessageDigest.getInstance(method);
				var buffer = new byte[BUFFER_LENGTH];
				isr = new FileInputStream(source);

				while((read = isr.read(buffer)) != -1) {
					md.update(Arrays.copyOf(buffer, read));
				}

				var digest = md.digest();
				callback.setDigest(digest);
				callback.run();

			} catch (IOException | NoSuchAlgorithmException e) {
				Logger.getGlobal().log(Level.SEVERE, "Error while hashing file.", e);
				failCallback.setException(e);
				failCallback.run();
			} finally {
				if(isr != null) {
					try {
						isr.close();
					} catch (IOException e) {
						Logger.getGlobal().log(Level.SEVERE, "Couldn't close file input stream.", e);
					}
				}
			}

		}).start();
	}

}
