/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui;

import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Method;

import fr.spi4j.business.dto.AttributesNames_Itf;
import fr.spi4j.business.dto.DtoAttributeHelper;
import fr.spi4j.business.dto.Dto_Itf;
import fr.spi4j.exception.Spi4jValidationException;
import fr.spi4j.ui.graal.UserView;
import fr.spi4j.ui.mvp.PresenterAdapter;
import fr.spi4j.ui.mvp.Presenter_Abs;
import fr.spi4j.ui.mvp.SpiFlowManager_Abs;
import fr.spi4j.ui.mvp.View_Itf;
import fr.spi4j.ui.mvp.binding.BindedPresenter_Abs;
import fr.spi4j.ui.mvp.rda.PresenterId;
import fr.spi4j.ui.mvp.rda.RichViewsAssociation;

/**
 * Classe qui contient des classes d'exemples pour les tests.
 * @author MINARM
 */
public class Sample
{

   /**
    * DTO 'Personne'.
    * @author MINARM
    */
   static public class HumainDto implements Dto_Itf<Long>
   {
      private static final long serialVersionUID = 1L;

      private Long _id;

      private String _nom;

      private String _prenom;

      @Override
      public Long getId ()
      {
         return _id;
      }

      @Override
      public void setId (final Long p_id)
      {
         _id = p_id;
      }

      /**
       * @return le nom
       */
      public String getNom ()
      {
         return _nom;
      }

      /**
       * @param p_nom
       *           le nom
       */
      public void setNom (final String p_nom)
      {
         _nom = p_nom;
      }

      /**
       * @return le prénom
       */
      public String getPrenom ()
      {
         return _prenom;
      }

      /**
       * @param p_prenom
       *           le prénom
       */
      public void setPrenom (final String p_prenom)
      {
         _prenom = p_prenom;
      }

      @Override
      public String toString ()
      {
         return String.valueOf(getNom()) + ' ' + getPrenom();
      }

      @Override
      public void validate () throws Spi4jValidationException
      {
         // RAS
      }
   }

   /**
    * L'énumération définissant les informations de chaque attribut pour le type 'Humain'.
    * @author MINARM
    */
   static enum HumainAttributes_Enum implements AttributesNames_Itf
   {
      // id
      id("id", "id", Long.class, true, -1),
      // libelle
      nom("nom", "le nom", String.class, true, -1),
      // trigramme
      prenom("prenom", "le prenom", String.class, true, -1);

      /** Le nom de l'attribut. */
      private final String _name;

      /** La description de l'attribut. */
      private final String _description;

      /** Le type associé à l'attribut. */
      private final Class<?> _type;

      /** Est-ce que la saisie de la valeur est obligatoire pour cet attribut ? */
      private final boolean _mandatory;

      /** La taille de l'attribut. */
      private final int _size;

      /** La méthode du getter. */
      private Method _getterMethod;

      /** La méthode du setter. */
      private Method _setterMethod;

      /**
       * Constructeur.
       * @param p_name
       *           (In)(*) Le nom de l'attribut.
       * @param p_description
       *           (In)(*) La description de l'attribut.
       * @param p_ClassType
       *           (In)(*) Le type de l'attribut.
       * @param p_mandatory
       *           (In)(*) Est-ce que la saisie de la valeur est obligatoire pour cette colonne?
       * @param p_size
       *           (In)(*) La taille de la colonne
       */
      private HumainAttributes_Enum (final String p_name, final String p_description, final Class<?> p_ClassType,
               final boolean p_mandatory, final int p_size)
      {
         _name = p_name;
         _description = p_description;
         _type = p_ClassType;
         _mandatory = p_mandatory;
         _size = p_size;
      }

      @Override
      public String getName ()
      {
         return _name;
      }

      @Override
      public String getDescription ()
      {
         return _description;
      }

      @Override
      public Class<?> getType ()
      {
         return _type;
      }

      @Override
      public boolean isMandatory ()
      {
         return _mandatory;
      }

      @Override
      public int getSize ()
      {
         return _size;
      }

      @Override
      public String toString ()
      {
         return _description;
      }

      @Override
      public Method getGetterMethod ()
      {
         if (_getterMethod == null)
         {
            _getterMethod = DtoAttributeHelper.getInstance().getGetterMethodForAttribute(HumainDto.class, getName());
         }
         return _getterMethod;
      }

      @Override
      public Method getSetterMethod ()
      {
         if (_setterMethod == null)
         {
            _setterMethod = DtoAttributeHelper.getInstance().getSetterMethodForAttribute(HumainDto.class, getName(),
                     getType());
         }
         return _setterMethod;
      }
   }

   /**
    * Exemple de présenteur pour les tests.
    * @author MINARM
    */
   public static class SamplePresenter extends BindedPresenter_Abs<NoView, HumainDto>
   {

