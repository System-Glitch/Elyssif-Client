package fr.elyssif.client.callback;

public abstract class HashCallback implements Runnable {

	private byte[] digest;

	public final byte[] getDigest() {
		return digest;
	}

	public final void setDigest(byte[] digest) {
		this.digest = digest;
	}

	public final String getDigestHex() {
		var builder = new StringBuilder();
		for(byte b : digest) {
			builder.append(String.format("%02x", b));
		}
		return builder.toString();
	}

}
