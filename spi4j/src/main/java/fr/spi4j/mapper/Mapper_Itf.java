/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.mapper;

import java.util.List;

import fr.spi4j.business.dto.Dto_Itf;
import fr.spi4j.ws.xto.Xto_Itf;

/**
 * Interface de services de conversion entre un objet métier et un objet xml.
 * @author MINARM
 * @param <TypeDto>
 *           Le type d'objet métier.
 * @param <TypeXto>
 *           Le type d'objet xml.
 */
abstract public interface Mapper_Itf<TypeDto extends Dto_Itf<?>, TypeXto extends Xto_Itf<?>>
{

   /**
    * Convertit un objet xml typé en objet métier.
    * @param p_xto
    *           (In)(*) L'objet xml typé.
    * @return L'objet métier typé correspondant.
    */
   TypeDto convertXtoToDto (TypeXto p_xto);

   /**
    * Convertit un objet métier typé en objet xml.
    * @param p_dto
    *           (In)(*) L'objet métier typé.
    * @return L'objet xml typé correspondant.
    */
   TypeXto convertDtoToXto (TypeDto p_dto);

   /**
    * Convertit une liste d'objets métiers typés en liste d'objets xml.
    * @param p_tab_xto
    *           (In)(*) La liste des objets métiers typés.
    * @return La liste des objets xml typés correspondant.
    */
   List<TypeDto> convertListXtoToListDto (List<TypeXto> p_tab_xto);

   /**
    * Convertit une liste d'objets xml typés en liste d'objets métiers.
    * @param p_tab_dto
    *           (In)(*) La liste des objets xml typés.
    * @return La liste des objets métiers typés correspondant.
    */
   List<TypeXto> convertListDtoToListXto (List<TypeDto> p_tab_dto);

   /**
    * Convertit un objet xml en objet métier typé.
    * @param p_xto
    *           (In)(*) L'objet xml non typé.
    * @return L'objet métier typé.
    */
   TypeDto convertXtoItfToDto (Xto_Itf<?> p_xto);

   /**
    * Convertit un objet métier en objet xml typé.
    * @param p_dto
    *           (In)(*) L'objet métier non typé.
    * @return L'objet xml typé.
    */
   TypeXto convertDtoItfToXto (Dto_Itf<?> p_dto);

}
