/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.persistence.dao.jdbc;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.spi4j.Identifiable_Itf;
import fr.spi4j.exception.Spi4jRuntimeException;
import fr.spi4j.persistence.dao.DatabaseType;

/**
 * Classe utilitaire pour préparation de statement JDBC.
 * @author MINARM
 */
class JdbcStatementPreparator
{

   private static final Logger c_log = LogManager.getLogger(DaoJdbc_Abs.class);

   private static final int[] c_generated_key_column = new int[]
   {1 };

   private final String _initialSqlQuery;

   private final Map<String, ? extends Object> _map_value_by_name;

   private String _sql;

   private String _formattedSql;

   /** Liste des valeurs de paramètres de la requête sql, dans l'ordre. */
   private final List<Object> _parameterValues;

   /** Booléen pour indiquer qu'une erreur a été interceptée et qu'il faut reconstruire la requête */
   private boolean _hasException;

   /**
    * Constructeur.
    * @param p_sqlQuery
    *           (In)(*) La requête (obligatoire).
    * @param p_map_value_by_name
    *           (In) Map des valeurs des paramètres selon les noms de paramètres (non obligatoire).
    */
   public JdbcStatementPreparator (final String p_sqlQuery, final Map<String, ? extends Object> p_map_value_by_name)
   {
      super();
      _parameterValues = new ArrayList<>();
      _initialSqlQuery = p_sqlQuery;
      _map_value_by_name = p_map_value_by_name;
      _sql = _initialSqlQuery;
      _formattedSql = _initialSqlQuery;
   }

   /**
    * Prépare un statement jdbc à partir d'une requête sql et définit les valeurs des paramètres dans ce statement.
    * @param p_Connection
    *           (In)(*) Connexion jdbc
    * @param p_returnGeneratedKey
    *           boolén selon que le statement sera utilisé pour récupérer la clé primaire générée
    * @return PreparedStatement jdbc
    * @throws SQLException
    *            Si erreur sql
    */
   public PreparedStatement prepareStatement (final Connection p_Connection, final boolean p_returnGeneratedKey)
            throws SQLException
   {
      if (_initialSqlQuery.indexOf('?') != -1)
      {
         throw new Spi4jRuntimeException("Erreur 'prepareStatement'",
                  "La requête sql ne peut pas contenir le caractère '?' (p_sql=" + _initialSqlQuery + ")");
      }
      if (_map_value_by_name != null && !_map_value_by_name.isEmpty())
      {
         manageParametersInStatement();
      }

      c_log.debug(_formattedSql);

      final PreparedStatement v_Statement;
      if (p_returnGeneratedKey)
      {
         if (DatabaseType.findTypeForConnection(p_Connection) == DatabaseType.ORACLE)
         {
            // si Oracle, il faut utiliser un tableau donnant la colonne à retourner (c'est-à-dire l'id qui est la première colonne),
            // car Statement.RETURN_GENERATED_KEYS ne retournerait pas la colonne de la PK mais seulement le RowId interne à Oracle
            v_Statement = p_Connection.prepareStatement(_sql, c_generated_key_column);
         }
         else
         {
            // si postgresql, H2, mysql ou autre, on utilise simplement le RETURN_GENERATED_KEYS standard
            v_Statement = p_Connection.prepareStatement(_sql, Statement.RETURN_GENERATED_KEYS);
         }
      }
      else
      {
         // en l'occurrence, il n'y a pas d'injection sql ici car la requête sql est certes
         // construite dynamiquement mais seulement pour remplacer les paramètres nommés par de '?' pour jdbc
         v_Statement = p_Connection.prepareStatement(_sql);
      }
      for (int v_i = 1; v_i <= _parameterValues.size(); v_i++)
      {
         final Object v_parameterValue = _parameterValues.get(v_i - 1);
         setStatementValue(v_Statement, v_i, v_parameterValue);
      }

      return v_Statement;
   }

   /**
    * Gère les paramètres dans la requête
    */
   private void manageParametersInStatement ()
   {
      _sql = _initialSqlQuery;
      _formattedSql = _initialSqlQuery;

      final List<String> v_parameterNames = new ArrayList<>(_map_value_by_name.keySet());
      // on commence par la fin pour faire "parameter2" avant "parameter"
      Collections.sort(v_parameterNames);
      Collections.reverse(v_parameterNames);
      for (final String v_parameterName : v_parameterNames)
      {
         manageParameter(v_parameterName);
      }
   }

