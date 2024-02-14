/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ws.rs;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONObject;

import fr.spi4j.ws.rs.exception.RsExceptionXtoContainer;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.ResponseBuilder;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.Response.StatusType;

/**
 * This class is used to simplify and standardize the creation of an object of
 * type Response for a REST service.
 *
 * @apiNote At this time, all is in JSON format !
 * @author MINARM
 */
public final class RsResponseHelper {

	/**
	 * Create a simple response with a status code.
	 *
	 * @param p_status : The HTTP status code.
	 * @return The HTTP response.
	 */
	public static Response response(final StatusType p_status) {
		return getBuilder(RsMediaType.c_application_json_utf8, p_status, null).build();
	}

	/**
	 * Create a simple response with a status code and headers.
	 *
	 * @param p_status  : The Http status code.
	 * @param p_headers : The list of properties to include in the headers.
	 * @return The HTTP response.
	 */
	public static Response response(final RsStatusType p_status, final Map<String, String> p_headers) {
		return buildHeaders(getBuilder(RsMediaType.c_application_json_utf8, p_status, null), p_headers).build();
	}

	/**
	 * Create a response with an object.
	 *
	 * @param p_object : The object to be sent in the response.
	 * @return The HTTP response.
	 */
	public static Response response(final Object p_object) {
		return getBuilder(RsMediaType.c_application_json_utf8, Status.OK, p_object).build();
	}

	/**
	 * Create a simple response with an object and headers.
	 *
	 * @param p_object  : The object to be sent in the response.
	 * @param p_headers : The list of properties to include in the headers.
	 * @return The HTTP response.
	 */
	public static Response response(final Object p_object, final Map<String, String> p_headers) {
		return buildHeaders(getBuilder(RsMediaType.c_application_json_utf8, null, p_object), p_headers).build();
	}

	/**
	 * Create a response with an object and let the developer to specify the status.
	 *
	 * @param p_object : The object to be sent in the response.
	 * @param p_status : The HTTP status code.
	 * @return The HTTP response.
	 */
	public static Response response(final Object p_object, final StatusType p_status) {
		return getBuilder(RsMediaType.c_application_json_utf8, p_status, p_object).build();
	}

	/**
	 * Create a response with an object, headers and let the developer to specify
	 * the status.
	 *
	 * @param p_object  : The object to be sent in the response.
	 * @param p_status  : The HTTP status code.
	 * @param p_headers : The list of properties to include in the headers.
	 * @return The HTTP response.
	 */
	public static Response response(final Object p_object, final StatusType p_status,
			final Map<String, String> p_headers) {
		return buildHeaders(getBuilder(RsMediaType.c_application_json_utf8, p_status, p_object), p_headers).build();
	}

	/**
	 * Create a response with an JSON object.
	 *
	 * @apiNote Be careful, the object is already in JSON format !
	 * @param p_jsonObject : The JSON object to be sent in the response.
	 * @return The HTTP response.
	 */
	public static Response responseForJSONObject(final JSONObject p_jsonObject) {
		return getBuilder(RsMediaType.c_application_json_utf8, Status.OK, p_jsonObject.toString()).build();
	}

	/**
	 * Create a response with an JSON object and headers.
	 *
	 * @apiNote Be careful, the object is already in JSON format !
	 * @param p_jsonObject : The JSON object to be sent in the response.
	 * @param p_headers    : The list of properties to include in the headers.
	 * @return The HTTP response.
	 */
	public static Response response(final JSONObject p_jsonObject, final Map<String, String> p_headers) {
		return buildHeaders(getBuilder(RsMediaType.c_application_json_utf8, Status.OK, p_jsonObject.toString()),
				p_headers).build();
	}

	/**
	 * Create a response with an JSON object and let the developer to specify the
	 * status.
	 *
	 * @apiNote Be careful, the object is already in JSON format !
	 * @param p_jsonObject : The JSON object to be sent in the response.
	 * @param p_status     : The HTTP status code.
	 * @return The HTTP response.
	 */
	public static Response responseForJSONObject(final JSONObject p_jsonObject, final StatusType p_status) {
		return getBuilder(RsMediaType.c_application_json_utf8, p_status, p_jsonObject.toString()).build();
	}

