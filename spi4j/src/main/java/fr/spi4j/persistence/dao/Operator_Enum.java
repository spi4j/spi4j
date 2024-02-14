/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.persistence.dao;

/**
 * Définir les opérateurs pour les critères.
 */
public enum Operator_Enum
{
   /** Egal. */
   equals("="),
   /** Différent. */
   different("<>"),
   /** Contient. */
   contains("like"),
   /** Commence par. */
   startsWith("like"),
   /** Termine par. */
   endsWith("like"),
   /** Inférieur à. */
   inferior("<"),
   /** Supérieur à */
   superior(">"),
   /** Inférieur ou égal à. */
   inferiorOrEquals("<="),
   /** Supérieur ou égal à. */
   superiorOrEquals(">="),
   /** Dans. */
   in("in");

   private final String _sqlName;

   /**
    * Construit un opérateur à partir de son code sql.
    * @param p_sqlName
    *           (In)(*) le code sql de l'opérateur
    */
   Operator_Enum (final String p_sqlName)
   {
      _sqlName = p_sqlName;
   }

   /**
    * Retourne le code sql de l'opérateur.
    * @return le code sql de l'opérateur
    */
   public String getSqlName ()
   {
      return _sqlName;
   }
}
