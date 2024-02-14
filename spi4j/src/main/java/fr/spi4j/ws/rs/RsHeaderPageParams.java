/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ws.rs;

/**
 * @author MinArm.
 */
public class RsHeaderPageParams {

	/**
	 * The header key for total count storage.
	 */
	private final String _totalCountKey;

	/**
	 * The header key for page count storage.
	 */
	private final String _pageCountKey;

	/**
	 * The header key for current page size.
	 */
	private final String _currentPageSizeKey;

	/**
	 * The header key for current index page.
	 */
	private final String _currentPageIdxKey;

	/**
	 * Constructor.
	 *
	 * @param p_totalCountKey      : The header key for total count storage.
	 * @param p_pageCountKey       : The header key for page count storage.
	 * @param p_currentPageSizeKey : The header key for current page size.
	 * @param p_currentPageIdxKey  : The header key for current index page.
	 */
	RsHeaderPageParams(final String p_totalCountKey, final String p_pageCountKey, final String p_currentPageSizeKey,
			final String p_currentPageIdxKey) {
		_currentPageIdxKey = p_currentPageIdxKey;
		_currentPageSizeKey = p_currentPageSizeKey;
		_pageCountKey = p_pageCountKey;
		_totalCountKey = p_totalCountKey;
	}

	/**
	 * @return The key for total count of the paged request.
	 */
	String get_totalCountKey() {
		if (null != _totalCountKey || _totalCountKey.isEmpty()) {
			return _totalCountKey;
		}
		return RsConstants.c_list_header_total_count;
	}

	/**
	 * @return The key for the total number of pages for the request.
	 */
	String get_pageCountKey() {
		if (null != _pageCountKey || _pageCountKey.isEmpty()) {
			return _pageCountKey;
		}
		return RsConstants.c_list_header_page_count;
	}

	/**
	 * @return The number of elements for the current page.
	 */
	String get_currentPageSizeKey() {
		if (null != _currentPageSizeKey || _currentPageIdxKey.isEmpty()) {
			return _currentPageSizeKey;
		}
		return RsConstants.c_list_header_page_size;
	}

	/**
	 * @return The current page index.
	 */
	String get_currentPageIdxKey() {
		if (null != _currentPageIdxKey || _currentPageIdxKey.isEmpty()) {
			return _currentPageIdxKey;
		}
		return RsConstants.c_list_header_page_current;
	}
}
