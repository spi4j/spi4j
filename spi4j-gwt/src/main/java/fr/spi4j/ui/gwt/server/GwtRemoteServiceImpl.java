package fr.spi4j.ui.gwt.server;

import java.util.List;

import fr.spi4j.business.Service_Itf;
import fr.spi4j.business.dto.Dto_Itf;
import fr.spi4j.exception.Spi4jValidationException;
import fr.spi4j.mapper.Mapper_Itf;
import fr.spi4j.ui.gwt.client.services.GwtRemoteService;
import fr.spi4j.ws.xto.Xto_Itf;

/**
 * Implémentation de service proposant les opérations de base sur les XTO (CRUD).
 * @author MINARM
 * @param <TypeId>
 *           le type d'id du XTO
 * @param <TypeXto>
 *           le type du XTO
 * @param <TypeDto>
 *           le DTO correspondant au XTO
 */
public abstract class GwtRemoteServiceImpl<TypeId, TypeXto extends Xto_Itf<TypeId>, TypeDto extends Dto_Itf<TypeId>>
         extends SpiRemoteServiceImpl implements GwtRemoteService<TypeId, TypeXto>
{

   private static final long serialVersionUID = 1L;

   @Override
   public TypeXto save (final TypeXto p_xto) throws Spi4jValidationException
   {
      return getMapper().convertDtoItfToXto(getService().save(getMapper().convertXtoItfToDto(p_xto)));
   }

   @Override
   public TypeXto findById (final TypeId p_id)
   {
      return getMapper().convertDtoItfToXto(getService().findById(p_id));
   }

   @Override
   public List<TypeXto> findAll ()
   {
      return getMapper().convertListDtoToListXto(getService().findAll());
   }

   @Override
   public void delete (final TypeXto p_xto) throws Spi4jValidationException
   {
      getService().delete(getMapper().convertXtoItfToDto(p_xto));
   }

   /**
    * @return le mapper permettant de transformer le DTO en XTO et vice-versa
    */
   protected abstract Mapper_Itf<TypeDto, TypeXto> getMapper ();

   /**
    * @return le service réel proposant les opérations sur le DTO
    */
   protected abstract Service_Itf<TypeId, TypeDto> getService ();

}
