/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.report;

import java.io.OutputStream;
import java.util.List;

/**
 * Interface de création/gestion d'un document.
 * @author MINARM
 */
public interface Document_Itf
{
   /**
    * Permet d'ajouter une page au document.
    * @param p_pageToAdd
    *           La page à ajouter.
    */
   void addPage (Page_Itf p_pageToAdd);

   /**
    * Permet de retirer une page au document.
    * @param p_pageNumber
    *           Le numero de la page à retirer.
    */
   void removePage (final int p_pageNumber);

   /**
    * Retourne une page.
    * @param p_pageNumber
    *           Le numéro de la page.
    * @return La page.
    */
   Page_Itf get_page (int p_pageNumber);

   /**
    * Retourne l'ensemble des pages du document.
    * @return Toutes les pages du document.
    */
   List<Page_Itf> getPages ();

   /**
    * Retourne le nombre de pages.
    * @return Le nombre de pages.
    */
   int get_size ();

   /**
    * Permet de créer un document, avec les numéros de pages.
    * @param p_outputStream
    *           (In) Le flux de sortie du document.
    */
   void writeDocumentWithPageNumbers (OutputStream p_outputStream);

   /**
    * Permet de créer un document, sans ajouter les numéros de page.
    * @param p_outputStream
    *           (In) Le flux de sortie du document.
    */
   void writeDocumentWithoutPageNumbers (OutputStream p_outputStream);

   /**
    * Affecter le mot de passe nécessaire pour ouvrir le document.
    * @param p_password
    *           Le mot de passe.
    */
   void setUserPassword (final String p_password);

   /**
    * Lecture du positionnement d'une permission mot de passe permettant d'ouvrir le pdf généré.
    * @return Le mot de passe de l'utilisateur.
    */
   String getUserPassword ();

   /**
    * Positionnement d'une permission Nom de l'utilisateur qui peut modifier les droits sur le pdf généré.
    * @param p_ownerPassword
    *           Le nom de l'utilisateur (in)
    */
   void setOwnerPassword (final String p_ownerPassword);

   /**
    * Lecture du positionnement d'une permission Nom de l'utilisateur qui peut modifier les droits sur le pdf généré.
    * @return Le mot de passe du propriétaire
    */
   String getOwnerPassword ();

   /**
    * Retourne un booléen selon qu'une permission est active.
    * @param p_permission
    *           PermissionEnum
    * @return boolean
    */
   boolean isPermissionEnabled (Permission_Enum p_permission);

   /**
    * Active ou désactive une permission.
    * @param p_permission
    *           PermissionEnum
    * @param p_enabled
    *           boolean
    */
   void setPermissionEnabled (Permission_Enum p_permission, boolean p_enabled);
}
