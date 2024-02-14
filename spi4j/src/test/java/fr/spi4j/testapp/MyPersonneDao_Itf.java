/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.testapp;

import fr.spi4j.persistence.dao.Dao_Itf;

/**
 * Interface de services spécifiques pour le DAO Personne.
 * @author MINARM
 */
public interface MyPersonneDao_Itf extends Dao_Itf<Long, MyPersonneEntity_Itf, MyPersonneColumns_Enum>
{
   // RAS
}
