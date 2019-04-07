package fr.elyssif.client.security;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;

import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.ECPointUtil;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jce.spec.ECPrivateKeySpec;
import org.bouncycastle.util.Arrays;

import fr.elyssif.client.callback.ErrorCallback;

/**
 * Handles asynchronous encryption and decryption using elliptic curves (ECIES).
 * @author Jérémy LAMBERT
 * @author Micka�l PROUST
 *
 */
public class Crypter {

	private static final String CURVE = "secp256k1";
	
	private File source;
	private static final int BUFFER_LENGTH = 2048;

	/**
	 * Create a new instance of Crypter.
	 * @param source the file to encrypt or to decrypt
	 */
	public Crypter (File source) {
		this.source = source;
		Security.addProvider(new BouncyCastleProvider());
	}

	private void process(String keyHex, int cipherMode, File destination, Runnable callback, ErrorCallback failCallback) {

		new Thread(() -> {
			try {
				Key key = cipherMode == Cipher.ENCRYPT_MODE ? getPublicKey(keyHex) : getPrivateKey(keyHex);

				int read;
				FileInputStream isr = null;
				CipherOutputStream output = null;
				var buffer = new byte[BUFFER_LENGTH];
				try {
					var cipher = Cipher.getInstance("ECIES");
					isr = new FileInputStream(source);

					cipher.init(cipherMode, key);
					output = new CipherOutputStream(new FileOutputStream(destination), cipher);

					while((read = isr.read(buffer)) != -1) {
						output.write(Arrays.copyOf(buffer, read));
					}

					closeStream(output, failCallback);
					output = null;
					callback.run();

				} catch (IOException | NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException e) {
					Logger.getGlobal().log(Level.SEVERE, "Error while encrypting or decrypting file.", e);
					failCallback.setException(e);
					failCallback.run();
				} finally {
					closeStream(isr, failCallback);
					closeStream(output, failCallback);
				}
			} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
				Logger.getGlobal().log(Level.SEVERE, "Couldn't retrieve key from hex string.", e);
				failCallback.setException(e);
				failCallback.run();
			}
		}).start();
	}

	private void closeStream(Closeable stream, ErrorCallback failCallback) {
		if(stream != null) {
			try {
				stream.close();
			} catch (IOException e) {
				Logger.getGlobal().log(Level.SEVERE, "Couldn't close stream.", e);
				failCallback.setException(e);
				failCallback.run();
			}
		}
	}

	private Key getPrivateKey(String key) throws NoSuchAlgorithmException, InvalidKeySpecException {
		KeyFactory keyFactory = KeyFactory.getInstance("EC");
		ECParameterSpec ecParameterSpec = ECNamedCurveTable.getParameterSpec(CURVE);
		ECPrivateKeySpec privateKeySpec = new ECPrivateKeySpec(new BigInteger(key, 16), ecParameterSpec);

		return keyFactory.generatePrivate(privateKeySpec);
	}

	private Key getPublicKey(String key) throws NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] bytes = new BigInteger(key, 16).toByteArray();
		KeyFactory keyFactory = KeyFactory.getInstance("EC");

		ECNamedCurveParameterSpec spec = ECNamedCurveTable.getParameterSpec(CURVE);
		ECNamedCurveSpec params = new ECNamedCurveSpec(CURVE, spec.getCurve(), spec.getG(), spec.getN());
		ECPoint point =  ECPointUtil.decodePoint(params.getCurve(), bytes);
		ECPublicKeySpec pubKeySpec = new ECPublicKeySpec(point, params);
		return keyFactory.generatePublic(pubKeySpec);
	}

	/**
	 * Encrypt the source file using the given public key and save
	 * the result to the given destination file.
	 * @param publicKey a hex representation of the public key
	 * @param destination the output file
	 * @param callback the callback executed on success
	 * @param failCallback the callback executed on error
	 */
	public void encrypt(String publicKey, File destination, Runnable callback, ErrorCallback failCallback) {
		process(publicKey, Cipher.ENCRYPT_MODE, destination, callback, failCallback);
	}

	/**
	 * Decrypt the source file using the given private key and save
	 * the result to the given destination file.
	 * @param privateKey a hex representation of the private key
	 * @param destination the output file
	 * @param callback the callback executed on success
	 * @param failCallback the callback executed on error
	 */
	public void decrypt(String privateKey, File destination, Runnable callback, ErrorCallback failCallback) {
		process(privateKey, Cipher.DECRYPT_MODE, destination, callback, failCallback);
	}

}