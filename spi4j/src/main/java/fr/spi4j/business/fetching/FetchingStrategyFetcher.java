/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.business.fetching;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.spi4j.business.dto.AttributesNames_Itf;
import fr.spi4j.business.dto.DtoAttributeHelper;
import fr.spi4j.business.dto.Dto_Itf;

/**
 * Classe permettant de fetcher, et donc d'appliquer une FetchingStrategy, sur
 * une liste de DTOs.
 * <p>
 * Seule la méthode statique applyFetchingStrategy(FetchingStrategy, List) doit
 * être utilisée.
 * <p>
 * Les instances de cette classe contiennent un état dépendant de la transaction
 * en cours de leurs fetchingStrategies ;
 * <p>
 * ces instances ne doivent donc pas être ni réutilisées, ni réutilisables.
 *
 * @param <TypeId> Type de l'id
 * @author MINARM
 */
public final class FetchingStrategyFetcher<TypeId> {
	private final FetchingStrategy_Abs<TypeId, ? extends Dto_Itf<TypeId>> _fetchingStrategy;

	private final AttributesNames_Itf _attribute;

	private final List<FetchingStrategy_Abs<TypeId, ? extends Dto_Itf<TypeId>>> _childrenStrategies;

	private final Method _getter_method;

	private final Method _setter_method;

	private final Method _getter_method_for_id;

	private final Map<TypeId, Dto_Itf<TypeId>> _loadedDTOs;

	/**
	 * Constructeur.
	 *
	 * @param p_fetchingStrategy FetchingStrategy_Abs
	 * @param p_dtoClass         Classe du DTO
	 */
	// ce constructeur doit rester private pour en maîtriser l'instanciation et la
	// fin de vie
	private FetchingStrategyFetcher(final FetchingStrategy_Abs<TypeId, ? extends Dto_Itf<TypeId>> p_fetchingStrategy,
			final Class<? extends Dto_Itf<TypeId>> p_dtoClass) {
		super();

		_fetchingStrategy = p_fetchingStrategy;
		_attribute = _fetchingStrategy.getAttribute();

		_childrenStrategies = _fetchingStrategy.getChildren();

		_loadedDTOs = new HashMap<>();

		try {
			_getter_method = _attribute.getGetterMethod();
			if (_getter_method == null) {
				throw new NoSuchMethodException("Getter de l'attribut " + _attribute.getName()
						+ " non trouvé pour le DTO " + p_dtoClass.getSimpleName());
			}
			if (!List.class.equals(_getter_method.getReturnType())) {
				_getter_method_for_id = DtoAttributeHelper.getInstance().getGetterMethodForAttributeId(p_dtoClass,
						_attribute);
				_setter_method = _attribute.getSetterMethod();
			} else {
				_getter_method_for_id = null;
				_setter_method = null;
			}
		} catch (final Exception v_ex) {
			throw new RuntimeException(v_ex);
		}
	}

	/**
	 * Applique des fetchingStrategy filles à une liste de DTO.
	 *
	 * @param p_fetchingStrategy FetchingStrategy
	 * @param p_listeDto         Liste de DTOs
	 * @param <TypeId>           Type de l'id dans les DTOs
	 * @param <TypeDto>          Type des DTOs qui doit être le même pour la liste
	 *                           et pour la FetchingStrategy
	 */
	// cette méthode est la seule méthode typée et doit être la seule publique
	public static <TypeId, TypeDto extends Dto_Itf<TypeId>> void applyFetchingStrategy(
			final FetchingStrategy_Abs<TypeId, TypeDto> p_fetchingStrategy, final List<TypeDto> p_listeDto) {
		final List<FetchingStrategy_Abs<TypeId, ? extends Dto_Itf<TypeId>>> v_childrenStrategies = p_fetchingStrategy
				.getChildren();
		applyFetchingStrategy(v_childrenStrategies, p_listeDto);
	}

