package fr.elyssif.client.http;

import java.lang.reflect.InvocationTargetException;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;

/**
 * This enum's purpose is to simplify Http request creation and avoid redundancy in the code using a single method to instantiate a request with any method. 
 * @author Jérémy LAMBERT
 * 
 * @see HttpRequestBase
 */
public enum HttpMethod {

	GET(HttpGet.class),
	POST(HttpPost.class),
	PUT(HttpPut.class),
	PATCH(HttpPatch.class),
	DELETE(HttpDelete.class);

	private Class<? extends HttpRequestBase> cls;

	HttpMethod(Class<? extends HttpRequestBase> cls) {
		this.cls = cls;
	}

	/**
	 * Get the correct HttpRequestBase subclass for the method.
	 * @return the class to use for the method
	 */
	protected Class<? extends HttpRequestBase> getRequestClass() {
		return cls;
	}

	/**
	 * Instantiate and get a request for this method.
	 * @param url the url of the request
	 * @return an instance of the correct subclass for the method.
	 * 
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	protected HttpRequestBase instantiate(String url) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		HttpRequestBase request = cls.getDeclaredConstructor(String.class).newInstance(url);
		return request;
	}

}
