package com.dipp;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.Map;

import javax.ws.rs.QueryParam;

import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

@Controller
public class WelcomeController {

	private static final Logger LOGGER = getLogger(WelcomeController.class);
	private static final RestTemplate TEMPLATE = new RestTemplate();
	private static final String OAUTH_AUTHORIZE_REQUEST_PARAMS = "/oauth/authorize?response_type=code";
	private static final String OAUTH_TOKEN_REQUEST_QUERY_PARAMS = "/oauth/token?grant_type=authorization_code&code=%code%&redirect_uri=";
	private String authorizationServerUrl;
	private String authorizationUrl;

	@Value("${oauth.authorization_server_protocol}")
	private String authorizationServerProtocol;

	@Value("${oauth.authorization_server_host}")
	private String authorizationServerHost;

	@Value("${oauth.authorization_server_port}")
	private String authorizationServerPort;

	@Value("${oauth.redirect_uri}")
	private String redirectUri;

	@Value("${oauth.client_id}")
	private String clientId;

	@Value("${trustStore.location}")
	private String trustStoreLocation;
	
	@Value("${trustStore.password}")
	private String trustStorePassword;

	@Value("${keyStore.location}")
	private String keyStoreLocation;

	@Value("${keyStore.password}")
	private String keyStorePassword;

	@Value("${oauth.client_secret}")
	private String clientSecret;
	
	private static boolean initializedSSLProps = false;
	
	/**
	 * Default handler for welcme page
	 * @param model
	 * @return
	 */
	@RequestMapping("/")
	public String welcome(Map<String, Object> model) {
		authorizationServerUrl = new StringBuffer().append(authorizationServerProtocol).append("://")
				.append(authorizationServerHost).append(":").append(authorizationServerPort).toString();

		authorizationUrl = new StringBuffer().append(authorizationServerUrl).append(OAUTH_AUTHORIZE_REQUEST_PARAMS)
				.append("&client_id=").append(clientId).append("&redirect_uri=").append(redirectUri).toString();

		model.put("authorizationUrl", authorizationUrl);
		model.put("authorizationServerUrl", authorizationServerUrl);
		model.put("redirectUri", redirectUri);
		model.put("clientId", clientId);
		model.put("clientSecret", clientSecret);
		model.put("authorizationServerProtocol", authorizationServerProtocol);
		model.put("authorizationServerHost", authorizationServerHost);
		model.put("authorizationServerPort", authorizationServerPort);
		return "welcome";
	}

	/**
	 * Method for getting Token from Authorization Code
	 * 
	 * @param model
	 * @param code
	 * @return
	 */
	@RequestMapping("/authorization/code")
	public String getCode(Map<String, Object> model, @QueryParam(value = "code") String code) {
		LOGGER.info("Authorization code: {}", code);
		model.put("code", code);
		model.put("authorizationServerProtocol", authorizationServerProtocol);
		model.put("authorizationServerHost", authorizationServerHost);
		model.put("authorizationServerPort", authorizationServerPort);

		String tokenRequestUrl = parseTokenRequestUrl(code);
		LOGGER.info("postUrl {}", tokenRequestUrl);

		// Send the token post request
		LOGGER.info("tokenRequestUrl {}", tokenRequestUrl);
		initSSL();
		ResponseEntity<AccessToken> token = TEMPLATE.exchange(tokenRequestUrl, HttpMethod.POST, prepareHttpEntity(),
				AccessToken.class);

		model.put("access_token", token.getBody().toString());
		LOGGER.info("accessToken: {}", model.get("access_token"));
		return "token";
	}

	/**
	 * Method for initializing SSL props (if needed)
	 */
	private void initSSL() {
		if (!initializedSSLProps) {
			System.setProperty("javax.net.ssl.keyStoreType", "pkcs12");
			System.setProperty("javax.net.ssl.trustStoreType", "jks");
			//System.setProperty("javax.net.debug", "ssl,handshake");
			System.setProperty("javax.net.ssl.trustStore",
					trustStoreLocation);
			System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);
			//this part is for 2-way TLS
			if (keyStoreLocation!=null
					&& !keyStoreLocation.isEmpty()) {
				System.setProperty("javax.net.ssl.keyStore",
						keyStoreLocation);
				System.setProperty("javax.net.ssl.keyStorePassword", keyStorePassword);
			}
			initializedSSLProps = true;
		}
	}

	private String parseTokenRequestUrl(String code) {
		authorizationServerUrl = new StringBuffer().append(authorizationServerProtocol).append("://")
				.append(authorizationServerHost).append(":").append(authorizationServerPort).toString();

		final String tokenRequestUrl = authorizationServerUrl + OAUTH_TOKEN_REQUEST_QUERY_PARAMS + redirectUri;
		LOGGER.info("tokenRequestUrl {}", tokenRequestUrl);
		try {
			return tokenRequestUrl.replaceAll("%code%", code);
		} catch (Exception e) {
			LOGGER.error("Token replace error", e);
		}
		return tokenRequestUrl;
	}
	
	/**
	 * Method for preperation of request headers
	 * @return
	 */
	private HttpEntity<?> prepareHttpEntity() {
		HttpHeaders headers = new HttpHeaders();
		byte[] authenticationToken = Base64.encodeBase64((clientId + ":" + clientSecret).getBytes());
		headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE);
		headers.add("Authorization", "Basic " + new String(authenticationToken));
		return new HttpEntity<Void>(headers);
	}
}