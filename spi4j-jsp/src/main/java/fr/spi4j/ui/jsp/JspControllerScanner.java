/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.jsp;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;

/**
 * Scan the target application for jsp controllers. Create a list of sub
 * controllers and send it to the main controller for registering.
 * 
 * @author MINARM
 */
final class JspControllerScanner {

	private static final String EXT_JAVA_WEB_ARCHIVE = ".war";
	private static final String EXT_JAVA_CLASS = ".class";
	private static final String CHAR_PACKAGE_SEPARATOR = ".";
	private static final String CHAR_SEPARATOR_REPLACE = "\\\\";
	private static final String CHAR_SEPARATOR_2_REPLACE = "/";

	/**
	 * The target resource for scanning.
	 */
	private File _resourceToScan;

	/**
	 * Internal abstract class for class collectors. There are many implementations
	 * for manifolds, depending on whether the engine needs to progress a
	 * application already packaged (war only in our case) or an application still
	 * under development.
	 * <p>
	 * This class is not really useful but nevertheless allows to simplify the use
	 * of class collectors. To see in time if it is to be kept. We avoid using an
	 * annotation that does not provide a lot since we do not benefit from the
	 * injection. We just test that the class inherits from JspController_Abs.
	 */
	private abstract class ClassCollector {
		protected final Set<Class<? extends JspController_Abs>> _contextClasses = new HashSet<>();

		abstract Set<Class<? extends JspController_Abs>> findControllers() throws ServletException;

		@SuppressWarnings("unchecked")
		void collectController(final Class<?> p_contextClass) {
			// if (!p_contextClass.isInterface() &&
			// p_contextClass.isAnnotationPresent(JspController.class))
			// _contextClasses.add((Class<? extends JspController_Abs>) p_contextClass);
			if (!p_contextClass.isInterface() && JspController_Abs.class != p_contextClass
					&& JspController_Abs.class.isAssignableFrom(p_contextClass))
				_contextClasses.add((Class<? extends JspController_Abs>) p_contextClass);
		}
	}

	/**
	 * Main method for the class.
	 * <p>
	 * We launch the collection (scan) on the target application and we recover the
	 * whole classes. Then we place in the Set only the classes that are concerned
	 * by the annotation that interests us.
	 * <p>
	 * Previously, we must solve the correct manifold according to the state of the
	 * application (the application is still under development or the application
	 * already deployed in the form of .war. (For now only war in our case).
	 * 
	 * @return An unordered list of all the classes affected by the spi4j framework
	 *         (no duplicates).
	 * @throws ServletException
	 */
	Set<Class<? extends JspController_Abs>> scan(final ServletConfig p_config, final String p_dispatcher)
			throws ServletException {
		return getResourceToScan(p_config, p_dispatcher).resolve().findControllers();
	}

	/**
	 * Returns the correct class collector for the target application, depending on
	 * whether the application either an application under development or a
	 * application already deployed ('.war').
	 * 
	 * @return The right class collector for the project (target application).
	 */
	private ClassCollector resolve() {
		ClassCollector contextClassCollector = new SourceClassCollector();
		if (isArchive(_resourceToScan))
			contextClassCollector = new ArchiveClassCollector();
		return contextClassCollector;
	}

	/**
	 * We check if the target application is an already deployed application or a
	 * application still under development.
	 * <p>
	 * The application is considered deployed if it is indeed a file (and not a
	 * directory) and that this file has an archive type extension.
	 * 
	 * @param p_mainPackage :
	 * @return 'true' if the resource is an already deployed application.
	 */
	private boolean isArchive(final File p_mainPackage) {
		return (!p_mainPackage.isDirectory() && hasArchiveExtension(p_mainPackage));
	}

	/**
	 * Checking the resource extension.
	 * 
	 * @param p_mainPackage : the main package for the target application.
	 * @return 'true' if the resource has an archive type extension.
	 */
	private boolean hasArchiveExtension(final File p_mainPackage) {
		return p_mainPackage.getPath().endsWith(EXT_JAVA_WEB_ARCHIVE);
	}