   /**
    * Remplace un paramètre dans une requête sql paramétrée.
    * @param p_parameterName
    *           le nom du paramètre
    */
   private void manageParameter (final String p_parameterName)
   {
      final Object v_parameterValue = _map_value_by_name.get(p_parameterName);
      final String v_key = ":" + p_parameterName;
      // on vérifie que tous les paramètres sont utilisés dans la requête sql
      if (!_sql.contains(v_key))
      {
         throw new IllegalArgumentException("Le paramètre nommé '" + p_parameterName
                  + "' n'est pas dans la requête sql '" + _initialSqlQuery + "'");
      }
      final Pattern v_replaceKeyPattern = Pattern.compile(v_key, Pattern.LITERAL);
      int v_indexOfKey = _sql.indexOf(v_key);
      while (v_indexOfKey != -1)
      {
         int v_parameterIndex = 0;
         for (int v_i = 0; v_i < v_indexOfKey; v_i++)
         {
            if (_sql.charAt(v_i) == '?')
            {
               v_parameterIndex++;
            }
         }
         if (v_parameterValue instanceof Collection<?>)
         {
            final Collection<?> v_collection = (Collection<?>) v_parameterValue;
            // Si la collection est vide, il faut faire comme si le paramètre était null
            if (v_collection.isEmpty())
            {
               throw new Spi4jRuntimeException("Erreur 'prepareStatement' : la liste '" + p_parameterName
                        + "' en paramètre de la requête SQL est vide dans la requête suivante :\n" + _initialSqlQuery,
                        "S'assurer que la liste passée en paramètre de la requête SQL n'est pas vide");
            }
            else
            {
               int v_index = v_parameterIndex;
               for (final Object v_value : v_collection)
               {
                  if (v_value instanceof Identifiable_Itf)
                  {
                     final Object v_id = ((Identifiable_Itf<?>) v_value).getId();
                     _parameterValues.add(v_index, v_id);
                  }
                  else
                  {
                     _parameterValues.add(v_index, v_value);
                  }
                  v_index++;
               }
               manageParameterAsCollection(v_collection, v_replaceKeyPattern);
            }
         }
         else if (v_parameterValue instanceof Identifiable_Itf<?>)
         {
            final Object v_id = ((Identifiable_Itf<?>) v_parameterValue).getId();
            _parameterValues.add(v_parameterIndex, v_id);
            manageParameterDefault(v_id, v_replaceKeyPattern);
         }
         else
         {
            // cas par défaut:
            // ajoute la valeur du paramètre à la bonne position dans la liste qui sera utilisée pour définir les paramètres dans le PreparedStatement jdbc
            _parameterValues.add(v_parameterIndex, v_parameterValue);
            manageParameterDefault(v_parameterValue, v_replaceKeyPattern);
         }
         v_indexOfKey = _sql.indexOf(v_key);
      }
   }

