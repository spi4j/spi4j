/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.gwt.client;

import com.google.gwt.user.client.ui.Widget;

/**
 * Classe abstraite de renderer d'éléments dans une table.
 * @author MINARM
 * @param <TypeValue>
 *           le type de la valeur à afficher
 */
public abstract class SpiTableRenderer<TypeValue>
{

   /**
    * Retourne le widget à afficher dans la table.
    * @param p_xto
    *           le XTO à afficher
    * @return le widget
    */
   public abstract Widget render (final TypeValue p_xto);

}