	/**
	 * Create a response with an JSON object, headers, and let the developer to
	 * specify the status.
	 *
	 * @apiNote Be careful, the object is already in JSON format !
	 * @param p_jsonObject : The JSON object to be sent in the response.
	 * @param p_status     : The HTTP status code.
	 * @param p_headers    : The list of properties to include in the headers.
	 * @return The HTTP response.
	 */
	public static Response response(final JSONObject p_jsonObject, final StatusType p_status,
			final Map<String, String> p_headers) {
		return buildHeaders(getBuilder(RsMediaType.c_application_json_utf8, p_status, p_jsonObject.toString()),
				p_headers).build();
	}

	/**
	 * Create a response for URI redirection.
	 *
	 * @param p_uri    : The full URI with all parameters.
	 * @param p_status : The HTTP status code.
	 * @return The HTTP response.
	 */
	public static Response responseForRedirect(final URI p_uri, final StatusType p_status) {
		return Response.temporaryRedirect(p_uri).type(RsMediaType.c_application_json_utf8).status(p_status).build();
	}

	/**
	 * Create a response for URI redirection.
	 *
	 * @param p_uri : The full URI with all parameters.
	 * @return The HTTP response.
	 */
	public static Response responseForRedirect(final URI p_uri) {
		return getBuilder(RsMediaType.c_text_plain_utf8, Status.FOUND, null).header(HttpHeaders.LOCATION, p_uri)
				.build();
	}

	/**
	 * Create a response for URI redirection.
	 *
	 * @param p_uri : The full RsUri with all parameters.
	 * @return The HTTP response.
	 */
	public static Response responseForRedirect(final RsUri p_uri) {
		return responseForRedirect(p_uri.get_uri());
	}

	/**
	 * Create a response with only headers values. Try to simplify (not a
	 * MultivaluedMap !)
	 *
	 * @param p_headers : The Map for information storage.
	 * @return The HTTP response.
	 */
	public static Response responseForHeaders(final Map<String, String> p_headers) {
		return buildHeaders(getBuilder(RsMediaType.c_application_json_utf8, Status.OK, null), p_headers).build();
	}

	/**
	 * Create a response with only headers values. and let the developer to specify
	 * the status.
	 *
	 * @param p_headers : The Map for information storage
	 * @param p_status  : The HTTP status code.
	 * @return The HTTP response.
	 */
	public static Response responseForHeaders(final Map<String, String> p_headers, final StatusType p_status) {
		return buildHeaders(getBuilder(RsMediaType.c_application_json_utf8, p_status, null), p_headers).build();
	}

	/**
	 * Create an HTTP response with a token containing all informations for
	 * application security.
	 *
	 * @param p_tokenContainer : The token(s) directly encapsulated in a container
	 *                         defined by the authentication server and under json
	 *                         format.
	 * @return The HTTP response.
	 */
	public static Response responseForTokens(final String p_tokenContainer) {
		return responseForTokens(p_tokenContainer, RsStatusType.create(200, "OK"));
	}

	/**
	 * Create an HTTP response with a token containing all informations for
	 * application security.
	 *
	 * @param p_tokenContainer : The token directly encapsulated in a container
	 *                         defined by the authentication server and under json
	 *                         format.
	 * @return The HTTP response.
	 */
	public static Response responseForTokens(final JSONObject p_tokenContainer) {
		return responseForTokens(p_tokenContainer.toString(), RsStatusType.create(200, "OK"));
	}

	/**
	 * Create an HTTP response with a token containing all informations for
	 * application security.
	 *
	 * @param p_tokenContainer : The token encapsulated in a container defined by
	 *                         the developer for rest message.
	 * @return The HTTP response.
	 */
	public static Response responseForToken(final RsXto_Itf p_tokenContainer) {
		return responseForTokens(p_tokenContainer, RsStatusType.create(200, "OK"));
	}

	/**
	 * Create an HTTP response with a token containing all informations for
	 * application security.
	 *
	 * @param p_tokenContainer : The token(s) encapsulated in a container defined by
	 *                         the developer for rest message.
	 * @param p_status         : The custom HTTP status code.
	 * @return The HTTP response.
	 */
	public static Response responseForTokens(final Object p_tokenContainer, final RsStatusType p_status) {
		return getBuilder(RsMediaType.c_application_json_utf8, p_status, p_tokenContainer)
				.header("Cache-Control", "no-cache, no-store , max-age=0, must-revalidate").header("Pragma", "no-cache")
				.build();
	}

	/**
	 * Create an HTTP response for an asynchronous service.
	 *
	 * @param p_uri : The URI returned by the service. Has to be set in 'Location'
	 *              header.
	 * @return The HTTP response.
	 */
	public static Response responseForAsynchronous(final URI p_uri) {
		return getBuilder(RsMediaType.c_application_json_utf8, Status.ACCEPTED, null)
				.header(HttpHeaders.LOCATION, p_uri).build();
	}

