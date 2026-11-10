package fr.spi4j.ui.mvp.binding;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import fr.spi4j.Identifiable_Itf;
import fr.spi4j.business.dto.AttributesNames_Itf;
import fr.spi4j.exception.Spi4jRuntimeException;
import fr.spi4j.ui.HasValue_Itf;
import fr.spi4j.ui.mvp.PresenterListener_Itf;
import fr.spi4j.ui.mvp.Presenter_Abs;
import fr.spi4j.ui.mvp.View_Itf;

/**
 * L'ancêtre des présenteurs de vue avec gestion de binding des attributs.
 * Chaque instance de présenteur est lié à une et une seule instance de vue et à
 * une et une seule instance Dto.
 * 
 * @param <TypeView> le type de vue présentée
 * @param <TypeDto>  le type de DTO affiché, ou Object si aucun DTO
 * @author MINARM
 */
public abstract class BindedPresenter_Abs<TypeView extends View_Itf, TypeDto extends Identifiable_Itf<?>>
		extends Presenter_Abs<TypeView, TypeDto> {
	/**
	 * Les bindings entre attributs de DTO et champs de saisie.
	 */
	private List<Binding<TypeView, TypeDto>> _bindings;

	/**
	 * Constructeur du présenteur.
	 * 
	 * @param p_parent le présenteur parent
	 */
	public BindedPresenter_Abs(final Presenter_Abs<? extends View_Itf, ?> p_parent) {
		super(p_parent);
	}

	/**
	 * Constructeur du présenteur.
	 * 
	 * @param p_parent le présenteur parent
	 * @param p_dto    le dto de ce présenteur
	 */
	public BindedPresenter_Abs(final Presenter_Abs<? extends View_Itf, ?> p_parent, final TypeDto p_dto) {
		super(p_parent, p_dto);
	}

	/**
	 * Constructeur du présenteur.
	 * 
	 * @param p_parent        le présenteur parent
	 * @param p_dto           le dto de ce présenteur
	 * @param p_listener      le listener
	 * @param <TypePresenter> le type de presenter
	 */
	public <TypePresenter extends Presenter_Abs<TypeView, TypeDto>> BindedPresenter_Abs(
			final Presenter_Abs<? extends View_Itf, ?> p_parent, final TypeDto p_dto,
			final PresenterListener_Itf<TypePresenter> p_listener) {
		super(p_parent, p_dto, p_listener);
	}

	@Override
	protected void initPresenter() {
		super.initPresenter();
		_bindings = new ArrayList<Binding<TypeView, TypeDto>>();
		initBindingDtoView();
		// remplissage des champs à partir du DTO (aucune action si le DTO est null)
		fillFieldsFromDto();
		updateId();
		updateTitle();
	}

	/**
	 * Initialise un binding entre un attribut de DTO (p_attribute) et un champ de
	 * la vue (p_field). C'est la méthode getValue ou setValue qui sera appelée sur
	 * le champ de la vue.
	 * 
	 * @param p_attribute l'attribut d'un DTO
	 * @param p_field     le champ de la vue
	 */
	protected void bind(final AttributesNames_Itf p_attribute, final HasValue_Itf<?> p_field) {
		// on peut binder un attribut sur plusieurs champs, par exemple pour affichage
		// d'une même valeur dans plusieurs champs
		_bindings.add(new Binding<TypeView, TypeDto>(p_attribute, p_field, this));
	}

	/**
	 * Remplit les champs de la vue avec les données du DTO grâce aux bindings
	 * précédement initialisés.
	 */
	public void fillFieldsFromDto() {
		if (getObjet() != null) {
			// parcourt les bindings
			for (final Binding<TypeView, TypeDto> v_binding : _bindings) {
				v_binding.fillFieldFromAttribute(getObjet());
			}
		}
	}

	/**
	 * Met à jour le DTO à partir des données des champs de la vue.
	 */
	public void fillDtoFromFields() {
		// si le DTO est null
		TypeDto v_dto = getObjet();
		if (v_dto == null) {
			try {
				// instanciation du DTO
				final Class<TypeDto> v_typeDtoClass = getDtoClass();
				v_dto = v_typeDtoClass.getDeclaredConstructor().newInstance();
			} catch (final Exception v_e) {
				throw new Spi4jRuntimeException(v_e,
						"Le DTO passé en paramètre pour remplir à partir des champs de l'IHM est null et n'a pas pu être instancié",
						"Vérifier l'instance courante du DTO de ce présenteur, ainsi que le type générique");
			}
		}
		// parcourt les bindings
		for (final Binding<TypeView, TypeDto> v_binding : _bindings) {
			v_binding.fillAttributeFromField(v_dto);
		}
		// on met à jour le DTO de ce présenteur
		setObjet(v_dto);
	}

	/**
	 * @return La classe du DTO à partir du type générique spécifié par l'appelant.
	 */
	@SuppressWarnings("unchecked")
	Class<TypeDto> getDtoClass() {
		return (Class<TypeDto>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
	}

	/**
	 * Associe les champs de cette vue avec un DTO.
	 */
	abstract protected void initBindingDtoView();

	/**
	 * Méthode permettant de vider les associations de binding déjà existantes.
	 * <p>
	 * Cette méthode est utile dans le cas où l'écran se raffraichit et que de
	 * nouveaux composants à binder apparaissent.
	 */
	protected void clearBindings() {
		_bindings.clear();
	}

}
