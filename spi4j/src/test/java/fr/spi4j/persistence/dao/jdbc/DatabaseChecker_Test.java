/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.persistence.dao.jdbc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import fr.spi4j.exception.Spi4jRuntimeException;
import fr.spi4j.testapp.MyParamPersistence;
import fr.spi4j.testapp.MyUserPersistence;

/**
 * Classe de test de {@link DatabaseChecker}
 * @author MINARM
 */
public class DatabaseChecker_Test
{

   /**
    * Méthode d'initialisation de la classe de tests.
    */
   @Test
   public void testCheckDatabase ()
   {
      final MyUserPersistence v_userPersistence = MyParamPersistence.getUserPersistence();
      try
      {
         v_userPersistence.checkDatabase();
         fail("Erreur attendue car Grade contient (volontairement) des erreurs");
      }
      catch (final Spi4jRuntimeException v_e)
      {
         assertTrue(v_e.getMessage().startsWith(
                  "Les tables en base de données ne sont pas en cohérence avec cette version d'application :\nColonnes manquantes dans la table AW_GRADE : [NO_COL, LIBELLE_ABREGE] dans la base "),
                  "Message d'erreur incorrect : " + v_e.getMessage());
      }
   }

}
