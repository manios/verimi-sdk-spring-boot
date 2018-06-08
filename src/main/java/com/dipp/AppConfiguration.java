package com.dipp;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;

import javax.net.ssl.SSLContext;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.ResourceUtils;
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
	 * "https://stackoverflow.com/questions/45713593/what-is-the-right-way-to-send-a-client-certificate-with-every-request-made-by-th">#45713593</a>.
	 *
	 * @param builder
	 * @return
	 * @throws Exception
	 */
	@Bean
	public RestTemplate restTemplate(final RestTemplateBuilder builder) throws Exception {
		final char[] password = this.keyStorePassword.toCharArray();

		final SSLContext sslContext = SSLContextBuilder.create()
				// .loadKeyMaterial(this.keyStore("classpath:cert.jks", password), password)
				.loadKeyMaterial(this.readStore(), password)
				.loadTrustMaterial(null, new TrustSelfSignedStrategy()).build();

		final HttpClient client = HttpClients.custom().setSSLContext(sslContext).build();
		return builder
				.requestFactory(new HttpComponentsClientHttpRequestFactory(client))
				.build();
	}

	private KeyStore keyStore(final String file, final char[] password) throws Exception {
		final KeyStore keyStore = KeyStore.getInstance("PKCS12");
		final File key = ResourceUtils.getFile(file);
		try (InputStream in = new FileInputStream(key)) {
			keyStore.load(in, password);
		}
		return keyStore;
	}

	private KeyStore readStore() {
		try {
			final KeyStore keyStore = KeyStore.getInstance("PKCS12");
			keyStore.load(this.getClass().getClassLoader().getResourceAsStream(this.keyStoreLocation),
					this.keyStorePassword.toCharArray());
			return keyStore;
		} catch (final Exception e) {
			logger.error("Failed to load PKCS12 keystore from : " + this.keyStoreLocation, e);
		}
		return null;
	}

}
