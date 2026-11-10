/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.persistence.transaction;

import fr.spi4j.persistence.resource.jdbc.NonXAJdbcResourceManager_Abs;
import jakarta.transaction.Status;
import jakarta.transaction.SystemException;
import jakarta.transaction.UserTransaction;

/**
 * Instance de javax.transaction.UserTransaction supportant des connexions jdbc
 * multiples au mieux mais sans gestion de XA.
 * <p>
 * Contrairement à l'implémentation NonXAGlobalUserTransaction, cette
 * implémentation est la plus "conforme" à l'interface UserTransaction.
 * <p>
 * Elle ne gère pas de transactions locales par thread : il y a une seule
 * transaction pour le thread courant.
 * <p>
 * Et elle ne gère pas de transactions imbriquées : commit et rollback font
 * réellement le commit ou rollback et le close des connexions.
 *
 * @author MINARM
 */
public class NonXALocalUserTransaction implements UserTransaction {
	// initialisé à 0 par java
	private int _nestedCount;

	private int _status = Status.STATUS_ACTIVE;

	@Override
	public void begin() throws SystemException {
		// begin ne change pas le statut (notamment, on ne repasse pas en status active
		// après un setRollbackOnly())
	}

	@Override
	public void commit() throws SystemException {
		final boolean v_rollbackOnly = getStatus() == Status.STATUS_MARKED_ROLLBACK;
		// après le commit le status est NO_TRANSACTION, même si le commit fait une
		// erreur
		setStatus(Status.STATUS_NO_TRANSACTION);
		if (v_rollbackOnly) {
			NonXAJdbcResourceManager_Abs.rollbackAndCloseAllNonXAConnections();
		} else {
			NonXAJdbcResourceManager_Abs.commitAndCloseAllNonXAConnections();
		}
	}

	@Override
	public void rollback() throws SystemException {
		// après le rollback le status est NO_TRANSACTION, même si le rollback fait une
		// erreur
		setStatus(Status.STATUS_NO_TRANSACTION);
		NonXAJdbcResourceManager_Abs.rollbackAndCloseAllNonXAConnections();
	}

	@Override
	public void setRollbackOnly() throws SystemException {
		setStatus(Status.STATUS_MARKED_ROLLBACK);
	}

	@Override
	public int getStatus() throws SystemException {
		return _status;
	}

	/**
	 * Définit le status.
	 *
	 * @param p_status int
	 */
	private void setStatus(final int p_status) {
		this._status = p_status;
	}

	@Override
	public void setTransactionTimeout(final int p_seconds) {
		throw new UnsupportedOperationException("Cette méthode n'est pas supportée dans cette implémentation");
	}

	// ces méthodes non publiques sont utilisées dans la gestion interne de
	// NonXAGlobalUserTransaction pour les transactions imbriquées

	/**
	 * Est-ce que cette transaction est imbriquée ?
	 *
	 * @return boolean
	 */
	boolean isNestedTransaction() {
		return _nestedCount > 0;
	}

	/**
	 * Incrémente le compteur d'imbrication.
	 */
	void incrementNestedCount() {
		_nestedCount++;
	}

	/**
	 * Décrémente le compteur d'imbrication.
	 */
	void decrementNestedCount() {
		_nestedCount--;
	}
}
