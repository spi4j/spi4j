/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.persistence.dao.jdbc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;

import fr.spi4j.exception.Spi4jRuntimeException;
import fr.spi4j.persistence.dao.DatabaseType;
import fr.spi4j.persistence.entity.ColumnsNames_Itf;
import fr.spi4j.persistence.entity.Entity_Itf;
import fr.spi4j.testapp.MyPersonneColumns_Enum;
import fr.spi4j.testapp.MyPersonneEntity_Itf;
import fr.spi4j.testapp.MyVersionnedPersonneEntity;

/**
 * Test unitaire de la classe JdbcQueryBuilder.
 * @author MINARM
 */
public class JdbcQueryBuilder_Test
{
   private JdbcQueryBuilder<Entity_Itf<Long>> _jdbcQueryBuilder;

   /**
    * Initialisation de chaque test.
    */
   @BeforeEach
   public void setup ()
   {
      _jdbcQueryBuilder = new JdbcQueryBuilder<>(MyPersonneColumns_Enum.c_tableName, MyPersonneColumns_Enum.values());
   }

   /**
    * Test.
    */
   @Test
   public void getPkColumnName ()
   {
      final String v_resultatObtenu = _jdbcQueryBuilder.getPkColumnName();
      final String v_resultatAttendu = "PERSONNE_ID";
      assertEquals(v_resultatAttendu, v_resultatObtenu, "getPkColumnName");
   }

   /**
    * Test.
    */
   @Test
   public void getSequenceName ()
   {
      final String v_resultatObtenu = _jdbcQueryBuilder.getSequenceName();
      final String v_resultatAttendu = "AW_PERSONNE_SEQ";
      assertEquals(v_resultatAttendu, v_resultatObtenu, "getSequenceName");
   }

   /**
    * Test.
    */
   @Test
   public void getSequenceNamePourNomLongDeTable ()
   {
      final JdbcQueryBuilder<Entity_Itf<Long>> v_jdbcQueryBuilder = new JdbcQueryBuilder<>(
               "AW_PERSONNE_NOM_LONG_DE_TABLE_EN_BASE", MyPersonneColumns_Enum.values());
      final String v_resultatObtenu = v_jdbcQueryBuilder.getSequenceName();
      final String v_resultatAttendu = "AW_PERSONNE_NOM_LONG_DE_TA_SEQ";
      assertEquals(v_resultatAttendu, v_resultatObtenu, "getSequenceNamePourNomLongDeTable");
   }

   /**
    * Test.
    * @throws SQLException
    *            e
    */
   @Test
   public void getInsertQuery () throws SQLException
   {
      final Connection v_connection = Mockito.mock(Connection.class);
      final DatabaseMetaData v_metaData = Mockito.mock(DatabaseMetaData.class);
      // given
      BDDMockito.given(v_connection.getMetaData()).willReturn(v_metaData);
      BDDMockito.given(v_metaData.getURL()).willReturn("jdbc:oracle:thin:@blabla");
      final String v_resultatObtenu = _jdbcQueryBuilder
               .getInsertQuery(DatabaseType.findTypeForConnection(v_connection));
      final String v_resultatAttendu = "insert into AW_PERSONNE (PERSONNE_ID, NOM, PRENOM, CIVIL, DATE_NAISSANCE, SALAIRE, VERSION, GRADE_ID) values (AW_PERSONNE_SEQ.nextval, :nom, :prenom, :civil, :dateNaissance, :salaire, :version, :grade_id)";
      assertEquals(v_resultatAttendu, v_resultatObtenu, "getInsertQuery");
   }

   /**
    * Test.
    * @throws SQLException
    *            e
    */
   @Test
   public void getInsertQueryWithoutSequance () throws SQLException
   {
      final Connection v_connection = Mockito.mock(Connection.class);
      final DatabaseMetaData v_metaData = Mockito.mock(DatabaseMetaData.class);
      // given
      BDDMockito.given(v_connection.getMetaData()).willReturn(v_metaData);
      BDDMockito.given(v_metaData.getURL()).willReturn("jdbc:oracle:thin:@blabla");
      final String v_resultatObtenu = _jdbcQueryBuilder
               .getInsertQueryWithoutSequence(DatabaseType.findTypeForConnection(v_connection));
      final String v_resultatAttendu = "insert into AW_PERSONNE (PERSONNE_ID, NOM, PRENOM, CIVIL, DATE_NAISSANCE, SALAIRE, VERSION, GRADE_ID) values (:Personne_id, :nom, :prenom, :civil, :dateNaissance, :salaire, :version, :grade_id)";
      assertEquals(v_resultatAttendu, v_resultatObtenu, "getInsertQueryWithoutSequance");
   }

   /**
    * Test.
    */
   @Test
   public void getUpdateQuery ()
   {
      final String v_resultatObtenu = _jdbcQueryBuilder.getUpdateQuery();
      final String v_resultatAttendu = "update AW_PERSONNE set NOM = :nom, PRENOM = :prenom, CIVIL = :civil, DATE_NAISSANCE = :dateNaissance, SALAIRE = :salaire, VERSION = :version, GRADE_ID = :grade_id where PERSONNE_ID = :id";
      assertEquals(v_resultatAttendu, v_resultatObtenu, "getUpdateQuery");
   }

