/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import fr.spi4j.business.dto.AttributesNames_Itf;
import fr.spi4j.exception.Spi4jRuntimeException;

/**
 * Classe utilitaire pour des traitements sur les Identifiables.
 *
 * @author MINARM
 */
public abstract class IdentifiableUtil_Abs {
	private static final Collator c_collator = Collator.getInstance();
	static {
		c_collator.setStrength(Collator.PRIMARY);
	}

	/**
	 * Constructeur protected : pas d'instance.
	 */
	protected IdentifiableUtil_Abs() {
		super();
	}

	/**
	 * Recherche un identifiabel dans une collection (une liste par exemple) selon
	 * son identifiant.
	 *
	 * @param <T>          Type du Identifiable_Itf
	 * @param p_collection Collection de Identifiable_Itf
	 * @param p_id         Identifiant
	 * @return Le Identifiable_Itf si trouvé ou null sinon
	 */
	protected static <T extends Identifiable_Itf<?>> T findInCollectionByIdAbs(final Collection<T> p_collection,
			final Object p_id) {
		if (p_id == null) {
			throw new IllegalArgumentException("L'id en paramètre ne doit pas être null");
		}
		for (final T v_identifiable : p_collection) {
			if (v_identifiable != null && p_id.equals(v_identifiable.getId())) {
				return v_identifiable;
			}
		}
		// si non trouvé (ce return null est nécessaire pour spi4j-swing)
		return null;
	}

	/**
	 * Recherche une liste de Identifiables dans une collection (une liste par
	 * exemple) selon la valeur d'un attribut (clé fonctionnelle par exemple).
	 *
	 * @param <T>          Type du Identifiable
	 * @param p_collection Collection de Identifiable
	 * @param p_attribute  Attribut (obligatoire)
	 * @param p_value      Valeur de l'attribut (peut être null)
	 * @return Les Identifiables trouvés (ne peut pas être null)
	 */
	protected static <T extends Identifiable_Itf<?>> List<T> findInCollectionByAttributeAbs(
			final Collection<T> p_collection, final AttributesNames_Itf p_attribute, final Object p_value) {
		final List<T> v_result = new ArrayList<>();
		try {
			for (final T v_identifiable : p_collection) {
				final Method v_getterMethod = p_attribute.getGetterMethod();
				if (v_getterMethod == null) {
					throw new Spi4jRuntimeException(
							"Getter pour l'attribut " + p_attribute + " non trouvé dans " + p_attribute.getClass(),
							"Vérifier que le type de l'attribut correspond au type du Identifiable");
				}
				if (v_identifiable != null) {
					final Object v_value = v_getterMethod.invoke(v_identifiable);
					if (p_value == null && v_value == null || p_value != null && p_value.equals(v_value)) {
						v_result.add(v_identifiable);
					}
				}
			}
		} catch (final Exception v_ex) {
			throw new Spi4jRuntimeException(v_ex, v_ex.toString(), "???");
		}
		return v_result;
	}

	/**
	 * Recherche un et un seul Identifiable dans une collection (une liste par
	 * exemple) selon la valeur d'un attribut (clé fonctionnelle par exemple)
	 *
	 * @param <T>          Type du Identifiable
	 * @param p_collection Collection de Identifiable
	 * @param p_attribute  Attribut (obligatoire)
	 * @param p_value      Valeur de l'attribut (peut être null)
	 * @return Les Identifiables trouvés (ne peut pas être null)
	 */
	protected static <T extends Identifiable_Itf<?>> T findOneInCollectionByAttributeAbs(
			final Collection<T> p_collection, final AttributesNames_Itf p_attribute, final Object p_value) {
		final List<T> v_list = findInCollectionByAttributeAbs(p_collection, p_attribute, p_value);
		// un et un seul Identifiable doit correspondre à la valeur de l'attribut
		// recherchée
		if (v_list.size() > 1) {
			throw new Spi4jRuntimeException("Plusieurs enregistrements trouvés pour la valeur '" + p_value + '\'',
					"Vérifier les doublons dans les données");
		} else if (v_list.isEmpty()) {
			throw new Spi4jRuntimeException("Aucun enregistrement trouvé pour la valeur '" + p_value + '\'',
					"Vérifier la cohérence des données");
		}
		return v_list.get(0);
	}