   /**
    * Remplace un paramètre dans une requête sql.
    * @param p_parameterValue
    *           la valeur du paramètre
    * @param p_replaceKeyPattern
    *           la clé du paramètre
    */
   private void manageParameterDefault (final Object p_parameterValue, final Pattern p_replaceKeyPattern)
   {
      // on remplace le paramètre nommé pour le caractère '?' nécessaire en jdbc
      _sql = p_replaceKeyPattern.matcher(_sql).replaceFirst("?");
      // debug
      if (isDebugEnabled() || _hasException)
      {
         final String v_parameterValueString;
         if (p_parameterValue != null)
         {
            if (p_parameterValue instanceof String)
            {
               v_parameterValueString = '\'' + (String) p_parameterValue + '\'';
            }
            else if (p_parameterValue instanceof Boolean)
            {
               if ((Boolean) p_parameterValue)
               {
                  v_parameterValueString = "1";
               }
               else
               {
                  v_parameterValueString = "0";
               }
            }
            else if (p_parameterValue instanceof Date)
            {
               // on formatte un minimum la date en utilisant la Locale, et on ajoute des quotes
               final DateFormat v_dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM,
                        Locale.getDefault());
               v_parameterValueString = '\'' + v_dateFormat.format(p_parameterValue) + '\'';
            }
            else
            {
               v_parameterValueString = p_parameterValue.toString();
            }
         }
         else
         {
            v_parameterValueString = "null";
         }
         _formattedSql = p_replaceKeyPattern.matcher(_formattedSql).replaceFirst(
                  v_parameterValueString.replace("\\", "\\\\").replace("$", "\\$"));
      }
   }

   /**
    * Remplace un paramètre de type {@link Collection} dans une requête sql.
    * @param p_parameterValue
    *           la valeur du paramètre
    * @param p_replaceKeyPattern
    *           la clé du paramètre
    */
   private void manageParameterAsCollection (final Collection<?> p_parameterValue, final Pattern p_replaceKeyPattern)
   {
      // si c'est une Collection (une List par exemple) alors on met autant de paramètres jdbc que de valeurs
      // (normalement pour une Collection, la requête sql est du genre "where id in :listeId")
      final int v_size = p_parameterValue.size();
      final StringBuilder v_sb = new StringBuilder("(?");
      for (int v_i = 1; v_i < v_size; v_i++)
      {
         v_sb.append(", ?");
      }
      v_sb.append(')');
      _sql = p_replaceKeyPattern.matcher(_sql).replaceFirst(v_sb.toString());
      // debug
      if (isDebugEnabled())
      {
         final StringBuilder v_sbFormatter = new StringBuilder("(");
         // concaténation des données de la liste
         for (final Object v_o : p_parameterValue)
         {
            if (v_o instanceof Identifiable_Itf)
            {
               final Object v_id = ((Identifiable_Itf<?>) v_o).getId();
               v_sbFormatter.append(v_id);
            }
            else
            {
               v_sbFormatter.append(v_o);
            }
            v_sbFormatter.append(", ");
         }
         // suppression de la dernière virgule
         if (v_sbFormatter.length() >= 2)
         {
            v_sbFormatter.setLength(v_sbFormatter.length() - 2);
         }
         v_sbFormatter.append(')');
         _formattedSql = p_replaceKeyPattern.matcher(_formattedSql).replaceFirst(v_sbFormatter.toString());
      }
   }

   /**
    * Définit la valeur d'un paramètre dans le statement jdbc.
    * @param p_Statement
    *           Statement jdbc
    * @param p_parameterIndex
    *           Index des paramètres entre 1 et n
    * @param p_parameterValue
    *           Valeur du paramètre
    * @throws SQLException
    *            Si erreur sql
    */
   private static void setStatementValue (final PreparedStatement p_Statement, final int p_parameterIndex,
            final Object p_parameterValue) throws SQLException
   {
      if (p_parameterValue == null)
      {
         // Types.NULL pour postgresql notamment
         p_Statement.setNull(p_parameterIndex, Types.NULL);
      }
      else if (p_parameterValue instanceof String)
      {
         p_Statement.setString(p_parameterIndex, (String) p_parameterValue);
      }
      else if (p_parameterValue instanceof Integer)
      {
         p_Statement.setInt(p_parameterIndex, (Integer) p_parameterValue);
      }
      else if (p_parameterValue instanceof Long)
      {
         p_Statement.setLong(p_parameterIndex, (Long) p_parameterValue);
      }
      else if (p_parameterValue instanceof Boolean)
      {
         p_Statement.setBoolean(p_parameterIndex, (Boolean) p_parameterValue);
      }
      else if (p_parameterValue instanceof java.util.Date)
      {
         final Timestamp v_timestamp = new Timestamp(((java.util.Date) p_parameterValue).getTime());
         p_Statement.setTimestamp(p_parameterIndex, v_timestamp);
      }
      else if (p_parameterValue instanceof Float)
      {
         p_Statement.setFloat(p_parameterIndex, (Float) p_parameterValue);
      }
      else if (p_parameterValue instanceof Double)
      {
         p_Statement.setDouble(p_parameterIndex, (Double) p_parameterValue);
      }
      else if (p_parameterValue instanceof BigDecimal)
      {
         p_Statement.setBigDecimal(p_parameterIndex, (BigDecimal) p_parameterValue);
      }
      else if (p_parameterValue instanceof Byte)
      {
         p_Statement.setByte(p_parameterIndex, (Byte) p_parameterValue);
      }
      else if (p_parameterValue.getClass().isEnum())
      {
         p_Statement.setString(p_parameterIndex, p_parameterValue.toString());
      }
      else if (p_parameterValue instanceof Blob)
      {
         p_Statement.setBlob(p_parameterIndex, (Blob) p_parameterValue);
      }
      else
      {
         p_Statement.setObject(p_parameterIndex, p_parameterValue);
      }
   }

   /**
    * @return true si le Logger est en mode DEBUG
    */
   private boolean isDebugEnabled ()
   {
      return c_log.isDebugEnabled();
   }

   /**
    * @return la requête SQL formattée (avec paramètres réels).
    */
   String getFormattedSqlForException ()
   {
      _hasException = true;
      if (_map_value_by_name != null && !_map_value_by_name.isEmpty())
      {
         manageParametersInStatement();
      }
      return _formattedSql;
   }
}
