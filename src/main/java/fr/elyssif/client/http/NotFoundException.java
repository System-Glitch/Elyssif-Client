package fr.elyssif.client.http;

/**
 * Exeption passed to the Logger when a RestRequest returns a status code 404.
 * @author Jérémy LAMBERT
 * 
 * @see RestRequest
 *
 */
public class NotFoundException extends RuntimeException {

	private static final long serialVersionUID = 4800168628239251143L;

	public NotFoundException(String message) {
		super(message);
	}

	public NotFoundException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
