/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui;

/**
 * Interface mockable d'un widget représentant un booléen.
 * @author MINARM
 */
public interface HasBoolean_Itf extends HasValue_Itf<Boolean>
{
   @Override
   Boolean getValue ();
}