	/**
	 * Recherche un Identifiable dans une collection (une liste par exemple) selon
	 * l'identifiant du Identifiable en paramètre.
	 *
	 * @param <T>            Type du Identifiable
	 * @param p_collection   Collection de Identifiable
	 * @param p_identifiable Identifiable contenant un identiant
	 * @return Le Identifiable si trouvé (Spi4jRuntimeException si non trouvé)
	 */
	protected static <T extends Identifiable_Itf<?>> T findInCollectionByIdAbs(final Collection<T> p_collection,
			final T p_identifiable) {
		if (p_identifiable == null) {
			throw new IllegalArgumentException("Le identifiable en paramètre ne doit pas être null");
		}
		return findInCollectionByIdAbs(p_collection, p_identifiable.getId());
	}

	/**
	 * Recherche les Identifiables dans une collection (une liste par exemple) selon
	 * l'identifiant du Identifiable en paramètre.
	 *
	 * @param <T>          Type du Identifiable
	 * @param <I>          Type de l'identifiant
	 * @param p_collection Collection de Identifiable
	 * @param p_listIds    Identifiable contenant un identiant
	 * @return Les Identifiables trouvés (ne peut pas être null)
	 */
	protected static <T extends Identifiable_Itf<I>, I> List<T> findInCollectionByListIdsAbs(
			final Collection<T> p_collection, final Collection<I> p_listIds) {
		final List<T> v_list = new ArrayList<>(p_listIds.size());
		if (!p_listIds.isEmpty()) {
			final Map<I, T> v_map = createMapByIdAbs(p_collection);
			for (final I v_id : p_listIds) {
				final T v_identifiable = v_map.get(v_id);
				if (v_identifiable == null) {
					throw new Spi4jRuntimeException(
							"Aucun enregistrement trouvé dans la liste pour l'id '" + v_id + '\'',
							"Vérifier la cohérence des données");
				}
				v_list.add(v_identifiable);
			}
		}
		return v_list;
	}

	/**
	 * A partir d'une collection de Identifiables, construit une liste avec les
	 * identifiants des Identifiables.
	 *
	 * @param <T>          Type du Identifiable
	 * @param <I>          Type de l'identifiant
	 * @param p_collection Collection de Identifiable
	 * @return Le Identifiable si trouvé (Spi4jRuntimeException si non trouvé)
	 */
	protected static <T extends Identifiable_Itf<I>, I> List<I> createListIdsAbs(final Collection<T> p_collection) {
		final List<I> v_list = new ArrayList<>(p_collection.size());
		for (final T v_identifiable : p_collection) {
			v_list.add(v_identifiable.getId());
		}
		return v_list;
	}

	/**
	 * A partir d'une collection de Identifiables, construit une map non ordonnée
	 * avec les identifiants comme clés et les Identifiables comme valeur.
	 *
	 * @param <T>          Type du Identifiable
	 * @param <I>          Type de l'identifiant
	 * @param p_collection Collection de Identifiable
	 * @return Le Identifiable si trouvé (Spi4jRuntimeException si non trouvé)
	 */
	protected static <T extends Identifiable_Itf<I>, I> Map<I, T> createMapByIdAbs(final Collection<T> p_collection) {
		// map non ordonnée
		final Map<I, T> v_map = new HashMap<>(p_collection.size());
		for (final T v_identifiable : p_collection) {
			v_map.put(v_identifiable.getId(), v_identifiable);
		}
		return v_map;
	}

	/**
	 * A partir d'une collection de Identifiables, construit une map ordonnée avec
	 * les identifiants comme clés et les Identifiables comme valeur.
	 * <p>
	 * Si la collection est ordonnée la map conserve cet ordre.
	 *
	 * @param <T>          Type du Identifiable
	 * @param <I>          Type de l'identifiant
	 * @param p_collection Collection de Identifiable
	 * @return Le Identifiable si trouvé (Spi4jRuntimeException si non trouvé)
	 */
	protected static <T extends Identifiable_Itf<I>, I> Map<I, T> createSortedMapByIdAbs(
			final Collection<T> p_collection) {
		// map ordonnée pour conserver l'ordre s'il y en avait un dans la collection en
		// paramètre (si c'est une liste par exemple)
		final Map<I, T> v_map = new LinkedHashMap<>(p_collection.size());
		for (final T v_identifiable : p_collection) {
			v_map.put(v_identifiable.getId(), v_identifiable);
		}
		return v_map;
	}

