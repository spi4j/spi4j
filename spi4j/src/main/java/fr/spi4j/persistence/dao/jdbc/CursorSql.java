/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.persistence.dao.jdbc;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.spi4j.exception.Spi4jRuntimeException;
import fr.spi4j.persistence.dao.Binary;
import fr.spi4j.persistence.dao.Cursor_Abs;

/**
 * Implémentation de curseur à base de ResultSet JDBC.
 * @author MINARM
 */
class CursorSql extends Cursor_Abs
{
   // classe et constructeur visibilité package pour que la classe ne puisse être instanciée
   // et utilisée que par les classes de ce package
   private final ResultSet _ResultSet;

   private final int _nbLignesMax;

   private int _nbLignesRead;

   private List<String> _columnNames;

   /**
    * Constructeur (visibilité package).
    * @param p_sqlQuery
    *           (In)(*) La requête SQL (obligatoire).
    * @param p_ResultSet
    *           (In)(*) Le ResultSet JDBC (obligatoire).
    * @param p_nbLignesMax
    *           (In) Le nombre de lignes maximum à ramener (-1 : toutes les lignes)
    * @param p_nbLignesStart
    *           (In) Nombre de lignes à passer avant de commencer la lecture.
    */
   CursorSql (final String p_sqlQuery, final ResultSet p_ResultSet, final int p_nbLignesMax, final int p_nbLignesStart)
   {
      super(p_sqlQuery);
      // Si les attributs ne sont pas renseignés
      if (p_ResultSet == null)
      {
         throw new IllegalArgumentException("Le paramètre 'resultSet' est obligatoire, il ne peut pas être 'null'\n");
      }
      _ResultSet = p_ResultSet;

      try
      {
         // filtre nbLignesStart : on passe autant de lignes que souhaité (négatif ou 0 : filtre désactivé)
         for (int v_i = 0; v_i < p_nbLignesStart; v_i++)
         {
            if (!_ResultSet.next())
            {
               // si il n'y a pas assez de lignes pour nbLignesStart, alors pas la peine de continuer (et l'application aura simplement un curseur sans ligne à lire)
               break;
            }
         }
      }
      catch (final SQLException v_ex)
      {
         throw createException(v_ex, null);
      }

      _nbLignesMax = p_nbLignesMax;
      // _nbLignesRead initialisé à 0 par java
   }

   @Override
   public boolean isClosed ()
   {
      try
      {
         return _ResultSet.isClosed();
      }
      catch (final SQLException v_ex)
      {
         throw createException(v_ex, null);
      }
   }

   @Override
   public void close ()
   {
      Connection v_connection = null;
      try
      {
         final Statement v_statement = _ResultSet.getStatement();
         v_connection = v_statement.getConnection();
         try
         {
            _ResultSet.close();
         }
         finally
         {
            v_statement.close();
         }
      }
      catch (final SQLException v_ex)
      {
         throw new Spi4jRuntimeException(v_ex, "Problème lors de la fermeture: " + v_ex.getMessage(),
                  "Regarder les logs de votre moteur de persistance (query=" + getQuery() + " - connection="
                           + v_connection + ")");
      }
   }

   @Override
   public boolean next ()
   {
      try
      {
         // filte nbLignesMax : si on a lu le nombre max de lignes souhaitées, on dit qu'il n'y a plus de lignes à lire (négatif : filtre désactivé)
         if (_nbLignesMax >= 0 && _nbLignesRead >= _nbLignesMax)
         {
            return false;
         }
         final boolean v_result = _ResultSet.next();
         if (v_result)
         {
            // si on a bien lu une nouvelle ligne, on incrémente le compteur qui servira ci-dessus
            _nbLignesRead++;
         }
         return v_result;
      }
      catch (final SQLException v_ex)
      {
         throw createException(v_ex, null);
      }
   }

   @Override
   public List<String> getColumnNames ()
   {
      if (_columnNames == null)
      {
         try
         {
            final ResultSetMetaData v_metaData = _ResultSet.getMetaData();
            final int v_columnCount = v_metaData.getColumnCount();
            final List<String> v_list = new ArrayList<>(v_columnCount);
            for (int v_column = 1; v_column <= v_columnCount; v_column++)
            {
               v_list.add(v_metaData.getColumnName(v_column));
            }
            _columnNames = Collections.unmodifiableList(v_list);
         }
         catch (final SQLException v_ex)
         {
            throw createException(v_ex, null);
         }
      }
      return _columnNames;
   }

   @Override
   public String getString (final String p_columnName)
   {
      // cela retourne null si null en base de données selon javadoc du ResultSet
      try
      {
         return _ResultSet.getString(p_columnName);
      }
      catch (final SQLException v_ex)
      {
         throw createException(v_ex, p_columnName);
      }
   }

   @Override
   public Boolean getBoolean (final String p_columnName)
   {
      try
      {
         // Remarque : le driver jdbc gère en interne les booléens en base de données ainsi que 0 / 1 et "0" / "1" (par exemple, en Oracle)
         final boolean v_value = _ResultSet.getBoolean(p_columnName);
         final Boolean v_return;
         if (_ResultSet.wasNull())
         {
            v_return = null;
         }
         else
         {
            v_return = v_value;
         }
         return v_return;
      }
      catch (final SQLException v_ex)
      {
         throw createException(v_ex, p_columnName);
      }
   }

   @Override
   public Integer getInteger (final String p_columnName)
   {
      try
      {
         final int v_value = _ResultSet.getInt(p_columnName);
         if (_ResultSet.wasNull())
         {
            return null;
         }
         else
         {
            return v_value;
         }
      }
      catch (final SQLException v_ex)
      {
         throw createException(v_ex, p_columnName);
      }
   }

