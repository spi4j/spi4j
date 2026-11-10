/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ws.rs;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import fr.spi4j.ws.rs.exception.RsPagingException;
import fr.spi4j.ws.xto.Xto_Itf;
import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.UriInfo;

/**
 * Paginator for all lists. Decodes information for pagination, transmitted in
 * the headers in order to send them to the service which performs the request.
 * Gives the sets of elements in order to construct the response. The paginator
 * works with offset param OR cursor param from Token OR with page and size
 * parameters without Token , depends of developer choice in service layer. With
 * the increasing number of parameters in constructors, maybe we could think of
 * going through a builder pattern ?
 *
 * @author MINARM
 */
public class RsPaginatorHelper {

	/**
	 * The current paged list stored in the paginator.
	 */
	private List<? extends RsXto_Itf> _pagedList;

	/**
	 * Store the parameters of the decoded pageToken or page & size.
	 */
	private final RsPageParams _pageParams;

	/**
	 * Store the path of the URI called for the operation.
	 */
	private final UriInfo _uriInfo;

	/**
	 * Indicate if the list is complete.
	 */
	private boolean _complete;

	/**
	 * Indicate if the paginator class works in token mode.
	 */
	private final boolean _tokenMode;

	/**
	 * Store the result of the count(*) query.
	 */
	private int _totalCount;

	/**
	 * Constructor.
	 *
	 * @param p_uriInfo         : Provides access to application and request URI
	 *                          information.
	 * @param p_defaultPageSize : The default number of elements the service must
	 *                          return by page.
	 * @param p_pageToken       : This token contains all the (other) needs
	 *                          informations for making pagination.
	 */
	public RsPaginatorHelper(final UriInfo p_uriInfo, final int p_defaultPageSize, final String p_pageToken) {

		// Set working informations.
		_pageParams = new RsPageParams(p_pageToken);

		if (0 == _pageParams.get_pageSize()) {
			_pageParams.set_pageSize(p_defaultPageSize);
		}
		_tokenMode = Boolean.TRUE;
		_complete = Boolean.FALSE;
		_uriInfo = p_uriInfo;
	}

	/**
	 * Constructor.
	 *
	 * @param p_uriInfo         : Provides access to application and request URI
	 *                          information.
	 * @param p_defaultPageSize : The default number of elements the service must
	 *                          return by page.
	 * @param p_pageIdx         : The number for the page.
	 * @param p_pageSize        : The number of elements the service must return by
	 *                          page.
	 */
	public RsPaginatorHelper(final UriInfo p_uriInfo, final int p_defaultPageSize, final int p_pageIdx,
			final int p_pageSize) {

		// Set working informations.
		int v_pageSize = p_pageSize;
		if (0 == v_pageSize) {
			v_pageSize = p_defaultPageSize;
		}

		_pageParams = new RsPageParams(p_pageIdx, v_pageSize);
		_tokenMode = Boolean.FALSE;
		_complete = Boolean.FALSE;
		_uriInfo = p_uriInfo;
	}

	/**
	 * Build links to be integrated in the response header.
	 *
	 * @return The table of links for performing pagination.
	 */
	public Link[] get_links() {
		final List<Link> v_links = new ArrayList<>();

		// The list does not exist, this is not the normal behaviour.
		if (null == _pagedList) {
			throw new RsPagingException("Impossible de récupérer la liste dans le paginateur.");
		}

		// The list does not need to be paginated (empty).
		if (_pagedList.isEmpty()) {
			_complete = Boolean.TRUE;
			return new Link[0];
		}

		// Check if it's the last page.
		if (get_pageCount() == get_currentPageIdx()) {
			_complete = Boolean.TRUE;
		}

		// Another way to check if it's the last page (case of no totalCount returned).
		if (_pagedList.size() < _pageParams.get_pageSize()) {
			_complete = Boolean.TRUE;
		}

		// Build link for previous page (token mode).
		if (_pageParams.get_pageOffset() > 0 && _tokenMode) {
			v_links.add(newLink(RsConstants.c_prev_rel, get_previousCursor(), get_previousOffset()));
		}

		// Build link for next page (token mode).
		if (!_complete && _tokenMode) {
			v_links.add(newLink(RsConstants.c_next_rel, get_nextCursor(), get_nextOffset()));
		}

		// Build link for previous page (not in token mode).
		if (_pageParams.get_pageOffset() > 0 && !_tokenMode) {
			v_links.add(newLink(RsConstants.c_prev_rel, get_previousPage(), get_pageSize()));
		}

		// Build link for next page (not in token mode).
		if (!_complete && !_tokenMode) {
			v_links.add(newLink(RsConstants.c_next_rel, get_nextPage(), get_pageSize()));
		}

		// Build link for the first page (token mode).
		if (_tokenMode) {
			v_links.add(newLink(RsConstants.c_first_rel, 0L, get_pageSize()));
		}

		// Build link for the first page (not in token mode).
		if (!_tokenMode) {
			v_links.add(newLink(RsConstants.c_first_rel, 0, get_pageSize()));
		}

		// Build the link for the last page (not in token mode).
		if (!_tokenMode) {
			v_links.add(newLink(RsConstants.c_last_rel, get_pageCount(), get_pageSize()));
		}

		// Convert the list to an array and return.
		final Link[] v_tbLinks = new Link[v_links.size()];
		v_links.toArray(v_tbLinks);

		return v_tbLinks;
	}

