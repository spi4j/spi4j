/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui;

import java.util.List;

/**
 * Interface générique qui pourra être héritée et mockable pour un widget qui possède une liste de valeurs.
 * @param <TypeValue>
 *           Le type de valeur dans la liste
 * @author durandbe
 */
public interface HasList_Itf<TypeValue>
{

   /**
    * Retourne la liste de valeur possible dans le widget.
    * @return la liste de valeur possible dans le widget
    */
   List<TypeValue> getList ();

   /**
    * Définit la liste de valeurs possibles dans le widget.
    * @param p_list
    *           la liste des valeurs possibles
    */
   void setList (List<TypeValue> p_list);

}