      /**
       * Constructeur sans paramètres pour ouvrir la vue via Graal et story.
       */
      public SamplePresenter ()
      {
         this(null);
      }

      /**
       * Constructeur.
       * @param p_parent
       *           le présenteur parent
       */
      public SamplePresenter (final Presenter_Abs<? extends View_Itf, ?> p_parent)
      {
         super(p_parent, null, new PresenterAdapter<SamplePresenter>()
         {
            @Override
            public void beforeCreate (final SamplePresenter p_presenter)
            {
               super.beforeCreate(p_presenter);
            }

            @Override
            public void afterCreate (final SamplePresenter p_presenter)
            {
               super.afterCreate(p_presenter);
            }
         });
      }

      /**
       * Constructeur.
       * @param p_parent
       *           le présenteur parent
       * @param p_dto
       *           le DTO
       */
      public SamplePresenter (final Presenter_Abs<? extends View_Itf, ?> p_parent, final HumainDto p_dto)
      {
         super(p_parent, p_dto);
         fillDtoFromFields();
      }

      @Override
      protected Object doGenerateId ()
      {
         return id1(getObjet());
      }

      /**
       * Génère un id pour ce présenteur.
       * @param p_personneDto
       *           la personne affichée dans ce présenteur
       * @return l'id de ce présenteur pour cette personne
       */
      @PresenterId
      public static Long id1 (final HumainDto p_personneDto)
      {
         if (p_personneDto == null)
         {
            return -1L;
         }
         return p_personneDto.getId();
      }

      @Override
      public void initView ()
      {
         // RAS
      }

      @Override
      public void initBindingDtoView ()
      {
         // RAS
         bind(HumainAttributes_Enum.nom, new HasString_Itf()
         {
            private String _value;

            @Override
            public void setValue (final String p_value)
            {
               _value = p_value;
            }

            @Override
            public String getValue ()
            {
               return _value;
            }
         });
      }

      @Override
      protected String doGenerateTitle ()
      {
         return "";
      }

   }

   /**
    * Exemple de présenteur pour les tests sans générateur d'id.
    * @author MINARM
    */
   public static class SamplePresenterWithoutIdGenerator extends Presenter_Abs<NoView, HumainDto>
   {

      /**
       * Constructeur sans paramètres pour ouvrir la vue via Graal et story.
       */
      public SamplePresenterWithoutIdGenerator ()
      {
         this(null);
      }

      /**
       * Constructeur.
       * @param p_parent
       *           le présenteur parent
       */
      public SamplePresenterWithoutIdGenerator (final Presenter_Abs<? extends View_Itf, ?> p_parent)
      {
         super(p_parent, null, new PresenterAdapter<SamplePresenterWithoutIdGenerator>()
         {
            @Override
            public void beforeCreate (final SamplePresenterWithoutIdGenerator p_presenter)
            {
               super.beforeCreate(p_presenter);
            }

            @Override
            public void afterCreate (final SamplePresenterWithoutIdGenerator p_presenter)
            {
               super.afterCreate(p_presenter);
            }
         });
         setObjet(null);
      }

      /**
       * Constructeur.
       * @param p_parent
       *           le présenteur parent
       * @param p_dto
       *           le DTO
       */
      public SamplePresenterWithoutIdGenerator (final Presenter_Abs<? extends View_Itf, ?> p_parent,
               final HumainDto p_dto)
      {
         super(p_parent, p_dto);
      }

      @Override
      public void initView ()
      {
         // RAS
      }

      @Override
      protected String doGenerateTitle ()
      {
         return "";
      }

   }

   /**
    * Exemple de présenteur pour les tests avec générateur d'id privé.
    * @author MINARM
    */
   public static class SamplePresenterWithPrivateIdGenerator extends Presenter_Abs<NoView, HumainDto>
   {

      /**
       * Constructeur.
       * @param p_parent
       *           le présenteur parent
       */
      public SamplePresenterWithPrivateIdGenerator (final Presenter_Abs<? extends View_Itf, ?> p_parent)
      {
         super(p_parent);
      }

      /**
       * Constructeur.
       * @param p_parent
       *           le présenteur parent
       * @param p_dto
       *           le DTO
       */
      public SamplePresenterWithPrivateIdGenerator (final Presenter_Abs<? extends View_Itf, ?> p_parent,
               final HumainDto p_dto)
      {
         super(p_parent, p_dto);
      }

      @Override
      public void initView ()
      {
         // RAS
      }

      /**
       * Génère un id pour ce présenteur.
       * @param p_personneDto
       *           la personne affichée dans ce présenteur
       * @return l'id de ce présenteur pour cette personne
       */
      @PresenterId
      private static Long id1 (final HumainDto p_personneDto)
      {
         return p_personneDto.getId();
      }