	/**
	 * Get the previous cursor for pagination. The list is not empty.
	 *
	 * @return The cursor.
	 */
	@SuppressWarnings("unchecked")
	private long get_previousCursor() {

		long v_cursor = ((Xto_Itf<Long>) _pagedList.get(0)).getId() - _pageParams.get_pageSize();

		if (v_cursor < 1L) {
			v_cursor = 1L;
		}
		return v_cursor;
	}

	/**
	 * Get the previous offset for pagination. The list is not empty.
	 *
	 * @return The offset.
	 */
	private int get_previousOffset() {

		int v_offset = _pageParams.get_pageOffset() - _pageParams.get_pageSize();

		if (v_offset < 0) {
			v_offset = 0;
		}

		return v_offset;
	}

	/**
	 * Get the previous page index for the paged request.
	 *
	 * @return The previous page idx.
	 */
	private int get_previousPage() {

		int v_pageIdx = _pageParams.get_currentPageIdx() - 1;

		if (v_pageIdx < 0) {
			v_pageIdx = 0;
		}

		return v_pageIdx;
	}

	/**
	 * Get the next cursor for pagination. The list is not empty.
	 *
	 * @return The cursor.
	 */
	@SuppressWarnings("unchecked")
	private long get_nextCursor() {

		return ((Xto_Itf<Long>) _pagedList.get(_pagedList.size() - 1)).getId();
	}

	/**
	 * Get the next offset for pagination. The list is not empty.
	 *
	 * @return The offset.
	 */
	private int get_nextOffset() {

		return _pageParams.get_pageOffset() + _pageParams.get_pageSize();
	}

	/**
	 * Get the next page index for the paged request.
	 *
	 * @return The next page idx.
	 */
	private int get_nextPage() {

		return _pageParams.get_currentPageIdx() + 1;
	}

	/**
	 * Create the new specific link (token mode).
	 *
	 * @param p_rel    : The type for the link (rel or prev).
	 * @param p_cursor : The id for the cursor (previous or next).
	 * @param p_offset : The number of lines to jump for the query (previous or
	 *                 next).
	 * @return The new created link.
	 */
	private Link newLink(final String p_rel, final long p_cursor, final int p_offset) {

		final byte[] v_cursorParam = (RsConstants.c_page_size_param + _pageParams.get_pageSize()
				+ RsConstants.c_page_cursor_param + p_cursor + RsConstants.c_page_offset_param
				+ p_offset).getBytes();

		return Link
				.fromUriBuilder(_uriInfo.getRequestUriBuilder().replaceQueryParam(
						RsConstants.c_page_token_param, Base64.getEncoder().encodeToString(v_cursorParam)))
				.rel(p_rel).build();
	}

	/**
	 * Create the new specific link (not in token mode).
	 *
	 * @param p_rel      : The type for the link (rel or prev).
	 * @param p_pageIdx  : The index of the page for the link.
	 * @param p_pageSize : The size for the pagination.
	 * @return The new created link.
	 */
	private Link newLink(final String p_rel, final int p_pageIdx, final int p_pageSize) {

		return Link.fromUriBuilder(_uriInfo.getRequestUriBuilder()
				.replaceQueryParam(_pageParams.get_pageParamName(), Integer.valueOf(p_pageIdx))
				.replaceQueryParam(_pageParams.get_sizeParamName(), p_pageSize)).rel(p_rel).build();
	}

