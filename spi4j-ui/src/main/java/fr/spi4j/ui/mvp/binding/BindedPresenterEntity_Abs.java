package fr.spi4j.ui.mvp.binding;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import fr.spi4j.exception.Spi4jRuntimeException;
import fr.spi4j.persistence.entity.ColumnsNames_Itf;
import fr.spi4j.persistence.entity.Entity_Itf;
import fr.spi4j.ui.HasValue_Itf;
import fr.spi4j.ui.mvp.PresenterListener_Itf;
import fr.spi4j.ui.mvp.Presenter_Abs;
import fr.spi4j.ui.mvp.View_Itf;

/**
 * L'ancêtre des présenteurs de vue avec gestion de binding des attributs.<br/>
 * Chaque instance de présenteur est lié à une et une seule instance de vue et à une et une seule instance Entity.
 * @param <TypeView>
 *           le type de vue présentée
 * @param <TypeEntity>
 *           le type de Entity affiché, ou Object si aucun Entity
 * @author MINARM
 */
public abstract class BindedPresenterEntity_Abs<TypeView extends View_Itf, TypeEntity extends Entity_Itf<?>>
         extends Presenter_Abs<TypeView, TypeEntity>
{
   /**
    * Les bindings entre attributs de Entity et champs de saisie.
    */
   private List<BindingEntity<TypeView, TypeEntity>> _bindings;

   /**
    * Constructeur du présenteur.
    * @param p_parent
    *           le présenteur parent
    */
   public BindedPresenterEntity_Abs (final Presenter_Abs<? extends View_Itf, ?> p_parent)
   {
      super(p_parent);
   }

   /**
    * Constructeur du présenteur.
    * @param p_parent
    *           le présenteur parent
    * @param p_entity
    *           l'entity de ce présenteur
    */
   public BindedPresenterEntity_Abs (final Presenter_Abs<? extends View_Itf, ?> p_parent, final TypeEntity p_entity)
   {
      super(p_parent, p_entity);
   }

   /**
    * Constructeur du présenteur.
    * @param p_parent
    *           le présenteur parent
    * @param p_entity
    *           l'entity de ce présenteur
    * @param p_listener
    *           le listener
    * @param <TypePresenter>
    *           le type de presenter
    */
   public <TypePresenter extends Presenter_Abs<TypeView, TypeEntity>> BindedPresenterEntity_Abs (
            final Presenter_Abs<? extends View_Itf, ?> p_parent, final TypeEntity p_entity,
            final PresenterListener_Itf<TypePresenter> p_listener)
   {
      super(p_parent, p_entity, p_listener);
   }

   @Override
   protected void initPresenter ()
   {
      super.initPresenter();
      _bindings = new ArrayList<BindingEntity<TypeView, TypeEntity>>();
      initBindingEntityView();
      // remplissage des champs à partir de l'entity (aucune action si l entity est null)
      fillFieldsFromEntity();
      updateId();
      updateTitle();
   }

   /**
    * Initialise un binding entre un attribut de Entity (p_attribute) et un champ de la vue (p_field). C'est la méthode getValue ou setValue qui sera appelée sur le champ de la vue.
    * @param p_column
    *           la colonne d'une entity
    * @param p_field
    *           le champ de la vue
    */
   protected void bind (final ColumnsNames_Itf p_column, final HasValue_Itf<?> p_field)
   {
      // on peut binder un attribut sur plusieurs champs, par exemple pour affichage d'une même valeur dans plusieurs champs
      _bindings.add(new BindingEntity<TypeView, TypeEntity>(p_column, p_field, this));
   }

   /**
    * Remplit les champs de la vue avec les données de l'etity grâce aux bindings précédement initialisés.
    */
   public void fillFieldsFromEntity ()
   {
      if (getObjet() != null)
      {
         // parcourt les bindings
         for (final BindingEntity<TypeView, TypeEntity> v_binding : _bindings)
         {
            v_binding.fillFieldFromAttribute(getObjet());
         }
      }
   }

   /**
    * Met à jour l entity à partir des données des champs de la vue.
    */
   public void fillEntityFromFields ()
   {
      // si l'entity est null
      TypeEntity v_entity = getObjet();
      if (v_entity == null)
      {
         try
         {
            // instanciation de l'entity
            final Class<TypeEntity> v_typeEntityClass = getEntityClass();
            v_entity = v_typeEntityClass.getDeclaredConstructor().newInstance();
         }
         catch (final Exception v_e)
         {
            throw new Spi4jRuntimeException(
                     v_e,
                     "L'entity passé en paramètre pour remplir à partir des champs de l'IHM est null et n'a pas pu être instancié",
                     "Vérifier l'instance courante de l'entity de ce présenteur, ainsi que le type générique");
         }
      }
      // parcourt les bindings
      for (final BindingEntity<TypeView, TypeEntity> v_binding : _bindings)
      {
         v_binding.fillAttributeFromField(v_entity);
      }
      // on met à jour l entity de ce présenteur
      setObjet(v_entity);
   }

   /**
    * @return La classe du Entity à partir du type générique spécifié par l'appelant.
    */
   @SuppressWarnings("unchecked")
   Class<TypeEntity> getEntityClass ()
   {
      return (Class<TypeEntity>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];

      // Class<TypeEntity> v_retour = null;
      //
      // Class <?> v_classes[] = getClass().getClasses();
      // for (Class<?> v_class :v_classes)
      // {
      // if ( !v_class.isInterface() && (v_class.getClasses()) instanceof Entity_Itf )
      // {
      //
      // }
      //
      //
      // }
      //
      // return (Class<TypeEntity>) ((ParameterizedType) getClass(). .getGenericSuperclass()).getActualTypeArguments()[1];
   }

   /**
    * Associe les champs de cette vue avec une Entity.
    */
   abstract protected void initBindingEntityView ();

   /**
    * Méthode permettant de vider les associations de binding déjà existantes.<br />
    * Cette méthode est utile dans le cas où l'écran se raffraichit et que de nouveaux composants à binder apparaissent.
    */
   protected void clearBindings ()
   {
      _bindings.clear();
   }

}
