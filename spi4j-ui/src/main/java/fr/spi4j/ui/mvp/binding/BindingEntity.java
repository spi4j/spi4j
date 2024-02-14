/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.mvp.binding;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import fr.spi4j.exception.Spi4jRuntimeException;
import fr.spi4j.persistence.entity.ColumnsNames_Itf;
import fr.spi4j.persistence.entity.EntityAttributeHelper;
import fr.spi4j.persistence.entity.EntityUtil;
import fr.spi4j.persistence.entity.Entity_Itf;
import fr.spi4j.ui.HasMandatory_Itf;
import fr.spi4j.ui.HasMaxLength_Itf;
import fr.spi4j.ui.HasValue_Itf;
import fr.spi4j.ui.mvp.View_Itf;

/**
 * Binding entre un attribut d'un Entity et un champ d'une instance de vue. C'est la méthode getValue ou setValue qui sera appelée sur le champ de la vue.
 * @param <TypeView>
 *           le type de vue présentée
 * @param <TypeEntity>
 *           le type de Entity affiché, ou Object si aucun entity
 * @author MINARM
 */
class BindingEntity<TypeView extends View_Itf, TypeEntity extends Entity_Itf<?>>
{
   /**
    * Le nom de la colonne de l'entity
    */
   private final ColumnsNames_Itf _column;

   /**
    * Le champ de la vue.
    */
   private final HasValue_Itf<? extends Object> _field;

   /**
    * Le présenteur ayant créé ce binding.
    */
   private final BindedPresenterEntity_Abs<TypeView, TypeEntity> _presenter;

   /**
    * Constructeur.
    * @param p_column
    *           la colonne d'une Entity
    * @param p_field
    *           le champ de la vue
    * @param p_presenter
    *           le présenteur qui crée ce binding
    */
   BindingEntity (final ColumnsNames_Itf p_column, final HasValue_Itf<?> p_field,
            final BindedPresenterEntity_Abs<TypeView, TypeEntity> p_presenter)
   {
      super();
      this._column = p_column;
      this._field = p_field;
      this._presenter = p_presenter;

      if (p_field instanceof HasMaxLength_Itf && p_column.getSize() != -1)
      {
         ((HasMaxLength_Itf) p_field).setMaxLength(p_column.getSize());
      }
      if (p_field instanceof HasMandatory_Itf)
      {
         ((HasMandatory_Itf) p_field).setMandatory(p_column.isMandatory());
      }

      // final Class<TypeEntity> v_entityClass = p_presenter.getEntityClass();
      final Entity_Itf<?> v_entityClass = p_presenter.getObjet();

      // si on essaye de faire un bind entre un champ et un attribut qui n'a ni getter ni setter on lance une Spi4jRuntimeException
      if (EntityAttributeHelper.getInstance().getGetterMethodForColumn(v_entityClass, p_column) == null
               && EntityAttributeHelper.getInstance().getSetterMethodForColumn(v_entityClass, p_column) == null)
      {
         throw new Spi4jRuntimeException(
                  "Impossible de faire un bind entre un champ et une colonne si cette colonne n'a ni getter ni setter dans l'entity",
                  "Vérifier le binding dans le présenteur " + _presenter.getClass().getSimpleName()
                           + " et vérifiez l'entity " + v_entityClass.toString());
      }
   }

   /**
    * Remplit le champs de la vue avec la valeur de la colonne de l'entity.
    * @param p_entity
    *           Instance de l'entity
    */
   @SuppressWarnings("unchecked")
   void fillFieldFromAttribute (final TypeEntity p_entity)
   {
      try
      {
         // récupère la valeur de cette propriété dans le DTO
         final Object v_propertyValue = EntityUtil.getAttributeValue(p_entity, _column);

         // remplissage du champ
         ((HasValue_Itf<Object>) _field).setValue(v_propertyValue);
      }
      catch (final Exception v_e)
      {
         throw new Spi4jRuntimeException(
                  v_e,
                  "Impossible de remplir les champs à partir d'une entity : problème avec le champ "
                           + _column.getLogicalColumnName(),
                  "Vérifier le binding dans le présenteur " + _presenter.getClass().getSimpleName());
      }
   }

   /**
    * Met à jour l'attribut de l'entity à partir du champs de la vue.
    * @param p_entity
    *           Instance de l'entity
    */
   void fillAttributeFromField (final TypeEntity p_entity)
   {
      try
      {
         // remplissage de l'entity
         setPropertyValue(p_entity, _column, _field.getValue());
      }
      catch (final Exception v_e)
      {
         throw new Spi4jRuntimeException(v_e,
                  "Impossible de remplir une Entity à partir des champs : problème avec le champ "
                           + _column.getLogicalColumnName(),
                  "Vérifier le binding dans le présenteur " + _presenter.getClass().getSimpleName());
      }
   }

   /**
    * Modifie la valeur d'une propriété de Entity.
    * @param p_entity
    *           Instance de l'entity
    * @param p_column
    *           la propriété
    * @param p_value
    *           la valeur de la colonne pour cette Entity
    * @throws IllegalAccessException
    *            si la valeur n'a pas réussie à être trouvée pour cette entity
    * @throws InvocationTargetException
    *            si la valeur n'a pas réussie à être trouvée pour cette entity
    * @throws NoSuchMethodException
    *            si la valeur n'a pas réussie à être trouvée pour cette entity
    */
   private void setPropertyValue (final TypeEntity p_entity, final ColumnsNames_Itf p_column, final Object p_value)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
   {
      // recherche l'existance du setter de p_property dans p_entity
      final Method v_setterMethod = EntityAttributeHelper.getInstance().getSetterMethodForColumn(p_entity, p_column);
      if (v_setterMethod == null)
      {
         // recherche l'existance du getter de p_property dans p_entity
         final Method v_getterMethod = EntityAttributeHelper.getInstance().getGetterMethodForColumn(p_entity, p_column);

         if (v_getterMethod == null)
         {
            throw new NoSuchMethodException("Setter de la colonne " + p_column.getLogicalColumnName()
                     + " non trouvé pour l'entity " + p_entity.getClass().getSimpleName());
         }
         // Else : Si un setter n'existe pas mais qu'un getter existe pour ce p_property c'est qu'on est en présence d'un champ calculé.
         // C'est normal que le setter d'un champ calculé n'existe pas donc on ne lance pas d'exception.
      }
      else
      {
         // met à jour la valeur de la propriété
         v_setterMethod.invoke(p_entity, p_value);
      }
   }
}
