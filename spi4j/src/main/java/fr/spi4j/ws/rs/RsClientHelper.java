/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ws.rs;

import java.awt.Image;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;

/**
 * Utility class for client resources.
 *
 * @author MINARM
 */
public class RsClientHelper {

	/**
	 * Private constructor.
	 */
	private RsClientHelper() {
		// RAS.
	}

	/**
	 * Retreive an image from a remote resource provider.
	 *
	 * @param p_imgUrl : The full URI for the image (URL + /image name).
	 * @return The downloaded image.
	 * @throws IOException : Exception while downloading and reading the image
	 *                     resource.
	 */
	public static Image loadImage(final URL p_imgUrl) throws IOException {
		return ImageIO.read(p_imgUrl);
	}

	/**
	 * Retreive an image from a remote resource provider.
	 *
	 * @param p_imgUrl : The full URI for the image (URL + /image name).
	 * @return The downloaded image.
	 * @throws IOException : Exception while downloading and reading the image
	 *                     resource.
	 */
	public static Image loadImage(final String p_imgUrl) throws IOException {
		return loadImage(new URL(p_imgUrl));
	}

	/**
	 * Retreive an image from a remote resource provider and save it in the
	 * specified repository.
	 *
	 * @param p_imgUrl     : The full URI for the image (URL + /image name).
	 * @param p_savingPath : The path for the saving repository.
	 * @throws IOException : Exception while downloading, reading and saving the
	 *                     image resource in a file.
	 */
	public static void loadImage(final URL p_imgUrl, final String p_savingPath) throws IOException {
		// Return without exception if URL empty.
		if (null == p_imgUrl || p_imgUrl.getPath().trim().isEmpty()) {
			return;
		}

		// Get the name of the specific image to download.
		final String v_imgName = p_imgUrl.getPath().substring(p_imgUrl.getPath().lastIndexOf("/") + 1,
				p_imgUrl.getPath().length());

		if (null == v_imgName || v_imgName.isEmpty()) {
			throw new IOException("Impossible de charger l'image, vérifiez l'URL.");
		}

		// Get the specific extension for the image.
		final String v_ext = FilenameUtils.getExtension(v_imgName);

		if (null == v_ext || v_ext.trim().isEmpty()) {
			throw new IOException("Impossible de charger l'image, aucune extension trouvée.");
		}

		// Download and save the image.
		ImageIO.write((RenderedImage) loadImage(p_imgUrl), v_ext, new File(p_savingPath + File.separator + v_imgName));
	}

	/**
	 * Retreive an image from a remote resource provider and save it in the
	 * specified repository.
	 *
	 * @param p_imgUrl : The full URI for the image (URL + /image name).
	 * @param p_path   : The path for the saving repository.
	 * @throws IOException : Exception while downloading, reading and saving the
	 *                     image resource in a file.
	 */
	public static void loadImage(final String p_imgUrl, final String p_path) throws IOException {
		loadImage(new URL(p_imgUrl), p_path);
	}
}
