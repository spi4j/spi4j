/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.persistence.dao;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Classe abstraite de curseur pour une requête base de données.<br/>
 * L'instance du curseur est créée par l'instance d'un DAO qui choisit l'implémentation (CursorSql ou CursorJpa par exemple).
 * @author MINARM
 */
public abstract class Cursor_Abs
{
   private static final Logger c_log = LogManager.getLogger(Cursor_Abs.class);

   private final String _query;

   private final Exception _exceptionWithOpeningStackTrace;

   /**
    * Constructeur (visibilité package).
    * @param p_query
    *           (In)(*) La requête (obligatoire).
    */
   protected Cursor_Abs (final String p_query)
   {
      super();
      // Si les attributs ne sont pas renseignés
      if (p_query == null)
      {
         throw new IllegalArgumentException("Le paramètre 'sql' est obligatoire, il ne peut pas être 'null'\n");
      }
      _query = p_query;
      _exceptionWithOpeningStackTrace = new Exception();
   }

   /**
    * Retourne la requête (non null).
    * @return String
    */
   public String getQuery ()
   {
      return _query;
   }

   /**
    * Retourne un booléen selon que le curseur a été fermé.<br/>
    * Un curseur est fermé quand sa méthode "close()" a été appelé.
    * @return boolean
    */
   public abstract boolean isClosed ();

   /**
    * Libère les ressources JDBC et base de données associées à ce curseur.<br/>
    * Cette méthode doit obligatoirement être appelée dans un bloc finally dès que le curseur n'est plus utilisé. <h4>Exemple:</h4>
    *
    * <pre>
    *  final Cursor_Abs v_cursor = v_XxxDao.executeQuery(...);
    *  try
    *  {
    *     ...
    *  }
    *  finally
    *  {
    *     v_cursor.close();
    *  }
    * </pre>
    *
    * <br/>
    * L'appel de la méthode close() sur un curseur déjà fermé n'a pas d'effet.
    */
   public abstract void close ();

   /**
    * Place le curseur sur la ligne suivante et retourne un booléen selon que cette ligne suivante existe. <br/>
    * Le curseur est initialement positionné avant la première ligne, la méthode next() doit être appelée avant de pouvoir lire les données de cette première ligne. <br/>
    * Si next() retourne false, les appels de méthodes get* lancent une exception.
    * @return boolean <h4>Exemple:</h4>
    *
    *         <pre>
    *  final Cursor_Abs v_cursor = v_XxxDao.executeQuery(...);
    *  try
    *  {
    *     while (v_cursor.next())
    *     {
    *        ...
    *     }
    *  }
    *  finally
    *  {
    *     v_cursor.close();
    *  }
    * </pre>
    */
   public abstract boolean next ();

   /**
    * Retourne la liste des noms de colonnes tels qu'indiqués dans la requête de ce curseur.<br/>
    * Ce nom correspond au nom des colonnes dans les tables sélectionnées ou au nom des alias si la syntaxe "colonne as alias" est utilisée dans la requête.<br/>
    * Ces noms de colonnes peuvent ensuite être utilisée dans les méthodes get* pour récupérer les valeurs.
    * @return Liste des noms de colonnes.
    */
   public abstract List<String> getColumnNames ();

   /**
    * Retourne en chaîne de caractères la valeur de la colonne dont le nom est en paramètre pour la ligne courante.<br/>
    * Retourne null si la valeur est null dans la requête.
    * @param p_columnName
    *           Nom de la colonne ou de l'alias dans la requête
    * @return String
    */
   public abstract String getString (String p_columnName);

   /**
    * Retourne en booléen la valeur de la colonne dont le nom est en paramètre pour la ligne courante.<br/>
    * Retourne null si la valeur est null dans la requête.<br/>
    * Retourne Boolean.TRUE si la valeur est 0 (entier) ou "0" (chaîne) dans la requête et retourne Boolean.FALSE si la valeur est 1 (entier) ou "1" (chaîne) dans la requête.
    * @param p_columnName
    *           Nom de la colonne ou de l'alias dans la requête
    * @return String
    */
   public abstract Boolean getBoolean (String p_columnName);

   /**
    * Retourne en entier la valeur de la colonne dont le nom est en paramètre pour la ligne courante.<br/>
    * Retourne null si la valeur est null dans la requête.
    * @param p_columnName
    *           Nom de la colonne ou de l'alias dans la requête
    * @return String
    */
   public abstract Integer getInteger (String p_columnName);

   /**
    * Retourne en entier long la valeur de la colonne dont le nom est en paramètre pour la ligne courante.<br/>
    * Retourne null si la valeur est null dans la requête.
    * @param p_columnName
    *           Nom de la colonne ou de l'alias dans la requête
    * @return String
    */
   public abstract Long getLong (String p_columnName);

