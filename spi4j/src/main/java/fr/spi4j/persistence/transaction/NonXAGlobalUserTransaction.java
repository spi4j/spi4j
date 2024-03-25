/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.persistence.transaction;

import jakarta.transaction.Status;
import jakarta.transaction.SystemException;
import jakarta.transaction.UserTransaction;

/**
 * Instance de javax.transaction.UserTransaction supportant des connexions jdbc
 * multiples au mieux mais sans gestion de XA.
 * <p>
 * Comme pour l'implémentation XA "UserTransactionImpl", une instance de cette
 * classe est partageable par plusieurs threads et des transactions locales par
 * thread sont gérées dans cette implémentation.
 * <p>
 * Et cette implémentation gère également des transactions imbriquées (par
 * exemple, begin / begin / commit / rollback en séquence) : la transaction
 * extérieure est seule maître.
 * <p>
 * Mais contrairement à l'implémentation XA "UserTransactionImpl", cette
 * implémentation ne gère pas son propre pool de resources et de connexions et
 * fait réellement les close() sur les connexions après les commit et rollback.
 *
 * @author MINARM
 */
public class NonXAGlobalUserTransaction implements UserTransaction {

	/**
	 * Attribut static car les transactions doivent être globales à la JVM pour un
	 * thread.
	 * <p>
	 * Par exemple, pour 2 instances de UserPersistence contenant 2 instances de
	 * ParamPersistence contenant 2 instances de NonXAGlobalUserTransaction, il ne
	 * doit y avoir qu'une et une seule transaction par thread (donc 1 seule
	 * instance de NonXALocalUserTransaction par thread).
	 */
	private static final ThreadLocal<NonXALocalUserTransaction> c_transactionThreadLocal = new ThreadLocal<>();

	/**
	 * @return la transaction du thread courant ou null s'il n'y en a pas
	 */
	private NonXALocalUserTransaction getCurrentTransactionIfExists() {
		return c_transactionThreadLocal.get();
	}

	/**
	 * @return la transaction du thread courant (non null)
	 * @throws SystemException Une SystemException est lancée s'il n'y a pas de
	 *                         transaction associée au thread courant (begin n'a pas
	 *                         été appelée)
	 */
	private NonXALocalUserTransaction getCurrentTransaction() throws SystemException {
		final NonXALocalUserTransaction v_currentTransaction = getCurrentTransactionIfExists();
		if (v_currentTransaction == null) {
			throw new SystemException(
					"Aucune transaction associé au thread courant. Avez-vous appelé la méthode begin pour démarrer la transaction ?");
		}
		return v_currentTransaction;
	}

	/**
	 * Définit la transaction du thread courant, qui peut être null s'il n'y en a
	 * plus
	 *
	 * @param p_currentTransaction Transaction courante ou null
	 */
	private void setCurrentTransaction(final NonXALocalUserTransaction p_currentTransaction) {
		if (p_currentTransaction == null) {
			c_transactionThreadLocal.remove();
		} else {
			c_transactionThreadLocal.set(p_currentTransaction);
		}
	}

	@Override
	public void begin() throws SystemException {
		NonXALocalUserTransaction v_currentTransaction = getCurrentTransactionIfExists();
		if (v_currentTransaction == null) {
			v_currentTransaction = new NonXALocalUserTransaction();
			setCurrentTransaction(v_currentTransaction);
		}
		v_currentTransaction.begin();
		v_currentTransaction.incrementNestedCount();
	}

	@Override
	public void commit() throws SystemException {
		final NonXALocalUserTransaction v_currentTransaction = getCurrentTransaction();
		v_currentTransaction.decrementNestedCount();

		if (!v_currentTransaction.isNestedTransaction()) {
			try {
				v_currentTransaction.commit();
			} finally {
				setCurrentTransaction(null);
			}
		}
	}

	@Override
	public void rollback() throws SystemException {
		final NonXALocalUserTransaction v_currentTransaction = getCurrentTransaction();
		v_currentTransaction.decrementNestedCount();

		if (!v_currentTransaction.isNestedTransaction()) {
			try {
				v_currentTransaction.rollback();
			} finally {
				setCurrentTransaction(null);
			}
		} else {
			v_currentTransaction.setRollbackOnly();
		}
	}

	@Override
	public void setRollbackOnly() throws SystemException {
		final NonXALocalUserTransaction v_currentTransaction = getCurrentTransaction();
		v_currentTransaction.setRollbackOnly();
	}

	@Override
	public int getStatus() throws SystemException {
		final NonXALocalUserTransaction v_currentTransaction = getCurrentTransactionIfExists();
		if (v_currentTransaction == null) {
			return Status.STATUS_NO_TRANSACTION;
		} else {
			return v_currentTransaction.getStatus();
		}
	}

	@Override
	public void setTransactionTimeout(final int p_seconds) {
		throw new UnsupportedOperationException("Cette méthode n'est pas supportée dans cette implémentation");
	}
}
