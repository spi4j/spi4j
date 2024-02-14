/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui.swing;

import java.awt.Component;
import java.awt.Dialog.ModalExclusionType;
import java.awt.Window;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.apache.logging.log4j.LogManager;

import fr.spi4j.ReflectUtil;
import fr.spi4j.ui.HasMandatory_Itf;
import fr.spi4j.ui.HasValue_Itf;
import fr.spi4j.ui.graal.Field;
import fr.spi4j.ui.graal.UserView;
import fr.spi4j.ui.mvp.ModifiableView_Itf;
import fr.spi4j.ui.mvp.Presenter_Abs;
import fr.spi4j.ui.mvp.View_Itf;
import fr.spi4j.ui.swing.fields.SpiDatePanel;
import fr.spi4j.ui.swing.fields.SpiStringArea;
import fr.spi4j.ui.swing.table.SpiTableScrollPane;

/**
 * Panel de vue.
 * @param <TypePresenter>
 *           le type du présenteur de cette vue
 * @author MINARM
 */
public abstract class SpiViewPanel<TypePresenter extends Presenter_Abs<? extends View_Itf, ?>> extends SpiPanel
         implements ModifiableView_Itf
{
   private static final long serialVersionUID = 1L;

   private String _title;

   private final TypePresenter _presenter;

   private boolean _isModified;

   /**
    * Création d'un Panel avec un parent.
    * @param p_presenter
    *           le présenteur de cette vue
    */
   public SpiViewPanel (final TypePresenter p_presenter)
   {
      super();
      if (p_presenter == null)
      {
         // on fait un log à la place d'un throw pour que WindowBuilder puisse afficher le design de l'écran
         LogManager.getLogger(getClass()).error("Le paramètre presenter ne doit pas être null");
         // throw new NullPointerException("Le paramètre presenter ne doit pas être null");
      }
      _presenter = p_presenter;
      _title = getClass().getCanonicalName();

      // initialisation des propriétés name des composants de cette vue en fonction des annotations Graal sur l'interface de la vue
      SwingUtilities.invokeLater(new Runnable()
      {
         @Override
         public void run ()
         {
            initNameProperties();
            registerFieldsForModification();
         }
      });
   }

   @Override
   public void addView (final View_Itf p_view)
   {
      // implémentation par défaut si aucune vue fille, à surcharger sinon
      throw new IllegalStateException("Vue inconnue : " + p_view.getClass().getName());
   }

   @Override
   public void restoreView (final View_Itf p_view)
   {
      // implémentation par défaut si aucune vue fille, à surcharger sinon
      throw new IllegalStateException("Vue inconnue : " + p_view.getClass().getName());
   }

   @Override
   public void removeView (final View_Itf p_view)
   {
      // implémentation par défaut si aucune vue fille, à surcharger sinon
      throw new IllegalStateException("Vue inconnue : " + p_view.getClass().getName());
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
      final Window v_window = SpiSwingUtilities.getAncestorOfClass(Window.class, this);
      return v_window.getModalExclusionType() == ModalExclusionType.APPLICATION_EXCLUDE
               || v_window.getModalExclusionType() == ModalExclusionType.TOOLKIT_EXCLUDE;
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

   /**
    * Méthode à surcharger pour exécuter un traitement lorsque la vue a été modifiée. <br />
    * Attention à ne pas faire de traitement trop lourd dans cette méthode car elle est appelée à chaque modification d'un champ.
    * @param p_oldValue
    *           l'ancienne valeur
    * @param p_newValue
    *           la nouvelle valeur
    * @param p_event
    *           l'événement source (si existant)
    */
   protected void onModificationChange (final boolean p_oldValue, final boolean p_newValue, final Object p_event)
   {
      // Par défaut, aucun traitement particulier
   }

   @Override
   public void fireModification (final boolean p_modified, final Object p_event)
   {
      final boolean v_oldValue = _isModified;
      _isModified = p_modified;
      onModificationChange(v_oldValue, _isModified, p_event);
   }

   @Override
   public void setModified (final boolean p_modified)
   {
      _isModified = p_modified;
   }

   @Override
   public boolean isModified ()
   {
      return _isModified;
   }

   /**
    * Méthode de vérification des champs obligatoires. <br />
    * Cette implémentation vérifie uniquement les champs qui sont marqués avec setMandatory(true) dans l'interface {@link HasMandatory_Itf}.
    * @return true si tous les champs de type {@link HasMandatory_Itf} sont obligatoires (setMandatory(true)) et remplis (non null et toString() non vide)
    */
   public boolean checkMandatoryFields ()
   {
      boolean v_mandatoryFieldsFilled = true;
      try
      {
         // mantis 2134 pour Controle surfacique des champs d'un écran
         for (final java.lang.reflect.Field v_field : getClass().getDeclaredFields())
         {
            if (!Modifier.isStatic(v_field.getModifiers()))
            {
               v_field.setAccessible(true);
               final Object v_fieldValue = v_field.get(this);
               // Vérification que le champ est bien un HasMandatory et HasValue
               if (v_fieldValue instanceof HasMandatory_Itf && v_fieldValue instanceof HasValue_Itf)
               {
                  // Cast
                  final HasMandatory_Itf v_hasMandatory = (HasMandatory_Itf) v_fieldValue;
                  // Traitement uniquement si le champ est marqué obligatoire
                  if (v_hasMandatory.isMandatory())
                  {
                     final HasValue_Itf<?> v_hasValue = (HasValue_Itf<?>) v_fieldValue;
                     // Est-ce que la valeur n'est pas nulle
                     boolean v_fieldHasValue = v_hasValue.getValue() != null;
                     if (v_fieldHasValue && v_hasValue.getValue() instanceof String)
                     {
                        // Si la valeur est une String, est-ce qu'elle n'est pas vide
                        v_fieldHasValue = !((String) v_hasValue.getValue()).isEmpty();
                     }
                     if (!v_fieldHasValue)
                     {
                        v_mandatoryFieldsFilled = false;
                        break;
                     }

                  }
               }
            }
         }
      }
      catch (final IllegalAccessException e)
      {
         // cette exception ne peut pas arriver vu setAccessible(true), mais au cas où on fait un throw
         throw new IllegalStateException(e);
      }
      return v_mandatoryFieldsFilled;
   }

   /**
    * Initialisation des propriétés name des composants de cette vue en fonction des annotations Graal sur l'interface de la vue.
    */
   protected void initNameProperties ()
   {
      try
      {
         // mantis 1321 pour Marathon pour avoir un nom par défaut même en l'absence d'annotations @Field utilisées ci-dessous
         for (final java.lang.reflect.Field v_field : getClass().getDeclaredFields())
         {
            if (!Modifier.isStatic(v_field.getModifiers()))
            {
               v_field.setAccessible(true);
               final Object v_fieldValue = v_field.get(this);
               if (v_fieldValue instanceof Component)
               {
                  final Component v_component = (Component) v_fieldValue;
                  if (v_component.getName() == null)
                  {
                     v_component.setName(v_field.getName());
                  }
               }
            }
         }
      }
      catch (final IllegalAccessException e)
      {
         // cette exception ne peut pas arriver vu setAccessible(true), mais au cas où on fait un throw
         throw new IllegalStateException(e);
      }

      // récupère la liste des interfaces (récursivement) de la classe courante, pas plus loin que View_Itf
      final Set<Class<?>> v_interfaces = getInterfaces(getClass(), new HashSet<Class<?>>());

      for (final Class<?> v_clazz : v_interfaces)
      {
         // initialisation du name du panel : basé sur @UserView
         if (v_clazz.isAnnotationPresent(UserView.class))
         {
            if (getName() == null)
            {
               setName(v_clazz.getAnnotation(UserView.class).value());
            }
         }

         // initialisation du name des composants : basé sur @Field des méthodes de l'interface de vue
         for (final Method v_method : v_clazz.getDeclaredMethods())
         {
            // si le getter possède l'annotation Field et n'a pas de paramètres
            if (v_method.isAnnotationPresent(Field.class) && v_method.getParameterTypes().length == 0)
            {
               try
               {
                  final Object v_field = ReflectUtil.invokeMethod(v_method, this);
                  // on vérifie que le composant récupéré est bien un Component et on lui met à jour sa propriété name
                  if (v_field instanceof Component)
                  {
                     final Component v_component = (Component) v_field;
                     v_component.setName(v_method.getAnnotation(Field.class).value());
                  }
               }
               catch (final Throwable e)
               {
                  // si le getter renvoie une exception, rien d'anormal, on tente les méthodes suivantes
                  continue;
               }
            }
         }
      }

   }

   /**
    * Récupère la liste des interfaces qui contiennent potentiellement les annotations Field
    * @param p_clazz
    *           la classe d'origine
    * @param p_set
    *           le set des interfaces à retourner (à instancier par l'appelant)
    * @return la liste des interfaces qui contiennent potentiellement les annotations Field
    */
   private Set<Class<?>> getInterfaces (final Class<?> p_clazz, final Set<Class<?>> p_set)
   {
      // on cherche la superclasse pour aller chercher ses interfaces (pas plus haut que View_Itf)
      if (p_clazz.getSuperclass() != null && View_Itf.class.isAssignableFrom(p_clazz.getSuperclass()))
      {
         getInterfaces(p_clazz.getSuperclass(), p_set);
      }
      // on cherche les interfaces de la classe courante (pas plus haut que View_Itf)
      for (final Class<?> v_interface : p_clazz.getInterfaces())
      {
         if (View_Itf.class.isAssignableFrom(v_interface))
         {
            p_set.add(v_interface);
            getInterfaces(v_interface, p_set);
         }
      }
      return p_set;
   }

   /**
    * Ecoute les modifications faites sur les champs
    */
   protected void registerFieldsForModification ()
   {
      try
      {
         // mantis 2134 pour Controle surfacique des champs d'un écran
         for (final java.lang.reflect.Field v_field : getClass().getDeclaredFields())
         {
            if (!Modifier.isStatic(v_field.getModifiers()))
            {
               v_field.setAccessible(true);
               final Object v_fieldValue = v_field.get(this);
               registerFieldForModification(v_fieldValue);
            }
         }
      }
      catch (final IllegalAccessException e)
      {
         // cette exception ne peut pas arriver vu setAccessible(true), mais au cas où on fait un throw
         throw new IllegalStateException(e);
      }
   }

   /**
    * Enregistre un champ pour écouter ses modifications
    * @param p_component
    *           le composant à écouter
    */
   @SuppressWarnings("rawtypes")
   protected void registerFieldForModification (final Object p_component)
   {
      if (p_component instanceof JTextField)
      {
         final JTextField v_component = (JTextField) p_component;
         v_component.getDocument().addDocumentListener(createDocumentListener());
      }
      else if (p_component instanceof JCheckBox)
      {
         final JCheckBox v_component = (JCheckBox) p_component;
         v_component.addItemListener(new ItemListener()
         {
            @Override
            public void itemStateChanged (final ItemEvent p_e)
            {
               fireModification(true, p_e);
            }
         });
      }
      else if (p_component instanceof JComboBox)
      {
         final JComboBox v_component = (JComboBox) p_component;
         v_component.addItemListener(new ItemListener()
         {
            @Override
            public void itemStateChanged (final ItemEvent p_e)
            {
               fireModification(true, p_e);
            }
         });
      }
      else if (p_component instanceof SpiDatePanel)
      {
         final SpiDatePanel v_component = (SpiDatePanel) p_component;
         registerFieldForModification(v_component.getDateField());
      }
      else if (p_component instanceof JTextArea)
      {
         final JTextArea v_component = (JTextArea) p_component;
         v_component.getDocument().addDocumentListener(createDocumentListener());
      }
      else if (p_component instanceof JRadioButton)
      {
         final JRadioButton v_component = (JRadioButton) p_component;
         v_component.addItemListener(new ItemListener()
         {
            @Override
            public void itemStateChanged (final ItemEvent p_e)
            {
               fireModification(true, p_e);
            }
         });
      }
      else if (p_component instanceof JTable)
      {
         final JTable v_component = (JTable) p_component;
         v_component.getModel().addTableModelListener(new TableModelListener()
         {
            @Override
            public void tableChanged (final TableModelEvent p_e)
            {
               fireModification(true, p_e);
            }
         });
      }
      else if (p_component instanceof SpiTableScrollPane)
      {
         if (((SpiTableScrollPane<?>) p_component).getViewport().getView() instanceof JTable)
         {
            registerFieldForModification(((SpiTableScrollPane<?>) p_component).getViewport().getView());
         }
      }
      else if (p_component instanceof SpiStringArea)
      {
         registerFieldForModification(((SpiStringArea) p_component).getTextArea());
      }
   }

   /**
    * @return un DocumentListener pour écouter les modifications sur un champ de saisie
    */
   private DocumentListener createDocumentListener ()
   {
      return new DocumentListener()
      {
         @Override
         public void removeUpdate (final DocumentEvent p_e)
         {
            fireModification(true, p_e);
         }

         @Override
         public void insertUpdate (final DocumentEvent p_e)
         {
            fireModification(true, p_e);
         }

         @Override
         public void changedUpdate (final DocumentEvent p_e)
         {
            // RAS
         }
      };
   }

}
