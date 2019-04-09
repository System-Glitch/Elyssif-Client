package fr.elyssif.client.callback;

/**
 * Callback for the asynchronous hash calculation.
 * @author Jérémy LAMBERT
 *
 */
public abstract class HashCallback implements Runnable { // TODO change to functionnal interface

	private byte[] digest;

	public final byte[] getDigest() {
		return digest;
	}

	public final void setDigest(byte[] digest) {
		this.digest = digest;
	}

	/**
	 * Convert the digest bytes to a hex string.
	 * @return a hex representation of the digest
	 */
	public final String getDigestHex() {
		var builder = new StringBuilder();
		for(byte b : digest) {
			builder.append(String.format("%02x", b));
		}
		return builder.toString();
	}

}
