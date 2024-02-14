/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.mvp;

/**
 * Interface générique qui pourra être héritée et mockable pour une vue.
 * @author MINARM
 */
public interface ModifiableView_Itf extends View_Itf
{

   /**
    * Marque l'écran comme modifié ou non, et envoie une notification.
    * @param p_modified
    *           boolean indiquant le nouvel état de la vue
    * @param p_event
    *           l'événement source (si existant)
    */
   void fireModification (boolean p_modified, final Object p_event);

   /**
    * Marque l'écran comme modifié ou non, sans envoyer de notification.
    * @param p_modified
    *           boolean indiquant le nouvel état de la vue
    */
   void setModified (boolean p_modified);

   /**
    * @return true si l'écran a été modifié.
    */
   boolean isModified ();

}
