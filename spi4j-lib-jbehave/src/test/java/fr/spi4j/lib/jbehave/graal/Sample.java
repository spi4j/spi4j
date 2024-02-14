/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.jbehave.graal;

import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import fr.spi4j.business.dto.AttributesNames_Itf;
import fr.spi4j.business.dto.DtoAttributeHelper;
import fr.spi4j.business.dto.Dto_Itf;
import fr.spi4j.exception.Spi4jValidationException;
import fr.spi4j.ui.HasBoolean_Itf;
import fr.spi4j.ui.HasDate_Itf;
import fr.spi4j.ui.HasDouble_Itf;
import fr.spi4j.ui.HasInteger_Itf;
import fr.spi4j.ui.HasMultipleSelection_Itf;
import fr.spi4j.ui.HasSelection_Itf;
import fr.spi4j.ui.HasString_Itf;
import fr.spi4j.ui.graal.Field;
import fr.spi4j.ui.graal.UserAction;
import fr.spi4j.ui.graal.UserView;
import fr.spi4j.ui.mvp.Presenter_Abs;
import fr.spi4j.ui.mvp.View_Itf;
import fr.spi4j.ui.mvp.binding.BindedPresenter_Abs;
import fr.spi4j.ui.mvp.rda.PresenterId;
import fr.spi4j.ui.mvp.rda.RichViewsAssociation;

/**
 * Classe qui contient des classes d'exemples pour les tests.
 * 
 * @author MINARM
 */
public class Sample {

	/**
	 * DTO 'Personne'.
	 * 
	 * @author MINARM
	 */
	static public class HumainDto implements Dto_Itf<Long> {
		private static final long serialVersionUID = 1L;

		private Long _id;

		private String _nom;

		private String _prenom;

		@Override
		public Long getId() {
			return _id;
		}

		@Override
		public void setId(final Long p_id) {
			_id = p_id;
		}

		/**
		 * @return le nom
		 */
		public String get_nom() {
			return _nom;
		}

		/**
		 * @param p_nom le nom
		 */
		public void set_nom(final String p_nom) {
			_nom = p_nom;
		}

		/**
		 * @return le prénom
		 */
		public String get_prenom() {
			return _prenom;
		}

		/**
		 * @param p_prenom le prénom
		 */
		public void set_prenom(final String p_prenom) {
			_prenom = p_prenom;
		}

		@Override
		public String toString() {
			return String.valueOf(get_nom()) + ' ' + get_prenom();
		}

		@Override
		public void validate() throws Spi4jValidationException {
			// RAS
		}
	}

	/**
	 * L'énumération définissant les informations de chaque attribut pour le type
	 * 'Humain'.
	 * 
	 * @author MINARM
	 */
	static enum HumainAttributes_Enum implements AttributesNames_Itf {
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
		 * 
		 * @param p_name        (In)(*) Le nom de l'attribut.
		 * @param p_description (In)(*) La description de l'attribut.
		 * @param p_ClassType   (In)(*) Le type de l'attribut.
		 * @param p_mandatory   (In)(*) Est-ce que la saisie de la valeur est
		 *                      obligatoire pour cette colonne?
		 * @param p_size        (In)(*) La taille de la colonne
		 */
		private HumainAttributes_Enum(final String p_name, final String p_description, final Class<?> p_ClassType,
				final boolean p_mandatory, final int p_size) {
			_name = p_name;
			_description = p_description;
			_type = p_ClassType;
			_mandatory = p_mandatory;
			_size = p_size;
		}

		@Override
		public String getName() {
			return _name;
		}

		@Override
		public String getDescription() {
			return _description;
		}

		@Override
		public Class<?> getType() {
			return _type;
		}

		@Override
		public boolean isMandatory() {
			return _mandatory;
		}

		@Override
		public int getSize() {
			return _size;
		}

		@Override
		public Method getGetterMethod() {
			if (_getterMethod == null) {
				_getterMethod = DtoAttributeHelper.getInstance().getGetterMethodForAttribute(HumainDto.class,
						getName());
			}
			return _getterMethod;
		}

