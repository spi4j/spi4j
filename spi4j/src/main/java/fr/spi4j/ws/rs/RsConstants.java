/**
 * (C) Copyright Ministere des Armees (France)
 *
 * Apache License 2.0
 */
package fr.spi4j.ws.rs;

/**
 * List of all constants for the REST framework in Spi4J.
 *
 * @author MINARM
 */
public final class RsConstants {
	/**
	 * GENERAL : If * do not check the content, but only presence of content.
	 */
	static final String c_joker = "*";

	/**
	 * KEYSTORE : The default type for the keystore (signing keys).
	 */
	static final String c_keystore_default_type = "JKS";

	/**
	 * GENERAL :
	 */
	static final String c_tls = "TLS";

	/**
	 * HEADER GENERAL : Default encoding charset for REST service.
	 */
	static final String c_charset_utf8 = "charset=utf-8";

	/**
	 * CONFIG : Suffix for the main configuration file (REST application).
	 */
	static final String c_conf_filter_properties_application_file = "Application.properties";

	/**
	 * CONFIG : Suffix for the tokens configuration file (REST application).
	 */
	static final String c_conf_filter_properties_tokens_file = "Tokens.properties";

	/**
	 * HEADER APPLICATION : The ID to find the APPLICATION credentials for
	 * authentication token (NOT PES).
	 */
	static final String c_auth_header_appli_credentials = "x-app-auth-credentials";

	/**
	 * HEADER APPLICATION : First expected string in APPLICATION credentials.
	 */
	static final String c_auth_header_basic = "basic";

	/**
	 * HEADER APPLICATION : Default key for basic auth in header.
	 */
	public static final String c_auth_header_authorization = "authorization";

	/**
	 * HEADER APPLICATION : Default key for cache control.
	 */
	static final String c_auth_header_cache_control = "cache-control";

	/**
	 * HEADER APPLICATION : Default key for content type.
	 */
	static final String c_auth_header_content_type = "content-type";

	/**
	 * HEADER APPLICATION : Default key for accept.
	 */
	static final String c_auth_header_accept = "accept";

	/**
	 * HEADER APPLICATION : First expected string for a bearer token.
	 */
	static final String c_auth_header_bearer = "bearer";

	/**
	 * HEADER APPLICATION : Separator character for the APPLICATION credentials.
	 */
	static final String c_auth_header_credentials_separator = ":";

	/**
	 * HEADER PAGED LIST : The total count of potential returned elements for a
	 * paged list.
	 */
	static final String c_list_header_total_count = "Resource-Count";

	/**
	 * HEADER PAGED LIST : The total cunt of pages for the paged list.
	 */
	static final String c_list_header_page_count = "Page-Count";

	/**
	 * HEADER PAGED LIST : The number of the current page for the paged list.
	 */
	static final String c_list_header_page_current = "Current-Page";

	/**
	 * HEADER PAGED LIST : The count of returned elements for the actual page of a
	 * paged list.
	 */
	static final String c_list_header_page_size = "Current-Page-Size";

	/**
	 * HEADER PES : The key to find the APPLICATION authentication token (DR).
	 */
	public static final String c_auth_header_pes_token = "filter.pes.header.token.key";

	/**
	 * HEADER PES : The property for mandatory header key for the PES (DR).
	 */
	public static final String c_auth_header_pes_secu = "filter.pes.header.secu.key";

	/**
	 * HEADER PES : The property for mandatory header key for the PES (DR).
	 */
	public static final String c_auth_header_pes_mention = "filter.pes.header.mention.key";

	/**
	 * HEADER PES : The property for mandatory header key for the PES (DR).
	 */
	public static final String c_auth_header_pes_constraint = "filter.pes.header.constraint.key";

	/**
	 * HEADER PES : The property for mandatory header key for the PES (DR).
	 */
	public static final String c_auth_header_pes_appli_name = "filter.pes.header.app.key";

	/**
	 * HEADER PES : The property for mandatory header key for the PES (DR).
	 */
	public static final String c_auth_header_pes_dlpp_name = "filter.pes.header.dlpp.key";

	/**
	 * OIDC : The EndPoint for the authentication server (/auth).
	 */
	static final String c_auth_oidc_server = "filter.oidc.server.auth";

