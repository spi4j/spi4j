/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui;

/**
 * Interface mockable d'un widget représentant une chaîne de caractères.
 * @author MINARM
 */
public interface HasString_Itf extends HasValue_Itf<String>
{
   @Override
   String getValue ();
}
