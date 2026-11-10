/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j;

/**
 * Factory de création de proxys qui seront positionnés sur les services, côté client ou côté serveur.
 * @author MINARM
 */
public interface ProxyFactory_Itf
{

   /**
    * @param <TypeService>
    *           le type de service
    * @param p_interfaceService
    *           l'interface du service
    * @param p_serviceClassName
    *           la classe de l'instance du service à encapsuler (si côté serveur)
    * @return le service proxysé
    */
   <TypeService> TypeService getProxiedService (final Class<TypeService> p_interfaceService,
            final String p_serviceClassName);

}
