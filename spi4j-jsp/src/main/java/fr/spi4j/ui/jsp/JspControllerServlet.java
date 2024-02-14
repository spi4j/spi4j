/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.jsp;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Thin client servlet.
 * <p>
 * FrontController design pattern: front controller that will delegate to
 * different page controllers.
 * 
 * @author MINARM
 */
public class JspControllerServlet extends HttpServlet {

	static final String DISPATCHER_CLASS_PARAM = "fr.spi4j.jsp.dispatcher";
	static final String CONTROLLER_MESSENGER_ATTRIBUT = "fr.spi4j.jsp.messenger";
	static final String CONTROLER_SUFFIX = "controller";

	private static final long serialVersionUID = 1L;
	private boolean _applicationControllers;

	/**
	 * The map for all sub controllers storage.
	 */
	private final Map<String, Class<? extends JspController_Abs>> _controllers = new HashMap<>();

	@Override
	public void init(final ServletConfig p_config) throws ServletException {
		super.init(p_config);
		try {
			String v_dispatcher = p_config.getServletContext().getInitParameter(DISPATCHER_CLASS_PARAM);
			JspControllerScanner jspControllerScanner = new JspControllerScanner();
			registerControllers(jspControllerScanner.scan(p_config, v_dispatcher));
		} catch (Exception p_unknownException) {
			p_unknownException.printStackTrace();
			System.exit(-1);
		}
	}

	/**
	 * Register all the sub controllers of the target application.
	 * 
	 * @param p_controllers : set of sub controller to be registered.
	 * @throws ServletException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	private void registerControllers(Set<Class<? extends JspController_Abs>> p_controllers)
			throws ServletException, InstantiationException, IllegalAccessException {
		for (final Class<? extends JspController_Abs> v_controller : p_controllers)
			_controllers.put(buildControllerName(v_controller), v_controller);
	}

	/**
	 * Activate all controllers with application beans.
	 * 
	 * @param p_request the http request.
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 */
	private void activateApplicationControllers(final HttpServletRequest p_request)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {

		if (_applicationControllers)
			return;

		for (Entry<String, Class<? extends JspController_Abs>> v_entry : _controllers.entrySet()) {
			final Class<? extends JspController_Abs> v_controller = v_entry.getValue();
			JspController_Abs v = v_controller.getDeclaredConstructor().newInstance();
			if (v.activateOnLoad())
				v.execute(p_request);
		}
		_applicationControllers = Boolean.TRUE;
	}

	/**
	 * Get the specific name of a sub controller. It will be the registration key of
	 * the controller for the main jsp controller (this class).
	 * 
	 * @param p_controller : the current controller to be registered.
	 * @return the name (key) of the current controller to be registered.
	 */
	private String buildControllerName(final Class<? extends JspController_Abs> p_controller) {
		String v_simpleName = p_controller.getSimpleName().toLowerCase();
		if (v_simpleName.endsWith(CONTROLER_SUFFIX))
			v_simpleName = v_simpleName.substring(0, v_simpleName.length() - CONTROLER_SUFFIX.length());
		return v_simpleName;
	}

	/**
	 * Find the controller to be activated from the specific request.
	 * 
	 * @param p_pathInfo : the path informations for the request, with the name of
	 *                   the targeted controller.
	 * @return the controller.
	 * @throws ServletException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 */
	private JspController_Abs findController(final String p_pathInfo)
			throws ServletException, InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		String v_controllerName = p_pathInfo.substring(1).toLowerCase();
		if (v_controllerName.endsWith(".xml") || v_controllerName.endsWith(".json"))
			v_controllerName = v_controllerName.substring(0, v_controllerName.lastIndexOf('.'));

		final Class<? extends JspController_Abs> v_controller = _controllers.get(v_controllerName);
		if (v_controller == null)
			throw new ServletException("Contrôleur non trouvé : " + v_controllerName);
		return v_controller.getDeclaredConstructor().newInstance();
	}

	/**
	 * Get all the parameters for the request (also with header parameters);
	 * 
	 * @param p_request : the specific request to be analyzed.
	 * @return a list of all parameters contained in the request (with header
	 *         parameters).
	 */
	@SuppressWarnings("unused")
	private Map<String, String> get_allRequestParams(final HttpServletRequest p_request) {
		final Map<String, String> v_params = new LinkedHashMap<>();
		final Enumeration<String> v_paramsNames = p_request.getParameterNames();
		final Enumeration<String> v_headerNames = p_request.getHeaderNames();

		while (v_paramsNames.hasMoreElements()) {
			final String v_paramName = v_paramsNames.nextElement();
			v_params.put(v_paramName, p_request.getParameter(v_paramName));
		}
		while (v_headerNames.hasMoreElements()) {
			final String v_headerName = v_headerNames.nextElement();
			v_params.put(v_headerName, p_request.getHeader(v_headerName));
		}
		return v_params;
	}

	/**
	 * Add the specific message bean to the request if necessary.
	 * 
	 * @param p_controller the controller with the messages.
	 * @param p_request    the http request.
	 */
	private void addControllerMessengerBeanToRequest(final JspController_Abs p_controller,
			final HttpServletRequest p_request) {
		if (!p_controller.get_messenger().get_messages().isEmpty())
			p_request.setAttribute(JspControllerServlet.CONTROLLER_MESSENGER_ATTRIBUT, p_controller.get_messenger());
	}

	/**
	 * Override the service methode for the servlet.
	 */
	@Override
	protected void service(HttpServletRequest p_request, HttpServletResponse p_response)
			throws ServletException, IOException {

		try {
			activateApplicationControllers(p_request);
			JspController_Abs v_controller = findController(p_request.getPathInfo());
			JspDispatcherEnum_Itf v_dispatcher = v_controller.execute(p_request);
			addControllerMessengerBeanToRequest(v_controller, p_request);
			dispatchRequest(p_request, p_response, v_dispatcher);
		} catch (Exception p_e) {
			throw new ServletException(p_e.getMessage());
		}
	}

	/**
	 * Externalization for the last step as this method may by complexified in the
	 * future. For now we just check the response has not been committed by a
	 * redirect from any filter (for example). If committed, nothing to do, let the
	 * redirection doing the job.
	 * 
	 * @param p_request    the http request.
	 * @param p_response   the http response.
	 * @param p_dispatcher the internal spi4j dispatcher.
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void dispatchRequest(final HttpServletRequest p_request, final HttpServletResponse p_response,
			final JspDispatcherEnum_Itf p_dispatcher) throws ServletException, IOException {
		if (!p_response.isCommitted()) {
			p_request.getRequestDispatcher(p_dispatcher.dispatch()).forward(p_request, p_response);
		}
	}
}
