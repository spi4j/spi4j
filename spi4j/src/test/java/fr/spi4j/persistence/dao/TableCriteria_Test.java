/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.persistence.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import fr.spi4j.exception.Spi4jRuntimeException;
import fr.spi4j.testapp.MyGradeColumns_Enum;
import fr.spi4j.testapp.MyPersonneColumns_Enum;
import fr.spi4j.testapp.MyWrongGradeColumns_Enum;

/**
 * Test unitaire de la classe TableCriteria.
 * @author MINARM
 */
public class TableCriteria_Test
{

   /**
    * Scénario nominal No1 avec 'Operator_Enum.equals'.
    */
   @Test
   public void toString_SN1 ()
   {
      final TableCriteria<MyPersonneColumns_Enum> v_TableCriteria = new TableCriteria<>("toString_SN1");
      // Critère No1
      v_TableCriteria.addCriteria(MyPersonneColumns_Enum.nom, Operator_Enum.equals, "DUPONT");
      // ET Critère No2
      v_TableCriteria.addCriteria(OperatorLogical_Enum.and, MyPersonneColumns_Enum.prenom, Operator_Enum.contains,
               "an");
      // Définir le résultat attendu
      final String v_resultAttendu = " where " + MyPersonneColumns_Enum.nom + " = :"
               + MyPersonneColumns_Enum.nom.getLogicalColumnName() + " and " + MyPersonneColumns_Enum.prenom + " like :"
               + MyPersonneColumns_Enum.prenom.getLogicalColumnName() + ' ';
      // Obtenir le résultat
      final String v_resultObtenu = v_TableCriteria.toString();
      // Vérifier le résultat
      assertEquals(v_resultAttendu, v_resultObtenu, "toString_SN1");
   }

   /**
    * Scénario nominal No2 avec 'order by'.
    */
   @Test
   public void toString_SN2 ()
   {
      final TableCriteria<MyPersonneColumns_Enum> v_TableCriteria = new TableCriteria<>("toString_SN2");
      // order by No1
      v_TableCriteria.addOrderByAsc(MyPersonneColumns_Enum.nom);
      // order by No2
      v_TableCriteria.addOrderByDesc(MyPersonneColumns_Enum.prenom);
      // Définir le résultat attendu
      final String v_resultAttendu = " order by " + MyPersonneColumns_Enum.nom + " asc, "
               + MyPersonneColumns_Enum.prenom + " desc ";
      // Obtenir le résultat
      final String v_resultObtenu = v_TableCriteria.toString();
      // Vérifier le résultat
      assertEquals(v_resultAttendu, v_resultObtenu, "toString_SN2");
   }

   /**
    * Scénario nominal No2 avec 'inner join'.
    */
   @Test
   public void toString_SN3 ()
   {
      final TableCriteria<MyPersonneColumns_Enum> v_TableCriteria = new TableCriteria<>("toString_SN3");
      final String v_alias = "gradePrincipal";
      v_TableCriteria.addInnerJoinWithAlias(v_alias, MyPersonneColumns_Enum.grade_id, MyGradeColumns_Enum.Grade_id,
               MyGradeColumns_Enum.trigramme + " = :trigramme");
      // Critère
      v_TableCriteria.addCriteria(MyPersonneColumns_Enum.nom, Operator_Enum.different, null);
      // order by
      v_TableCriteria.addOrderByDesc(MyPersonneColumns_Enum.prenom);
      // Définir le résultat attendu
      final String v_resultAttendu = " inner join " + MyGradeColumns_Enum.c_tableName + ' ' + v_alias + " on "
               + MyPersonneColumns_Enum.c_tableName + "." + MyPersonneColumns_Enum.grade_id + " = " + v_alias + "."
               + MyGradeColumns_Enum.Grade_id + " AND " + MyGradeColumns_Enum.trigramme + " = :trigramme where "
               + MyPersonneColumns_Enum.nom.getCompletePhysicalName() + " is not null" + " order by "
               + MyPersonneColumns_Enum.prenom.getCompletePhysicalName() + " desc ";
      // Obtenir le résultat
      final String v_resultObtenu = v_TableCriteria.toString();
      // Vérifier le résultat
      assertEquals(v_resultAttendu, v_resultObtenu, "toString_SN3");
      // Vérifier les paramètres
      assertTrue(v_TableCriteria.getMapValue().isEmpty(), "parametres_SN3");
   }

