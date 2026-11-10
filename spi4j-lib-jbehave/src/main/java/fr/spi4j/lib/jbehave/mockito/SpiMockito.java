/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.jbehave.mockito;

import org.mockito.Mockito;

import fr.spi4j.ui.mvp.Presenter_Abs;
import fr.spi4j.ui.mvp.View_Itf;

/**
 * Ajoute des types de mock au framework Mockito.
 * @author MINARM
 */
public final class SpiMockito
{

   /**
    * Constructeur privé.
    */
   private SpiMockito ()
   {
      super();
   }

   /**
    * Créer un mock Mockito qui se comporte comme un JavaBean. Utilisé en particulier pour les mocks de View_Itf.
    * @param <T>
    *           le type de classe mockée
    * @param p_classToMock
    *           la classe à mocker
    * @return un mock qui se comporte comme un JavaBean
    */
   public static <T> T mockBean (final Class<T> p_classToMock)
   {
      return Mockito.mock(p_classToMock, new JavaBeanBehaviorAnswer(p_classToMock));
   }

   /**
    * Créer un mock Mockito qui se comporte comme un JavaBean. Ce mock peut lui même mocker en profondeur pour éviter les null.
    * @param <T>
    *           le type de classe mockée
    * @param p_classToMock
    *           la classe à mocker
    * @param p_returnSmartJavaBeanNulls
    *           true si le mocking doit se faire en profondeur, false sinon
    * @return un mock qui se comporte comme un JavaBean
    */
   public static <T> T mockBean (final Class<T> p_classToMock, final boolean p_returnSmartJavaBeanNulls)
   {
      return Mockito.mock(p_classToMock, new JavaBeanBehaviorAnswer(p_classToMock, p_returnSmartJavaBeanNulls));
   }

   /**
    * Créer un mock Mockito qui se comporte comme un JavaBean. Utilisé en particulier pour les mocks de View_Itf.
    * @param <TypeView>
    *           le type de classe mockée
    * @param p_classToMock
    *           la classe à mocker
    * @param p_presenter
    *           le présenteur de la vue mockée
    * @return un mock de vue qui se comporte comme un JavaBean
    */
   // public static <TypeView> TypeView mockViewBean (final Class<TypeView> p_classToMock,
   // final Presenter_Abs<? extends View_Itf, ?> p_presenter)
   // {
   // return mock(p_classToMock, new JavaBeanViewBehaviorAnswer(p_classToMock, p_presenter, false));
   // }

   /**
    * Créer un mock Mockito qui se comporte comme un JavaBean, avec mocking en profondeurUtilisé en particulier pour les mocks de View_Itf.
    * @param <TypeView>
    *           le type de classe mockée
    * @param p_classToMock
    *           la classe à mocker
    * @param p_presenter
    *           le présenteur de la vue mockée
    * @param p_returnSmartJavaBeanNulls
    *           true si le mocking doit se faire en profondeur, false sinon
    * @return un mock de vue qui se comporte comme un JavaBean
    */
   public static <TypeView> TypeView mockViewBean (final Class<TypeView> p_classToMock,
            final Presenter_Abs<? extends View_Itf, ?> p_presenter, final boolean p_returnSmartJavaBeanNulls)
   {
      return Mockito.mock(p_classToMock, new JavaBeanViewBehaviorAnswer(p_classToMock, p_presenter,
               p_returnSmartJavaBeanNulls));
   }

}
