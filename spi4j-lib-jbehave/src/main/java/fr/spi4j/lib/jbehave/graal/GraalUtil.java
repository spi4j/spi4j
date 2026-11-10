/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.jbehave.graal;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;

import fr.spi4j.exception.Spi4jRuntimeException;
import fr.spi4j.ui.HasBoolean_Itf;
import fr.spi4j.ui.HasDate_Itf;
import fr.spi4j.ui.HasDouble_Itf;
import fr.spi4j.ui.HasInteger_Itf;
import fr.spi4j.ui.HasList_Itf;
import fr.spi4j.ui.HasMultipleSelection_Itf;
import fr.spi4j.ui.HasSelection_Itf;
import fr.spi4j.ui.HasString_Itf;
import fr.spi4j.ui.HasValue_Itf;
import fr.spi4j.ui.graal.Field;
import fr.spi4j.ui.graal.UserAction;
import fr.spi4j.ui.graal.UserView;
import fr.spi4j.ui.mvp.Presenter_Abs;
import fr.spi4j.ui.mvp.View_Itf;

/**
 * Classe util Graal utilisée dans les steps JBehave.
 * 
 * @author MINARM
 */
public class GraalUtil {

	/**
	 * Change la valeur du champ <code>p_Champ</code> de la vue <code>p_View</code>.
	 * Le nom du champ est le nom Graal.
	 * 
	 * @param p_presenter le présenteur
	 * @param p_Champ     le nom Graal du champ
	 * @param p_valeur    la valeur du champ
	 */
	public void setFieldValue(final Presenter_Abs<? extends View_Itf, ?> p_presenter, final String p_Champ,
			final Object p_valeur) {
		// on recherche le getter qui porte l'annotation @Field dans la classe
		// de la vue
		final Class<? extends View_Itf> v_class = p_presenter.getView().getClass(); // la classe de la vue

		// on recherche toutes les méthodes héritées ou non de la classe
		// final Iterator<Method> v_allMethods =
		// SpiReflectIterators.allMethodsOf(v_class);

		// on recherche d'abord le getter, on en déduira le setter
		final Method v_getter = findFieldGetter(v_class, p_Champ, true);

		if (HasValue_Itf.class.isAssignableFrom(v_getter.getReturnType())) {
			final HasValue_Itf<?> v_HasValue_Itf = doGetFieldValue(p_presenter, v_getter);
			if (v_HasValue_Itf == null) {
				throw new NullPointerException(
						"Le champ \"" + p_Champ + "\" de type \"" + v_getter.getReturnType().getName() + "\" est null");
			}
			doSetFieldValue(v_HasValue_Itf, p_Champ, p_valeur);
		} else {
			throw new GraalException("Le champ \"" + p_Champ + "\" n'est pas un HasValue_Itf");
		}
	}

	/**
	 * Remplit le composant HasValue avec la valeur.
	 * 
	 * @param p_HasValue_Itf le composant HasValue
	 * @param p_champ        le champ
	 * @param p_valeur       la valeur à mettre dans le composant
	 */
	protected void doSetFieldValue(final HasValue_Itf<?> p_HasValue_Itf, final String p_champ, final Object p_valeur) {
		if (p_HasValue_Itf instanceof HasBoolean_Itf) {
			final Boolean v_value = parseBoolean(p_valeur.toString());
			((HasBoolean_Itf) p_HasValue_Itf).setValue(v_value);
		} else if (p_HasValue_Itf instanceof HasDouble_Itf) {
			final Double v_double = parseDouble(p_champ, p_valeur.toString());
			((HasDouble_Itf) p_HasValue_Itf).setValue(v_double);
		} else if (p_HasValue_Itf instanceof HasDate_Itf) {
			final Date v_date = parseDate(p_champ, p_valeur.toString());
			((HasDate_Itf) p_HasValue_Itf).setValue(v_date);
		} else if (p_HasValue_Itf instanceof HasInteger_Itf) {
			final Integer v_int = parseInteger(p_champ, p_valeur.toString());
			((HasInteger_Itf) p_HasValue_Itf).setValue(v_int);
		} else if (p_HasValue_Itf instanceof HasString_Itf) {
			((HasString_Itf) p_HasValue_Itf).setValue(p_valeur.toString());
		} else {
			try {
				@SuppressWarnings("unchecked")
				final HasValue_Itf<Object> v_HasValue = (HasValue_Itf<Object>) p_HasValue_Itf;
				v_HasValue.setValue(p_valeur);
			} catch (final ClassCastException v_e) {
				throw new GraalException("Impossible de remplir le champ " + p_champ + " avec la valeur " + p_valeur
						+ " : Type du champ indéterminé (" + v_e.getMessage() + ")");
			}
		}
	}

