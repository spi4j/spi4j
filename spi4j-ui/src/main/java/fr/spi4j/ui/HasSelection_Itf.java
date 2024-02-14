/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui;

import java.util.List;

/**
 * Interface de widget qui contient une liste de valeurs dont une est sélectionnable.
 * @param <TypeValue>
 *           Type de la valeur
 * @author MINARM
 */
public interface HasSelection_Itf<TypeValue> extends HasValue_Itf<TypeValue>, HasList_Itf<TypeValue>
{
   @Override
   TypeValue getValue ();

   @Override
   void setValue (TypeValue p_value);

   @Override
   List<TypeValue> getList ();

   @Override
   void setList (List<TypeValue> p_list);

}
