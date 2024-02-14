/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui;

import static org.junit.jupiter.api.Assertions.fail;

import fr.spi4j.exception.Spi4jValidationException;
import fr.spi4j.persistence.entity.ColumnsNames_Itf;
import fr.spi4j.persistence.entity.Entity_Itf;
import fr.spi4j.ui.graal.UserView;
import fr.spi4j.ui.mvp.PresenterAdapter;
import fr.spi4j.ui.mvp.Presenter_Abs;
import fr.spi4j.ui.mvp.SpiFlowManager_Abs;
import fr.spi4j.ui.mvp.View_Itf;
import fr.spi4j.ui.mvp.binding.BindedPresenterEntity_Abs;
import fr.spi4j.ui.mvp.rda.PresenterId;
import fr.spi4j.ui.mvp.rda.RichViewsAssociation;

/**
 * Classe qui contient des classes d'exemples pour les tests.
 * 
 * @author MINARM
 */
public class SampleEntity {

	/**
	 * L'interface définissant le contrat de persistance pour le type Telephone.
	 * 
	 * @author MINARM
	 */
	public interface HumainEntity_Itf extends Entity_Itf<Long> {
		// CONSTANTES

		// Start of user code Constantes HumainEntity_Itf

		// End of user code

		// METHODES ABSTRAITES

		/**
		 * Obtenir nom.
		 * 
		 * @return nom.
		 */
		String getNom();

		/**
		 * Affecter nom.
		 * 
		 * @param p_nom (In)(*) pro.
		 */
		void setNom(final String p_nom);

		/**
		 * Obtenir prenom.
		 * 
		 * @return prenom.
		 */
		String getPrenom();

		/**
		 * Affecter prenom.
		 * 
		 * @param p_prenom (In)(*) prenom.
		 */
		void setPrenom(final String p_prenom);

		// Start of user code Methodes HumainEntity_Itf

		// End of user code

	}

	/**
	 * Entity 'Humain'.
	 * 
	 * @author MINARM
	 */
	static public class HumainEntity implements HumainEntity_Itf {
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
		@Override
		public String getNom() {
			return _nom;
		}

		/**
		 * @param p_nom le nom
		 */
		@Override
		public void setNom(final String p_nom) {
			_nom = p_nom;
		}

		/**
		 * @return le prénom
		 */
		@Override
		public String getPrenom() {
			return _prenom;
		}

		/**
		 * @param p_prenom le prénom
		 */
		@Override
		public void setPrenom(final String p_prenom) {
			_prenom = p_prenom;
		}