   /**
    * Scénario nominal No4 avec verrouillage de lignes.
    */
   @Test
   public void toString_SN4 ()
   {
      final TableCriteria<MyPersonneColumns_Enum> v_TableCriteria = new TableCriteria<>("toString_SN4");
      // Critère No1
      v_TableCriteria.addCriteria(MyPersonneColumns_Enum.nom, Operator_Enum.equals, "DUPONT");
      // ET Critère No2
      v_TableCriteria.addCriteria(OperatorLogical_Enum.and, MyPersonneColumns_Enum.prenom, Operator_Enum.contains,
               "an");
      // verrouillage
      assertFalse(v_TableCriteria.isLockingRowsForUpdate());
      v_TableCriteria.lockRowsForUpdate();
      assertTrue(v_TableCriteria.isLockingRowsForUpdate());
      // Définir le résultat attendu
      final String v_resultAttendu = " where " + MyPersonneColumns_Enum.nom + " = :"
               + MyPersonneColumns_Enum.nom.getLogicalColumnName() + " and " + MyPersonneColumns_Enum.prenom + " like :"
               + MyPersonneColumns_Enum.prenom.getLogicalColumnName() + " for update";
      // Obtenir le résultat
      final String v_resultObtenu = v_TableCriteria.toString();
      // Vérifier le résultat
      assertEquals(v_resultAttendu, v_resultObtenu, "toString_SN4");
   }

   /**
    * Scénario alternatif No1 avec 'Operator_Enum.in'.
    */
   @Test
   public void toString_SA1 ()
   {
      final TableCriteria<MyPersonneColumns_Enum> v_TableCriteria = new TableCriteria<>("toString_CA1");
      // Critère
      v_TableCriteria.addCriteria(MyPersonneColumns_Enum.Personne_id, Operator_Enum.in, Arrays.asList(1, 2, 3));
      // Définir le résultat attendu
      final String v_resultAttendu = " where " + MyPersonneColumns_Enum.Personne_id + " in :"
               + MyPersonneColumns_Enum.Personne_id.getLogicalColumnName() + ' ';
      // Obtenir le résultat
      final String v_resultObtenu = v_TableCriteria.toString();
      // Vérifier le résultat
      assertEquals(v_resultAttendu, v_resultObtenu, "toString_SA1");
   }

   /**
    * Scénario alternatif No2 avec 'Operator_Enum.equals' et getCriteriaSql.
    */
   @Test
   public void toString_SA2 ()
   {
      final TableCriteria<MyPersonneColumns_Enum> v_TableCriteria = new TableCriteria<>("toString_SA2");
      // Critère No1
      v_TableCriteria.addCriteria(MyPersonneColumns_Enum.nom, Operator_Enum.equals, "DUPONT");
      // ET Critère No2
      v_TableCriteria.addCriteria(OperatorLogical_Enum.and, MyPersonneColumns_Enum.prenom, Operator_Enum.different,
               "Jean");
      // ET Critère No3
      v_TableCriteria.addCriteria(OperatorLogical_Enum.and, MyPersonneColumns_Enum.prenom, Operator_Enum.contains,
               "an");
      // Définir le résultat attendu
      final String v_resultAttendu = " where " + MyPersonneColumns_Enum.nom + " = :nom" + " and "
               + MyPersonneColumns_Enum.prenom + " <> :prenom" + " and " + MyPersonneColumns_Enum.prenom
               + " like :prenom1 ";
      // Obtenir le résultat
      final String v_resultObtenu = v_TableCriteria.getCriteriaSql();
      // Vérifier le résultat
      assertEquals(v_resultAttendu, v_resultObtenu, "toString_SA2");

      // Définir le résultat attendu
      final Map<String, Object> v_map_resultAttendu = new LinkedHashMap<>();
      v_map_resultAttendu.put(MyPersonneColumns_Enum.nom.getLogicalColumnName(), "DUPONT");
      v_map_resultAttendu.put(MyPersonneColumns_Enum.prenom.getLogicalColumnName(), "Jean");
      v_map_resultAttendu.put(MyPersonneColumns_Enum.prenom.getLogicalColumnName() + "1", "%an%");
      // Obtenir le résultat
      final Map<String, Object> v_map_resultObtenu = v_TableCriteria.getMapValue();
      // Vérifier le résultat
      assertEquals(v_map_resultAttendu, v_map_resultObtenu, "toString_SA2");
   }

