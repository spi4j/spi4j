/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.testapp;

import fr.spi4j.matching.Match_Itf;

/**
 * L'interface définissant le contrat pour le Matcher (= persistance <-> business) sur le type 'Personne'.
 */
public interface MyPersonneMatch_Itf extends
         Match_Itf<Long, MyPersonneDto, MyPersonneEntity_Itf, MyPersonneColumns_Enum>
{
   // RAS
}
