/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ws.rs;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

import jakarta.ws.rs.ext.ContextResolver;

/**
 * The default resolver for Jackson. Tell Jackson not using the processing of
 * JAXB Annotations.
 *
 * @author MINARM
 */
public class RsDefaultContextResolver implements ContextResolver<ObjectMapper> {

	/**
	 * The object mapper for Jackson.
	 */
	protected final ObjectMapper _objectMapper;

	/**
	 * Constructor.
	 *
	 * @param p_annotationIntrospector : The default annotation instrospector
	 *                                 (injected by Pacman).
	 */
	public RsDefaultContextResolver(final AnnotationIntrospector p_annotationIntrospector) {

		_objectMapper = new ObjectMapper();
		AnnotationIntrospector v_annotationIntrospector = p_annotationIntrospector;

		if (null == v_annotationIntrospector) {
			v_annotationIntrospector = new JacksonAnnotationIntrospector();
		}

		// Default settings.
		_objectMapper.setAnnotationIntrospector(p_annotationIntrospector);
		_objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		_objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		_objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
		_objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		_objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	}

	@Override
	public ObjectMapper getContext(final Class<?> type) {
		return _objectMapper;
	}
}
