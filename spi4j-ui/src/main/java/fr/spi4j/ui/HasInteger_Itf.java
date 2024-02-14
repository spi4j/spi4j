/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui;

/**
 * Interface mockable d'un widget représentant un entier.
 * @author MINARM
 */
public interface HasInteger_Itf extends HasValue_Itf<Integer>
{
   @Override
   Integer getValue ();
}