	/**
	 * Recovery for the resource (deployed file or package) to start the scan the
	 * target application.
	 * 
	 * @throws ServletException
	 */
	private JspControllerScanner getResourceToScan(final ServletConfig p_config, final String p_dispatcher)
			throws ServletException {

		try {
			Class<?> dispatcher = Class.forName(p_dispatcher);
			_resourceToScan = new File(dispatcher.getProtectionDomain().getCodeSource().getLocation().getFile());
		} catch (ClassNotFoundException p_e) {
			throw new ServletException(
					"Impossible de récupérer la ressource à scanner pour le controlleur principal de JSP : ", p_e);
		}
		// TODO : Pas encore teste, de memoire cela ne suffit pas....
//		if (DependencyConfig.getInstance().isWebApplication() && !_resourceToScan.exists())
//			throw new ServletException("err");
		return this;
	}

	/**
	 * Internal class for the collector dedicated to already deployed applications.
	 * We retrieves a list of files, all files are stored under one directory.
	 * <p>
	 * As it is an inner class, it has access to instance variables of the scanner.
	 */
	private class ArchiveClassCollector extends ClassCollector {
		/**
		 * Main method for the course and the recovery of all the class of the target
		 * application.
		 */
		@Override
		public Set<Class<? extends JspController_Abs>> findControllers() throws ServletException {
			try (JarFile v_jarFile = new JarFile(_resourceToScan)) {
				collect(v_jarFile.entries());
			} catch (IOException | ClassNotFoundException p_e) {
				throw new ServletException("Impossible de charger un controleur JSP : ", p_e);
			}
			return _contextClasses;
		}

		/**
		 * Scanning method for the collector.
		 * 
		 * @param p_entries : the list of entries for the packaged application.
		 * @throws ClassNotFoundException
		 * @throws ServletException
		 */
		private void collect(final Enumeration<JarEntry> p_entries) throws ClassNotFoundException {
			while (p_entries.hasMoreElements()) {
				JarEntry v_jarEntry = p_entries.nextElement();
				if (v_jarEntry.getName().endsWith(EXT_JAVA_CLASS)) {
					addToContext(v_jarEntry);
				}
			}
		}

		/**
		 * Add the class to the list if the class is a jsp controller.
		 * 
		 * @param jarEntry : a specific entry for the packaged application.
		 * @throws ClassNotFoundException
		 * @throws ServletException
		 */
		private void addToContext(final JarEntry p_jarEntry) throws ClassNotFoundException {

			collectController(Class.forName(p_jarEntry.getName().replace(EXT_JAVA_CLASS, "")
					.replaceAll(CHAR_SEPARATOR_REPLACE, CHAR_PACKAGE_SEPARATOR)
					.replaceAll(CHAR_SEPARATOR_2_REPLACE, CHAR_PACKAGE_SEPARATOR)));

		}
	}

	/**
	 * Internal class for the collector dedicated to applications that are in the
	 * process of being development. We retrieve a list of directories (list of
	 * packages) and we browse all the directories to retrieve the list of classes.
	 * <p>
	 * As it is an inner class, it has access to instance variables of the scanner.
	 */
	private class SourceClassCollector extends ClassCollector {
		/**
		 * Main method for the course and the recovery of all the class of the target
		 * application.
		 */
		@Override
		public Set<Class<? extends JspController_Abs>> findControllers() throws ServletException {
			for (File v_innerFile : _resourceToScan.listFiles()) {
				try {
					collect(v_innerFile, "");
				} catch (ClassNotFoundException p_e) {
					throw new ServletException("Impossible de charger un controleur JSP : ", p_e);
				}
			}
			return _contextClasses;
		}

		/**
		 * Scanning method for the collector.
		 * 
		 * @param file        : the current scanned resource.
		 * @param packageName : the name for the current scanned package.
		 * @throws ClassNotFoundException
		 * @throws ServletException
		 */
		private void collect(final File p_file, String p_packageName) throws ClassNotFoundException {
			if (p_file.isDirectory()) {
				p_packageName += p_file.getName() + CHAR_PACKAGE_SEPARATOR;
				for (File v_innerFile : p_file.listFiles()) {
					collect(v_innerFile, p_packageName);
				}
			} else if (p_file.getName().endsWith(EXT_JAVA_CLASS)) {
				addToContext(p_file, p_packageName);
			}
		}

		/**
		 * Add the class to the list if the class is a jsp controller.
		 * 
		 * @param file        : the current scanned resource.
		 * @param packageName : the name for the current scanned package.
		 * @throws ClassNotFoundException
		 * @throws ServletException
		 */
		private void addToContext(final File p_file, final String p_packageName) throws ClassNotFoundException {
			collectController(Class.forName(p_packageName + p_file.getName().replace(EXT_JAVA_CLASS, "")));
		}
	}
}