	/**
	 * Create an HTTP response for an asynchronous service and let the developer to
	 * specify the status.
	 *
	 * @param p_uri    : The URI returned by the service. Has to be set in
	 *                 'Location' header.
	 * @param p_status : The custom HTTP status code.
	 * @return The HTTP response.
	 */
	public static Response responseForAsynchronous(final URI p_uri, final RsStatusType p_status) {
		return getBuilder(RsMediaType.c_application_json_utf8, p_status, null).header(HttpHeaders.LOCATION, p_uri)
				.build();
	}

	/**
	 * Create an HTTP response with an exception.
	 *
	 * @param p_exceptionContainer : The original exception encapsulated in a
	 *                             specific container for rest message.
	 * @return The HTTP response.
	 */
	public static Response responseForException(final RsExceptionXtoContainer p_exceptionContainer) {
		return getBuilder(RsMediaType.c_application_json_utf8, p_exceptionContainer.get_status(), p_exceptionContainer)
				.build();
	}

	/**
	 * Create an HTTP response with the entity returned by the service.
	 *
	 * @param p_xto : The object (entity) returned by the service.
	 * @return The HTTP response.
	 */
	public static Response responseForEntity(final RsXto_Itf p_xto) {
		return getBuilder(RsMediaType.c_application_json_utf8, Status.OK, p_xto).build();
	}

	/**
	 * Create an HTTP response with the entity returned by the service and headers.
	 *
	 * @param p_xto     : The object (entity) returned by the service.
	 * @param p_headers : The list of properties to include in the headers.
	 * @return The HTTP response.
	 */
	public static Response response(final RsXto_Itf p_xto, final Map<String, String> p_headers) {
		return buildHeaders(getBuilder(RsMediaType.c_application_json_utf8, Status.OK, p_xto), p_headers).build();
	}

	/**
	 * Create an HTTP response with the entity returned by the service and let the
	 * developer to specify the status.
	 *
	 * @param p_xto    : The object (entity) returned by the service.
	 * @param p_status : The HTTP status code.
	 * @return The HTTP response.
	 */
	@Deprecated
	public static Response responseForEntity(final RsXto_Itf p_xto, final StatusType p_status) {
		return getBuilder(RsMediaType.c_application_json_utf8, p_status, p_xto).build();
	}

	/**
	 * Create an HTTP response with the entity returned by the service and let the
	 * developer to specify a custom status.
	 *
	 * @param p_xto    : The object (entity) returned by the service.
	 * @param p_status : The custom HTTP status code.
	 * @return The HTTP response.
	 */
	public static Response responseForEntity(final RsXto_Itf p_xto, final RsStatusType p_status) {
		return getBuilder(RsMediaType.c_application_json_utf8, p_status, p_xto).build();
	}

	/**
	 * Create an HTTP response with the entity / headers returned by the service and
	 * let the developer to specify a custom status.
	 *
	 * @param p_xto     : The object (entity) returned by the service.
	 * @param p_status  : The custom HTTP status code.
	 * @param p_headers : : The list of properties to include in the headers.
	 * @return The HTTP response.
	 */
	public static Response responseForEntity(final RsXto_Itf p_xto, final RsStatusType p_status,
			final Map<String, String> p_headers) {
		return buildHeaders(getBuilder(RsMediaType.c_application_json_utf8, p_status, p_xto), p_headers).build();
	}

	/**
	 * Create an HTTP response with the list of entities returned by the service.
	 *
	 * @param p_lstEntity : The object (list of entities) returned by the service.
	 * @return The HTTP response.
	 */
	public static Response responseForEntity(final List<? extends RsXto_Itf> p_lstEntity) {
		return getBuilder(RsMediaType.c_application_json_utf8, Status.OK, p_lstEntity).build();
	}

	/**
	 * Create an HTTP response with the list of entities returned by the service and
	 * headers.
	 *
	 * @param p_lstEntity : The object (list of entities) returned by the service..
	 * @param p_headers   : : The list of properties to include in the headers.
	 * @return The HTTP response.
	 */
	public static Response responseForEntity(final List<? extends RsXto_Itf> p_lstEntity,
			final Map<String, String> p_headers) {
		return buildHeaders(getBuilder(RsMediaType.c_application_json_utf8, Status.OK, p_lstEntity), p_headers).build();
	}

