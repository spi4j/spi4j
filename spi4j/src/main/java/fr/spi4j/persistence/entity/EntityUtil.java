/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.persistence.entity;

import java.lang.reflect.Method;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import fr.spi4j.SerializationHelper;
import fr.spi4j.exception.Spi4jRuntimeException;

/**
 * Classe utilitaire pour des traitements sur les Entitys.
 *
 * @author MINARM
 */
public final class EntityUtil
// extends IdentifiableUtil_Abs
{
	private static final Collator c_collator = Collator.getInstance();
	static {
		c_collator.setStrength(Collator.PRIMARY);
	}

	/**
	 * Constructeur privé : pas d'instance.
	 */
	private EntityUtil() {
		super();
	}

	/**
	 * Recherche un Entity dans une collection (une liste par exemple) selon son
	 * identifiant.
	 *
	 * @param <T>          Type du Entity
	 * @param p_collection Collection de Entity
	 * @param p_id         Identifiant
	 * @return Le Entity si trouvé ou null sinon
	 */
	public static <T extends Entity_Itf<?>> T findInCollectionById(final Collection<T> p_collection,
			final Object p_id) {
		if (p_id == null) {
			throw new IllegalArgumentException("L'id en paramètre ne doit pas être null");
		}
		for (final T v_entity : p_collection) {
			if (v_entity != null && p_id.equals(v_entity.getId())) {
				return v_entity;
			}
		}
		// si non trouvé (ce return null est nécessaire pour spi4j-swing)
		return null;

	}

	/**
	 * Recherche une liste de Entitys dans une collection (une liste par exemple)
	 * selon la valeur d'un attribut (clé fonctionnelle par exemple).
	 *
	 * @param <T>          Type du Entity
	 * @param p_collection Collection de Entity
	 * @param p_column     Attribut (obligatoire)
	 * @param p_value      Valeur de l'attribut (peut être null)
	 * @return Les Entitys trouvés (ne peut pas être null)
	 */
	public static <T extends Entity_Itf<?>> List<T> findInCollectionByAttribute(final Collection<T> p_collection,
			final ColumnsNames_Itf p_column, final Object p_value) {
		final List<T> v_result = new ArrayList<>();
		try {
			for (final T v_entity : p_collection) {
				if (v_entity != null) {
					final Method v_getterMethod = EntityAttributeHelper.getInstance().getGetterMethodForColumn(v_entity,
							p_column);
					if (v_getterMethod == null) {
						throw new Spi4jRuntimeException(
								"Getter pour l'attribut " + p_column + " non trouvé dans " + p_column.getClass(),
								"Vérifier que le type de l'attribut correspond au type de l'entity");
					}
					final Object v_value = v_getterMethod.invoke(v_entity);
					if (p_value == null && v_value == null || p_value != null && p_value.equals(v_value)) {
						v_result.add(v_entity);
					}
				}
			}
		} catch (final Exception v_ex) {
			throw new Spi4jRuntimeException(v_ex, v_ex.toString(), "???");
		}
		return v_result;

	}

	/**
	 * Recherche un et un seul Entity dans une collection (une liste par exemple)
	 * selon la valeur d'un attribut (clé fonctionnelle par exemple)
	 *
	 * @param <T>          Type du Entity
	 * @param p_collection Collection de Entity
	 * @param p_attribute  Attribut (obligatoire)
	 * @param p_value      Valeur de l'attribut (peut être null)
	 * @return Les Entitys trouvés (ne peut pas être null)
	 */
	public static <T extends Entity_Itf<?>> T findOneInCollectionByAttribute(final Collection<T> p_collection,
			final ColumnsNames_Itf p_attribute, final Object p_value) {
		final List<T> v_list = findInCollectionByAttribute(p_collection, p_attribute, p_value);
		// une et une seul Entite doit correspondre à la valeur de l'attribut recherchée
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
	 * Recherche un Entity dans une collection (une liste par exemple) selon
	 * l'identifiant du Identifiable en paramètre.
	 *
	 * @param <T>          Type du Entity
	 * @param p_collection Collection de Entity
	 * @param p_entity     Entity contenant un identiant
	 * @return Le Entity si trouvé (Spi4jRuntimeException si non trouvé)
	 */
	public static <T extends Entity_Itf<?>> T findInCollectionById(final Collection<T> p_collection, final T p_entity) {
		if (p_entity == null) {
			throw new IllegalArgumentException("L'entity en paramètre ne doit pas être null");
		}
		return findInCollectionById(p_collection, p_entity.getId());

	}

	/**
	 * Recherche les Entitys dans une collection (une liste par exemple) selon
	 * l'identifiant du Entity en paramètre.
	 *
	 * @param <T>          Type du Entity
	 * @param <I>          Type de l'identifiant
	 * @param p_collection Collection de Entity
	 * @param p_listIds    Entity contenant un identiant
	 * @return Les Entitys trouvés (ne peut pas être null)
	 */
	public static <T extends Entity_Itf<I>, I> List<T> findInCollectionByListIds(final Collection<T> p_collection,
			final Collection<I> p_listIds) {
		final List<T> v_list = new ArrayList<>(p_listIds.size());
		if (!p_listIds.isEmpty()) {
			final Map<I, T> v_map = createMapById(p_collection);
			for (final I v_id : p_listIds) {
				final T v_entity = v_map.get(v_id);
				if (v_entity == null) {
					throw new Spi4jRuntimeException(
							"Aucun enregistrement trouvé dans la liste pour l'id '" + v_id + '\'',
							"Vérifier la cohérence des données");
				}
				v_list.add(v_entity);
			}
		}
		return v_list;
	}

	/**
	 * A partir d'une collection de Entitys, construit une liste avec les
	 * identifiants des Entitys.
	 *
	 * @param <T>          Type du Entity
	 * @param <I>          Type de l'identifiant
	 * @param p_collection Collection de Entity
	 * @return Le Entity si trouvé (Spi4jRuntimeException si non trouvé)
	 */
	public static <T extends Entity_Itf<I>, I> List<I> createListIds(final Collection<T> p_collection) {
		final List<I> v_list = new ArrayList<>(p_collection.size());
		for (final T v_entity : p_collection) {
			v_list.add(v_entity.getId());
		}
		return v_list;
	}

	/**
	 * A partir d'une collection de Entitys, construit une map non ordonnée avec les
	 * identifiants comme clés et les Entitys comme valeur.
	 *
	 * @param <T>          Type du Entity
	 * @param <I>          Type de l'identifiant
	 * @param p_collection Collection de Entity
	 * @return Le Entity si trouvé (Spi4jRuntimeException si non trouvé)
	 */
	public static <T extends Entity_Itf<I>, I> Map<I, T> createMapById(final Collection<T> p_collection) {
		// map non ordonnée
		final Map<I, T> v_map = new HashMap<>(p_collection.size());
		for (final T v_entity : p_collection) {
			v_map.put(v_entity.getId(), v_entity);
		}
		return v_map;
	}

	/**
	 * A partir d'une collection de Entitys, construit une map ordonnée avec les
	 * identifiants comme clés et les Entitys comme valeur.
	 * <p>
	 * Si la collection est ordonnée la map conserve cet ordre.
	 *
	 * @param <T>          Type du Entity
	 * @param <I>          Type de l'identifiant
	 * @param p_collection Collection de Entity
	 * @return Le Entity si trouvé (Spi4jRuntimeException si non trouvé)
	 */
	public static <T extends Entity_Itf<I>, I> Map<I, T> createSortedMapById(final Collection<T> p_collection) {
		// map ordonnée pour conserver l'ordre s'il y en avait un dans la collection en
		// paramètre (si c'est une liste par exemple)
		final Map<I, T> v_map = new LinkedHashMap<>(p_collection.size());
		for (final T v_entity : p_collection) {
			v_map.put(v_entity.getId(), v_entity);
		}
		return v_map;
	}

	/**
	 * Tri une liste selon un attribut en paramètre. La liste en paramètre est
	 * directement modifiée pour être triée.
	 *
	 * @param <T>         Type du Entity
	 * @param p_list      Liste de Entitys
	 * @param p_attribute Attribut de tri
	 */
	public static <T extends Entity_Itf<?>> void sortListByAttribute(final List<T> p_list,
			final ColumnsNames_Itf p_attribute) {
		sortListByAttribute(p_list, p_attribute, true, true);
	}

	/**
	 * Tri une liste selon un attribut en paramètre. La liste en paramètre est
	 * directement modifiée pour être triée.
	 *
	 * @param <T>          Type du Entity
	 * @param p_list       Liste de Entitys
	 * @param p_attribute  Attribut de tri
	 * @param p_ascending  Ascendant ou descendant
	 * @param p_ignoreCase Ignore la casse
	 */
	public static <T extends Entity_Itf<?>> void sortListByAttribute(final List<T> p_list,
			final ColumnsNames_Itf p_attribute, final boolean p_ascending, final boolean p_ignoreCase) {
		Comparator<T> v_comparator = new Comparator<>() {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public int compare(final T p_entity1, final T p_entity2) {
				final Object v_value1 = getAttributeValue(p_entity1, p_attribute);
				final Object v_value2 = getAttributeValue(p_entity2, p_attribute);
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
	 * Retourne la valeur d'une colonne dans un Entity.
	 *
	 * @param p_entity Entity
	 * @param p_column Colonne
	 * @return Valeur
	 */
	public static Object getAttributeValue(final Entity_Itf<?> p_entity, final ColumnsNames_Itf p_column) {
		final Method v_method = EntityAttributeHelper.getInstance().getGetterMethodForColumn(p_entity, p_column);

		if (v_method == null) {
			throw new Spi4jRuntimeException(
					"Getter EntityAttributeHelper.getInstance().getGetterMethodForColumn renvoie null pour les arguments",
					"Verifier les arguments entity = < " + p_entity + ">;  column = < " + p_column + " >");
		}
		try {
			return v_method.invoke(p_entity);
		} catch (final Exception v_ex) {
			throw new Spi4jRuntimeException(v_ex, v_ex.toString(), "???");
		}
	}

	/**
	 * Définit la valeur d'un attribut dans un Entity.
	 *
	 * @param p_entity Entity
	 * @param p_column Attribut
	 * @param p_value  Valeur
	 */
	public static void setAttributeValue(final Class<? extends Entity_Itf<?>> p_entity, final ColumnsNames_Itf p_column,
			final Object p_value) {
		final Method v_method = EntityAttributeHelper.getInstance().getSetterMethodForColumn(p_entity, p_column);
		if (v_method == null) {
			throw new Spi4jRuntimeException(
					"Setter pour l'attribut " + p_column + " non trouvé dans " + p_column.getClass(),
					"Vérifier que le type de l'attribut correspond au type de l'entity");
		}
		try {
			v_method.invoke(p_entity, p_value);
		} catch (final Exception v_ex) {
			throw new Spi4jRuntimeException(v_ex, v_ex.toString(), "???");
		}
	}

	/**
	 * Définit la valeur d'un attribut dans un Entity.
	 *
	 * @param p_entity Entity
	 * @param p_column Attribut
	 * @param p_value  Valeur
	 */
	public static void setAttributeValue(final Entity_Itf<?> p_entity, final ColumnsNames_Itf p_column,
			final Object p_value) {
		final Method v_method = EntityAttributeHelper.getInstance().getSetterMethodForColumn(p_entity, p_column);
		if (v_method == null) {
			throw new Spi4jRuntimeException(
					"Setter pour l'attribut " + p_column + " non trouvé dans " + p_column.getClass(),
					"Vérifier que le type de l'attribut correspond au type de l'entity");
		}
		try {
			v_method.invoke(p_entity, p_value);
		} catch (final Exception v_ex) {
			throw new Spi4jRuntimeException(v_ex, v_ex.toString(), "???");
		}

	}

	/**
	 * Clone/Copie complète d'un Entity, y compris des références telles que des
	 * listes.
	 *
	 * @param <T>      Type du Entity
	 * @param p_entity Entity
	 * @return Le Entity cloné
	 */
	public static <T extends Entity_Itf<?>> T deepClone(final T p_entity) {
		return SerializationHelper.deepClone(p_entity);
	}

	/**
	 * Clone/Copie d'un Entity, sans cloner les références et même si le Entity
	 * n'implémente pas Cloneable.
	 *
	 * @param <T>      Type du Entity
	 * @param p_entity Entity
	 * @return Le Entity cloné
	 */
	public static <T extends Entity_Itf<?>> T clone(final T p_entity) {
		return SerializationHelper.clone(p_entity);
	}

	/**
	 * Vérifie qu'un champ de Entity est bien renseigné.
	 *
	 * @param p_nomChamp        le nom fonctionnel du champ
	 * @param p_valeur          la valeur du champ
	 * @param p_champsInvalides la liste des champs déjà invalides ou null si aucun
	 *                          champ invalide
	 * @return la liste des champs invalides + ce dernier si celui-ci est null
	 */
	public static List<String> checkMandatoryField(final String p_nomChamp, final Object p_valeur,
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
	 * Vérifie qu'un champ de Entity n'est pas trop grand.
	 *
	 * @param p_nomChamp        le nom fonctionnel du champ
	 * @param p_valeur          la valeur du champ
	 * @param p_maxSize         la taille maximale du champ (définie dans
	 *                          AttributesEnum ou ColumnsEnum)
	 * @param p_champsInvalides la liste des champs déjà invalides ou null si aucun
	 *                          champ invalide
	 * @return la liste des champs invalides + ce dernier si celui-ci est null
	 */
	public static List<String> checkFieldSize(final String p_nomChamp, final String p_valeur, final int p_maxSize,
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
