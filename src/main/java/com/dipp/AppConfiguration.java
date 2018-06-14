package com.dipp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLContext;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(AppConfiguration.class);

	@Value("${keyStore.location}")
	private String keyStoreLocation;

	@Value("${keyStore.password}")
	private String keyStorePassword;

	/**
	 * A custom RestTemplate which sends a client certificate along with every request to Verimi API. This is useful for
	 * testing in UAT environment. Based on StackOverflow question <a href=
	 * "https://stackoverflow.com/questions/6994944/connect-to-a-https-site-with-a-given-p12-certificate">#6994944</a>.
	 *
	 * https://stackoverflow.com/questions/6340918/trust-store-vs-key-store-creating-with-keytool/6341566#6341566
	 * https://github.com/jonashackt/spring-boot-rest-clientcertificate
	 *
	 */
	@Bean
	public RestTemplate restTemplate(final RestTemplateBuilder builder) throws Exception {
		final char[] password = this.keyStorePassword.toCharArray();
		// System.setProperty("javax.net.debug", "ssl,handshake");

		final HttpClient client = this.createCustomClient();
		return builder
				.requestFactory(new HttpComponentsClientHttpRequestFactory(client))
				.additionalInterceptors(new LoggingRequestInterceptor())
				.build();
	}

	private HttpClient createCustomClient()
			throws KeyManagementException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException,
			CertificateException, IOException {
		// https://stackoverflow.com/questions/6994944/connect-to-a-https-site-with-a-given-p12-certificate

		final KeyStore keyStore = KeyStore.getInstance("PKCS12");
		final FileInputStream instream = new FileInputStream(new File(this.keyStoreLocation));
		try {
			keyStore.load(instream, this.keyStorePassword.toCharArray());
		} finally {
			instream.close();
		}

		// Trust own CA and all self-signed certs
		final SSLContext sslcontext = SSLContexts.custom()
				.loadKeyMaterial(keyStore, this.keyStorePassword.toCharArray())
				// .loadTrustMaterial(trustStore, new TrustSelfSignedStrategy())
				.build();
		// Allow TLSv1 protocol only
		final SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
				sslcontext,
				new NoopHostnameVerifier());

		final HttpClient httpclient = HttpClients.custom()
				// .setHostnameVerifier(SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER)
				.setSSLSocketFactory(sslsf)
				.build();

		return httpclient;
	}

}
