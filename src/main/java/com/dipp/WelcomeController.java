package com.dipp;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.Map;

import javax.ws.rs.QueryParam;

import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import com.dipp.service.VerimiBasketService;

@Controller
public class WelcomeController {

	private static final Logger LOGGER = getLogger(WelcomeController.class);

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

	@Autowired
	private VerimiBasketService verimiBasketService;

	@Autowired
	private RestTemplate restTemplate;

	private static boolean initializedSSLProps = false;

	/**
	 * Default handler for welcme page
	 *
	 * @param model
	 * @return
	 */
	@RequestMapping("/")
	public String welcome(final Map<String, Object> model) {
		this.authorizationServerUrl = new StringBuffer().append(this.authorizationServerProtocol).append("://")
				.append(this.authorizationServerHost).append(":").append(this.authorizationServerPort).toString();

		this.authorizationUrl = new StringBuffer().append(this.authorizationServerUrl)
				.append(OAUTH_AUTHORIZE_REQUEST_PARAMS)
				.append("&client_id=").append(this.clientId).append("&redirect_uri=").append(this.redirectUri)
				.toString();

		model.put("authorizationUrl", this.authorizationUrl);
		model.put("authorizationServerUrl", this.authorizationServerUrl);
		model.put("redirectUri", this.redirectUri);
		model.put("clientId", this.clientId);
		model.put("clientSecret", this.clientSecret);
		model.put("authorizationServerProtocol", this.authorizationServerProtocol);
		model.put("authorizationServerHost", this.authorizationServerHost);
		model.put("authorizationServerPort", this.authorizationServerPort);
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
	public String getCode(final Map<String, Object> model, @QueryParam(value = "code") final String code) {
		LOGGER.info("Authorization code: {}", code);
		model.put("code", code);
		model.put("authorizationServerProtocol", this.authorizationServerProtocol);
		model.put("authorizationServerHost", this.authorizationServerHost);
		model.put("authorizationServerPort", this.authorizationServerPort);

		final String tokenRequestUrl = this.parseTokenRequestUrl(code);
		LOGGER.info("postUrl {}", tokenRequestUrl);

		// Send the token post request
		LOGGER.info("tokenRequestUrl {}", tokenRequestUrl);
		// this.initSSL();
		final ResponseEntity<AccessToken> token = this.restTemplate.exchange(tokenRequestUrl, HttpMethod.POST,
				this.prepareHttpEntity(),
				AccessToken.class);

		model.put("access_token", token.getBody().toString());
		LOGGER.info("accessToken: {}", model.get("access_token"));

		LOGGER.info("Basket Response:{}", this.verimiBasketService.queryBaskets(token.getBody().getAccessToken()));

		return "token";
	}

	/**
	 * Method for initializing SSL props (if needed)
	 */
	private void initSSL() {
		if (!initializedSSLProps) {
			System.setProperty("javax.net.ssl.keyStoreType", "pkcs12");
			System.setProperty("javax.net.ssl.trustStoreType", "jks");
			System.setProperty("javax.net.debug", "ssl,handshake");
			System.setProperty("javax.net.ssl.trustStore",
					this.trustStoreLocation);
			System.setProperty("javax.net.ssl.trustStorePassword", this.trustStorePassword);
			// this part is for 2-way TLS
			if (this.keyStoreLocation != null
					&& !this.keyStoreLocation.isEmpty()) {
				System.setProperty("javax.net.ssl.keyStore",
						this.keyStoreLocation);
				System.setProperty("javax.net.ssl.keyStorePassword", this.keyStorePassword);
			}
			initializedSSLProps = true;
		}
	}

	private String parseTokenRequestUrl(final String code) {
		this.authorizationServerUrl = new StringBuffer().append(this.authorizationServerProtocol).append("://")
				.append(this.authorizationServerHost).append(":").append(this.authorizationServerPort).toString();

		final String tokenRequestUrl = this.authorizationServerUrl + OAUTH_TOKEN_REQUEST_QUERY_PARAMS
				+ this.redirectUri;
		LOGGER.info("tokenRequestUrl {}", tokenRequestUrl);
		try {
			return tokenRequestUrl.replaceAll("%code%", code);
		} catch (final Exception e) {
			LOGGER.error("Token replace error", e);
		}
		return tokenRequestUrl;
	}

	/**
	 * Method for preperation of request headers
	 *
	 * @return
	 */
	private HttpEntity<?> prepareHttpEntity() {
		final HttpHeaders headers = new HttpHeaders();
		final byte[] authenticationToken = Base64.encodeBase64((this.clientId + ":" + this.clientSecret).getBytes());
		headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE);
		headers.add("Authorization", "Basic " + new String(authenticationToken));
		return new HttpEntity<Void>(headers);
	}
}