   /**
    * Retourne en décimal la valeur de la colonne dont le nom est en paramètre pour la ligne courante.<br/>
    * Retourne null si la valeur est null dans la requête.
    * @param p_columnName
    *           Nom de la colonne ou de l'alias dans la requête
    * @return String
    */
   public abstract Float getFloat (String p_columnName);

   /**
    * Retourne en décimal long la valeur de la colonne dont le nom est en paramètre pour la ligne courante.<br/>
    * Retourne null si la valeur est null dans la requête.
    * @param p_columnName
    *           Nom de la colonne ou de l'alias dans la requête
    * @return String
    */
   public abstract Double getDouble (String p_columnName);

   /**
    * Retourne en java.util.Date (et non java.sql.Timestamp) la valeur de la colonne dont le nom est en paramètre pour la ligne courante.<br/>
    * Retourne null si la valeur est null dans la requête.
    * @param p_columnName
    *           Nom de la colonne ou de l'alias dans la requête
    * @return String
    */
   public abstract java.util.Date getDate (String p_columnName);

      /**
    * Retourne en java.sql.Timestamp la valeur de la colonne dont le nom est en paramètre pour la ligne courante.<br/>
    * Retourne null si la valeur est null dans la requête.
    * @param p_columnName
    *           Nom de la colonne ou de l'alias dans la requête
    * @return String
    */
   public abstract java.sql.Timestamp getTimestamp (String p_columnName);

   /**
    * Retourne en java.sql.Time la valeur de la colonne dont le nom est en paramètre pour la ligne courante.<br/>
    * Retourne null si la valeur est null dans la requête.
    * @param p_columnName
    *           Nom de la colonne ou de l'alias dans la requête
    * @return String
    */
   public abstract java.sql.Time getTime (String p_columnName);

   /**
    * Retourne en BigDecimal la valeur de la colonne dont le nom est en paramètre pour la ligne courante.<br/>
    * Retourne null si la valeur est null dans la requête.
    * @param p_columnName
    *           Nom de la colonne ou de l'alias dans la requête
    * @return String
    */
   public abstract BigDecimal getBigDecimal (String p_columnName);

   /**
    * Retourne en Byte la valeur de la colonne dont le nom est en paramètre pour la ligne courante.<br/>
    * Retourne null si la valeur est null dans la requête.
    * @param p_columnName
    *           Nom de la colonne ou de l'alias dans la requête
    * @return String
    */
   public abstract Byte getByte (String p_columnName);

   /**
    * Retourne en Blob (stockage binaire) la valeur de la colonne dont le nom est en paramètre pour la ligne courante.<br/>
    * Retourne null si la valeur est null dans la requête.
    * @param p_columnName
    *           Nom de la colonne ou de l'alias dans la requête
    * @return String
    */
   public abstract Blob getBlob (String p_columnName);

   /**
    * getClob.
    * @param p_columnName
    *           Nom de la colonne ou de l'alias dans la requête
    * @return String
    * @deprecated Non implémenté pour éviter des problèmes de charset : utiliser getBlob.
    */
   @Deprecated
   public Clob getClob (final String p_columnName)
   {
      throw new UnsupportedOperationException("Non implémenté pour éviter des problèmes de charset. Utiliser getBlob.");
   }

   /**
    * Retourne en Binary (stockage binaire) la valeur de la colonne dont le nom est en paramètre pour la ligne courante.<br/>
    * Retourne null si la valeur est null dans la requête.
    * @param p_columnName
    *           Nom de la colonne ou de l'alias dans la requête
    * @return Binary
    */
   public abstract Binary getBinary (String p_columnName);

   @Override
   @SuppressWarnings("deprecation")
   protected void finalize () throws Throwable
   {
      try
      {
         // Si curseur non fermé
         if (!isClosed())
         {
            final StringWriter v_stringWriter = new StringWriter();
            _exceptionWithOpeningStackTrace.printStackTrace(new PrintWriter(v_stringWriter));
            final String v_openingStackTrace = v_stringWriter.toString();

            println("*************************************************************************************************");
            println("ERREUR APPLICATION : Le curseur \"" + _query + "\" n'a pas été fermé");
            println("SOLUTION           : Ajouter un appel de la forme \"...} finally { _"
                     + getClass().getSimpleName()
                     + ".close() }\" à la fin de la requête se trouvant à la ligne indiquée dans la stack trace ci-dessous :");
            println(v_openingStackTrace);
            println("*************************************************************************************************");

            close();
         }
      }
      finally
      {
         super.finalize();
      }
   }

   /**
    * Afficher dans la console.
    * @param p_message
    *           (In) Le message à afficher.
    */
   private void println (final String p_message)
   {
      c_log.warn(p_message);
   }
}