	/**
	 * Parse un booléen d'une story.
	 * 
	 * @param p_valeur String
	 * @return Boolean
	 */
	private Boolean parseBoolean(final String p_valeur) {
		return p_valeur.equalsIgnoreCase("true") || p_valeur.equalsIgnoreCase("1");
	}

	/**
	 * Parse un booléen d'une story.
	 * 
	 * @param p_champ  Nom du champ
	 * @param p_valeur String
	 * @return Boolean
	 */
	private Double parseDouble(final String p_champ, final String p_valeur) {
		try {
			return Double.parseDouble(p_valeur);
		} catch (final NumberFormatException v_e) {
			throw new Spi4jRuntimeException(v_e,
					"Impossible de parser le double " + p_valeur + " pour le champ \"" + p_champ + "\"",
					"Vérifier que c'est bien un double");
		}
	}

	/**
	 * Parse un nombre décimal d'une story.
	 * 
	 * @param p_champ  Nom du champ
	 * @param p_valeur String
	 * @return Boolean
	 */
	private Date parseDate(final String p_champ, final String p_valeur) {
		final String v_dateFormat = "dd/MM/yyyy";
		final SimpleDateFormat v_sdf = new SimpleDateFormat(v_dateFormat);
		try {
			return v_sdf.parse(p_valeur);
		} catch (final ParseException v_e) {
			throw new Spi4jRuntimeException(v_e,
					"Impossible de parser la date " + p_valeur + " pour le champ \"" + p_champ + "\"",
					"Vérifier que la date est bien au format " + v_dateFormat);
		}
	}

	/**
	 * Parse un nombre entier d'une story.
	 * 
	 * @param p_champ  Nom du champ
	 * @param p_valeur String
	 * @return Boolean
	 */
	private Integer parseInteger(final String p_champ, final String p_valeur) {
		try {
			return Integer.parseInt(p_valeur);
		} catch (final NumberFormatException v_e) {
			throw new Spi4jRuntimeException(v_e,
					"Impossible de parser l'entier " + p_valeur + " pour le champ \"" + p_champ + "\"",
					"Vérifier que c'est bien un entier");
		}
	}

	/**
	 * Change la valeur de la liste <code>p_liste</code> de la vue
	 * <code>p_View</code>. Le nom du champ est le nom Graal.
	 * 
	 * @param p_presenter le présenteur
	 * @param p_liste     le nom Graal de la liste
	 * @param p_valeurs   les valeurs de la liste
	 */
	public void setSelectedValueInList(final Presenter_Abs<? extends View_Itf, ?> p_presenter, final String p_liste,
			final Object... p_valeurs) {
		// on recherche le getter qui porte l'annotation @Field dans la classe
		// de la vue
		final Class<? extends View_Itf> v_class = p_presenter.getView().getClass(); // la classe de la vue

		// on recherche d'abord le getter, on en déduira le setter
		final Method v_getter = findFieldGetter(v_class, p_liste, true);

		if (HasList_Itf.class.isAssignableFrom(v_getter.getReturnType())) {
			if (HasSelection_Itf.class.isAssignableFrom(v_getter.getReturnType()) && p_valeurs.length > 1) {
				// plusieurs valeurs à sélectionner mais composant à sélection unique
				throw new GraalException("La liste " + p_liste + " de l'écran " + p_presenter.getClass().getSimpleName()
						+ " est une liste à sélection unique et ne peut pas prendre plusieurs objets en paramètre");
			}
			final HasList_Itf<Object> v_HasList_Itf = doGetFieldValue(p_presenter, v_getter);
			if (v_HasList_Itf == null) {
				final String v_message = "Le champ \"" + p_liste + "\" de type \"" + v_getter.getReturnType().getName()
						+ "\" est null";
				fail(v_message);
				throw new NullPointerException(v_message);
			}
			final List<Object> v_values = parseValues(v_HasList_Itf, p_liste, p_valeurs);
			if (HasSelection_Itf.class.isAssignableFrom(v_getter.getReturnType()) && v_values.size() == 1) {
				// positionnement de la valeur trouvée dans le HasSelection
				((HasSelection_Itf<Object>) v_HasList_Itf).setValue(v_values.get(0));
			} else if (HasMultipleSelection_Itf.class.isAssignableFrom(v_getter.getReturnType())) {
				((HasMultipleSelection_Itf<Object>) v_HasList_Itf).setValue(v_values);
			} else {
				fail("Le type de liste n'est pas connu pour " + p_liste
						+ " (le type retourné doit être de type HasList_Itf)");
			}
		} else {
			throw new GraalException("Le champ \"" + p_liste + "\" n'est pas un HasList_Itf");
		}
	}

