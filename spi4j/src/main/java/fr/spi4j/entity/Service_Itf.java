/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.entity;

import java.util.List;

import fr.spi4j.business.ApplicationService_Itf;
import fr.spi4j.exception.Spi4jValidationException;
import fr.spi4j.persistence.entity.Entity_Itf;

/**
 * Interface "Façade" des services liés à un "objet métier" (représenté par un
 * ENTITY).
 * <p>
 * L'implémentation de cette façade de services est sans état.
 * <p>
 * Des proxy (ou interceptors) seront ajoutés devant ces services pour les
 * aspects transverses (transactions, logs, caches et par exemple sécurité lié à
 * un service). Ces services seront accessibles de la même façon depuis un
 * client java, avec une implémentation passant par du "remoting" vers le
 * serveur.
 *
 * @param <TypeId>     Type de clé primaire
 * @param <TypeEntity> Type du ENTITY
 */
abstract public interface Service_Itf<TypeId, TypeEntity extends Entity_Itf<TypeId>> extends ApplicationService_Itf {
	/**
	 * Créer ou mettre à jour un tuple.
	 *
	 * @param p_entity (In/Out)(*) Le tuple à insérer est obligatoire. Si sa clé
	 *                 primaire est renseigné, il est mis à jour, sinon il est créé
	 * @return le tuple inséré (avec notamment sa clé primaire renseignée) ou mis à
	 *         jour (avec par exemple sa date de dernière mise à jour renseignée))
	 * @throws Spi4jValidationException Si le tuple n'est pas valide
	 */
	TypeEntity save(TypeEntity p_entity) throws Spi4jValidationException;

	/**
	 * Obtenir le tuple à partir de sa clé primaire.
	 *
	 * @param p_id (In)(*) La clé primaire de l'objet recherché.
	 * @return Le tuple désiré.
	 */
	TypeEntity findById(TypeId p_id);

	/**
	 * Obtenir la liste des tuples.
	 *
	 * @return La liste des tuples.
	 */
	List<TypeEntity> findAll();

	/**
	 * Supprimer un tuple à partir de sa clé primaire.
	 *
	 * @param p_entity (In)(*) Le tuple à supprimer avec la clé primaire.
	 * @throws Spi4jValidationException Si le tuple n'est pas valide
	 */
	void delete(TypeEntity p_entity) throws Spi4jValidationException;

}
