/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui;

/**
 * Une interface de composant à état dans l'interface graphique.
 * @author MINARM
 */
public interface HasState_Itf
{

   /**
    * @return true si le composant est disponible, false sinon.
    */
   boolean isEnabled ();

   /**
    * Modifie l'état de disponibilité du composant.
    * @param p_enabled
    *           la disponibilité du composant
    */
   void setEnabled (boolean p_enabled);

   /**
    * @return true si le composant est visible, false sinon.
    */
   boolean isVisible ();

   /**
    * Modifie la visibilité du composant.
    * @param p_visible
    *           la visibilité du composant
    */
   void setVisible (boolean p_visible);

}
