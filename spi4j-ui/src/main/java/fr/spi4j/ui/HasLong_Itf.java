/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui;

/**
 * Interface mockable d'un widget représentant un entier long.
 * @author MINARM
 */
public interface HasLong_Itf extends HasValue_Itf<Long>
{
   @Override
   Long getValue ();
}
