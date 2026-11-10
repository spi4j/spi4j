/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.lib.jbehave;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import fr.spi4j.ReflectUtil;

/**
 * Proxy qui stocke les instances des DTOs créés durant la story.
 * 
 * @author MINARM
 * @param <TypeService> Type du service
 */
public final class SaveDtoProxy<TypeService> implements InvocationHandler {

	private final Object _delegate;

	/**
	 * Constructeur privé.
	 * 
	 * @param p_delegate l'objet proxysé
	 */
	private SaveDtoProxy(final TypeService p_delegate) {
		super();
		_delegate = p_delegate;
	}

	@Override
	public Object invoke(final Object p_proxy, final Method p_method, final Object[] p_args) throws Throwable {
		// on invoque le service
		final Object v_obj = ReflectUtil.invokeMethod(p_method, _delegate, p_args);
		final SpiStory_Abs v_story = SpiStoryContextHandler.get_story();
		if (v_story != null && v_story.hasToBeSaved(p_proxy, p_method, p_args)) {
			v_story.saveDto(v_obj);
		}
		return v_obj;
	}

	/**
	 * Création d'un proxy sur une classe
	 * 
	 * @param <TypeService> Le type du service
	 * @param p_delegate    Le service proxysé
	 * @return Le proxy du service
	 */
	@SuppressWarnings("unchecked")
	public static <TypeService> TypeService createProxy(final TypeService p_delegate) {
		final SaveDtoProxy<TypeService> v_Proxy = new SaveDtoProxy<>(p_delegate);

		return (TypeService) Proxy.newProxyInstance(p_delegate.getClass().getClassLoader(),
				p_delegate.getClass().getInterfaces(), v_Proxy);
	}

	/**
	 * Initialise le prochain identifiant et le prochain type pour l'objet à stocker
	 * l'objet dans la story.
	 * 
	 * @param p_nextIdentifiant le prochain identifiant
	 * @param p_nextType        le prochain type
	 */
	public static void setNextTypeToSave(final String p_nextIdentifiant, final String p_nextType) {
		SpiStoryContextHandler.get_story().setNextTypeToSave(p_nextIdentifiant, p_nextType);
	}
}