	/**
	 * Parse un booléen d'une story.
	 * 
	 * @param p_HasList_Itf Conteneur de la liste des valeurs possibles
	 * @param p_liste       le nom Graal de la liste
	 * @param p_valeurs     les valeurs à parser de la liste
	 * @return List<Object>
	 */
	private List<Object> parseValues(final HasList_Itf<Object> p_HasList_Itf, final String p_liste,
			final Object... p_valeurs) {
		final List<Object> v_findValues = new ArrayList<>();
		final List<Object> v_items = p_HasList_Itf.getList();
		for (final Object v_valeur : p_valeurs) {
			boolean v_found = false;
			if (v_items != null) {
				final String v_valeurAsString = v_valeur.toString();
				for (final Object v_item : v_items) {
					if (v_item.toString().equals(v_valeurAsString)) {
						v_findValues.add(v_item);
						v_found = true;
						break;
					}
				}
			}
			if (!v_found) {
				String v_possibleValues = "";
				if (v_items != null) {
					v_possibleValues = Arrays.toString(v_items.toArray());
				}
				assertTrue(v_found, "La liste \"" + p_liste + "\" n'a pas pu être remplie avec la valeur " + v_valeur
						+ " (valeurs possibles : " + v_possibleValues + ")");
			}
		}
		return v_findValues;
	}

	/**
	 * Vérifie que la liste <code>p_liste</code> de la vue <code>p_View</code>
	 * contient les objets <code>p_objetss</code>. Le nom du champ est le nom Graal.
	 * 
	 * @param <T>         le type de valeur saisie dans le champ
	 * @param p_presenter le présenteur
	 * @param p_liste     le nom Graal de la liste
	 * @param p_objets    les objets de la liste
	 * @return true si la valeur a été trouvée, false sinon
	 */
	public <T> boolean listContainsObjects(final Presenter_Abs<? extends View_Itf, ?> p_presenter, final String p_liste,
			final Object... p_objets) {
		// on recherche le getter qui porte l'annotation @Field dans la classe
		// de la vue
		final Class<? extends View_Itf> v_class = p_presenter.getView().getClass(); // la classe de la vue

		// on recherche d'abord le getter, on en déduira le setter
		final Method v_getter = findFieldGetter(v_class, p_liste, true);

		if (HasList_Itf.class.isAssignableFrom(v_getter.getReturnType())) {
			final HasList_Itf<T> v_HasList_Itf = doGetFieldValue(p_presenter, v_getter);
			if (v_HasList_Itf == null) {
				final String v_message = "Le champ \"" + p_liste + "\" de type \"" + v_getter.getReturnType().getName()
						+ "\" est null";
				fail(v_message);
				throw new NullPointerException(v_message);
			}
			boolean v_found = true;
			for (final Object v_object : p_objets) {
				boolean v_valeurFound = false;
				for (final T v_item : v_HasList_Itf.getList()) {
					if (v_item.toString().equals(v_object.toString())) {
						v_valeurFound = true;
						break;
					}
				}
				v_found &= v_valeurFound;
			}
			return v_found;
		} else {
			throw new GraalException("Le champ \"" + p_liste + "\" n'est pas un HasList_Itf");
		}
	}

