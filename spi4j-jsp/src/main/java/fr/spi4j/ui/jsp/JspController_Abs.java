/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.jsp;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import fr.spi4j.ws.rs.exception.RsFrontException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;

/**
 * Abstract class of all the sub controllers for the application.
 * 
 * @author MINARM
 */
public abstract class JspController_Abs {

	/**
	 * Specific key for method parameter.
	 */
	private static final String METHOD_PARAM = "method";

	/**
	 * Default method name if no specific method defined.
	 */
	private static final String METHOD_INIT = "init";

	/**
	 * Default memory threshold for file upload.
	 */
	@SuppressWarnings("unused")
	private static final int MEMORY_THRESHOLD = 0;

	/**
	 * Default maximum size for file upload.
	 */
	@SuppressWarnings("unused")
	private static final long MAX_FILE_SIZE = 0;

	/**
	 * Default for file upload.
	 */
	@SuppressWarnings("unused")
	private static final long MAX_REQUEST_SIZE = 0;

	/**
	 * Key for multipart form data detection in header.
	 */
	private static final String HEAD_MULTIPART_FORM = "multipart/form-data";

	/**
	 * Specific key for upload directory.
	 */
	private static final String DEFAULT_UPLOAD_DIR = "java.io.tmpdir";

	/**
	 * Map for file storage case of multiparts form (file upload).
	 */
	private Map<String, File> _files;

	/**
	 * Default dispatcher to activate in case of error for the controller
	 * (validation, etc...)
	 */
	private JspDispatcherEnum_Itf _defaultErrorDispatcher;

	/**
	 * Specific messenger class for the controller.
	 */
	private JspControllerMessenger _messenger;

	/**
	 * Return an indicator for automatic activation at main controller loading. This
	 * can be used in particular for loading controllers which serve as a repository
	 * for the entire application.
	 * 
	 * @return true if the init method for the controller must be activated
	 *         automatically by the main controller on loading.
	 */
	public abstract boolean activateOnLoad();

	/**
	 * Execute the target method and retrieve the dispatcher from the method
	 * controller.
	 * 
	 * @param p_request : all parameters for the request.
	 * @param p_method  : the method to execute for the controller.
	 * @return the application dispatcher.
	 */
	public abstract JspDispatcherEnum_Itf execute(final HttpServletRequest p_request,
			final JspControllerMessenger_Itf _messenger, final String p_method);

	/**
	 * Constructor.
	 */
	public JspController_Abs(final JspDispatcherEnum_Itf p_defaultErrorDispatcher) {
		_defaultErrorDispatcher = p_defaultErrorDispatcher;
		_messenger = new JspControllerMessenger();
	}

	/**
	 * Execute the target method and retrieve the dispatcher from the method
	 * controller. Extract the method parameter and test if the method parameter is
	 * null. If the request has a multipart content, the method parameter is ALWAYS
	 * null as we never find it from a 'getParameter()' but testing the nullity of
	 * this parameter makes it possible to avoid the test of the multipart format
	 * which is very cumbersome and useless in most cases.
	 * 
	 * @param p_request : all parameters for the request.
	 * @return the application dispatcher.
	 */
	public JspDispatcherEnum_Itf execute(final HttpServletRequest p_request) {

		try {
			String v_method = p_request.getParameter(METHOD_PARAM);
			if (p_request.getContentType() != null && p_request.getContentType().indexOf(HEAD_MULTIPART_FORM) != -1)
				processMultiPartForm(p_request);

			if (null == v_method || v_method.isEmpty())
				v_method = METHOD_INIT;

			return execute(p_request, _messenger, v_method);

		} catch (Exception p_exception) {
			String v_msg = p_exception.getMessage();
			if (p_exception instanceof RsFrontException)
				v_msg += " - " + ((RsFrontException) p_exception).get_additionalInfo();
			_messenger.addError(v_msg);
			return _defaultErrorDispatcher;
		}
	}

	/**
	 * Take all parts for the form and store all uploaded files in a specific map
	 * (avoid to be done by the developer in the sub controller). All the the other
	 * parameters are stored as attributes (as we cannot set them as specific
	 * parameters for the request (is it a good idea ?)). We take advantage of the
	 * traversal data to retrieve the name of the method to activate.
	 * 
	 * @param p_request : all parameters for the request.
	 * @return the method parameter.
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	private void processMultiPartForm(final HttpServletRequest p_request) throws Exception {
		String v_uploadPath = System.getenv(DEFAULT_UPLOAD_DIR);
		if (null == v_uploadPath)
			v_uploadPath = System.getProperty(DEFAULT_UPLOAD_DIR);
		File v_uploadDir = new File(v_uploadPath);

		if (!v_uploadDir.exists())
			v_uploadDir.mkdir();

		if (null == _files)
			_files = new HashMap<>();

		for (Part v_part : p_request.getParts()) {
			if (null != v_part.getContentType() && null != v_part.getSubmittedFileName()
					&& !v_part.getSubmittedFileName().isEmpty()) {
				String v_fileName = v_part.getSubmittedFileName();
				// v_part.write(v_uploadDir.getAbsolutePath() + File.separator + v_fileName);
				String v_filePath = v_uploadPath + File.separator + v_fileName;
				File v_fileStore = new File(v_filePath);
				if (v_fileStore.exists())
					v_fileStore.delete();

				v_part.write(v_filePath);
				v_fileStore = new File(v_filePath);
				_files.put(v_part.getName(), v_fileStore);
			}
		}
	}

	/**
	 * Retreive an uploaded file from the map using his specific key.
	 * 
	 * @param p_key : The specific key for the file.
	 * @return The file.
	 */
	protected File get_file(final String p_key) {
		if (null != _files && !_files.isEmpty())
			return _files.get(p_key);
		return null;
	}

	/**
	 * Retreive the messenger for the controller.
	 * 
	 * @return The messenger for the controller.
	 */
	JspControllerMessenger get_messenger() {
		return _messenger;
	}
}