      @Override
      protected String doGenerateTitle ()
      {
         return "";
      }

   }

   /**
    * Exemple de présenteur pour les tests avec générateur d'id buggé.
    * @author MINARM
    */
   public static class SamplePresenterWithBuggedIdGenerator extends Presenter_Abs<NoView, HumainDto>
   {

      /**
       * Constructeur.
       * @param p_parent
       *           le présenteur parent
       */
      public SamplePresenterWithBuggedIdGenerator (final Presenter_Abs<? extends View_Itf, ?> p_parent)
      {
         super(p_parent);
      }

      /**
       * Constructeur.
       * @param p_parent
       *           le présenteur parent
       * @param p_dto
       *           le DTO
       */
      public SamplePresenterWithBuggedIdGenerator (final Presenter_Abs<? extends View_Itf, ?> p_parent,
               final HumainDto p_dto)
      {
         super(p_parent, p_dto);
      }

      @Override
      public void initView ()
      {
         // RAS
      }

      /**
       * Génère un id pour ce présenteur.
       * @param p_personneDto
       *           la personne affichée dans ce présenteur
       * @return l'id de ce présenteur pour cette personne
       */
      @PresenterId
      public static Long id1 (final HumainDto p_personneDto)
      {
         throw new RuntimeException("bug");
      }

      @Override
      protected String doGenerateTitle ()
      {
         return "";
      }

   }

   /**
    * Exemple de présenteur pour les tests avec générateur non static.
    * @author MINARM
    */
   public static class SamplePresenterWithNonStaticIdGenerator extends Presenter_Abs<NoView, HumainDto>
   {

      /**
       * Constructeur.
       * @param p_parent
       *           le présenteur parent
       */
      public SamplePresenterWithNonStaticIdGenerator (final Presenter_Abs<? extends View_Itf, ?> p_parent)
      {
         super(p_parent);
      }

      /**
       * Constructeur.
       * @param p_parent
       *           le présenteur parent
       * @param p_dto
       *           le DTO
       */
      public SamplePresenterWithNonStaticIdGenerator (final Presenter_Abs<? extends View_Itf, ?> p_parent,
               final HumainDto p_dto)
      {
         super(p_parent, p_dto);
      }

      @Override
      public void initView ()
      {
         // RAS
      }

      /**
       * Génère un id pour ce présenteur.
       * @param p_personneDto
       *           la personne affichée dans ce présenteur
       * @return l'id de ce présenteur pour cette personne
       */
      @PresenterId
      public Long id1 (final HumainDto p_personneDto)
      {
         return getObjet().getId();
      }

      @Override
      protected String doGenerateTitle ()
      {
         return "";
      }

   }

   /**
    * Aucune vue.
    * @author MINARM
    */
   @UserView("NoView")
   public static class NoView implements View_Itf
   {

      @Override
      public String getTitle ()
      {
         return "no title";
      }

      @Override
      public void setTitle (final String p_title)
      {
         // nothing to do
      }

      @Override
      public void addView (final View_Itf p_view)
      {
         // nothing to do
      }

      @Override
      public void restoreView (final View_Itf p_view)
      {
         // nothing to do
      }

      @Override
      public void removeView (final View_Itf p_view)
      {
         // nothing to do
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
      public Presenter_Abs<? extends View_Itf, ?> getPresenter ()
      {
         return null;
      }

   }

   /**
    * Classe d'exemple pour ViewsAssociation.
    * @author MINARM
    */
   public static class SampleViewsAssociation extends RichViewsAssociation
   {

      /**
       * Constructeur des associations de vues.
       */
      public SampleViewsAssociation ()
      {
         addAssociation(SamplePresenter.class, NoView.class);
         addAssociation(SamplePresenterWithoutIdGenerator.class, NoView.class);
      }

      @SuppressWarnings("unchecked")
      @Override
      public <TypeView extends View_Itf> TypeView getViewForPresenter (final Presenter_Abs<TypeView, ?> p_presenter)
      {
         final Class<? extends View_Itf> v_viewClass = getAssociation((Class<? extends Presenter_Abs<? extends View_Itf, ?>>) p_presenter
                  .getClass());

         try
         {
            return (TypeView) v_viewClass.getDeclaredConstructor().newInstance();
         }
         catch (final Exception v_e)
         {
            fail(v_e.getMessage());
         }

         return null;
      }
   }

   /**
    * Flow 1.
    * @author MINARM
    */
   public static class Flow1 extends SpiFlowManager_Abs
   {

      /**
       * Constructeur.
       */
      public Flow1 ()
      {
         super(null);
      }

      @Override
      protected void onStart ()
      {
         // RAS
      }

   }
}
