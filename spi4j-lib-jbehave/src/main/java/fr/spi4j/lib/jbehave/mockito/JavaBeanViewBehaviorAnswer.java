/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.jbehave.mockito;

import java.util.ArrayList;
import java.util.List;

import fr.spi4j.ui.HasValue_Itf;
import fr.spi4j.ui.mvp.Presenter_Abs;
import fr.spi4j.ui.mvp.View_Itf;

/**
 * Comportement JavaBean d'un mock pour une Vue.
 * @author MINARM
 */
public class JavaBeanViewBehaviorAnswer extends JavaBeanBehaviorAnswer
{

   private final Presenter_Abs<? extends View_Itf, ?> _presenter;

   /**
    * Constructeur du comportement d'un mock bean d'une vue.
    * @param p_classToMock
    *           la vue à mocker
    * @param p_presenter
    *           le présenteur de cette vue
    * @param p_returnSmartJavaBeanNulls
    *           true pour mocker en profondeur, false sinon
    */
   public JavaBeanViewBehaviorAnswer (final Class<?> p_classToMock,
            final Presenter_Abs<? extends View_Itf, ?> p_presenter, final boolean p_returnSmartJavaBeanNulls)
   {
      super(p_classToMock, p_returnSmartJavaBeanNulls);
      _presenter = p_presenter;
   }

   // @Override
   // protected void doSetValue (final InvocationOnMock p_invocation, final String p_propertyName, final Object p_value)
   // {
   // super.doSetValue(p_invocation, p_propertyName, p_value);
   // if (p_invocation.getMock() instanceof HasValue_Itf<?> && "value".equals(p_propertyName.toLowerCase()))
   // {
   // final HasValue_Itf<?> v_hasValue = (HasValue_Itf<?>) p_invocation.getMock();
   // _presenter.fieldUpdated(v_hasValue);
   // }
   // }

   @Override
   protected Object newMock (final Class<?> p_type)
   {
      if (List.class.isAssignableFrom(p_type))
      {
         return new ArrayList<>();
      }
      return SpiMockito.mockViewBean(p_type, _presenter, !HasValue_Itf.class.isAssignableFrom(p_type)
               && is_returnSmartJavaBeanNulls());
   }
}
