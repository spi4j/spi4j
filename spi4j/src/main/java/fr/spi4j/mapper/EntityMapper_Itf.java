/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.mapper;

import java.util.List;

import fr.spi4j.persistence.entity.Entity_Itf;
import fr.spi4j.ws.xto.Xto_Itf;

/**
 * Interface de services de conversion entre un objet persitant et un objet xml.
 * @author MINARM
 * @param <TypeEntity>
 *           Le type d'objet persistant.
 * @param <TypeXto>
 *           Le type d'objet xml.
 */
abstract public interface EntityMapper_Itf<TypeEntity extends Entity_Itf<?>, TypeXto extends Xto_Itf<?>>
{

   /**
    * Convertit un objet xml typé en objet métier.
    * @param p_xto
    *           (In)(*) L'objet xml typé.
    * @return L'objet métier typé correspondant.
    */
   TypeEntity convertXtoToEntity (TypeXto p_xto);

   /**
    * Convertit un objet persitant typé en objet xml.
    * @param p_entity
    *           (In)(*) L'objet persitant typé.
    * @return L'objet xml typé correspondant.
    */
   TypeXto convertEntityToXto (TypeEntity p_entity);

   /**
    * Convertit une liste d'objets persitant typés en liste d'objets xml.
    * @param p_tab_xto
    *           (In)(*) La liste des objets métiers typés.
    * @return La liste des objets xml typés correspondant.
    */
   List<TypeEntity> convertListXtoToListEntity (List<TypeXto> p_tab_xto);

   /**
    * Convertit une liste d'objets xml typés en liste d'objets persitant.
    * @param p_tab_entity
    *           (In)(*) La liste des objets xml typés.
    * @return La liste des objets persistant typés correspondant.
    */
   List<TypeXto> convertListEntityToListXto (List<TypeEntity> p_tab_entity);

   /**
    * Convertit un objet xml en objet persitant typé.
    * @param p_xto
    *           (In)(*) L'objet xml non typé.
    * @return L'objet persitant typé.
    */
   TypeEntity convertXtoItfToEntity (Xto_Itf<?> p_xto);

   /**
    * Convertit un objet persitant en objet xml typé.
    * @param p_entity
    *           (In)(*) L'objet persitant non typé.
    * @return L'objet xml typé.
    */
   TypeXto convertEntityItfToXto (Entity_Itf<?> p_entity);

}
