/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.persistence.transaction;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.transaction.Status;
import jakarta.transaction.SystemException;

/**
 * Test unitaire de la classe NonXAGlobalUserTransaction.
 * @author MINARM
 */
public class NonXAGlobalUserTransaction_Test
{
   private final NonXAGlobalUserTransaction _nonXAGlobalUserTransaction = new NonXAGlobalUserTransaction();

   /**
    * Initialisation.
    * @throws SystemException
    *            e
    */
   @BeforeEach
   public void setUp () throws SystemException
   {
      while (_nonXAGlobalUserTransaction.getStatus() != Status.STATUS_NO_TRANSACTION)
      {
         _nonXAGlobalUserTransaction.rollback();
      }
   }

   /**
    * Test.
    * @throws SystemException
    *            e
    */
   @Test
   public void testTransactionNotStarted () throws SystemException
   {
      assertTrue(_nonXAGlobalUserTransaction.getStatus() == Status.STATUS_NO_TRANSACTION,
               "aucune transaction démarrée");
      boolean v_testOk = false;
      try
      {
         _nonXAGlobalUserTransaction.commit();
      }
      catch (final SystemException v_ex)
      {
         v_testOk = true;
      }

      boolean v_test2Ok = false;
      try
      {
         _nonXAGlobalUserTransaction.commit();
      }
      catch (final SystemException v_ex)
      {
         v_test2Ok = true;
      }
      if (!v_testOk || !v_test2Ok)
      {
         fail("l'appel de commit() ou de rollabck() doit lancer une SystemException quand il n'y a pas de transaction démarrée");
      }
   }

   /**
    * Test.
    * @throws SystemException
    *            e
    */
   @Test
   public void testBeginCommit () throws SystemException
   {
      assertTrue(_nonXAGlobalUserTransaction.getStatus() == Status.STATUS_NO_TRANSACTION,
               "aucune transaction démarrée");
      _nonXAGlobalUserTransaction.begin();
      if (_nonXAGlobalUserTransaction.getStatus() != Status.STATUS_ACTIVE)
      {
         fail("quand une transaction est démarrée, son status doit être STATUS_ACTIVE");
      }
      _nonXAGlobalUserTransaction.commit();
      if (_nonXAGlobalUserTransaction.getStatus() != Status.STATUS_NO_TRANSACTION)
      {
         fail("après commit(), le status de la transaction doit être STATUS_NO_TRANSACTION");
      }
   }

   /**
    * Test.
    * @throws SystemException
    *            e
    */
   @Test
   public void testBeginRollback () throws SystemException
   {
      assertTrue(_nonXAGlobalUserTransaction.getStatus() == Status.STATUS_NO_TRANSACTION,
               "aucune transaction démarrée");
      _nonXAGlobalUserTransaction.begin();
      if (_nonXAGlobalUserTransaction.getStatus() != Status.STATUS_ACTIVE)
      {
         fail("quand une transaction est démarrée, son status doit être STATUS_ACTIVE");
      }
      _nonXAGlobalUserTransaction.rollback();
      if (_nonXAGlobalUserTransaction.getStatus() != Status.STATUS_NO_TRANSACTION)
      {
         fail("après rollback(), le status de la transaction doit être STATUS_NO_TRANSACTION");
      }
   }

   /**
    * Test.
    * @throws SystemException
    *            e
    */
   @Test
   public void testBeginRollbackOnlyCommitRollback () throws SystemException
   {
      assertTrue(_nonXAGlobalUserTransaction.getStatus() == Status.STATUS_NO_TRANSACTION,
               "aucune transaction démarrée");
      _nonXAGlobalUserTransaction.begin();
      if (_nonXAGlobalUserTransaction.getStatus() != Status.STATUS_ACTIVE)
      {
         fail("quand une transaction est démarrée, son status doit être STATUS_ACTIVE");
      }
      _nonXAGlobalUserTransaction.setRollbackOnly();
      if (_nonXAGlobalUserTransaction.getStatus() != Status.STATUS_MARKED_ROLLBACK)
      {
         fail("après setRollbackOnly(), le status de la transaction doit être STATUS_MARKED_ROLLBACK");
      }
      _nonXAGlobalUserTransaction.commit();
      if (_nonXAGlobalUserTransaction.getStatus() != Status.STATUS_NO_TRANSACTION)
      {
         fail("après commit(), le status de la transaction doit être STATUS_NO_TRANSACTION");
      }
   }

