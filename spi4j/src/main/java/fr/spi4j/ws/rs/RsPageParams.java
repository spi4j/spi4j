/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ws.rs;

import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.spi4j.ws.rs.exception.RsPagingException;

/**
 * Parameters for list pagination.
 *
 * @author MINARM.
 */
class RsPageParams {

	/**
	 * Regex to find the count parameter in the pageToken string.
	 */
	private static final Pattern c_page_size_pattern = Pattern
			.compile("^" + RsConstants.c_page_size_param + "([0-9]+)(.*)");

	/**
	 * Regex to find the cursor parameter in the pageToken string.
	 */
	private static final Pattern c_cusror_pattern = Pattern
			.compile("(.*)" + RsConstants.c_page_cursor_param + "([0-9]+)(.*)");

	/**
	 * Regex to find the offset parameter in the pageToken string.
	 */
	private static final Pattern c_page_offset_pattern = Pattern
			.compile("(.*)" + RsConstants.c_page_offset_param + "([0-9]+)(.*)");

	/**
	 * The value for the requested count of elements for the page.
	 */
	private int _pageSize = 0;

	/**
	 * The value for the calculated offset for the request.
	 */
	private int _pageOffset = 0;

	/**
	 * The value for the next or previous id for the request. (not used for now).
	 */
	private long _pageCursor = 0L;

	/**
	 * The idx for the current page.
	 */
	private int _currentPageIdx = 0;

	/**
	 * Store the name for the page parameter (choose by developer). Needed for
	 * building the links in the headers.
	 */
	private String _pageParamName;

	/**
	 * Store the name for the size parameter (choose by developer). Needed for
	 * building the links in the headers.
	 */
	private String _sizeParamName;

	/**
	 * Constructor.
	 *
	 * @param p_encodedToken : The base 64 token to decode.
	 */
	public RsPageParams(final String p_encodedToken) {
		decodePageToken(p_encodedToken);
	}

	/**
	 * Constructor.
	 *
	 * @param p_pageIdx  : The number for the request page (Index of the page).
	 * @param p_pageSize : The number of requested elements per page.
	 */
	public RsPageParams(final int p_pageIdx, final int p_pageSize) {

		_pageSize = p_pageSize;
		_pageOffset = p_pageIdx * p_pageSize;
		_currentPageIdx = p_pageIdx;
	}

	/**
	 * Decode the parameters for pagination contained in the token.
	 *
	 * @param p_token : The base 64 token to decode.
	 */
	private void decodePageToken(final String p_token) {
		if (null != p_token) {
			if (p_token.isEmpty()) {
				throw new RsPagingException("Impossible de décoder les paramètres de pagination : le token est vide.");
			}

			try {

				final byte[] v_decodedBytesFromToken = Base64.getDecoder().decode(p_token);
				final String v_decodedFromToken = new String(v_decodedBytesFromToken);

				Matcher v_matcher = c_cusror_pattern.matcher(v_decodedFromToken);
				v_matcher.matches();
				_pageCursor = Long.parseLong(v_matcher.group(2));

				v_matcher = c_page_size_pattern.matcher(v_decodedFromToken);
				v_matcher.matches();
				_pageSize = Integer.parseInt(v_matcher.group(1));

				v_matcher = c_page_offset_pattern.matcher(v_decodedFromToken);
				v_matcher.matches();
				_pageOffset = Integer.parseInt(v_matcher.group(2));

			} catch (final Exception p_ex) {
				throw new RsPagingException("Impossible de décoder le token de pagination.", p_ex);
			}
		}
	}

	/**
	 * Decode and set the two parameters names for page and size. (Developer
	 * choice).
	 *
	 * @param p_paramNames : The string for the two parameters (page & size) to
	 *                     decode.
	 */
	private void decodeParamNames(final String p_paramNames) {

		final String[] v_paramNames = p_paramNames.trim().split(":");

		if (v_paramNames.length != 2) {
			throw new RsPagingException("Impossible de décoder les noms des paramètres de pagination");
		}

		_pageParamName = v_paramNames[0].trim();
		_sizeParamName = v_paramNames[1].trim();
	}

	/**
	 * Get the max numbers of elements per page for the pagination.
	 *
	 * @return The page count value.
	 */
	public int get_pageSize() {

		return _pageSize;
	}

	/**
	 * Set the default max numbers of elements per page if none in the token.
	 *
	 * @param p_pageSize : The page size value.
	 */
	public void set_pageSize(final int p_pageSize) {

		_pageSize = p_pageSize;
	}

	/**
	 * Get the next cursor value for the request.
	 *
	 * @return The cursor value for the page.
	 */
	public long get_pageCursor() {

		return _pageCursor;
	}

	/**
	 * Get the offset value for the request.
	 *
	 * @return The offset value for the page.
	 */
	public int get_pageOffset() {

		return _pageOffset;
	}

	/**
	 * Get the name for page parameter.
	 *
	 * @return The name for the page parameter. (Developer choice).
	 */
	public String get_pageParamName() {

		return _pageParamName;
	}

	/**
	 * Get the name for size parameter.
	 *
	 * @return The name for the size parameter. (Developer choice).
	 */
	public String get_sizeParamName() {

		return _sizeParamName;
	}

	/**
	 * Get the idx for the current page.
	 *
	 * @return The idx for the current page.
	 */
	public int get_currentPageIdx() {

		return _currentPageIdx;
	}

	/**
	 * Set the names choose by developer for page and size parameters.
	 *
	 * @param p_pageParamNames : The two page parameters with a ":" for separator.
	 */
	public void setPageParamNames(final String p_pageParamNames) {
		decodeParamNames(p_pageParamNames);
	}
}