	/**
	 * Applique des fetchingStrategy filles à une liste de DTO.
	 *
	 * @param p_childrenStrategies FetchingStrategy
	 * @param p_listeDto           Liste de DTOs
	 * @param <TypeId>             Type de l'id dans les DTOs
	 */
	@SuppressWarnings("unchecked")
	private static <TypeId> void applyFetchingStrategy(
			final List<FetchingStrategy_Abs<TypeId, ? extends Dto_Itf<TypeId>>> p_childrenStrategies,
			final List<? extends Dto_Itf<TypeId>> p_listeDto) {
		if (!p_childrenStrategies.isEmpty() && !p_listeDto.isEmpty()) {
			for (final FetchingStrategy_Abs<TypeId, ? extends Dto_Itf<TypeId>> v_fetchingStrategy : p_childrenStrategies) {
				if (isActive(v_fetchingStrategy)) {
					final Class<? extends Dto_Itf<TypeId>> v_dtoClass = (Class<? extends Dto_Itf<TypeId>>) p_listeDto
							.get(0).getClass();
					final FetchingStrategyFetcher<TypeId> v_fetchingStrategyFetcher = new FetchingStrategyFetcher<>(
							v_fetchingStrategy, v_dtoClass);
					v_fetchingStrategyFetcher.apply(p_listeDto);
				}
			}
		}
	}

	/**
	 * Applique des fetchingStrategy filles à une liste de DTO.
	 *
	 * @param p_childrenStrategies FetchingStrategy
	 * @param p_dto                DTO
	 * @param <TypeId>             Type de l'id dans les DTOs
	 */
	@SuppressWarnings("unchecked")
	private static <TypeId> void applyFetchingStrategy(
			final List<FetchingStrategy_Abs<TypeId, ? extends Dto_Itf<TypeId>>> p_childrenStrategies,
			final Dto_Itf<TypeId> p_dto) {
		if (!p_childrenStrategies.isEmpty()) {
			for (final FetchingStrategy_Abs<TypeId, ? extends Dto_Itf<TypeId>> v_fetchingStrategy : p_childrenStrategies) {
				if (isActive(v_fetchingStrategy)) {
					final Class<? extends Dto_Itf<TypeId>> v_dtoClass = (Class<? extends Dto_Itf<TypeId>>) p_dto
							.getClass();
					final FetchingStrategyFetcher<TypeId> v_fetchingStrategyFetcher = new FetchingStrategyFetcher<>(
							v_fetchingStrategy, v_dtoClass);
					v_fetchingStrategyFetcher.apply(p_dto);
				}
			}
		}
	}

	/**
	 * Est-ce que la fetchingStrategy est active (non null et marquée comme "fetch")
	 * ?
	 *
	 * @param <TypeId>           Type de l'id dans les DTOs
	 * @param p_fetchingStrategy FetchingStrategy
	 * @return boolean
	 */
	private static <TypeId> boolean isActive(
			final FetchingStrategy_Abs<TypeId, ? extends Dto_Itf<TypeId>> p_fetchingStrategy) {
		return p_fetchingStrategy != null && p_fetchingStrategy.isFetch();
	}

	/**
	 * Application de la fetchingStrategy à tous les DTOs de la liste en paramètre.
	 *
	 * @param p_listeDto Liste de DTOs
	 */
	private void apply(final List<? extends Dto_Itf<TypeId>> p_listeDto) {
		for (final Dto_Itf<TypeId> v_dto : p_listeDto) {
			apply(v_dto);
		}
	}