   /**
    * Scénario alternatif No3 avec 'order by'.
    */
   @Test
   public void toString_SA3 ()
   {
      final TableCriteria<MyPersonneColumns_Enum> v_TableCriteria = new TableCriteria<>("toString_SA3");
      // order by No1
      v_TableCriteria.addOrderByAsc(MyPersonneColumns_Enum.nom);
      // order by No2
      v_TableCriteria.addOrderByDesc(MyPersonneColumns_Enum.prenom);
      // Critère ajouté ensuite pour compliquer les choses
      v_TableCriteria.addCriteria(MyPersonneColumns_Enum.nom, Operator_Enum.equals, "DUPONT");
      // Critère ajouté ensuite pour compliquer les choses
      v_TableCriteria.addCriteria(OperatorLogical_Enum.and, MyPersonneColumns_Enum.prenom, Operator_Enum.equals,
               "JEAN");
      // Définir le résultat attendu
      final String v_resultAttendu = " where NOM = :nom and PRENOM = :prenom order by " + MyPersonneColumns_Enum.nom
               + " asc, " + MyPersonneColumns_Enum.prenom + " desc ";
      // Obtenir le résultat
      final String v_resultObtenu = v_TableCriteria.getCriteriaSql();
      // Vérifier le résultat
      assertEquals(v_resultAttendu, v_resultObtenu, "toString_SA3");
   }

   /**
    * Scénario alternatif No4 avec 'Operator_Enum.in'.
    */
   @Test
   public void toString_SA4 ()
   {
      final TableCriteria<MyPersonneColumns_Enum> v_TableCriteria = new TableCriteria<>("toString_SA4");
      // Critère
      v_TableCriteria.addCriteria(MyPersonneColumns_Enum.Personne_id, Operator_Enum.in, Arrays.asList(1, 2, 3));
      // Définir le résultat attendu
      final String v_resultAttendu = " where " + MyPersonneColumns_Enum.Personne_id + " in :Personne_id ";
      // Obtenir le résultat
      final String v_resultObtenu = v_TableCriteria.getCriteriaSql();
      // Vérifier le résultat
      assertEquals(v_resultAttendu, v_resultObtenu, "toString_SA4");
      // Définir le résultat attendu
      final Map<String, Object> v_map_resultAttendu = new LinkedHashMap<>();
      v_map_resultAttendu.put("Personne_id", Arrays.asList(1, 2, 3));
      // Obtenir le résultat
      final Map<String, Object> v_map_resultObtenu = v_TableCriteria.getMapValue();
      // Vérifier le résultat
      assertEquals(v_map_resultAttendu, v_map_resultObtenu, "toString_SA4");
   }

   /**
    * Scénario alternatif No5 avec valeur nulle.
    */
   @Test
   public void toString_SA5 ()
   {
      final TableCriteria<MyPersonneColumns_Enum> v_TableCriteria = new TableCriteria<>("toString_SA5");
      // Critère
      v_TableCriteria.addCriteria(MyPersonneColumns_Enum.nom, Operator_Enum.equals, null);
      // Définir le résultat attendu
      final String v_resultAttendu = " where " + MyPersonneColumns_Enum.nom + " is null ";
      // Obtenir le résultat
      final String v_resultObtenu = v_TableCriteria.getCriteriaSql();
      // Vérifier le résultat
      assertEquals(v_resultAttendu, v_resultObtenu, "toString_SA5");
      // Définir le résultat attendu
      final Map<String, Object> v_map_resultAttendu = new LinkedHashMap<>();
      // Obtenir le résultat
      final Map<String, Object> v_map_resultObtenu = v_TableCriteria.getMapValue();
      // Vérifier le résultat
      assertEquals(v_map_resultAttendu, v_map_resultObtenu, "toString_SA5");
   }

