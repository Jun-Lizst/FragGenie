/**
 * 
 */
package uk.ac.liverpool.metfrag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author neilswainston
 */
@WebServlet(name = "MetFragFragmentServlet", urlPatterns = { "/fragment" })
public class MetFragFragmentServlet extends HttpServlet {

	/**
	 *
	 **/
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * @param smiles
	 * @param mz
	 * @param inten
	 * @param response
	 * @throws Exception
	 */
	private static void run(final String[] smiles, final int maximumTreeDepth, final HttpServletResponse response)
			throws Exception {
		final List<float[]> fragments = new ArrayList<>();

		for (String s : smiles) {
			fragments.add(MetFrag.getFragments(s, maximumTreeDepth));
		}

		final JsonArray json = toJson(fragments);
		MetFragUtils.sendJson(json, response);
	}

	/**
	 * 
	 * @param fragments
	 * @return JsonArray
	 */
	private static JsonArray toJson(final List<float[]> fragments) {
		final JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();

		for (final float[] result : fragments) {
			arrayBuilder.add(MetFragUtils.fromFloatArray(result));
		}

		return arrayBuilder.build();
	}

	@Override
	public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
		try {
			run(MetFragTestData.SMILES, 2, response);
		} catch (Exception e) {
			MetFragUtils.handleException(e);
		}
	}

	@Override
	public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
		try (final JsonReader jsonReader = MetFragUtils.getReader(request)) {
			final JsonObject json = jsonReader.readObject();
			final JsonArray smiles = (JsonArray) json.get("smiles"); //$NON-NLS-1$

			try {
				run(MetFragUtils.toStringArray(smiles), json.getInt("maximumTreeDepth"), response); //$NON-NLS-1$
			} catch (Exception e) {
				MetFragUtils.handleException(e);
			}
		}
	}
}