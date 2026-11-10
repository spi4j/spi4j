/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.report;

/**
 * Conteneur de données : couple [nom de la colonne, type de la colonne]
 * @author MINARM
 */
public class DataContainer
{
   private final String _columnName;

   private final String _columnType;

   /**
    * Constructeur de la classe DataContainer.
    * @param p_columnName
    *           (In) Le nom de la colonne.
    * @param p_columnType
    *           (In) Le type de la colonne.
    */
   public DataContainer (final String p_columnName, final String p_columnType)
   {
      _columnName = p_columnName;
      _columnType = p_columnType;
   }

   /**
    * Permet d'obtenir le nom de la colonne.
    * @return Le nom de la colonne.
    */
   public String get_columnName ()
   {
      return _columnName;
   }

   /**
    * Permet d'obtenir le type de la colonne.
    * @return Le type de la colonne.
    */
   public String get_columnType ()
   {
      return _columnType;
   }
}