	/**
	 * Tri une liste selon un attribut en paramètre. La liste en paramètre est
	 * directement modifiée pour être triée.
	 *
	 * @param <T>         Type du Identifiable
	 * @param p_list      Liste de Identifiables
	 * @param p_attribute Attribut de tri
	 */
	protected static <T extends Identifiable_Itf<?>> void sortListByAttributeAbs(final List<T> p_list,
			final AttributesNames_Itf p_attribute) {
		sortListByAttributeAbs(p_list, p_attribute, true, true);
	}

	/**
	 * Tri une liste selon un attribut en paramètre. La liste en paramètre est
	 * directement modifiée pour être triée.
	 *
	 * @param <T>          Type du Identifiable
	 * @param p_list       Liste de Identifiables
	 * @param p_attribute  Attribut de tri
	 * @param p_ascending  Ascendant ou descendant
	 * @param p_ignoreCase Ignore la casse
	 */
	protected static <T extends Identifiable_Itf<?>> void sortListByAttributeAbs(final List<T> p_list,
			final AttributesNames_Itf p_attribute, final boolean p_ascending, final boolean p_ignoreCase) {
		Comparator<T> v_comparator = new Comparator<>() {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public int compare(final T p_identifiable1, final T p_identifiable2) {
				final Object v_value1 = getAttributeValueAbs(p_identifiable1, p_attribute);
				final Object v_value2 = getAttributeValueAbs(p_identifiable2, p_attribute);
				if (v_value1 == v_value2) {
					// si deux nulls ou deux ==
					return 0;
				} else if (v_value1 == null) {
					// null est avant tout le reste (v_value2 ne peut pas être null ici)
					return -1;
				} else if (v_value2 == null) {
					return 1;
				} else if (p_ignoreCase && v_value1 instanceof String && v_value2.getClass() == v_value1.getClass()) {
					// on teste String avant Comparable pour pouvoir utiliser collator ou
					// compareToIgnoreCase
					// si on ignore la casse, alors on ignore aussi les accents donc on utilise un
					// collator au lieu de String.compareToIgnoreCase
					return c_collator.compare((String) v_value1, (String) v_value2);
					// return ((String) v_value1).compareToIgnoreCase((String) v_value2);
				} else if (v_value1 instanceof Comparable && v_value2.getClass() == v_value1.getClass()) {
					// Integer, Long, Date, String et autres sont Comparables
					return ((Comparable) v_value1).compareTo(v_value2);
				} else {
					// si non Comparable, au pire on compare selon toString()
					if (p_ignoreCase) {
						// return v_value1.toString().compareToIgnoreCase(v_value2.toString());
						// si on ignore la casse, alors on ignore aussi les accents donc on utilise un
						// collator au lieu de String.compareToIgnoreCase
						return c_collator.compare(v_value1.toString(), v_value2.toString());
					} else {
						return v_value1.toString().compareTo(v_value2.toString());
					}
				}
			}
		};
		if (!p_ascending) {
			v_comparator = Collections.reverseOrder(v_comparator);
		}
		Collections.sort(p_list, v_comparator);
	}

	/**
	 * Retourne la valeur d'un attribut dans un Identifiable.
	 *
	 * @param p_identifiable Identifiable
	 * @param p_attribute    Attribut
	 * @return Valeur
	 */
	protected static Object getAttributeValueAbs(final Identifiable_Itf<?> p_identifiable,
			final AttributesNames_Itf p_attribute) {
		final Method v_method = p_attribute.getGetterMethod();
		if (v_method == null) {
			throw new Spi4jRuntimeException(
					"Getter pour l'attribut " + p_attribute + " non trouvé dans " + p_attribute.getClass(),
					"Vérifier que le type de l'attribut correspond au type du Identifiable");
		}
		try {
			return v_method.invoke(p_identifiable);
		} catch (final Exception v_ex) {
			throw new Spi4jRuntimeException(v_ex, v_ex.toString(), "???");
		}
	}