		@Override
		public Method getSetterMethod() {
			if (_setterMethod == null) {
				_setterMethod = DtoAttributeHelper.getInstance().getSetterMethodForAttribute(HumainDto.class, getName(),
						getType());
			}
			return _setterMethod;
		}

		@Override
		public String toString() {
			return _description;
		}
	}

	/**
	 * Exemple de présenteur pour les tests.
	 * 
	 * @author MINARM
	 */
	public static class SamplePresenter extends BindedPresenter_Abs<NoView, HumainDto> {

		// initialisé à false
		private boolean _actionDone;

		/**
		 * Constructeur.
		 */
		public SamplePresenter() {
			super(null);
		}

		/**
		 * UserAction.
		 */
		@UserAction(UserAction.c_BOUTON + "Action")
		public void doAction() {
			_actionDone = true;
		}

		/**
		 * @return true si l'action a été faite, false sinon
		 */
		public boolean isActionDone() {
			return _actionDone;
		}

		/**
		 * @return l'id standard
		 */
		@PresenterId
		public static String presenterId() {
			return "";
		}

		/**
		 * Génère un id pour ce présenteur.
		 * 
		 * @param p_personneDto la personne affichée dans ce présenteur
		 * @return l'id de ce présenteur pour cette personne
		 */
		@PresenterId
		public static Long id1(final HumainDto p_personneDto) {
			return p_personneDto.getId();
		}

		@Override
		public void initView() {
			// RAS
		}

		@Override
		public void initBindingDtoView() {
			// RAS
			bind(HumainAttributes_Enum.nom, new HasString_Itf() {
				private String _value;

				@Override
				public void setValue(final String p_value) {
					_value = p_value;
				}

				@Override
				public String getValue() {
					return _value;
				}
			});
		}

		@Override
		protected String doGenerateTitle() {
			return "";
		}

	}

	/**
	 * Aucune vue.
	 * 
	 * @author MINARM
	 */
	@UserView("NoView")
	static class NoView implements View_Itf {

		private final HasString_Itf _champ = new HasString_Itf() {

			private String _champValue;

			@Override
			public void setValue(final String p_value) {
				_champValue = p_value;
			}

			@Override
			public String getValue() {
				return _champValue;
			}
		};

		private final HasDate_Itf _date = new HasDate_Itf() {

			private Date _dateValue;

			@Override
			public void setValue(final Date p_value) {
				_dateValue = p_value;
			}

			@Override
			public Date getValue() {
				return _dateValue;
			}
		};

		private final HasInteger_Itf _int = new HasInteger_Itf() {

			private Integer _intValue;

			@Override
			public void setValue(final Integer p_value) {
				_intValue = p_value;
			}

			@Override
			public Integer getValue() {
				return _intValue;
			}
		};

		private final HasDouble_Itf _double = new HasDouble_Itf() {

			private Double _doubleValue;

			@Override
			public void setValue(final Double p_value) {
				_doubleValue = p_value;
			}

			@Override
			public Double getValue() {
				return _doubleValue;
			}
		};

		private final HasBoolean_Itf _boolean = new HasBoolean_Itf() {

			private Boolean _boolValue;

			@Override
			public void setValue(final Boolean p_value) {
				_boolValue = p_value;
			}

			@Override
			public Boolean getValue() {
				return _boolValue;
			}
		};

		private final HasSelection_Itf<String> _liste = new HasSelection_Itf<String>() {

			private String _value;

			private List<String> _listeValues = Arrays.asList("abc", "def", "ghi");

			@Override
			public void setValue(final String p_value) {
				_value = p_value;
			}

			@Override
			public void setList(final List<String> p_list) {
				_listeValues = p_list;
			}

			@Override
			public String getValue() {
				return _value;
			}

			@Override
			public List<String> getList() {
				return _listeValues;
			}
		};

		private final HasMultipleSelection_Itf<String> _listeMulti = new HasMultipleSelection_Itf<String>() {

			private List<String> _values;

			private List<String> _listeValues = Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9");

			@Override
			public void setValue(final List<String> p_values) {
				_values = p_values;
			}

			@Override
			public void setList(final List<String> p_list) {
				_listeValues = p_list;
			}

			@Override
			public List<String> getValue() {
				return _values;
			}

			@Override
			public List<String> getList() {
				return _listeValues;
			}
		};