   /**
    * Scénario alternatif 6 : idem Scénario nominal 1 mais avec ignoreCase.
    */
   @Test
   public void toString_SA6 ()
   {
      final TableCriteria<MyPersonneColumns_Enum> v_TableCriteria = new TableCriteria<>("toString_SA6");
      // Critère No1
      v_TableCriteria.addCriteriaIgnoreCase(MyPersonneColumns_Enum.nom, Operator_Enum.equals, "DUPONT");
      // ET Critère No2
      v_TableCriteria.addCriteriaIgnoreCase(OperatorLogical_Enum.and, MyPersonneColumns_Enum.prenom,
               Operator_Enum.contains, "AN");
      // ET Critère No3
      v_TableCriteria.addCriteriaIgnoreCase(OperatorLogical_Enum.and, MyPersonneColumns_Enum.prenom,
               Operator_Enum.superior, null);
      // Définir le résultat attendu
      final String v_resultAttendu = " where lower(" + MyPersonneColumns_Enum.nom + ") = :"
               + MyPersonneColumns_Enum.nom.getLogicalColumnName() + " and lower(" + MyPersonneColumns_Enum.prenom
               + ") like :" + MyPersonneColumns_Enum.prenom.getLogicalColumnName() + " and lower("
               + MyPersonneColumns_Enum.prenom + ") > :prenom1 ";
      // Obtenir le résultat
      final String v_resultObtenu = v_TableCriteria.toString();
      // Vérifier le résultat
      assertEquals(v_resultAttendu, v_resultObtenu, "toString_SA6");
   }

   /**
    * Scénario alternatif 7 : idem Scénario alternatif 2 mais avec ignoreCase.
    */
   @Test
   public void toString_SA7 ()
   {
      final TableCriteria<MyPersonneColumns_Enum> v_TableCriteria = new TableCriteria<>("toString_SA7");
      // Critère No1
      v_TableCriteria.addCriteriaIgnoreCase(MyPersonneColumns_Enum.nom, Operator_Enum.equals, "DUPONT");
      // ET Critère No2
      v_TableCriteria.addCriteriaIgnoreCase(OperatorLogical_Enum.and, MyPersonneColumns_Enum.prenom,
               Operator_Enum.contains, "AN");
      // Définir le résultat attendu
      final String v_resultAttendu = " where lower(" + MyPersonneColumns_Enum.nom + ") = :nom" + " and lower("
               + MyPersonneColumns_Enum.prenom + ") like :prenom ";
      // Obtenir le résultat
      final String v_resultObtenu = v_TableCriteria.getCriteriaSql();
      // Vérifier le résultat
      assertEquals(v_resultAttendu, v_resultObtenu, "toString_SA7");

      // Définir le résultat attendu
      final Map<String, Object> v_map_resultAttendu = new LinkedHashMap<>();
      v_map_resultAttendu.put(MyPersonneColumns_Enum.nom.getLogicalColumnName(), "dupont");
      v_map_resultAttendu.put(MyPersonneColumns_Enum.prenom.getLogicalColumnName(), "%an%");
      // Obtenir le résultat
      final Map<String, Object> v_map_resultObtenu = v_TableCriteria.getMapValue();
      // Vérifier le résultat
      assertEquals(v_map_resultAttendu, v_map_resultObtenu, "toString_SA7");
   }

   /**
    * Scénario alternatif No8.
    */
   @Test
   public void toString_SA8 ()
   {
      final TableCriteria<MyPersonneColumns_Enum> v_TableCriteria = new TableCriteria<>("toString_SA8");

      // Vérifier la description
      assertEquals("toString_SA8", v_TableCriteria.getDescription(), "description");

      // Vérifier nbLignesMax
      v_TableCriteria.setNbLignesMax(1000);
      assertEquals(1000, v_TableCriteria.getNbLignesMax(), "nbLignesMax");

      // Vérifier nbLignesStart
      v_TableCriteria.setNbLignesStart(1);
      assertEquals(1, v_TableCriteria.getNbLignesStart(), "nbLignesStart");
   }