		@Override
		public String toString() {
			return String.valueOf(getNom()) + ' ' + getPrenom();
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
	static enum HumainEntityColumns_Enum implements ColumnsNames_Itf {
		// id
		id("Humain_id", "HUMAIN_ID", Long.class, true, -1, true),
		// libelle
		nom("nom", "NOM", String.class, true, -1, false),
		// trigramme
		prenom("prenom", "PRENOM", String.class, true, -1, false);

		/**
		 * Le nom physique de la table.
		 */
		public static final String c_tableName = "HUMAIN";

		/** Le nom logique de la colonne. */
		private final String _logicalColumnName;

		/** Le nom physique de la colonne. */
		private final String _physicalColumnName;

		/** Le type associé à la colonne. */
		private final Class<?> _typeColumn;

		/** Est-ce que la saisie de la valeur est obligatoire pour cette colonne? */
		private final boolean _mandatory;

		/** La taille de la colonne. */
		private final int _size;

		/** Est-ce que la colonne est la clé primaire? */
		private final boolean _id;

		/**
		 * Constructeur permettant de spécifier le type de la colonne.
		 * 
		 * @param p_logicalColumnName  (In)(*) Le nom logique de la colonne.
		 * @param p_physicalColumnName (In)(*) Le nom physique de la colonne.
		 * @param p_ClassType          (In)(*) Le type de la colonne.
		 * @param p_mandatory          (In)(*) Est-ce que la saisie de la valeur est
		 *                             obligatoire pour cette colonne?
		 * @param p_size               (In)(*) La taille de la colonne
		 * @param p_id                 (In)(*) Est-ce que la colonne est la clé
		 *                             primaire?
		 */
		private HumainEntityColumns_Enum(final String p_logicalColumnName, final String p_physicalColumnName,
				final Class<?> p_ClassType, final boolean p_mandatory, final int p_size, final boolean p_id) {
			_logicalColumnName = p_logicalColumnName;
			_physicalColumnName = p_physicalColumnName;
			_typeColumn = p_ClassType;
			_mandatory = p_mandatory;
			_size = p_size;
			_id = p_id;
		}

		@Override
		public String getLogicalColumnName() {
			return _logicalColumnName;
		}

		@Override
		public String getPhysicalColumnName() {
			return _physicalColumnName;
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
		public boolean isId() {
			return _id;
		}

		@Override
		public Class<?> getTypeColumn() {
			return _typeColumn;
		}

		@Override
		public String toString() {
			return _physicalColumnName;
		}

		@Override
		public String getTableName() {
			return c_tableName;
		}

		@Override
		public String getCompletePhysicalName() {
			return getTableName() + '.' + getPhysicalColumnName();
		}
	}

	/**
	 * Exemple de présenteur pour les tests.
	 * 
	 * @author MINARM
	 */
	public static class SamplePresenterEntity extends BindedPresenterEntity_Abs<NoViewEntity, HumainEntity_Itf> {

		/**
		 * Constructeur sans paramètres pour ouvrir la vue via Graal et story.
		 */
		public SamplePresenterEntity() {
			this(null);
		}

		/**
		 * Constructeur.
		 * 
		 * @param p_parent le présenteur parent
		 */
		public SamplePresenterEntity(final Presenter_Abs<? extends View_Itf, ?> p_parent) {
			super(p_parent, null, new PresenterAdapter<SamplePresenterEntity>() {
				@Override
				public void beforeCreate(final SamplePresenterEntity p_presenter) {
					super.beforeCreate(p_presenter);
				}

				@Override
				public void afterCreate(final SamplePresenterEntity p_presenter) {
					super.afterCreate(p_presenter);
				}
			});
		}

		/**
		 * Constructeur.
		 * 
		 * @param p_parent le présenteur parent
		 * @param p_entity le entity
		 */
		public SamplePresenterEntity(final Presenter_Abs<? extends View_Itf, ?> p_parent,
				final HumainEntity_Itf p_entity) {
			super(p_parent, p_entity);
			fillEntityFromFields();
		}

		@Override
		protected Object doGenerateId() {
			return id1(getObjet());
		}

		/**
		 * Génère un id pour ce présenteur.
		 * 
		 * @param p_entity la personne affichée dans ce présenteur
		 * @return l'id de ce présenteur pour cette personne
		 */
		@PresenterId
		public static Long id1(final HumainEntity_Itf p_entity) {
			if (p_entity == null) {
				return -1L;
			}
			return p_entity.getId();
		}

		@Override
		public void initView() {
			// RAS
		}

		@Override
		public void initBindingEntityView() {
			// RAS
			bind(HumainEntityColumns_Enum.nom, new HasString_Itf() {
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
	 * Exemple de présenteur pour les tests sans générateur d'id.
	 * 
	 * @author MINARM
	 */
	public static class SamplePresenterEntityWithoutIdGenerator extends Presenter_Abs<NoViewEntity, HumainEntity_Itf> {

		/**
		 * Constructeur sans paramètres pour ouvrir la vue via Graal et story.
		 */
		public SamplePresenterEntityWithoutIdGenerator() {
			this(null);
		}

		/**
		 * Constructeur.
		 * 
		 * @param p_parent le présenteur parent
		 */
		public SamplePresenterEntityWithoutIdGenerator(final Presenter_Abs<? extends View_Itf, ?> p_parent) {
			super(p_parent, null, new PresenterAdapter<SamplePresenterEntityWithoutIdGenerator>() {
				@Override
				public void beforeCreate(final SamplePresenterEntityWithoutIdGenerator p_presenter) {
					super.beforeCreate(p_presenter);
				}

				@Override
				public void afterCreate(final SamplePresenterEntityWithoutIdGenerator p_presenter) {
					super.afterCreate(p_presenter);
				}
			});
			setObjet(null);
		}

		/**
		 * Constructeur.
		 * 
		 * @param p_parent le présenteur parent
		 * @param p_entity le entity
		 */
		public SamplePresenterEntityWithoutIdGenerator(final Presenter_Abs<? extends View_Itf, ?> p_parent,
				final HumainEntity_Itf p_entity) {
			super(p_parent, p_entity);
		}

		@Override
		public void initView() {
			// RAS
		}

		@Override
		protected String doGenerateTitle() {
			return "";
		}

	}

	/**
	 * Exemple de présenteur pour les tests avec générateur d'id privé.
	 * 
	 * @author MINARM
	 */
	public static class SamplePresenterEntityWithPrivateIdGenerator
			extends Presenter_Abs<NoViewEntity, HumainEntity_Itf> {

		/**
		 * Constructeur.
		 * 
		 * @param p_parent le présenteur parent
		 */
		public SamplePresenterEntityWithPrivateIdGenerator(final Presenter_Abs<? extends View_Itf, ?> p_parent) {
			super(p_parent);
		}

		/**
		 * Constructeur.
		 * 
		 * @param p_parent le présenteur parent
		 * @param p_entity le entity
		 */
		public SamplePresenterEntityWithPrivateIdGenerator(final Presenter_Abs<? extends View_Itf, ?> p_parent,
				final HumainEntity_Itf p_entity) {
			super(p_parent, p_entity);
		}

		@Override
		public void initView() {
			// RAS
		}

		/**
		 * Génère un id pour ce présenteur.
		 * 
		 * @param p_personneEntity la personne affichée dans ce présenteur
		 * @return l'id de ce présenteur pour cette personne
		 */
		@PresenterId
		private static Long id1(final HumainEntity_Itf p_personneEntity) {
			return p_personneEntity.getId();
		}

		@Override
		protected String doGenerateTitle() {
			return "";
		}

	}

	/**
	 * Exemple de présenteur pour les tests avec générateur d'id buggé.
	 * 
	 * @author MINARM
	 */
	public static class SamplePresenterEntityWithBuggedIdGenerator
			extends Presenter_Abs<NoViewEntity, HumainEntity_Itf> {

		/**
		 * Constructeur.
		 * 
		 * @param p_parent le présenteur parent
		 */
		public SamplePresenterEntityWithBuggedIdGenerator(final Presenter_Abs<? extends View_Itf, ?> p_parent) {
			super(p_parent);
		}

		/**
		 * Constructeur.
		 * 
		 * @param p_parent le présenteur parent
		 * @param p_entity le entity
		 */
		public SamplePresenterEntityWithBuggedIdGenerator(final Presenter_Abs<? extends View_Itf, ?> p_parent,
				final HumainEntity_Itf p_entity) {
			super(p_parent, p_entity);
		}

		@Override
		public void initView() {
			// RAS
		}

		/**
		 * Génère un id pour ce présenteur.
		 * 
		 * @param p_entity la personne affichée dans ce présenteur
		 * @return l'id de ce présenteur pour cette personne
		 */
		@PresenterId
		public static Long id1(final HumainEntity_Itf p_entity) {
			throw new RuntimeException("bug");
		}

		@Override
		protected String doGenerateTitle() {
			return "";
		}

	}

	/**
	 * Exemple de présenteur pour les tests avec générateur non static.
	 * 
	 * @author MINARM
	 */
	public static class SamplePresenterEntityWithNonStaticIdGenerator
			extends Presenter_Abs<NoViewEntity, HumainEntity_Itf> {

		/**
		 * Constructeur.
		 * 
		 * @param p_parent le présenteur parent
		 */
		public SamplePresenterEntityWithNonStaticIdGenerator(final Presenter_Abs<? extends View_Itf, ?> p_parent) {
			super(p_parent);
		}

		/**
		 * Constructeur.
		 * 
		 * @param p_parent le présenteur parent
		 * @param p_entity le entity
		 */
		public SamplePresenterEntityWithNonStaticIdGenerator(final Presenter_Abs<? extends View_Itf, ?> p_parent,
				final HumainEntity_Itf p_entity) {
			super(p_parent, p_entity);
		}

		@Override
		public void initView() {
			// RAS
		}

		/**
		 * Génère un id pour ce présenteur.
		 * 
		 * @param p_entity la personne affichée dans ce présenteur
		 * @return l'id de ce présenteur pour cette personne
		 */
		@PresenterId
		public Long id1(final HumainEntity_Itf p_entity) {
			return getObjet().getId();
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
	@UserView("NoViewEntity")
	public static class NoViewEntity implements View_Itf {

		@Override
		public String getTitle() {
			return "no title";
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
	 * Classe d'exemple pour ViewsAssociation.
	 * 
	 * @author MINARM
	 */
	public static class SampleViewsEntityAssociation extends RichViewsAssociation {

		/**
		 * Constructeur des associations de vues.
		 */
		public SampleViewsEntityAssociation() {
			addAssociation(SamplePresenterEntity.class, NoViewEntity.class);

			addAssociation(SamplePresenterEntityWithoutIdGenerator.class, NoViewEntity.class);
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

	/**
	 * Flow 1.
	 * 
	 * @author MINARM
	 */
	public static class Flow1Entity extends SpiFlowManager_Abs {

		/**
		 * Constructeur.
		 */
		public Flow1Entity() {
			super(null);
		}

		@Override
		protected void onStart() {
			// RAS
		}

	}
}
