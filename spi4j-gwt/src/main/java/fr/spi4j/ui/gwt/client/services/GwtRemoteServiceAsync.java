package fr.spi4j.ui.gwt.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import fr.spi4j.ws.xto.Xto_Itf;

/**
 * Interface de service proposant les opérations de base sur les XTO (CRUD).
 * @author MINARM
 * @param <TypeId>
 *           le type d'id du XTO
 * @param <TypeXto>
 *           le type du XTO
 */
public interface GwtRemoteServiceAsync<TypeId, TypeXto extends Xto_Itf<TypeId>>
{
   /**
    * Créer ou mettre à jour un tuple.
    * @param p_xto
    *           (In/Out)(*) Le tuple à insérer est obligatoire. Si sa clé primaire est renseigné, il est mis à jour, sinon il est créé
    * @param callback
    *           the callback to return le tuple inséré (avec notamment sa clé primaire renseignée) ou mis à jour (avec par exemple sa date de dernière mise à jour renseignée))
    */
   abstract public void save (TypeXto p_xto, AsyncCallback<TypeXto> callback);

   /**
    * Obtenir le tuple à partir de sa clé primaire.
    * @param p_id
    *           (In)(*) La clé primaire de l'objet recherché.
    * @param callback
    *           the callback to return Le tuple désiré.
    */
   abstract public void findById (TypeId p_id, AsyncCallback<TypeXto> callback);

   /**
    * Obtenir la liste des tuples.
    * @param callback
    *           the callback to return La liste des tuples.
    */
   abstract public void findAll (AsyncCallback<List<TypeXto>> callback);

   /**
    * Supprimer un tuple à partir de sa clé primaire.
    * @param p_xto
    *           (In)(*) Le tuple à supprimer avec la clé primaire.
    * @param callback
    *           the callback
    */
   abstract public void delete (TypeXto p_xto, AsyncCallback<Void> callback);
}
