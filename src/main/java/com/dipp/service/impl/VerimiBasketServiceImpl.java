package com.dipp.service.impl;

import java.net.URI;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.dipp.service.VerimiBasketService;

@Service
public class VerimiBasketServiceImpl implements VerimiBasketService {

	private static final Logger logger = LoggerFactory.getLogger(VerimiBasketServiceImpl.class);

	private static final String OAUTH_QUERY_BASKETS = "/query/baskets";

	@Value("${oauth.authorization_server_protocol}")
	private String authorizationServerProtocol;

	@Value("${oauth.authorization_server_host}")
	private String authorizationServerHost;

	@Value("${oauth.authorization_server_port}")
	private String authorizationServerPort;

	@Autowired
	private RestTemplate restTemplate;

	@Override
	public String queryBaskets(final String accessToken) {

		final HttpEntity<?> httpEntity = this.prepareHttpEntity(accessToken);

		final String url = new StringBuffer().append(this.authorizationServerProtocol)
				.append("://")
				.append(this.authorizationServerHost).append(":").append(this.authorizationServerPort)
				.append(OAUTH_QUERY_BASKETS).toString();

		ResponseEntity<String> resp = null;
		try {
			resp = this.restTemplate.exchange(
					new URI(url), HttpMethod.GET,
					httpEntity, String.class);

		} catch (final RestClientException e) {

			logger.error("Failed to call:", e);
			return null;
		} catch (final URISyntaxException e) {

			logger.error("Failed to create URL:", e);
			return null;
		}

		return resp.getBody();
	}

	/**
	 * Method for preparation of request headers
	 *
	 * @return
	 */
	private HttpEntity<?> prepareHttpEntity(final String accessToken) {
		final HttpHeaders headers = new HttpHeaders();

		headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
		headers.add("Authorization", "Bearer " + new String(accessToken));
		return new HttpEntity<Void>(headers);
	}

}
