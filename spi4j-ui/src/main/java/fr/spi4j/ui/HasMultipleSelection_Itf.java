/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ui;

import java.util.List;

/**
 * Interface de widget qui contient une liste de valeurs dont plusieurs sont sélectionnables (sélection multiple).
 * @param <TypeValueInList>
 *           Type de la valeur
 * @author MINARM
 */
public interface HasMultipleSelection_Itf<TypeValueInList> extends HasValue_Itf<List<TypeValueInList>>,
         HasList_Itf<TypeValueInList>
{
   @Override
   List<TypeValueInList> getValue ();

   @Override
   void setValue (List<TypeValueInList> p_listValue);

   @Override
   List<TypeValueInList> getList ();

   @Override
   void setList (List<TypeValueInList> p_list);

}
