/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.report.impl;

import java.io.OutputStream;

/**
 * @author MINARM
 */
public class PageITEXT extends Page_Abs
{
   // RAS : pour l'instant PageITEXT ne sert à rien puisqu'on ne peut écrire aucun contenu avec

   @Override
   public void writeReport (final OutputStream p_outputStream)
   {
      throw new UnsupportedOperationException("Méthode non implémentée");
   }

}