   /**
    * Scénario alternatif No9 avec 'Operator_Enum.startsWith' et Operator_Enum.endsWith.
    */
   @Test
   public void toString_SA9 ()
   {
      final TableCriteria<MyPersonneColumns_Enum> v_TableCriteria = new TableCriteria<>("toString_SA9");
      // Critère No1
      v_TableCriteria.addCriteria(MyPersonneColumns_Enum.nom, Operator_Enum.startsWith, "DUP");
      // ET Critère No2
      v_TableCriteria.addCriteria(OperatorLogical_Enum.and, MyPersonneColumns_Enum.prenom, Operator_Enum.endsWith,
               "ANE");
      // Définir le résultat attendu
      final String v_resultAttendu = " where " + MyPersonneColumns_Enum.nom + " like :nom" + " and "
               + MyPersonneColumns_Enum.prenom + " like :prenom ";
      // Obtenir le résultat
      final String v_resultObtenu = v_TableCriteria.getCriteriaSql();
      // Vérifier le résultat
      assertEquals(v_resultAttendu, v_resultObtenu, "toString_SA9");

      // Définir le résultat attendu
      final Map<String, Object> v_map_resultAttendu = new LinkedHashMap<>();
      v_map_resultAttendu.put(MyPersonneColumns_Enum.nom.getLogicalColumnName(), "DUP%");
      v_map_resultAttendu.put(MyPersonneColumns_Enum.prenom.getLogicalColumnName(), "%ANE");
      // Obtenir le résultat
      final Map<String, Object> v_map_resultObtenu = v_TableCriteria.getMapValue();
      // Vérifier le résultat
      assertEquals(v_map_resultAttendu, v_map_resultObtenu, "toString_SA9");
   }

   /**
    * Scénario alternatif No10 avec 'Operator_Enum.startsWith'et Operator_Enum.endsWith.
    */
   @Test
   public void toString_SA10 ()
   {
      final TableCriteria<MyPersonneColumns_Enum> v_TableCriteria = new TableCriteria<>("toString_SA10");
      // Critère No1
      v_TableCriteria.addCriteria(MyPersonneColumns_Enum.nom, Operator_Enum.startsWith, "DUP");
      // ET Critère No2
      v_TableCriteria.addCriteria(OperatorLogical_Enum.and, MyPersonneColumns_Enum.prenom, Operator_Enum.endsWith,
               "ANE");
      // Définir le résultat attendu
      final String v_resultAttendu = " where " + MyPersonneColumns_Enum.nom + " like :"
               + MyPersonneColumns_Enum.nom.getLogicalColumnName() + " and " + MyPersonneColumns_Enum.prenom + " like :"
               + MyPersonneColumns_Enum.prenom.getLogicalColumnName() + ' ';
      // Obtenir le résultat
      final String v_resultObtenu = v_TableCriteria.toString();
      // Vérifier le résultat
      assertEquals(v_resultAttendu, v_resultObtenu, "toString_SA10");
   }

   /**
    * Scénario alternatif No11 avec inner join sans alias et sans contrainte.
    */
   @Test
   public void toString_SA11 ()
   {
      final TableCriteria<MyPersonneColumns_Enum> v_TableCriteria = new TableCriteria<>("toString_SA11");
      v_TableCriteria.addInnerJoin(MyPersonneColumns_Enum.grade_id, MyGradeColumns_Enum.Grade_id);
      v_TableCriteria.addCriteria(MyPersonneColumns_Enum.nom, Operator_Enum.startsWith, "DUPON");
      // Définir le résultat attendu
      final String v_resultAttendu = " inner join " + MyGradeColumns_Enum.c_tableName + " on "
               + MyPersonneColumns_Enum.c_tableName + "." + MyPersonneColumns_Enum.grade_id + " = "
               + MyGradeColumns_Enum.c_tableName + "." + MyGradeColumns_Enum.Grade_id + " where "
               + MyPersonneColumns_Enum.nom.getCompletePhysicalName() + " like :nom ";
      // Obtenir le résultat
      final String v_resultObtenu = v_TableCriteria.toString();
      // Vérifier le résultat
      assertEquals(v_resultAttendu, v_resultObtenu, "toString_SA11");
   }