   /**
    * Test.
    * @throws SystemException
    *            e
    */
   @Test
   public void testNestedWithCommit () throws SystemException
   {
      assertTrue(_nonXAGlobalUserTransaction.getStatus() == Status.STATUS_NO_TRANSACTION,
               "aucune transaction démarrée");
      _nonXAGlobalUserTransaction.begin();
      if (_nonXAGlobalUserTransaction.getStatus() != Status.STATUS_ACTIVE)
      {
         fail("quand une transaction est démarrée, son status doit être STATUS_ACTIVE");
      }
      _nonXAGlobalUserTransaction.begin();
      _nonXAGlobalUserTransaction.commit();
      if (_nonXAGlobalUserTransaction.getStatus() != Status.STATUS_ACTIVE)
      {
         fail("après commit() imbriqué, le status de la transaction doit être STATUS_ACTIVE");
      }
      _nonXAGlobalUserTransaction.commit();
      if (_nonXAGlobalUserTransaction.getStatus() != Status.STATUS_NO_TRANSACTION)
      {
         fail("après commit(), le status de la transaction doit être STATUS_NO_TRANSACTION");
      }
   }

   /**
    * Test.
    * @throws SystemException
    *            e
    */
   @Test
   public void testNestedWithOneRollback () throws SystemException
   {
      assertTrue(_nonXAGlobalUserTransaction.getStatus() == Status.STATUS_NO_TRANSACTION,
               "aucune transaction démarrée");
      _nonXAGlobalUserTransaction.begin();
      if (_nonXAGlobalUserTransaction.getStatus() != Status.STATUS_ACTIVE)
      {
         fail("quand une transaction est démarrée, son status doit être STATUS_ACTIVE");
      }
      _nonXAGlobalUserTransaction.begin();
      _nonXAGlobalUserTransaction.rollback();
      if (_nonXAGlobalUserTransaction.getStatus() != Status.STATUS_MARKED_ROLLBACK)
      {
         fail("après rollback() imbriqué, le status de la transaction doit être STATUS_MARKED_ROLLBACK");
      }
      // note: puisqu'il y a eu un setRollbackOnly(), ce commit() ne fera pas vraiment un commit() mais un rollback() en fait
      _nonXAGlobalUserTransaction.commit();
      if (_nonXAGlobalUserTransaction.getStatus() != Status.STATUS_NO_TRANSACTION)
      {
         fail("après commit(), le status de la transaction doit être STATUS_NO_TRANSACTION");
      }
   }

   /**
    * Test.
    * @throws SystemException
    *            e
    */
   @Test
   public void testDoubleNested () throws SystemException
   {
      assertTrue(_nonXAGlobalUserTransaction.getStatus() == Status.STATUS_NO_TRANSACTION,
               "aucune transaction démarrée");
      _nonXAGlobalUserTransaction.begin();
      if (_nonXAGlobalUserTransaction.getStatus() != Status.STATUS_ACTIVE)
      {
         fail("quand une transaction est démarrée, son status doit être STATUS_ACTIVE");
      }
      _nonXAGlobalUserTransaction.begin();
      _nonXAGlobalUserTransaction.begin();
      _nonXAGlobalUserTransaction.commit();
      if (_nonXAGlobalUserTransaction.getStatus() != Status.STATUS_ACTIVE)
      {
         fail("après commit() imbriqué, le status de la transaction doit être STATUS_ACTIVE");
      }
      _nonXAGlobalUserTransaction.rollback();
      if (_nonXAGlobalUserTransaction.getStatus() != Status.STATUS_MARKED_ROLLBACK)
      {
         fail("après rollback() imbriqué, le status de la transaction doit être STATUS_MARKED_ROLLBACK");
      }
      _nonXAGlobalUserTransaction.rollback();
      if (_nonXAGlobalUserTransaction.getStatus() != Status.STATUS_NO_TRANSACTION)
      {
         fail("après commit(), le status de la transaction doit être STATUS_NO_TRANSACTION");
      }
   }

   /**
    * Test.
    */
   @Test
   public void testTimeout ()
   {
      try
      {
         _nonXAGlobalUserTransaction.setTransactionTimeout(10);
      }
      catch (final UnsupportedOperationException v_ex)
      {
         // ok : méthode non supportée par cette implémentation

         try
         {
            new NonXALocalUserTransaction().setTransactionTimeout(10);
         }
         catch (final UnsupportedOperationException v_ex2)
         {
            // ok : méthode non supportée par cette implémentation
            return;
         }
      }
   }
}