	/**
	 * Retourne la valeur du champ <code>p_Champ</code> de la vue
	 * <code>p_View</code>. Le nom du champ est le nom au sens fonctionnel.
	 * 
	 * @param p_presenter le présenteur
	 * @param p_Champ     le nom Graal du champ
	 * @return la valeur du champ de la vue passé en paramètre dont le nom Graal est
	 *         passé en paramètre
	 */
	@SuppressWarnings("unchecked")
	public Object getFieldValue(final Presenter_Abs<? extends View_Itf, ?> p_presenter, final String p_Champ) {
		// on recherche le getter qui porte l'annotation @Field dans la classe
		// de la vue
		final Class<? extends View_Itf> v_class = p_presenter.getView().getClass(); // la classe de la vue

		final Method v_getter = findFieldGetter(v_class, p_Champ, true);

		Object v_value = doGetFieldValue(p_presenter, v_getter);

		// try to unbox HasValue_Itf
		if (v_value instanceof HasValue_Itf<?>) {
			v_value = ((HasValue_Itf<Object>) v_value).getValue();
		}
		return v_value;
	}

	/**
	 * Récupère la valeur d'un champ dans une vue.
	 * 
	 * @param <T>         le type de valeur cherchée
	 * @param p_presenter le présenteur
	 * @param p_getter    le nom de la méthode correspondant au champ
	 * @return la valeur du champ
	 */
	@SuppressWarnings("unchecked")
	protected <T> T doGetFieldValue(final Presenter_Abs<? extends View_Itf, ?> p_presenter, final Method p_getter) {
		// la valeur du champ
		T v_value = null;

		try {
			// ... on invoke le getter sur l'instance de vue passée en
			// paramètre
			v_value = (T) p_getter.invoke(p_presenter.getView());
		} catch (final Exception v_ex) {
			LogManager.getLogger(getClass()).error(v_ex);
			fail(v_ex.toString());
		}
		return v_value;
	}

	/**
	 * Recherche le getter annoté, dans cette classe et récursivement dans les
	 * classes ancêtres.
	 * 
	 * @param p_class               la classe dans laquelle rechercher
	 * @param p_Champ               le nom du champ
	 * @param p_exceptionIfNotFound true s'il faut lancer une exception si le getter
	 *                              n'a pas été trouvé
	 * @return le getter
	 */
	protected Method findFieldGetter(final Class<?> p_class, final String p_Champ,
			final boolean p_exceptionIfNotFound) {
		// recherche dans la classe courante
		Method v_method = findFieldGetterInClass(p_class, p_Champ);

		// recherche dans les interfaces
		if (v_method == null) {
			for (final Class<?> v_clazz : p_class.getInterfaces()) {
				v_method = findFieldGetter(v_clazz, p_Champ, false);
				if (v_method != null) {
					break;
				}
			}
		}

		// recherche dans la classe ancêtre
		if (v_method == null && p_class.getSuperclass() != null) {
			v_method = findFieldGetter(p_class.getSuperclass(), p_Champ, false);
		}

		// si la méthode avec l'annotation @Field n'a pas été trouvée, on lance une
		// exception
		if (v_method == null && p_exceptionIfNotFound) {
			throw new FieldNotFoundGraalException(p_class, p_Champ);
		}

		return v_method;
	}

	/**
	 * Recherche le getter annoté seulement dans la classe passée en paramètre.
	 * 
	 * @param p_class la classe dans laquelle rechercher
	 * @param p_Champ le nom du champ
	 * @return le getter
	 */
	protected Method findFieldGetterInClass(final Class<?> p_class, final String p_Champ) {
		for (final Method v_method : p_class.getMethods()) {
			// on cherche l'annotation @Field sur la méthode courante
			final Field v_Field = v_method.getAnnotation(Field.class);

			// si on la trouve ...
			if (v_Field != null && v_Field.value().equals(p_Champ)) {
				// ... alors la méthode retournée est la méthode courante
				return v_method;
			}
		}
		return null;
	}

	/**
	 * Retourne le nom Graal de la vue passée en paramètre. Le nom Graal est porté
	 * par l'annotation {@link UserView}.
	 * 
	 * @param p_presenter le présenteur
	 * @return le nom Graal de la vue, porté par l'annotation {@link UserView}
	 */
	public String getUserViewName(final Presenter_Abs<? extends View_Itf, ?> p_presenter) {
		// On récupère l'interface de la vue
		final Class<? extends View_Itf> v_class = p_presenter.getView().getClass();
		return getUserViewName(v_class);
	}