   /**
    * Scénario alternatif No12 avec inner join avec alias et sans contrainte.
    */
   @Test
   public void toString_SA12 ()
   {
      final TableCriteria<MyPersonneColumns_Enum> v_TableCriteria = new TableCriteria<>("toString_SA12");
      final String v_alias = "gradePrincipal";
      v_TableCriteria.addInnerJoinWithAlias(v_alias, MyPersonneColumns_Enum.grade_id, MyGradeColumns_Enum.Grade_id);
      // Définir le résultat attendu
      final String v_resultAttendu = " inner join " + MyGradeColumns_Enum.c_tableName + ' ' + v_alias + " on "
               + MyPersonneColumns_Enum.c_tableName + "." + MyPersonneColumns_Enum.grade_id + " = " + v_alias + "."
               + MyGradeColumns_Enum.Grade_id + ' ';
      // Obtenir le résultat
      final String v_resultObtenu = v_TableCriteria.toString();
      // Vérifier le résultat
      assertEquals(v_resultAttendu, v_resultObtenu, "toString_SA12");
   }

   /**
    * Scénario alternatif No13 avec inner join sans alias et avec contrainte.
    */
   @Test
   public void toString_SA13 ()
   {
      final TableCriteria<MyPersonneColumns_Enum> v_TableCriteria = new TableCriteria<>("toString_SA13");
      v_TableCriteria.addInnerJoin(MyPersonneColumns_Enum.grade_id, MyGradeColumns_Enum.Grade_id,
               MyGradeColumns_Enum.trigramme + " = :trigramme");
      // Définir le résultat attendu
      final String v_resultAttendu = " inner join " + MyGradeColumns_Enum.c_tableName + " on "
               + MyPersonneColumns_Enum.c_tableName + "." + MyPersonneColumns_Enum.grade_id + " = "
               + MyGradeColumns_Enum.c_tableName + "." + MyGradeColumns_Enum.Grade_id + " AND "
               + MyGradeColumns_Enum.trigramme + " = :trigramme ";
      // Obtenir le résultat
      final String v_resultObtenu = v_TableCriteria.toString();
      // Vérifier le résultat
      assertEquals(v_resultAttendu, v_resultObtenu, "toString_SA13");

      final TableCriteria<MyPersonneColumns_Enum> v_TableCriteria2 = new TableCriteria<>("toString_SA13");
      v_TableCriteria2.addInnerJoin(MyPersonneColumns_Enum.grade_id, MyGradeColumns_Enum.Grade_id, " ");
      // Définir le résultat attendu
      final String v_resultAttendu2 = " inner join " + MyGradeColumns_Enum.c_tableName + " on "
               + MyPersonneColumns_Enum.c_tableName + "." + MyPersonneColumns_Enum.grade_id + " = "
               + MyGradeColumns_Enum.c_tableName + "." + MyGradeColumns_Enum.Grade_id + " ";
      // Obtenir le résultat
      final String v_resultObtenu2 = v_TableCriteria2.toString();
      // Vérifier le résultat
      assertEquals(v_resultAttendu2, v_resultObtenu2, "toString_SA13");
   }

   /**
    * Scénario alternatif No14 avec inner join sans alias et avec contrainte, avec paramètre nommé déjà utilisé.
    */
   @Test
   public void toString_SA14 ()
   {
      final TableCriteria<MyPersonneColumns_Enum> v_TableCriteria = new TableCriteria<>("toString_SA14");
      v_TableCriteria.addInnerJoin(MyPersonneColumns_Enum.grade_id, MyGradeColumns_Enum.Grade_id,
               MyGradeColumns_Enum.trigramme + " = :nom");
      v_TableCriteria.addCriteria(MyPersonneColumns_Enum.nom, Operator_Enum.equals, "DUPONT");
      v_TableCriteria.addCriteria(OperatorLogical_Enum.and, MyPersonneColumns_Enum.prenom, Operator_Enum.equals,
               "JEAN");
      // Définir le résultat attendu
      final String v_resultAttendu = " inner join " + MyGradeColumns_Enum.c_tableName + " on "
               + MyPersonneColumns_Enum.c_tableName + "." + MyPersonneColumns_Enum.grade_id + " = "
               + MyGradeColumns_Enum.c_tableName + "." + MyGradeColumns_Enum.Grade_id + " AND "
               + MyGradeColumns_Enum.trigramme + " = :nom where " + MyPersonneColumns_Enum.nom.getCompletePhysicalName()
               + " = :" + MyPersonneColumns_Enum.nom.getLogicalColumnName() + '1' + " and "
               + MyPersonneColumns_Enum.prenom.getCompletePhysicalName() + " = :"
               + MyPersonneColumns_Enum.prenom.getLogicalColumnName() + ' ';
      // Obtenir le résultat
      final String v_resultObtenu = v_TableCriteria.toString();
      // Vérifier le résultat
      assertEquals(v_resultAttendu, v_resultObtenu, "toString_SA14");
      // Vérifier les paramètres
      assertTrue(v_TableCriteria.getMapValue().containsKey("nom1"), "parametres_SA14");
      assertTrue(v_TableCriteria.getMapValue().containsKey("prenom"), "parametres_SA14");
   }

