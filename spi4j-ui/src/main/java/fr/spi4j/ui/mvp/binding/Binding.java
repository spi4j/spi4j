/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.mvp.binding;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import fr.spi4j.Identifiable_Itf;
import fr.spi4j.business.dto.AttributesNames_Itf;
import fr.spi4j.business.dto.DtoUtil;
import fr.spi4j.business.dto.Dto_Itf;
import fr.spi4j.exception.Spi4jRuntimeException;
import fr.spi4j.ui.HasMandatory_Itf;
import fr.spi4j.ui.HasMaxLength_Itf;
import fr.spi4j.ui.HasValue_Itf;
import fr.spi4j.ui.mvp.View_Itf;

/**
 * Binding entre un attribut d'un DTO et un champ d'une instance de vue. C'est la méthode getValue ou setValue qui sera appelée sur le champ de la vue.
 * @param <TypeView>
 *           le type de vue présentée
 * @param <TypeDto>
 *           le type de DTO affiché, ou Object si aucun DTO
 * @author MINARM
 */
class Binding<TypeView extends View_Itf, TypeDto extends Identifiable_Itf<?>>
{
   /**
    * Le nom de l'attribut du DTO.
    */
   private final AttributesNames_Itf _attribute;

   /**
    * Le champ de la vue.
    */
   private final HasValue_Itf<? extends Object> _field;

   /**
    * Le présenteur ayant créé ce binding.
    */
   private final BindedPresenter_Abs<TypeView, TypeDto> _presenter;

   /**
    * Constructeur.
    * @param p_attribute
    *           l'attribut d'un DTO
    * @param p_field
    *           le champ de la vue
    * @param p_presenter
    *           le présenteur qui crée ce binding
    */
   Binding (final AttributesNames_Itf p_attribute, final HasValue_Itf<?> p_field,
            final BindedPresenter_Abs<TypeView, TypeDto> p_presenter)
   {
      super();
      this._attribute = p_attribute;
      this._field = p_field;
      this._presenter = p_presenter;

      if (p_field instanceof HasMaxLength_Itf && p_attribute.getSize() != -1)
      {
         ((HasMaxLength_Itf) p_field).setMaxLength(p_attribute.getSize());
      }
      if (p_field instanceof HasMandatory_Itf)
      {
         ((HasMandatory_Itf) p_field).setMandatory(p_attribute.isMandatory());
      }

      final Class<TypeDto> v_dtoClass = p_presenter.getDtoClass();
      // si on essaye de faire un bind entre un champ et un attribut qui n'a ni getter ni setter on lance une Spi4jRuntimeException
      if (p_attribute.getGetterMethod() == null && p_attribute.getSetterMethod() == null)
      {
         throw new Spi4jRuntimeException(
                  "Impossible de faire un bind entre un champ et un attribut si cet attribut n'a ni getter ni setter dans le DTO",
                  "Vérifier le binding dans le présenteur " + _presenter.getClass().getSimpleName()
                           + " et vérifiez le DTO " + v_dtoClass.getSimpleName());
      }
   }

   /**
    * Remplit le champs de la vue avec la valeur de l'attribut du DTO.
    * @param p_dto
    *           Instance du DTO
    */
   @SuppressWarnings("unchecked")
   void fillFieldFromAttribute (final TypeDto p_dto)
   {
      try
      {
         // récupère la valeur de cette propriété dans le DTO
         final Object v_propertyValue = DtoUtil.getAttributeValue((Dto_Itf<?>) p_dto, _attribute);

         // remplissage du champ
         ((HasValue_Itf<Object>) _field).setValue(v_propertyValue);
      }
      catch (final Exception v_e)
      {
         throw new Spi4jRuntimeException(
                  v_e,
                  "Impossible de remplir les champs à partir d'un DTO : problème avec le champ " + _attribute.getName(),
                  "Vérifier le binding dans le présenteur " + _presenter.getClass().getSimpleName());
      }
   }

   /**
    * Met à jour l'attribut du DTO à partir du champs de la vue.
    * @param p_dto
    *           Instance du DTO
    */
   void fillAttributeFromField (final TypeDto p_dto)
   {
      try
      {
         // remplissage du DTO
         setPropertyValue(p_dto, _attribute, _field.getValue());
      }
      catch (final Exception v_e)
      {
         throw new Spi4jRuntimeException(v_e,
                  "Impossible de remplir un DTO à partir des champs : problème avec le champ " + _attribute.getName(),
                  "Vérifier le binding dans le présenteur " + _presenter.getClass().getSimpleName());
      }
   }

   /**
    * Modifie la valeur d'une propriété de DTO.
    * @param p_dto
    *           Instance du DTO
    * @param p_property
    *           la propriété
    * @param p_value
    *           la valeur de la propriété pour ce DTO
    * @throws IllegalAccessException
    *            si la valeur n'a pas réussie à être trouvée pour ce DTO
    * @throws InvocationTargetException
    *            si la valeur n'a pas réussie à être trouvée pour ce DTO
    * @throws NoSuchMethodException
    *            si la valeur n'a pas réussie à être trouvée pour ce DTO
    */
   private void setPropertyValue (final TypeDto p_dto, final AttributesNames_Itf p_property, final Object p_value)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
   {
      // recherche l'existance du setter de p_property dans p_dto
      final Method v_setterMethod = p_property.getSetterMethod();
      if (v_setterMethod == null)
      {
         // recherche l'existance du getter de p_property dans p_dto
         final Method v_getterMethod = p_property.getGetterMethod();

         if (v_getterMethod == null)
         {
            throw new NoSuchMethodException("Setter de l'attribut " + p_property.getName() + " non trouvé pour le DTO "
                     + p_dto.getClass().getSimpleName());
         }
         // Else : Si un setter n'existe pas mais qu'un getter existe pour ce p_property c'est qu'on est en présence d'un champ calculé.
         // C'est normal que le setter d'un champ calculé n'existe pas donc on ne lance pas d'exception.
      }
      else
      {
         // met à jour la valeur de la propriété
         v_setterMethod.invoke(p_dto, p_value);
      }
   }
}
