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
public interface View_Itf
{

   /**
    * @return le titre de la vue.
    */
   String getTitle ();

   /**
    * Modifie le titre de la vue.
    * @param p_title
    *           le titre de la vue
    */
   void setTitle (String p_title);

   /**
    * Action d'ajout d'une vue à son parent.
    * @param p_view
    *           la vue à ajouter
    */
   void addView (View_Itf p_view);

   /**
    * Action de restauration d'une vue dans son parent.
    * @param p_view
    *           la vue à restaurer
    */
   void restoreView (View_Itf p_view);

   /**
    * Action de suppression d'une vue de son parent.
    * @param p_view
    *           la vue à supprimer
    */
   void removeView (View_Itf p_view);

   /**
    * @return true si cette vue est modale, false sinon.
    */
   boolean isModal ();

   /**
    * Méthode appelée automatiquement par le présenteur avant la fermeture de ce dernier.
    */
   void beforeClose ();

   /**
    * @return le présenteur associé à cette vue
    */
   Presenter_Abs<? extends View_Itf, ?> getPresenter ();

}