	/**
	 * Create an HTTP response with the list of entities returned by the service and
	 * let the developer to specify the status.
	 *
	 * @param p_lstEntity : The object (list of entities) returned by the service.
	 * @param p_status    : The HTTP status code.
	 * @return The HTTP response.
	 */
	@Deprecated
	public static Response responseForEntity(final List<? extends RsXto_Itf> p_lstEntity, final StatusType p_status) {
		return getBuilder(RsMediaType.c_application_json_utf8, p_status, p_lstEntity).build();
	}

	/**
	 * Create an HTTP response with the list of entities returned by the service and
	 * let the developer to specify a custom status.
	 *
	 * @param p_lstEntity : The object (list of entities) returned by the service.
	 * @param p_status    : The custom HTTP status code.
	 * @return The HTTP response.
	 */
	public static Response responseForEntity(final List<? extends RsXto_Itf> p_lstEntity, final RsStatusType p_status) {
		return getBuilder(RsMediaType.c_application_json_utf8, p_status, p_lstEntity).build();
	}

	/**
	 * Create an HTTP response with the list of entities returned by the service /
	 * headers and let the developer to specify a custom status.
	 *
	 * @param p_lstEntity : The object (list of entities) returned by the service.
	 * @param p_status    : The custom HTTP status code.
	 * @param p_headers   : : The list of properties to include in the headers.
	 * @return The HTTP response.
	 */
	public static Response responseForEntity(final List<? extends RsXto_Itf> p_lstEntity, final RsStatusType p_status,
			final Map<String, String> p_headers) {
		return buildHeaders(getBuilder(RsMediaType.c_application_json_utf8, p_status, p_lstEntity), p_headers).build();
	}

	/**
	 * Create an HTTP response with the paged list of entities returned by the
	 * service.
	 *
	 * @param p_paginator : The paginator object with the included paged list.
	 * @return The HTTP response with additional informations for paging.
	 */
	public static Response responseForEntity(final RsPaginatorHelper p_paginator) {
		return buildPagedResponse(p_paginator, RsMediaType.c_application_json_utf8, Status.OK).build();
	}

	/**
	 * Create an HTTP response with the paged list of entities returned by the
	 * service and headers.
	 *
	 * @param p_paginator : The paginator object with the included paged list.
	 * @param p_headers   : The list of properties to include in the headers.
	 * @return The HTTP response.
	 */
	public static Response responseForEntity(final RsPaginatorHelper p_paginator, final Map<String, String> p_headers) {
		return buildHeaders(buildPagedResponse(p_paginator, RsMediaType.c_application_json_utf8, Status.OK), p_headers)
				.build();
	}

	/**
	 * Create an HTTP response with the paged list of entities returned by the
	 * service and let the developer to specify a custom status.
	 *
	 * @param p_paginator : The paginator object with the included paged list .
	 * @param p_status    : The custom HTTP status code.
	 * @return The HTTP response with additional informations for paging.
	 */
	public static Response responseForEntity(final RsPaginatorHelper p_paginator, final RsStatusType p_status) {
		return buildPagedResponse(p_paginator, RsMediaType.c_application_json_utf8, p_status).build();
	}

	/**
	 * Create an HTTP response with the paged list of entities returned by the
	 * service, headers and let the developer to specify a custom status.
	 *
	 * @param p_paginator : The paginator object with the included paged list.
	 * @param p_status    : The custom HTTP status code.
	 * @param p_headers   : The list of properties to include in the headers.
	 * @return The HTTP response.
	 */
	public static Response responseForEntity(final RsPaginatorHelper p_paginator, final RsStatusType p_status,
			final Map<String, String> p_headers) {
		return buildHeaders(buildPagedResponse(p_paginator, RsMediaType.c_application_json_utf8, p_status), p_headers)
				.build();
	}

	/**
	 * Create an HTTP response for a new created resource.
	 *
	 * @param p_xto : The object (entity) returned by the service.
	 * @return The HTTP response with additional informations for the creation.
	 */
	public static Response responseForCreate(final RsXto_Itf p_xto) {
		return getBuilder(RsMediaType.c_application_json_utf8, Status.CREATED, p_xto).build();
	}

	/**
	 * Create an HTTP response for a new created resource.
	 *
	 * @param p_uri : The URI returned by the service. Has to be set in 'Location'
	 *              header.
	 * @return The HTTP response with additional informations for the creation.
	 */
	public static Response responseForCreate(final URI p_uri) {
		return getBuilder(RsMediaType.c_application_json_utf8, Status.CREATED, null).header(HttpHeaders.LOCATION, p_uri)
				.build();
	}