	/**
	 * Définit la valeur d'un attribut dans un Identifiable.
	 *
	 * @param p_identifiable Identifiable
	 * @param p_attribute    Attribut
	 * @param p_value        Valeur
	 */
	protected static void setAttributeValueAbs(final Identifiable_Itf<?> p_identifiable,
			final AttributesNames_Itf p_attribute, final Object p_value) {
		final Method v_method = p_attribute.getSetterMethod();
		if (v_method == null) {
			throw new Spi4jRuntimeException(
					"Setter pour l'attribut " + p_attribute + " non trouvé dans " + p_attribute.getClass(),
					"Vérifier que le type de l'attribut correspond au type du Identifiable");
		}
		try {
			v_method.invoke(p_identifiable, p_value);
		} catch (final Exception v_ex) {
			throw new Spi4jRuntimeException(v_ex, v_ex.toString(), "???");
		}
	}

	/**
	 * Clone/Copie complète d'un Identifiable, y compris des références telles que
	 * des listes.
	 *
	 * @param <T>            Type du Identifiable
	 * @param p_identifiable Identifiable
	 * @return Le Identifiable cloné
	 */
	protected static <T extends Identifiable_Itf<?>> T deepCloneAbs(final T p_identifiable) {
		return SerializationHelper.deepClone(p_identifiable);
	}

	/**
	 * Clone/Copie d'un Identifiable, sans cloner les références et même si le
	 * Identifiable n'implémente pas Cloneable.
	 *
	 * @param <T>            Type du Identifiable
	 * @param p_identifiable Identifiable
	 * @return Le Identifiable cloné
	 */
	@SuppressWarnings("unchecked")
	protected static <T extends Identifiable_Itf<?>> T cloneAbs(final T p_identifiable) {
		try {
			final Class<T> v_identifiableClass = (Class<T>) p_identifiable.getClass();
			// la classe du Identifiable doit avoir un constructeur public sans paramètre
			final T v_identifiable = v_identifiableClass.getDeclaredConstructor().newInstance();
			Class<?> v_class = v_identifiableClass;
			while (v_class != Object.class) {
				for (final Field v_field : v_class.getDeclaredFields()) {
					// si le champ est static, on ne le copie pas
					if (!Modifier.isStatic(v_field.getModifiers())) {
						v_field.setAccessible(true);
						v_field.set(v_identifiable, v_field.get(p_identifiable));
					}
				}
				v_class = v_class.getSuperclass();
			}
			return v_identifiable;
		} catch (final Exception v_e) {
			throw new IllegalArgumentException(v_e);
		}
	}

	/**
	 * Vérifie qu'un champ de Identifiable ou Entity est bien renseigné.
	 *
	 * @param p_nomChamp        le nom fonctionnel du champ
	 * @param p_valeur          la valeur du champ
	 * @param p_champsInvalides la liste des champs déjà invalides ou null si aucun
	 *                          champ invalide
	 * @return la liste des champs invalides + ce dernier si celui-ci est null
	 */
	protected static List<String> checkMandatoryFieldAbs(final String p_nomChamp, final Object p_valeur,
			final List<String> p_champsInvalides) {
		List<String> v_champsInvalides = p_champsInvalides;
		if (p_valeur == null) {
			if (v_champsInvalides == null) {
				v_champsInvalides = new ArrayList<>();
			}
			v_champsInvalides.add(p_nomChamp);
		}
		return v_champsInvalides;
	}

	/**
	 * Vérifie qu'un champ de Identifiable ou Entity n'est pas trop grand.
	 *
	 * @param p_nomChamp        le nom fonctionnel du champ
	 * @param p_valeur          la valeur du champ
	 * @param p_maxSize         la taille maximale du champ (définie dans
	 *                          AttributesEnum ou ColumnsEnum)
	 * @param p_champsInvalides la liste des champs déjà invalides ou null si aucun
	 *                          champ invalide
	 * @return la liste des champs invalides + ce dernier si celui-ci est null
	 */
	protected static List<String> checkFieldSizeAbs(final String p_nomChamp, final String p_valeur, final int p_maxSize,
			final List<String> p_champsInvalides) {
		List<String> v_champsInvalides = p_champsInvalides;
		if (p_valeur != null && p_valeur.length() > p_maxSize) {
			if (v_champsInvalides == null) {
				v_champsInvalides = new ArrayList<>();
			}
			v_champsInvalides.add(p_nomChamp + " (" + p_valeur.length() + " > " + p_maxSize + ")");
		}
		return v_champsInvalides;
	}
}