	/**
	 * Retourne le nom Graal d'une vue.
	 * 
	 * @param p_viewClass la classe de la vue.
	 * @return le nom de la vue
	 */
	public String getUserViewName(final Class<? extends View_Itf> p_viewClass) {
		UserView v_Field = null;
		v_Field = p_viewClass.getAnnotation(UserView.class);
		final Class<?>[] v_interfaces = p_viewClass.getInterfaces();
		for (int v_itfIndex = 0; v_itfIndex < v_interfaces.length && v_Field == null; v_itfIndex++) {
			final Class<?> v_currentItf = v_interfaces[v_itfIndex];
			v_Field = v_currentItf.getAnnotation(UserView.class);
		}

		if (v_Field == null || "".equals(v_Field.value())) {
			throw new UserViewNotFoundGraalException(p_viewClass);
		}

		return v_Field.value();
	}

	/**
	 * Retourne une méthode annotée par une @UserAction.
	 * 
	 * @param p_class      la classe qui devrait contenir la méthode annotée
	 * @param p_userAction le nom de l'action
	 * @return {@link Method}
	 */
	protected Method getUserActionMethod(final Class<?> p_class, final String p_userAction) {
		// on parcours les méthodes de la classe
		for (final Method v_method : p_class.getDeclaredMethods()) {
			// on cherche l'annotation @UserAction sur la méthode courante
			final UserAction v_userAction = v_method.getAnnotation(UserAction.class);

			// si on la trouve grace à son annotation ...
			if (v_userAction != null && v_userAction.value().equals(p_userAction)) {
				return v_method;
			}
		}
		return null;
	}

	/**
	 * Execute la méthode portant le nom Graal passé en paramètre. Le nom Graal est
	 * porté par l'annotation {@link UserAction}
	 * 
	 * @param p_Object     l'objet qui porte la {@link UserAction}
	 * @param p_userAction le nom Graal de la {@link UserAction}
	 */
	public void callUserAction(final Presenter_Abs<? extends View_Itf, ?> p_Object, final String p_userAction) {
		// Va chercher la méthode en parametre dans l'interface
		Method v_method = null;

		for (Class<?> v_class = p_Object.getClass(); v_method == null
				&& v_class.getSuperclass() != null; v_class = v_class.getSuperclass()) {
			v_method = getUserActionMethod(v_class, p_userAction);

			// on recherche par raport au class/interfaces parent
			final Class<?>[] v_interfaces = v_class.getInterfaces();
			for (int v_itfIndex = 0; v_itfIndex < v_interfaces.length && v_method == null; v_itfIndex++) {
				final Class<?> v_currentItf = v_interfaces[v_itfIndex];
				v_method = getUserActionMethod(v_currentItf, p_userAction);
			}
		}

		// si le v_method n'est pas null, on va invoquer la méthode
		/**
		 * Object invoke(Object obj, Object[] args) Invokes the underlying method
		 * represented by this Method object, on the specified object with the specified
		 * parameters.
		 */
		// si on trouve un getter ...
		if (v_method != null) {
			// ... on invoke le getter sur l'instance de vue passée en paramètre
			try {
				v_method.setAccessible(true);
				v_method.invoke(p_Object);
			} catch (final Exception v_err) {
				if (v_err.getCause() != null) {
					LogManager.getLogger(getClass()).error(v_err.getCause());
					fail(v_err.getCause().toString());
				} else {
					LogManager.getLogger(getClass()).error(v_err);
					fail(v_err.toString());
				}
			}
		} else {
			@SuppressWarnings("unchecked")
			final Class<? extends Presenter_Abs<? extends View_Itf, ?>> v_presenterClass = (Class<? extends Presenter_Abs<? extends View_Itf, ?>>) p_Object
					.getClass();
			throw new UserActionNotFoundGraalException(v_presenterClass, p_userAction);
		}
	}