   /**
    * Test.
    */
   @Test
   public void getUpdateQueryIncorrect ()
   {
      _jdbcQueryBuilder = new JdbcQueryBuilder<>(EmptyColumns_Enum.c_tableName, EmptyColumns_Enum.values());
      try
      {
         _jdbcQueryBuilder.getUpdateQuery();
         fail("Erreur attendue");
      }
      catch (final Spi4jRuntimeException v_e)
      {
         assertNotNull(v_e, "Erreur attendue");
      }
   }

   /**
    * Test.
    */
   @Test
   public void getVersionnedUpdateQuery ()
   {
      final MyPersonneEntity_Itf v_entity = new MyVersionnedPersonneEntity();
      final String v_resultatObtenu = _jdbcQueryBuilder.getVersionnedUpdateQuery(v_entity);
      final String v_resultatAttendu = "update AW_PERSONNE set NOM = :nom, PRENOM = :prenom, CIVIL = :civil, DATE_NAISSANCE = :dateNaissance, SALAIRE = :salaire, VERSION = :version, GRADE_ID = :grade_id where PERSONNE_ID = :id and VERSION = :"
               + JdbcVersionHelper.c_requestParameter;
      assertEquals(v_resultatAttendu, v_resultatObtenu, "getUpdateQuery");
   }

   /**
    * Test.
    */
   @Test
   public void getDeleteQuery ()
   {
      final String v_resultatObtenu = _jdbcQueryBuilder.getDeleteQuery();
      final String v_resultatAttendu = "delete from AW_PERSONNE where PERSONNE_ID = :id";
      assertEquals(v_resultatAttendu, v_resultatObtenu, "getDeleteQuery");
   }

   /**
    * Test.
    */
   @Test
   public void getVersionnedDeleteQuery ()
   {
      final MyPersonneEntity_Itf v_entity = new MyVersionnedPersonneEntity();
      final String v_resultatObtenu = _jdbcQueryBuilder.getVersionnedDeleteQuery(v_entity);
      final String v_resultatAttendu = "delete from AW_PERSONNE where PERSONNE_ID = :id and VERSION = :"
               + JdbcVersionHelper.c_requestParameter;
      assertEquals(v_resultatAttendu, v_resultatObtenu, "getDeleteQuery");
   }

   /**
    * Test.
    */
   @Test
   public void getSelectQuery ()
   {
      final String v_resultatObtenu = _jdbcQueryBuilder.getSelectQuery("AW_PERSONNE.*");
      final String v_resultatAttendu = "select AW_PERSONNE.* from AW_PERSONNE ";
      assertEquals(v_resultatAttendu, v_resultatObtenu, "getSelectQuery");
   }

   /**
    * Une énumération de colonnes pour une table sans autre colonne que son id.
    * @author MINARM
    */
   private static enum EmptyColumns_Enum implements ColumnsNames_Itf
   {
      /** Personne_id. */
      Empty_id("Empty_id", "EMPTY_ID", Long.class, true, -1, true);

      /**
       * Le nom physique de la table.
       */
      public static final String c_tableName = "AW_EMPTY";

      /** Le nom logique de la colonne. */
      private final String _logicalColumnName;

      /** Le nom physique de la colonne. */
      private final String _physicalColumnName;

      /** Le type associé à la colonne. */
      private final Class<?> _typeColumn;

      /** Est-ce que la saisie de la valeur est obligatoire pour cette colonne? */
      private final boolean _mandatory;

      /** La taille de la colonne. */
      private final int _size;

      /** Est-ce que la colonne est la clé primaire? */
      private final boolean _id;

      /**
       * Constructeur permettant de spécifier le type de la colonne.
       * @param p_logicalColumnName
       *           (In)(*) Le nom logique de la colonne.
       * @param p_physicalColumnName
       *           (In)(*) Le nom physique de la colonne.
       * @param p_ClassType
       *           (In)(*) Le type de la colonne.
       * @param p_mandatory
       *           (In)(*) Est-ce que la saisie de la valeur est obligatoire pour cette colonne?
       * @param p_size
       *           (In)(*) La taille de la colonne
       * @param p_id
       *           (In)(*) Est-ce que la colonne est la clé primaire?
       */
      private EmptyColumns_Enum (final String p_logicalColumnName, final String p_physicalColumnName,
               final Class<?> p_ClassType, final boolean p_mandatory, final int p_size, final boolean p_id)
      {
         _logicalColumnName = p_logicalColumnName;
         _physicalColumnName = p_physicalColumnName;
         _typeColumn = p_ClassType;
         _mandatory = p_mandatory;
         _size = p_size;
         _id = p_id;
      }

      @Override
      public String getLogicalColumnName ()
      {
         return _logicalColumnName;
      }

      @Override
      public String getPhysicalColumnName ()
      {
         return _physicalColumnName;
      }

      @Override
      public boolean isMandatory ()
      {
         return _mandatory;
      }

      @Override
      public int getSize ()
      {
         return _size;
      }

      @Override
      public boolean isId ()
      {
         return _id;
      }

      @Override
      public Class<?> getTypeColumn ()
      {
         return _typeColumn;
      }

      @Override
      public String toString ()
      {
         return _physicalColumnName;
      }

      @Override
      public String getTableName ()
      {
         return c_tableName;
      }

      @Override
      public String getCompletePhysicalName ()
      {
         return getTableName() + '.' + getPhysicalColumnName();
      }
   }
}