	/**
	 * Set the list recovered from the service. As this is the last operation before
	 * creating the response, making this method fluent permits to simplify the
	 * caller resource.
	 *
	 * @param p_pagedList : The list.
	 * @return The paginator object.
	 */
	public RsPaginatorHelper completeWithPagedList(final List<? extends RsXto_Itf> p_pagedList) {

		_pagedList = p_pagedList;
		return this;
	}

	/**
	 * Set the total count of elements for the paged request. Making this method
	 * fluent permits to simplify the caller resource.
	 *
	 * @param p_totalCount : The count of elements for the paged request.
	 * @return The paginator object.
	 */
	public RsPaginatorHelper completeWithTotalCount(final int p_totalCount) {

		_totalCount = p_totalCount;
		return this;
	}

	/**
	 * Set the names choose by developer for page and size parameters.
	 *
	 * @param p_pageParamNames : The two page parameters with a ":" for separator.
	 * @return The paginator object.
	 */
	public RsPaginatorHelper completeWithParamNames(final String p_pageParamNames) {

		_pageParams.setPageParamNames(p_pageParamNames);
		return this;
	}

	/**
	 * Get the paged list returned from the service.
	 *
	 * @return The paged list of Xto entities.
	 */
	public List<? extends RsXto_Itf> get_pagedList() {

		return _pagedList;
	}

	/**
	 * Get the count of elements for the current page (request).
	 *
	 * @return The count of elements.
	 */
	public int get_currentPageSize() {

		return _pagedList.size();
	}

	/**
	 * Get the max count of elements for a page (request) for the service.
	 *
	 * @return The max count of elements.
	 */
	public int get_pageSize() {

		return _pageParams.get_pageSize();
	}

	/**
	 * Get the total count of elements for the request.
	 *
	 * @return The total count of elements for the request.
	 */
	public int get_totalCount() {

		return _totalCount;
	}

	/**
	 * Get the cursor (start id) for making the next paged request.
	 *
	 * @return The cursor.
	 */
	public long get_pageCursor() {

		return _pageParams.get_pageCursor();
	}

	/**
	 * Get the offset (nb lines) for making the next paged request.
	 *
	 * @return The offset.
	 */
	public int get_pageOffset() {

		return _pageParams.get_pageOffset();
	}

	/**
	 * Indicate if the list is completed (no more request to do).
	 *
	 * @return 'true' if the request is completed.
	 */
	public boolean is_complete() {

		return _complete;
	}

	/**
	 * Indicate if the paginator is in token mode or not.
	 *
	 * @return 'true' if the paginator is in token mode
	 */
	public boolean is_tokenMode() {

		return _tokenMode;
	}

	/**
	 * Get the calculated count of pages for the request.
	 *
	 * @return the calculated count of pages.
	 */
	public int get_pageCount() {

		int v_pageCount = _totalCount / get_pageSize();

		// If Ex : total = 10 and size = 50 -> v_pageCount = 1.
		if (0 == v_pageCount) {
			v_pageCount = 1;
		}

		if (_totalCount % v_pageCount > 0) {
			v_pageCount++;
		}
		return v_pageCount;
	}

	/**
	 * @return the index for the current page.
	 */
	public int get_currentPageIdx() {

		return _pageParams.get_currentPageIdx();
	}

	/**
	 * @return The key for total count of page request.
	 */
	String get_totalCountKey() {

		return RsFilter_Abs.get_config().get_headersPageParams().get_totalCountKey();
	}

	/**
	 * @return The key for the number of pages for the request.
	 */
	String get_pageCountKey() {

		return RsFilter_Abs.get_config().get_headersPageParams().get_pageCountKey();
	}

	/**
	 * @return The key for the number of elements returned for the current page.
	 */
	String get_currentPageSizeKey() {

		return RsFilter_Abs.get_config().get_headersPageParams().get_currentPageSizeKey();
	}

	/**
	 * @return The key for the current page index.
	 */
	String get_currentPageIdxKey() {

		return RsFilter_Abs.get_config().get_headersPageParams().get_currentPageIdxKey();
	}
}
