/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ws.rs;

import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.Response.Status.Family;
import jakarta.ws.rs.core.Response.StatusType;

/**
 * Let the developer to create a partial custom response status.
 *
 * @author MINARM.
 */
public class RsStatusType implements StatusType {
	/**
	 * The status code.
	 */
	private final int _code;

	/**
	 * The reason phrase associated with the status.
	 */
	private String _reason;

	/**
	 * The family of the status (200 / 400 / 500 / etc...)
	 */
	private Family _family;

	/**
	 * Contructor.
	 *
	 * @param p_code   : The status code.
	 * @param p_reason : The reason phrase associated to the status code.
	 */
	private RsStatusType(final int p_code, final String p_reason) {
		// Check if status code already exist in ws.rs.
		final Status v_status = Status.fromStatusCode(p_code);

		// Set the default code / reason.
		_code = p_code;
		_reason = p_reason;
		_family = Status.Family.OTHER;

		// If exist in ws.rs.
		if (null != v_status) {
			_family = v_status.getFamily();
		}

		// If exist and reason empty, replace by official reason.
		if (null != v_status && (null == _reason || _reason.isEmpty())) {
			_reason = v_status.getReasonPhrase();
		}
	}

	/**
	 * Check if the status code is not a custom one and if we can retreive the full
	 * "context" of the status from javax.
	 *
	 * @param p_code   : The status code.
	 * @param p_reason : The reason phrase associated to the status code.
	 * @return A new RsCustomStatusType.
	 */
	public static RsStatusType create(final int p_code, final String p_reason) {
		return new RsStatusType(p_code, p_reason);
	}

	/**
	 * Check if the status code is not a custom one and if we can retreive the full
	 * "context" of the status from javax.
	 *
	 * @param p_code : The status code.
	 * @return A new RsCustomStatusType.
	 */
	public static RsStatusType create(final int p_code) {
		return new RsStatusType(p_code, null);
	}

	/**
	 * Get the status code.
	 */
	@Override
	public int getStatusCode() {
		return _code;
	}

	/**
	 * Get the family for the status code.
	 */
	@Override
	public Family getFamily() {
		return _family;
	}

	/**
	 * Get the reason phrase for the status code.
	 */
	@Override
	public String getReasonPhrase() {
		return _reason;
	}
}
