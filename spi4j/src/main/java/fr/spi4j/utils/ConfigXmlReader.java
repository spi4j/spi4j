/**
 * (C) Copyright Ministère des Armées (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

/**
 * Get all properties from an xml resource file. Go deeper to close resources in
 * case of exceptions ?
 *
 * @author MinArm
 */
public class ConfigXmlReader implements ConfigResourceReader {

	/**
	 * The name of the resource under string format.
	 */
	String _resourceToString;

	/**
	 * Load and read the specified resource.
	 *
	 * @param p_resource the resource, in this case a simple file path under string
	 *                   format.
	 */
	@Override
	public ConfigResourcesContent readContent(final String p_resource, final Optional<String> p_resourceBase)
			throws Exception {

		_resourceToString = ConfigFileUtils.getResource(p_resource, p_resourceBase);
		final StringBuilder v_key = new StringBuilder();
		final Path v_path = Paths.get(_resourceToString);
		final Stream<String> v_lines = Files.lines(v_path);
		final String v_data = v_lines.collect(Collectors.joining("\n"));
		v_lines.close();

		final Map<Object, Object> v_properties = new HashMap<>();
		final XMLInputFactory v_factory = XMLInputFactory.newInstance();
		final InputStream v_stream = new ByteArrayInputStream(v_data.getBytes());
		final XMLStreamReader v_reader = v_factory.createXMLStreamReader(v_stream);
		final Map<String, Integer> v_insertedKeys = new HashMap<>();

		while (v_reader.hasNext()) {
			final int v_evt = v_reader.next();
			switch (v_evt) {
			case XMLStreamConstants.START_ELEMENT:
				if (v_key.length() > 0) {
					v_key.append(".");
				}
				v_key.append(v_reader.getLocalName().trim());
				if (v_insertedKeys.containsKey(v_key.toString())) {
					int cpt = v_insertedKeys.get(v_key.toString());
					cpt++;
					v_insertedKeys.replace(v_key.toString(), cpt);
					v_key.append("-");
					v_key.append(cpt);
				}
				v_insertedKeys.put(v_key.toString(), 0);
				break;

			case XMLStreamConstants.CHARACTERS:
				if (v_key.length() > 0 && !v_reader.getText().trim().isEmpty()) {
					v_properties.put(v_key.toString(), v_reader.getText().trim());
				}
				break;

			case XMLStreamConstants.ATTRIBUTE:
				break;

			case XMLStreamConstants.END_ELEMENT:
				if (v_key.lastIndexOf(".") != -1) {
					v_key.delete(v_key.lastIndexOf("."), v_key.length());
				} else {
					v_key.setLength(0);
				}
				break;

			case XMLStreamConstants.START_DOCUMENT:
				break;
			}
		}
		return new ConfigResourcesContent(v_properties);
	}

	/**
	 * Get the specified default encoding for the reader.
	 *
	 * @return The suggested default encoding for the reader.
	 */
	@Override
	public Charset getDefaultEncoding() {
		return StandardCharsets.UTF_8;
	}

	/**
	 * Get the name of the resource under string format.
	 *
	 * @return the name of the resource.
	 */
	@Override
	public String getIdentification() {
		return _resourceToString;
	}
}