	/**
	 * Application de la fetchingStrategy au DTO en paramètre.
	 *
	 * @param p_dto DTO
	 */
	@SuppressWarnings("unchecked")
	private void apply(final Dto_Itf<TypeId> p_dto) {
		try {
			if (!List.class.equals(_getter_method.getReturnType())) {
				// Objectif : économiser le nombre de selects unitaires et économiser la mémoire
				// et le réseau en réduisant le nombre d'instances de DTOs s'il y a plusieurs
				// fois les mêmes

				// Exemple : pour une liste d'opérations d'infrastructure d'un projet donné (ou
				// de quelques projets),
				// alors le fetch des projets de chaque opération doit éviter d'appeler chaque
				// fois le getter pour charger le DTO :
				// il faut réutiliser la même instance de DTO sans faire de select pour
				// recharger la même chose

				// Solution : si dans l'application de la fetchingStrategy sur une liste de DTO,
				// les getters concernent des DTOs de même id,
				// alors on évite d'appeler le getter pour charger le DTO et on récupère
				// l'instance de DTO depuis le cache local
				// puis on appelle le setter ce qui a donc le même effet que d'appeler le getter

				// appelle le getter avec id
				final TypeId v_resultId = callGetterForId(p_dto);
				// regarde le cache locale selon l'id
				final Object v_result = getFromLocalCache(v_resultId);
				if (v_result != null) {
					// si dto trouvé en cache, on appelle le setter et c'est tout
					// (pas besoin d'appliquer les fetchingStrategy filles puisque cela a déjà été
					// fait pour ce DTO fils)
					callSetter(p_dto, v_result);
					return;
				}
				// sinon on fait la suite pour appeler le getter
			}

			final Object v_result = callGetter(p_dto);
			if (v_result != null) {
				if (v_result instanceof Dto_Itf) {
					final Dto_Itf<TypeId> v_childDto = (Dto_Itf<TypeId>) v_result;
					// stocke le DTO dans le cache locale à l'instance pour ne pas le recharger si
					// on rencontre le même plus tard dans ce fetcher
					putInLocalCache(v_childDto);

					// applique les fetchingStrategy filles au DTO fils
					FetchingStrategyFetcher.applyFetchingStrategy(_childrenStrategies, v_childDto);
				} else if (v_result instanceof List) {
					final List<Dto_Itf<TypeId>> v_listDto = (List<Dto_Itf<TypeId>>) v_result;
					// applique les fetchingStrategy filles aux DTOs fils
					FetchingStrategyFetcher.applyFetchingStrategy(_childrenStrategies, v_listDto);
				} else {
					throw new IllegalStateException(
							"Type de résultat du getter non géré par la FetchingStrategy : " + v_result);
				}
			}
		} catch (final RuntimeException v_ex) {
			throw v_ex;
		} catch (final Exception v_ex) {
			throw new RuntimeException(v_ex);
		}
	}

	/**
	 * Stocke un DTO dans le cache de cette instance de fetcher.
	 *
	 * @param p_childDto Dto_Itf
	 */
	private void putInLocalCache(final Dto_Itf<TypeId> p_childDto) {
		_loadedDTOs.put(p_childDto.getId(), p_childDto);
	}

	/**
	 * Retourne un DTO depuis dans le cache s'il est présent ou null sinon.
	 *
	 * @param p_dtoId TypeId
	 * @return Dto_Itf
	 */
	private Dto_Itf<TypeId> getFromLocalCache(final TypeId p_dtoId) {
		return _loadedDTOs.get(p_dtoId);
	}

	/**
	 * Appelle le getter correspondant à cette fetchingStrategy sur le DTO en
	 * paramètre.
	 *
	 * @param p_dto DTO
	 * @return Résultat du getter (null ou DTO ou List)
	 * @throws Exception e
	 */
	private Object callGetter(final Dto_Itf<TypeId> p_dto) throws Exception {
		return _getter_method.invoke(p_dto, (Object[]) null);
	}

	/**
	 * Appelle le getter de l'id correspondant à cette fetchingStrategy sur le DTO
	 * en paramètre.
	 *
	 * @param p_dto DTO
	 * @return L'id en résultat du getter
	 * @throws Exception e
	 */
	@SuppressWarnings("unchecked")
	private TypeId callGetterForId(final Dto_Itf<TypeId> p_dto) throws Exception {
		return (TypeId) _getter_method_for_id.invoke(p_dto, (Object[]) null);
	}

	/**
	 * Appelle le setter correspondant à cette fetchingStrategy sur le DTO en
	 * paramètre.
	 *
	 * @param p_dto   DTO
	 * @param p_value Valeur en paramètre du setter
	 * @throws Exception e
	 */
	private void callSetter(final Dto_Itf<TypeId> p_dto, final Object p_value) throws Exception {
		_setter_method.invoke(p_dto, p_value);
	}
}
