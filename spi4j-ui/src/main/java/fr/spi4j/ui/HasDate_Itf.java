/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui;

import java.util.Date;

/**
 * Interface mockable d'un widget représentant une date.
 * @author MINARM
 */
public interface HasDate_Itf extends HasValue_Itf<Date>
{
   @Override
   Date getValue ();
}