	/**
	 * OIDC : The EndPoint for the authentication server (/token).
	 */
	static final String c_auth_oidc_server_token = "filter.oidc.server.token";

	/**
	 * OIDC : The EndPoint for the authentication server (/userInfo).
	 */
	static final String c_auth_oidc_server_user_info = "filter.oidc.server.userinfo";

	/**
	 * OIDC : The ID for the application in order to call the authentication server.
	 */
	static final String c_auth_oidc_client_id = "filter.oidc.client.id";

	/**
	 * TOKEN : The full path for signing keys resources (url or file or keystore...)
	 */
	static final String c_signing_keys_resources_path = "filter.signingkeys.resources.path";

	/**
	 * TOKEN : The file name and format used by spi4j for asymmetric private file.
	 */
	static final String c_signing_key_private_filename = "private_key.der";

	/**
	 * TOKEN : The file name and format used by spi4j for asymmetric public file.
	 */
	static final String c_signing_key_public_filename = "public_key.der";

	/**
	 * TOKEN : Charging mode for the signing keys.
	 */
	static final String c_signing_keys_load = "filter.signingkeys.load.mode";

	/**
	 * OIDC : The secret for the application in order to call the authentication
	 * server.
	 */
	static final String c_auth_oidc_client_secret = "filter.oidc.client.secret";

	/**
	 * PAGED LIST : The default page count for paged list.
	 */
	public static final int c_list_default_page_count = 50;

	/**
	 * CONFIG PES : The property for the application name registered in the PES.
	 */
	public static final String c_conf_pes_app_name = "filter.pes.header.app";

	/**
	 * CONFIG PES : The property for the PES specific header (to complete).
	 */
	public static final String c_conf_pes_header_secu = "filter.pes.header.secu";

	/**
	 * CONFIG PES : The property for the PES specific header (to complete).
	 */
	public static final String c_conf_pes_header_mention = "filter.pes.header.mention";

	/**
	 * CONFIG PES : The property for the PES specific header (to complete).
	 */
	public static final String c_conf_pes_header_constraint = "filter.pes.header.constraint";

	/**
	 * CONFIG : The property for the filter routing strategy.
	 */
	public static final String c_conf_filter_routing_strategy = "filter.routing.strategy";

	/**
	 * CONFIG : The property for the filter operating mode.
	 */
	public static final String c_conf_filter_operating_mode = "filter.mode.operating";

	/**
	 * CONFIG : The name of system var for the filter configuration file.
	 */
	public static final String c_conf_filter_properties_file = "_REST_CONFIG_FILES";

	/**
	 * HTTP OIDC : Default standard parameter for security in HTTP communication.
	 */
	public static final String c_param_oauth2_state = "state";

	/**
	 * HTTP OIDC : Internal id for a token (id, access, refresh or applicative).
	 */
	public static final String c_param_token_id = "token_id";

	/**
	 * HTTP OIDC : URI for redirection in callback operation.
	 */
	public static final String c_param_oauth2_redirect_uri = "redirect_uri";

	/**
	 * HTTP OIDC : Application ID (client) for the authentication server.
	 */
	public static final String c_param_oauth2_client_id = "client_id";

	/**
	 * HTTP OIDC : Application secret (client) for the authentication server.
	 */
	public static final String c_param_oauth2_client_secret = "client_secret";

	/**
	 * HTTP OIDC : Default standard parameter for oidc communication.
	 */
	public static final String c_param_oauth2_code = "code";

	/**
	 * HTTP OIDC : Default standard parameter for oidc communication.
	 */
	public static final String c_param_oauth2_response_type = "response_type";

	/**
	 * HTTP OIDC : Default standard parameter for oidc communication.
	 */
	public static final String c_param_oauth2_grant_type = "grant_type";

	/**
	 * HTTP OIDC : Default standard parameter for oidc communication.
	 */
	public static final String c_param_oauth2_scope = "scope";

	/**
	 * HTTP OIDC : Default standard parameter for security in HTTP communication.
	 */
	public static final String c_param_oidc_nonce = "nonce";