	/**
	 * Retourne si les objets sont égaux en testant le type de l'objet récupéré
	 * 
	 * @param p_storyValue     valeur attendue passée par la story
	 * @param p_expectedObject objet récupéré dans l'applcation
	 * @return le résultat de la comparaison
	 */
	public boolean doComparaison(final String p_storyValue, final Object p_expectedObject) {
		boolean v_retour = false;
		final String v_expectedObjectStr = p_expectedObject.toString();
		final String v_storyValue = p_storyValue;
		if (p_expectedObject instanceof Date) {
			final SimpleDateFormat v_sdf = new SimpleDateFormat("dd/MM/yyyy");
			// v_expectedObjectStr = v_sdf.format((Date) p_expectedObject);
			try {
				final Date v_date = v_sdf.parse(v_storyValue);
				v_retour = v_date.equals(p_expectedObject);
			} catch (final ParseException v_e) {
				// la date n'a pas le bon format
				v_retour = false;
			}
		} else if (p_expectedObject instanceof Double) {
			final Double v_dble = Double.parseDouble(p_storyValue);
			v_retour = p_expectedObject.equals(v_dble);
		} else if (p_expectedObject instanceof Integer) {
			final Integer v_int = Integer.parseInt(p_storyValue);
			v_retour = p_expectedObject.equals(v_int);
		} else if (p_expectedObject instanceof Boolean) {
			final Boolean v_bool = Boolean.parseBoolean(p_storyValue);
			v_retour = p_expectedObject.equals(v_bool);
		} else if (p_expectedObject instanceof Long) {
			final Long v_long = Long.parseLong(p_storyValue);
			v_retour = p_expectedObject.equals(v_long);
		} else if (p_expectedObject instanceof Float) {
			final Float v_float = Float.parseFloat(p_storyValue);
			v_retour = p_expectedObject.equals(v_float);
		} else if (p_expectedObject.toString().matches("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}$")) {
			// Cas des mails, voire si on a une classe / méthode déjà présente
			v_retour = v_storyValue.equals(v_expectedObjectStr);
		} else {
			// Cas d'une comparaison de String
			v_retour = v_storyValue.equalsIgnoreCase(v_expectedObjectStr);
		}
		return v_retour;
	}

	/**
	 * Change la valeur de la liste <code>p_liste</code> de la vue
	 * <code>p_View</code> en chaine nulle. Le nom du champ est le nom Graal.
	 * 
	 * @param <T>         le type de valeur saisie dans le champ
	 * @param p_presenter le présenteur
	 * @param p_liste     le nom Graal de la liste
	 */
	public <T> void unsetValueInList(final Presenter_Abs<? extends View_Itf, ?> p_presenter, final String p_liste) {
		// on recherche le getter qui porte l'annotation @Field dans la classe
		// de la vue
		final Class<? extends View_Itf> v_class = p_presenter.getView().getClass(); // la classe de la vue

		// on recherche d'abord le getter, on en déduira le setter
		final Method v_getter = findFieldGetter(v_class, p_liste, true);

		if (HasSelection_Itf.class.isAssignableFrom(v_getter.getReturnType())) {
			final HasSelection_Itf<T> v_HasSelection_Itf = doGetFieldValue(p_presenter, v_getter);
			if (v_HasSelection_Itf == null) {
				throw new NullPointerException(
						"Le champ \"" + p_liste + "\" de type \"" + v_getter.getReturnType().getName() + "\" est null");
			}
			v_HasSelection_Itf.setValue(null);
		} else {
			throw new GraalException("Le champ \"" + p_liste + "\" n'est pas un HasSelection_Itf");
		}
	}

	/**
	 * Vérifie que la liste <code>p_liste</code> de la vue <code>p_View</code> ne
	 * contient aucune valeur. Le nom du champ est le nom Graal.
	 * 
	 * @param <T>         le type de valeur saisie dans le champ
	 * @param p_presenter le présenteur
	 * @param p_liste     le nom Graal de la liste
	 * @return true si la valeur a été trouvée, false sinon
	 */
	public <T> boolean listContainsNoValue(final Presenter_Abs<? extends View_Itf, ?> p_presenter,
			final String p_liste) {
		// on recherche le getter qui porte l'annotation @Field dans la classe
		// de la vue
		final Class<? extends View_Itf> v_class = p_presenter.getView().getClass(); // la classe de la vue

		// on recherche d'abord le getter, on en déduira le setter
		final Method v_getter = findFieldGetter(v_class, p_liste, true);

		if (HasSelection_Itf.class.isAssignableFrom(v_getter.getReturnType())) {
			final HasSelection_Itf<T> v_HasSelection_Itf = doGetFieldValue(p_presenter, v_getter);
			if (v_HasSelection_Itf == null) {
				throw new NullPointerException("Le champ \"" + p_liste + "\" de type \""
						+ v_getter.getReturnType().getName() + "\" n'est pas null");
			}
			return v_HasSelection_Itf.getValue() == null;
		} else {
			throw new GraalException("Le champ \"" + p_liste + "\" n'est pas un HasSelection_Itf");
		}
	}

}
