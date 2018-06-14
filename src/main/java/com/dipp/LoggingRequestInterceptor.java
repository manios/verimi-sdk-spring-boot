package com.dipp;

import java.io.IOException;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestTemplate;

/**
 * A special implementation of {@link ClientHttpRequestInterceptor}, which intercepts all requests performed by
 * {@link RestTemplate} and writes the URL if log level is DEBUG.
 */
public class LoggingRequestInterceptor implements ClientHttpRequestInterceptor {

	private static final Logger logger = LoggerFactory
			.getLogger(LoggingRequestInterceptor.class);

	@Override
	public ClientHttpResponse intercept(final HttpRequest request, final byte[] body,
			final ClientHttpRequestExecution execution) throws IOException {

		// examine or log request and response
		this.log(request, body, null);

		// perform the request
		final ClientHttpResponse response = execution.execute(request, body);

		return response;
	}

	private void log(final HttpRequest request, final byte[] body,
			final ClientHttpResponse response) throws IOException {

		if (!logger.isDebugEnabled()) {
			return;
		}

		logger.debug("Perform request : {}", request.getURI()
				.toString());

		logger.debug("Method: {}", request.getMethod());
		logger.debug("=== REQUEST HEADERS ===");

		for (final Entry<String, String> i : request.getHeaders().toSingleValueMap().entrySet()) {
			logger.debug("{}: {}", i.getKey(), i.getValue());
		}
	}
}