	/**
	 * HTTP PASSWORD : Default standard parameter for user name.
	 */
	public static final String c_param_passwd_username = "username";

	/**
	 * HTTP PASSWORD : Default standard parameter for password.
	 */
	public static final String c_param_passwd_passord = "password";

	/**
	 * TOKEN : Default parameter name for access token.
	 */
	public static final String c_param_access_token = "access_token";

	/**
	 * TOKEN : Default parameter name for refresh token.
	 */
	public static final String c_param_refresh_token = "refresh_token";

	/**
	 * JWKS : Default standard parameter in Json Web Key Set.
	 */
	static final String c_jwk_exponent = "e";

	/**
	 * JWKS : Default standard parameter in Json Web Key Set.
	 */
	static final String c_jwk_modulus = "n";

	/**
	 * JWKS : Default standard parameter in Json Web Key Set.
	 */
	static final String c_jwk_family_name = "kty";

	/**
	 * JWKS : Default standard parameter in Json Web Key Set.
	 */
	static final String c_token_key_id = "kid";

	/**
	 * JWKS : Default standard parameter in Json Web Key Set.
	 */
	static final String c_jwk_keys_arrays = "keys";

	/**
	 * TOKEN APPLICATION : Check the signature for the token (can bypass if
	 * necessary).
	 */
	static final String c_token_signature_check = "filter.token.signature.check";

	/**
	 * TOKEN APPLICATION : Default expiration delay (in minutes).
	 */
	static final String c_token_default_exp_nbmin = "15";

	/**
	 * TOKEN APPLICATION : Default expiration delay (in hour).
	 */
	static final String c_token_default_exp_nbhour = "5";

	/**
	 * TOKEN APPLICATION : Default delay to still get an access after expiration (in
	 * seconds).
	 */
	static final int c_token_default_allwd_nbsec = 0;

	/**
	 * TOKEN APPLICATION : Default delay to still decode after refresh acceptance
	 * (in seconds).
	 */
	static final int c_token_default_allwd_after_refresh_nbsec = 5180;

	/**
	 * TOKEN APPLICATION : The key for the token secret (private for symmetric and
	 * asymmetric) in configuration file.
	 */
	static final String c_token_signing_key_private = "filter.token.key.private";

	/**
	 * TOKEN APPLICATION : The key for the token secret (public for asymmetric) in
	 * configuration file.
	 */
	static final String c_token_signing_key_public = "filter.token.key.public";

	/**
	 * TOKEN APPLICATION : The key for the signature algorithm.
	 */
	static final String c_token_signature_algo = "filter.token.signature";

	/**
	 * TOKEN APPLICATION : The minimum size for RSA key (was 1024 before).
	 */
	static final int c_token_rsa_key_min_size = 2048;

	/**
	 * TOKEN APPLICATION : Default timeout for reading from auth server (oauth2 /
	 * oidc).
	 */
	static final int c_token_default_read_timeout = 10000;

	/**
	 * TOKEN APPLICATION : Default timeout for connecting to auth server (oauth2 /
	 * oidc).
	 */
	static final int c_token_default_connect_timeout = 15000;

	/**
	 * USER APPLICATION : For SPI4J security, default id for a temporary user.
	 */
	public static final String c_auth_user_id_default = "nonconnecte.front";

	/**
	 * PAGINATOR : Link for previous page.
	 */
	static final String c_prev_rel = "prev";

	/**
	 * PAGINATOR : Link for next page.
	 */
	static final String c_next_rel = "next";

	/**
	 * PAGINATOR : Link for the first page.
	 */
	static final String c_first_rel = "first";

	/**
	 * PAGINATOR : Link for the last page.
	 */
	static final String c_last_rel = "last";

	/**
	 * PAGINATOR : Parameter for paging storage in the query string.
	 */
	static final String c_page_token_param = "pageToken";

	/**
	 * PAGINATOR : Number of elements in a specific page.
	 */
	static final String c_page_size_param = "pageSize:";

	/**
	 * PAGINATOR : Cursor for the paged list.
	 */
	static final String c_page_cursor_param = "&pageCursor:";

	/**
	 * PAGINATOR : Offset for the paged list.
	 */
	static final String c_page_offset_param = "&pageOffset:";

}
