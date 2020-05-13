/**
 * 
 */
package uk.ac.liverpool.metfrag;

import java.io.IOException;
import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * @author neilswainston
 */
public class MetFragAppEngineTest {

	/**
	 * 
	 * @throws IOException
	 */
	@Test
	public void testGet() throws IOException {
		final MockHttpServletResponse response = new MockHttpServletResponse();
		new MetFragAppEngine().doGet(null, response);
		verify(response);
	}
	
	/**
	 * 
	 * @param response
	 */
	private static void verify(final MockHttpServletResponse response) {
		Assert.assertEquals("application/json", response.getContentType());
		Assert.assertEquals("UTF-8", response.getCharacterEncoding());

		final String resp = response.getWriterContent().toString();

		try(final JsonReader jsonReader = Json.createReader(new StringReader(resp))) {
			final JsonObject object = jsonReader.readObject();
			final JsonArray results = (JsonArray)object.get("results");
			Assert.assertEquals(5, results.size());
		}
	}
}