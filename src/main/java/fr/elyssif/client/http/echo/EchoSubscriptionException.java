package fr.elyssif.client.http.echo;

/**
 * Exception thrown on Echo channel subscription error.
 * @author Jérémy LAMBERT
 *
 */
public final class EchoSubscriptionException extends RuntimeException {

	private static final long serialVersionUID = -6342240883329521924L;
	private String channel;
	private Object response;

	public EchoSubscriptionException(Object[] response) {
		super("Echo subscription error: channel \"" + String.valueOf(response[0]) + "\" | response: " + String.valueOf(response[1]));
		channel = String.valueOf(response[0]);
		this.response = response[1];
	}

	public final String getChannel() {
		return channel;
	}

	public final Object getResponse() {
		return response;
	}

}