   /**
    * Scénario d'exception No1 avec appel du mauvais 'addCriteria'.
    */
   @Test
   public void toString_SE1 ()
   {
      assertThrows(IllegalArgumentException.class, () -> {
         final TableCriteria<MyPersonneColumns_Enum> v_TableCriteria = new TableCriteria<>("toString_SE1");
         // Critère No1 avec un 'and' : ERREUR
         v_TableCriteria.addCriteria(OperatorLogical_Enum.and, MyPersonneColumns_Enum.prenom, Operator_Enum.contains,
                  "an");
      });
   }

   /**
    * Scénario d'exception 2 : proche du scenario alternatif 7 mais avec un ignoreCase sur un champ non String.
    */
   @Test
   public void toString_SE2 ()
   {
      assertThrows(IllegalArgumentException.class, () -> {
         final TableCriteria<MyPersonneColumns_Enum> v_TableCriteria = new TableCriteria<>("toString_SE2");
         // Critère No1
         v_TableCriteria.addCriteriaIgnoreCase(MyPersonneColumns_Enum.Personne_id, Operator_Enum.equals, 1); // c'est le champ Personne_id de type Long qui entraîne l'exception
      });
   }

   /**
    * Scénario d'exception 3 : proche du scenario alternatif 7 mais avec un ignoreCase sur un champ certes String mais qu'on compare a une valeur entière.
    */
   @Test
   public void toString_SE3 ()
   {
      assertThrows(Spi4jRuntimeException.class, () -> {
         final TableCriteria<MyPersonneColumns_Enum> v_TableCriteria = new TableCriteria<>("toString_SE3");
         // Critère No1
         v_TableCriteria.addCriteriaIgnoreCase(MyPersonneColumns_Enum.nom, Operator_Enum.equals, 1); // c'est la valeur entière 1 qui entraîne l'exception
         // ET Critère No2
         v_TableCriteria.addCriteriaIgnoreCase(OperatorLogical_Enum.and, MyPersonneColumns_Enum.prenom,
                  Operator_Enum.contains, "AN");
         // Définir le résultat attendu
         final String v_resultAttendu = " where lower(" + MyPersonneColumns_Enum.nom + ") = :nom" + " and lower("
                  + MyPersonneColumns_Enum.prenom + ") like :prenom ";
         // Obtenir le résultat
         final String v_resultObtenu = v_TableCriteria.getCriteriaSql();
         // Vérifier le résultat
         assertEquals(v_resultAttendu, v_resultObtenu, "toString_SE3");

         // Définir le résultat attendu
         final Map<String, Object> v_map_resultAttendu = new LinkedHashMap<>();
         v_map_resultAttendu.put(MyPersonneColumns_Enum.nom.getLogicalColumnName(), "dupont");
         v_map_resultAttendu.put(MyPersonneColumns_Enum.prenom.getLogicalColumnName(), "%an%");
         // Obtenir le résultat
         final Map<String, Object> v_map_resultObtenu = v_TableCriteria.getMapValue(); // exception lancée ici
         // Vérifier le résultat
         assertEquals(v_map_resultAttendu, v_map_resultObtenu, "toString_SE3");
      });

   }

   /**
    * Scénario d'exception 4 : teste que le nom de colonne null dans addCriteriaIgnoreCase lance une exception
    */
   @Test
   public void toString_SE4 ()
   {
      assertThrows(IllegalArgumentException.class, () -> {
         final TableCriteria<MyPersonneColumns_Enum> v_TableCriteria = new TableCriteria<>("toString_SE4");
         // Critère No1
         v_TableCriteria.addCriteriaIgnoreCase(null, Operator_Enum.equals, 1); // teste que le nom de colonne null lance une exception
      });
   }