   @Override
   public Long getLong (final String p_columnName)
   {
      try
      {
         final long v_value = _ResultSet.getLong(p_columnName);
         if (_ResultSet.wasNull())
         {
            return null;
         }
         else
         {
            return v_value;
         }
      }
      catch (final SQLException v_ex)
      {
         throw createException(v_ex, p_columnName);
      }
   }

   @Override
   public Float getFloat (final String p_columnName)
   {
      try
      {
         final float v_value = _ResultSet.getFloat(p_columnName);
         if (_ResultSet.wasNull())
         {
            return null;
         }
         else
         {
            return v_value;
         }
      }
      catch (final SQLException v_ex)
      {
         throw createException(v_ex, p_columnName);
      }
   }

   @Override
   public Double getDouble (final String p_columnName)
   {
      try
      {
         final double v_value = _ResultSet.getDouble(p_columnName);
         if (_ResultSet.wasNull())
         {
            return null;
         }
         else
         {
            return v_value;
         }
      }
      catch (final SQLException v_ex)
      {
         throw createException(v_ex, p_columnName);
      }
   }

   @Override
   public java.util.Date getDate (final String p_columnName)
   {
      try
      {
         final Timestamp v_value = _ResultSet.getTimestamp(p_columnName);
         if (_ResultSet.wasNull())
         {
            return null;
         }
         else
         {
            // il faut absolument convertir en java.util.Date
            // même si java.sql.Timestamp hérite de java.util.Date
            // afin d'éviter les nombreux problèmes sur les égalités et les comparaisons entre
            // instances de java.util.Date (venant de l'application)
            // et instances de java.sql.Timestamp (venant du ResultSet)
            return new java.util.Date(v_value.getTime()); 
         }
      }
      catch (final SQLException v_ex)
      {
         throw createException(v_ex, p_columnName);
      }
   }

   @Override
   public java.sql.Timestamp getTimestamp (final String p_columnName)
   {
      try
      {
         final Timestamp v_value = _ResultSet.getTimestamp(p_columnName);
         if (_ResultSet.wasNull())
         {
            return null;
         }
         else
         {
            return v_value;
         }
      }
      catch (final SQLException v_ex)
      {
         throw createException(v_ex, p_columnName);
      }
   }

   @Override
   public java.sql.Time getTime (final String p_columnName)
   {
      try
      {
         final Time v_value = _ResultSet.getTime(p_columnName);
         if (_ResultSet.wasNull())
         {
            return null;
         }
         else
         {
            return v_value;
         }
      }
      catch (final SQLException v_ex)
      {
         throw createException(v_ex, p_columnName);
      }
   }

   @Override
   public BigDecimal getBigDecimal (final String p_columnName)
   {
      try
      {
         final BigDecimal v_value = _ResultSet.getBigDecimal(p_columnName);
         if (_ResultSet.wasNull())
         {
            return null;
         }
         else
         {
            return v_value;
         }
      }
      catch (final SQLException v_ex)
      {
         throw createException(v_ex, p_columnName);
      }
   }

   @Override
   public Byte getByte (final String p_columnName)
   {
      try
      {
         final byte v_value = _ResultSet.getByte(p_columnName);
         if (_ResultSet.wasNull())
         {
            return null;
         }
         else
         {
            return v_value;
         }
      }
      catch (final SQLException v_ex)
      {
         throw createException(v_ex, p_columnName);
      }
   }

   @Override
   public Blob getBlob (final String p_columnName)
   {
      try
      {
         final Blob v_value = _ResultSet.getBlob(p_columnName);
         if (_ResultSet.wasNull())
         {
            return null;
         }
         else
         {
            return v_value;
         }
      }
      catch (final SQLException v_ex)
      {
         throw createException(v_ex, p_columnName);
      }
   }

   @Override
   @Deprecated
   public Clob getClob (final String p_columnName)
   {
      throw new UnsupportedOperationException("Non implémenté pour éviter des problèmes de charset. Utiliser getBlob.");
   }

   @Override
   public Binary getBinary (final String p_columnName)
   {
      try
      {
         final Blob v_Blobvalue = _ResultSet.getBlob(p_columnName);
         if (_ResultSet.wasNull())
         {
            return null;
         }
         else
         {
            return new Binary(v_Blobvalue.getBinaryStream());
         }
      }
      catch (final SQLException v_ex)
      {
         throw createException(v_ex, p_columnName);
      }
   }

   /**
    * Crée une exception Spi4j runtime à partir d'une exception sql, en incluant dans le message de solution la requête sql et la connexion jdbc.
    * @param p_ex
    *           Exception sql
    * @param p_columnName
    *           Nom de la colonne demandée s'il y en a une
    * @return Exception spi4j runtime
    */
   private Spi4jRuntimeException createException (final SQLException p_ex, final String p_columnName)
   {
      Connection v_connection;
      try
      {
         v_connection = _ResultSet.getStatement().getConnection();
      }
      catch (final Exception v_e)
      {
         v_connection = null;
      }
      String v_message = p_ex.getMessage();
      if (p_ex.getErrorCode() == 17006 && p_columnName != null)
      {
         // Oracle dit seulement "Nom de colonne non valide" (errorCode 17006), donc on ajoute le nom de la colonne demandée dans le message de l'erreur
         v_message += " (\"" + p_columnName + "\")";
      }
      return new Spi4jRuntimeException(p_ex, v_message, "Regarder les logs de votre moteur de persistance (query="
               + getQuery() + " - connection=" + v_connection + ")");
   }
}