	/**
	 * Create an HTTP response for a new created resource and let the developer to
	 * specify a custom status.
	 *
	 * @param p_uri    : The URI returned by the service. Has to be set in
	 *                 'Location' header.
	 * @param p_status : The custom HTTP status code.
	 * @return The HTTP response with additional informations for the creation.
	 */
	public static Response responseForCreate(final URI p_uri, final RsStatusType p_status) {
		return getBuilder(RsMediaType.c_application_json_utf8, p_status, null).header(HttpHeaders.LOCATION, p_uri)
				.build();
	}

	/**
	 * Create an HTTP response for a updated resource.
	 *
	 * @return The HTTP response.
	 */
	public static Response responseForUpdate() {
		return getBuilder(RsMediaType.c_application_json_utf8, Status.NO_CONTENT, null).build();
	}

	/**
	 * Create an HTTP response for a updated resource.
	 *
	 * @param p_xto : The object (entity) returned by the service.
	 * @return The HTTP response.
	 */
	public static Response responseForUpdate(final RsXto_Itf p_xto) {
		return getBuilder(RsMediaType.c_application_json_utf8, Status.NO_CONTENT, p_xto).build();
	}

	/**
	 * Create an HTTP response for a deleted resource.
	 *
	 * @return The HTTP response.
	 */
	public static Response responseForDelete() {
		return getBuilder(RsMediaType.c_application_json_utf8, Status.NO_CONTENT, null).build();
	}

	/**
	 * Main method to create the HTTP response with pagination for a list. Add
	 * specific informations to the header.
	 *
	 * @param p_paginator              : The paginator container object for the
	 *                                 paged list returned by the service.
	 * @param p_mediaType              : The media type code (TEXT/JSON/Etc...).
	 * @param p_statusForCompletedList : The custom HTTP status code.
	 * @return The HTTP response builder.
	 */
	private static Response.ResponseBuilder buildPagedResponse(final RsPaginatorHelper p_paginator,
			final String p_mediaType, final StatusType p_statusForCompletedList) {
		// Get the navigation links for the paged list.
		final Link[] v_links = p_paginator.get_links();

		// Paste the correct status for the response (depends of the list status).
		final StatusType v_status = (p_paginator.is_complete()) ? p_statusForCompletedList : Status.PARTIAL_CONTENT;

		// Create the initial response object.
		return getBuilder(p_mediaType, v_status, p_paginator.get_pagedList()).links(v_links)
				.header(p_paginator.get_totalCountKey(), p_paginator.get_totalCount())
				.header(p_paginator.get_pageCountKey(), p_paginator.get_pageCount())
				.header(p_paginator.get_currentPageIdxKey(), p_paginator.get_currentPageIdx())
				.header(p_paginator.get_currentPageSizeKey(), p_paginator.get_currentPageSize());
	}

	/**
	 * Pattern Fluent for completing the builder with header parameters.
	 *
	 * @param p_resBuilder :The builder for Response object.
	 * @param p_headers    : The Map for header informations.
	 * @return The builder for the response object.
	 */
	private static Response.ResponseBuilder buildHeaders(final Response.ResponseBuilder p_resBuilder,
			final Map<String, String> p_headers) {
		// Loop over the parameter map<String, String>.
		for (final Entry<String, String> v_entry : p_headers.entrySet()) {
			// Add each property to the Header.
			p_resBuilder.header(v_entry.getKey(), v_entry.getValue());
		}
		return p_resBuilder;
	}

	/**
	 * Main method to create the initial builder for the HTTP response.
	 *
	 * @param p_mediaType : The media type code (TEXT/JSON/Etc...).
	 * @param p_status    : The HTTP status code.
	 * @param p_entity    : The object (Xto entity, primitive, etc..) or the
	 *                    exception returned by the service.
	 * @return The HTTP ResponseBuilder.
	 */
	private static ResponseBuilder getBuilder(final String p_mediaType, final StatusType p_status,
			final Object p_entity) {
		final ResponseBuilder v_resBuilder = Response.status(p_status);
		v_resBuilder.type(p_mediaType + ";" + RsConstants.c_charset_utf8);

		// If entity exists, paste in the response.
		if (null != p_entity) {
			v_resBuilder.entity(p_entity);
		}
		// Nothing more !! For the moment.
		return v_resBuilder;
	}
}