   /**
    * Scénario d'exception 5 : teste que l'operateur null dans addCriteriaIgnoreCase lance une exception
    */
   @Test
   public void toString_SE5 ()
   {
      assertThrows(IllegalArgumentException.class, () -> {
         final TableCriteria<MyPersonneColumns_Enum> v_TableCriteria = new TableCriteria<>("toString_SE5");
         // Critère No1
         v_TableCriteria.addCriteriaIgnoreCase(MyPersonneColumns_Enum.nom, null, "dupont"); // teste que l'operateur null lance une exception
      });
   }

   /**
    * Scénario d'exception 6 : teste que l'operateur null dans addCriteriaIgnoreCase lance une exception
    */
   @Test
   public void toString_SE6 ()
   {
      assertThrows(IllegalArgumentException.class, () -> {
         final TableCriteria<MyPersonneColumns_Enum> v_TableCriteria = new TableCriteria<>("toString_SE6");
         // Critère No1
         v_TableCriteria.addCriteriaIgnoreCase(MyPersonneColumns_Enum.nom, Operator_Enum.equals, "dupont");
         // ET Critère No2
         v_TableCriteria.addCriteriaIgnoreCase(MyPersonneColumns_Enum.prenom, Operator_Enum.contains, "AN"); // teste que le second appel sans opérateur logique lance une exception
      });
   }

   /**
    * Scénario d'exception 7 : teste que le nom de colonne null dans addOrderByAsc lance une exception
    */
   public void toString_SE7 ()
   {
      assertThrows(IllegalArgumentException.class, () -> {
         final TableCriteria<MyPersonneColumns_Enum> v_TableCriteria = new TableCriteria<>("toString_SE7");
         // Critère No1
         v_TableCriteria.addCriteriaIgnoreCase(MyPersonneColumns_Enum.nom, Operator_Enum.equals, "dupont");
         // order by No1
         v_TableCriteria.addOrderByAsc(null); // teste que le nom de colonne null dans lance une exception
      });
   }

   /**
    * Scénario d'exception 8 : teste que la description null dans le constructeur TableCriteria lance une exception
    */
   @Test
   public void toString_SE8 ()
   {
      assertThrows(IllegalArgumentException.class, () -> {
         final TableCriteria<MyPersonneColumns_Enum> v_TableCriteria = new TableCriteria<>(null); // teste que la description null dans le constructeur TableCriteria lance une exception
         // Critère No1
         v_TableCriteria.addCriteriaIgnoreCase(MyPersonneColumns_Enum.nom, Operator_Enum.equals, "dupont");
      });
   }

   /**
    * Scénario clone
    */
   @Test
   public void testClone ()
   {
      final TableCriteria<MyPersonneColumns_Enum> v_TableCriteria = new TableCriteria<>("testClone");
      // Critère No1
      v_TableCriteria.addCriteriaIgnoreCase(MyPersonneColumns_Enum.nom, Operator_Enum.equals, "dupont");
      final TableCriteria<MyPersonneColumns_Enum> v_clone = v_TableCriteria.clone();
      assertEquals(v_TableCriteria.getCriteriaSql(), v_clone.getCriteriaSql());
   }

   /**
    * Test avec des paramètres presque identiques. Il ne faut pas qu'ils soient confondus l'un avec l'autre.
    */
   @Test
   public void testParametresPresqueIdentiques ()
   {
      final TableCriteria<MyWrongGradeColumns_Enum> v_TableCriteria = new TableCriteria<>("testParametresIdentiques");
      v_TableCriteria.addCriteria(MyWrongGradeColumns_Enum.libelleAbrege, Operator_Enum.contains, "value_libA");
      v_TableCriteria.addCriteria(OperatorLogical_Enum.and, MyWrongGradeColumns_Enum.libelle, Operator_Enum.contains,
               "value_lib");

      final Map<String, Object> v_parameters = v_TableCriteria.getMapValue();
      final String[] v_motsRequete = v_TableCriteria.getCriteriaSql().split(" ");
      for (final String v_motRequete : v_motsRequete)
      {
         if (v_motRequete.startsWith(":"))
         {
            // il s'agit d'un paramètre, il doit exister dans la map des paramètres du TableCriteria
            assertTrue(v_parameters.containsKey(v_motRequete.substring(1)),
                     "Le paramètre n'a pas été trouvé : " + v_motRequete);
         }
      }

   }
}
