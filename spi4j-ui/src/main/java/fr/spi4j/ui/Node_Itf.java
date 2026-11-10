/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui;

import java.util.List;

/**
 * Interface des éléments à afficher dans un composant arbre
 * @author adamar (DCSID)
 * @param <TypeValue>
 *           Type de la valeur
 */
public interface Node_Itf<TypeValue>
{
   /**
    * @return Le noeud parent
    */
   TypeValue getParentNode ();

   /**
    * @return la liste des noeuds fils
    */
   List<TypeValue> getListNode ();
}
