/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.gwt.client;

import com.google.gwt.user.client.ui.SimplePanel;

import fr.spi4j.ui.mvp.Presenter_Abs;
import fr.spi4j.ui.mvp.View_Itf;

/**
 * Panel de vue.
 * @param <TypePresenter>
 *           le type du présenteur de cette vue
 * @author MINARM
 */
public abstract class SpiViewPanel<TypePresenter extends Presenter_Abs<? extends View_Itf, ?>> extends SimplePanel
         implements View_Itf
{
   private String _title;

   private final TypePresenter _presenter;

   /**
    * Création d'un Panel avec un parent.
    * @param p_presenter
    *           le présenteur de cette vue
    */
   public SpiViewPanel (final TypePresenter p_presenter)
   {
      super();
      // if (p_presenter == null)
      // {
      // on fait un log à la place d'un throw pour que WindowBuilder puisse afficher le design de l'écran
      // Logger.getLogger(getClass()).error("Le paramètre presenter ne doit pas être null");
      // throw new NullPointerException("Le paramètre presenter ne doit pas être null");
      // }
      _presenter = p_presenter;
      _title = getClass().getName();
      addStyleName("spi-view-panel");
      addStyleName(getClass().getName().replace('.', '-'));
   }

   @Override
   public String getTitle ()
   {
      return _title;
   }

   @Override
   public void setTitle (final String p_title)
   {
      _title = p_title;
   }

   @Override
   public boolean isModal ()
   {
      return false;
   }

   @Override
   public void beforeClose ()
   {
      // Aucun traitement par défaut.
      // Méthode à surcharger si besoin
   }

   @Override
   public TypePresenter getPresenter ()
   {
      return _presenter;
   }

}
