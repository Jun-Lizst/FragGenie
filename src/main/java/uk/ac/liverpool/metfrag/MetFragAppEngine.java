/**
 * 
 */
package uk.ac.liverpool.metfrag;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author neilswainston
 */
@WebServlet(name = "MetFrag", urlPatterns = { "/match" })
public class MetFragAppEngine extends HttpServlet {

	/**
	 *
	 **/
	private static final long serialVersionUID = 1L;

	@Override
	public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
		final String[] smiles = new String[] {
				"C(C(=O)O)OC1=NC(=C(C(=C1Cl)N)Cl)F",
				"C(C(=O)OC1=NC(=C(C(=C1Cl)N)Cl)F)O",
				"C1(=C(C(=NC(=C1Cl)F)C(C(=O)O)O)Cl)N",
				"CC(=O)OOC1=NC(=C(C(=C1Cl)N)Cl)F",
				"C(C(CSCP(=O)([O-])[O-])C(=O)[O-])C(=O)[O-]"
		};
		
		final String[] inchis = new String[] {
				"InChI=1S/C7H5Cl2FN2O3/c8-3-5(11)4(9)7(12-6(3)10)15-1-2(13)14/h1H2,(H2,11,12)(H,13,14)",
				"InChI=1S/C7H5Cl2FN2O3/c8-3-5(11)4(9)7(12-6(3)10)15-2(14)1-13/h13H,1H2,(H2,11,12)",
				"InChI=1S/C7H5Cl2FN2O3/c8-1-3(11)2(9)6(10)12-4(1)5(13)7(14)15/h5,13H,(H2,11,12)(H,14,15)",
				"InChI=1S/C7H5Cl2FN2O3/c1-2(13)14-15-7-4(9)5(11)3(8)6(10)12-7/h1H3,(H2,11,12)",
				"InChI=1S/C6H11O7PS/c7-5(8)1-4(6(9)10)2-15-3-14(11,12)13/h4H,1-3H2,(H,7,8)(H,9,10)(H2,11,12,13)/p-4"
		};
		
		final float[] mz = new float[] {
				90.97445f,
				106.94476f,
				110.0275f,
				115.98965f,
				117.9854f,
				124.93547f,
				124.99015f,
				125.99793f,
				133.95592f,
				143.98846f,
				144.99625f,
				146.0041f,
				151.94641f,
				160.96668f,
				163.00682f,
				172.99055f,
				178.95724f,
				178.97725f,
				180.97293f,
				196.96778f,
				208.9678f,
				236.96245f,
				254.97312f};
		
		final int[] inten = new int[] {
				681,
				274,
				110,
				95,
				384,
				613,
				146,
				207,
				777,
				478,
				352,
				999,
				962,
				387,
				782,
				17,
				678,
				391,
				999,
				720,
				999,
				999,
				999};
		
		try {
			final Collection<Map<String,Object>> results = MetFrag.match(smiles, inchis,  mz, inten);
			final JsonObject json = toJson(results);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().print(json.toString());
		}
		catch(Exception e) {
			e.printStackTrace();
			
			final Writer writer = new StringWriter();
            e.printStackTrace(new PrintWriter(writer));
			throw new IOException(writer.toString());
		}
	}

	/**
	 * 
	 * @param results
	 * @return JsonObject
	 */
	private static JsonObject toJson(final Collection<Map<String,Object>> results) {
		final JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();

		for(Map<String,Object> result : results) {
			final JsonObjectBuilder resBuilder = Json.createObjectBuilder();
			
			for (Map.Entry<String, Object> entry : result.entrySet()) {
				final Object value = entry.getValue();
				
				if(value instanceof String) {
					resBuilder.add(entry.getKey(), (String)value);
				}
				else if(value instanceof Byte) {
					resBuilder.add(entry.getKey(), (Byte)value);
				}
				else if(value instanceof Integer) {
					resBuilder.add(entry.getKey(), (Integer)value);
				}
				else if(value instanceof Double) {
					resBuilder.add(entry.getKey(), (Double)value);
				}
			}
			
			arrayBuilder.add(resBuilder.build());
		}
		
		final JsonObjectBuilder objBuilder = Json.createObjectBuilder();
		objBuilder.add("results", arrayBuilder.build());
		return objBuilder.build();
	}
}