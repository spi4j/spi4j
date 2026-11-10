/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui;

/**
 * Interface mockable d'un widget représentant un décimal (Double).
 * @author MINARM
 */
public interface HasDouble_Itf extends HasValue_Itf<Double>
{
   @Override
   Double getValue ();
}
