/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.gwt.client.services;

import javax.servlet.http.HttpServletResponse;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.StatusCodeException;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Callback par défaut avec gestion d'erreur en affichant une popup.
 * @param <T>
 *           Type de données attendues en retour
 * @author MINARM
 */
public class DefaultAsyncCallback<T> implements AsyncCallback<T>
{

   // initialisé à false
   private static boolean noPopup; // = false par défaut

   /**
    * La popup de chargement.
    */
   protected DialogBox _loadingPopup;

   /**
    * Le texte de chargement.
    */
   protected final String _loadingMessage;

   /**
    * Constructeur par défaut.
    */
   public DefaultAsyncCallback ()
   {
      this(false);
   }

   /**
    * Constructeur définissant si une popup de chargement s'affiche ou non.
    * @param p_loadingEnabled
    *           true si une popup de chargement est affichée, false sinon.
    */
   public DefaultAsyncCallback (final boolean p_loadingEnabled)
   {
      _loadingMessage = null;
      if (p_loadingEnabled)
      {
         startLoading();
      }
   }

   /**
    * Constructeur définissant le texte du chargement.
    * @param p_loadingMessage
    *           le message affiché lors du chargement
    */
   public DefaultAsyncCallback (final String p_loadingMessage)
   {
      _loadingMessage = p_loadingMessage;
      startLoading();
   }

   /**
    * Affichage de l'information de chargement.
    */
   protected void startLoading ()
   {
      if (!noPopup)
      {
         _loadingPopup = new DialogBox();
         _loadingPopup.setText("Chargement");
         if (_loadingMessage != null)
         {
            _loadingPopup.add(new HTML(_loadingMessage));
         }
         _loadingPopup.center();
      }
   }

   /**
    * Masquage de l'information de chargement.
    */
   protected void stopLoading ()
   {
      if (_loadingPopup != null)
      {
         _loadingPopup.hide();
      }
   }

   @Override
   public final void onFailure (final Throwable p_caught)
   {
      Throwable v_erreurATraiter = p_caught;
      if (p_caught instanceof StatusCodeException)
      {
         final StatusCodeException v_statusCodeException = (StatusCodeException) p_caught;
         if (v_statusCodeException.getStatusCode() == HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
         {
            final String v_errorMessage = v_statusCodeException.getEncodedResponse();
            if (v_errorMessage.contains("/"))
            {
               final String[] v_errorMessageSplit = v_errorMessage.split("/", 2);
               final String v_className = v_errorMessageSplit[0];
               final String v_errorMessageContent = v_errorMessageSplit[1];
               final Throwable v_realException = buildException(v_className, v_errorMessageContent);
               if (v_realException != null)
               {
                  v_erreurATraiter = v_realException;
               }
            }
         }
      }
      doOnError(v_erreurATraiter);
      stopLoading();
   }

   @Override
   public final void onSuccess (final T p_result)
   {
      doOnSuccess(p_result);
      stopLoading();
   }

   /**
    * Action à faire en cas de succès du service.
    * @param p_result
    *           le résultat du service
    */
   protected void doOnSuccess (final T p_result)
   {
      // aucune action par défaut
   }

   /**
    * Action à faire en cas d'échec du service. Par défaut, affiche une dialogue d'erreur.
    * @param p_caught
    *           l'erreur renvoyée par le service
    */
   protected void doOnError (final Throwable p_caught)
   {
      GWT.log(p_caught.toString(), p_caught);
      if (!noPopup)
      {
         // Create the popup dialog box
         final DialogBox v_dialogBox = new DialogBox();
         v_dialogBox.setText("Erreur");
         v_dialogBox.setAnimationEnabled(true);
         final Button v_closeButton = new Button("Fermer");
         final VerticalPanel v_dialogVPanel = new VerticalPanel();
         v_dialogVPanel.add(new HTML(p_caught.getMessage()));
         v_dialogVPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
         v_dialogVPanel.add(v_closeButton);
         v_dialogBox.setWidget(v_dialogVPanel);
         v_dialogBox.center();

         // Add a handler to close the DialogBox
         v_closeButton.addClickHandler(new ClickHandler()
         {
            @Override
            public void onClick (final ClickEvent p_event)
            {
               v_dialogBox.hide();
            }
         });
      }
   }

   /**
    * Construit une exception côté client.
    * @param p_className
    *           le nom de la classe
    * @param p_errorMessageContent
    *           le message d'erreur
    * @return une instance de l'exception
    */
   protected Throwable buildException (final String p_className, final String p_errorMessageContent)
   {
      return null;
   }

   /**
    * Force les Callbacks à ne pas afficher de popup, pour les tests JBehave.
    */
   public static void forceNoPopup ()
   {
      noPopup = true;
   }

}