		@Override
		public String getTitle() {
			return "NoViewTitle";
		}

		/**
		 * @return champ
		 */
		@Field("Champ")
		public HasString_Itf getChamp() {
			return _champ;
		}

		/**
		 * @return date
		 */
		@Field("Date")
		public HasDate_Itf getDate() {
			return _date;
		}

		/**
		 * @return int
		 */
		@Field("Int")
		public HasInteger_Itf getInt() {
			return _int;
		}

		/**
		 * @return double
		 */
		@Field("Double")
		public HasDouble_Itf getDouble() {
			return _double;
		}

		/**
		 * @return boolean
		 */
		@Field("Boolean")
		public HasBoolean_Itf getBoolean() {
			return _boolean;
		}

		/**
		 * @return champ
		 */
		@Field("Liste")
		public HasSelection_Itf<String> getListe() {
			return _liste;
		}

		/**
		 * @return champ
		 */
		@Field("ListeMulti")
		public HasMultipleSelection_Itf<String> getListeMulti() {
			return _listeMulti;
		}

		/**
		 * Wrong Field.
		 * 
		 * @return ""
		 */
		@Field("WrongField")
		public String getWrongField() {
			return "";
		}

		@Override
		public void setTitle(final String p_title) {
			// nothing to do
		}

		@Override
		public void addView(final View_Itf p_view) {
			// nothing to do
		}

		@Override
		public void restoreView(final View_Itf p_view) {
			// nothing to do
		}

		@Override
		public void removeView(final View_Itf p_view) {
			// nothing to do
		}

		@Override
		public boolean isModal() {
			return false;
		}

		@Override
		public void beforeClose() {
			// Aucun traitement par défaut.
			// Méthode à surcharger si besoin
		}

		@Override
		public Presenter_Abs<? extends View_Itf, ?> getPresenter() {
			return null;
		}

	}

	/**
	 * Presenteur2.
	 * 
	 * @author MINARM
	 */
	public static class SamplePresenter2 extends Presenter_Abs<NoView2, HumainDto> {

		/**
		 * Constructeur.
		 */
		public SamplePresenter2() {
			super(null);
		}

		@Override
		public void initView() {
			// RAS
		}

		@Override
		protected String doGenerateTitle() {
			return null;
		}
	}

	/**
	 * Vue sans UserView.
	 * 
	 * @author MINARM
	 */
	static class NoView2 implements View_Itf {

		@Override
		public String getTitle() {
			return null;
		}

		@Override
		public void setTitle(final String p_title) {
			// RAS
		}

		@Override
		public void addView(final View_Itf p_view) {
			// RAS
		}

		@Override
		public void restoreView(final View_Itf p_view) {
			// RAS
		}

		@Override
		public void removeView(final View_Itf p_view) {
			// RAS
		}

		@Override
		public boolean isModal() {
			return false;
		}

		@Override
		public void beforeClose() {
			// Aucun traitement par défaut.
			// Méthode à surcharger si besoin
		}

		@Override
		public Presenter_Abs<? extends View_Itf, ?> getPresenter() {
			return null;
		}
	}

	/**
	 * Classe d'exemple pour ViewsAssociation.
	 * 
	 * @author MINARM
	 */
	static class SampleViewsAssociation extends RichViewsAssociation {

		/**
		 * Constructeur.
		 */
		public SampleViewsAssociation() {
			super();
			addAssociation(SamplePresenter.class, NoView.class);
			addAssociation(SamplePresenter2.class, NoView2.class);

		}

		@SuppressWarnings("unchecked")
		@Override
		public <TypeView extends View_Itf> TypeView getViewForPresenter(final Presenter_Abs<TypeView, ?> p_presenter) {
			final Class<? extends View_Itf> v_viewClass = getAssociation(
					(Class<? extends Presenter_Abs<? extends View_Itf, ?>>) p_presenter.getClass());

			try {
				return (TypeView) v_viewClass.getDeclaredConstructor().newInstance();
			} catch (final Exception v_e) {
				fail(v_e.getMessage());
			}

			return null;
		}
	}
}
