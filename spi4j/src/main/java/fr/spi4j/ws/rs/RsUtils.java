/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ws.rs;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import fr.spi4j.ws.rs.exception.RsParameterException;
import jakarta.ws.rs.core.MultivaluedMap;

/**
 * Simple utility class.
 *
 * @author MINARM.
 */
public class RsUtils {

	/**
	 *
	 * Finds and returns the value of a parameter based on its key. The parameter is
	 * retrieved from the query parameter list.
	 *
	 * @param p_key    : The key for the requested parameter.
	 * @param p_params : All the parameters provided in the request.
	 * @return The value of the requested parameter.
	 */
	public static String get_param(final String p_key, final MultivaluedMap<String, String> p_params) {
		if (null != p_params.get(p_key) && !p_params.get(p_key).isEmpty()) {
			return p_params.get(p_key).get(0);
		}
		throw new RsParameterException("Récupération du jeton, le paramètre : " + p_key + " n'a pas été renseigné.");
	}

	/**
	 * Build an encoded string to pass into a url, from the providing a list of
	 * parameters. Please note, at this stage, no verification of the nullity of the
	 * parameters.
	 *
	 * @param p_params : The list of parameters to inject into the query.
	 * @return : The encoded string for the url fragment.
	 */
	static String convertParamsToHttpString(final Map<String, String> p_params) {
		final StringBuilder v_result = new StringBuilder();
		for (final Map.Entry<String, String> v_entry : p_params.entrySet()) {
			v_result.append(URLEncoder.encode(v_entry.getKey(), StandardCharsets.UTF_8)).append("=");
			v_result.append(URLEncoder.encode(v_entry.getValue(), StandardCharsets.UTF_8)).append("&");
		}
		final String v_resultString = v_result.toString();
		return v_resultString.length() > 0 ? v_resultString.substring(0, v_resultString.length() - 1) : v_resultString;
	}
}
