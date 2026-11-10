package fr.spi4j.ui.gwt.server.soalight;

import java.util.List;

import fr.spi4j.entity.Service_Itf;
import fr.spi4j.exception.Spi4jValidationException;
import fr.spi4j.mapper.EntityMapper_Itf;
import fr.spi4j.persistence.entity.Entity_Itf;
import fr.spi4j.ui.gwt.client.services.GwtRemoteService;
import fr.spi4j.ws.xto.Xto_Itf;

/**
 * Implémentation de service proposant les opérations de base sur les XTO (CRUD).
 * @author MINARM
 * @param <TypeId>
 *           le type d'id du XTO
 * @param <TypeXto>
 *           le type du XTO
 * @param <TypeEntity>
 *           l' ENTITY correspondant au XTO
 */
public abstract class GwtRemoteServiceImpl<TypeId, TypeXto extends Xto_Itf<TypeId>, TypeEntity extends Entity_Itf<TypeId>>
         extends SpiRemoteServiceImpl implements GwtRemoteService<TypeId, TypeXto>
{

   private static final long serialVersionUID = 1L;

   @Override
   public TypeXto save (final TypeXto p_xto) throws Spi4jValidationException
   {
      return getMapper().convertEntityItfToXto(getService().save(getMapper().convertXtoItfToEntity(p_xto)));
   }

   @Override
   public TypeXto findById (final TypeId p_id)
   {
      return getMapper().convertEntityItfToXto(getService().findById(p_id));
   }

   @Override
   public List<TypeXto> findAll ()
   {
      return getMapper().convertListEntityToListXto(getService().findAll());
   }

   @Override
   public void delete (final TypeXto p_xto) throws Spi4jValidationException
   {
      getService().delete(getMapper().convertXtoItfToEntity(p_xto));
   }

   /**
    * @return le mapper permettant de transformer le ENTITY en XTO et vice-versa
    */
   protected abstract EntityMapper_Itf<TypeEntity, TypeXto> getMapper ();

   /**
    * @return le service réel proposant les opérations sur l'Entity
    */
   protected abstract Service_Itf<TypeId, TypeEntity> getService ();

}
