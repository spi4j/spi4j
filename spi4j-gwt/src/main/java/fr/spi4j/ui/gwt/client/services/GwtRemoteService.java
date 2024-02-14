package fr.spi4j.ui.gwt.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;

import fr.spi4j.exception.Spi4jValidationException;
import fr.spi4j.ws.xto.Xto_Itf;

/**
 * Interface de service proposant les opérations de base sur les XTO (CRUD).
 * @author MINARM
 * @param <TypeId>
 *           le type d'id du XTO
 * @param <TypeXto>
 *           le type du XTO
 */
public interface GwtRemoteService<TypeId, TypeXto extends Xto_Itf<TypeId>> extends RemoteService
{
   /**
    * Créer ou mettre à jour un tuple.
    * @param p_xto
    *           (In/Out)(*) Le tuple à insérer est obligatoire. Si sa clé primaire est renseigné, il est mis à jour, sinon il est créé
    * @return le tuple inséré (avec notamment sa clé primaire renseignée) ou mis à jour (avec par exemple sa date de dernière mise à jour renseignée))
    * @throws Spi4jValidationException
    *            Si le tuple n'est pas valide
    */
   abstract public TypeXto save (TypeXto p_xto) throws Spi4jValidationException;

   /**
    * Obtenir le tuple à partir de sa clé primaire.
    * @param p_id
    *           (In)(*) La clé primaire de l'objet recherché.
    * @return Le tuple désiré.
    */
   abstract public TypeXto findById (TypeId p_id);

   /**
    * Obtenir la liste des tuples.
    * @return La liste des tuples.
    */
   abstract public List<TypeXto> findAll ();

   /**
    * Supprimer un tuple à partir de sa clé primaire.
    * @param p_xto
    *           (In)(*) Le tuple à supprimer avec la clé primaire.
    * @throws Spi4jValidationException
    *            Si le tuple n'est pas valide
    */
   abstract public void delete (TypeXto p_xto) throws Spi4jValidationException;
}
