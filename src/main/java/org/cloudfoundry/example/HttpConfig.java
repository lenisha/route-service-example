package org.cloudfoundry.example;


import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Configuration
public class HttpConfig {


    private Log logger = LogFactory.getLog(HttpConfig.class);

    @Bean
    public ClientHttpRequestFactory httpRequestFactory() {
        this.logger.debug("Setting Apache Commons factory with no SSL verification");

        CloseableHttpClient httpClient
                = HttpClients.custom()
                .setSSLHostnameVerifier(new NoopHostnameVerifier())
                .build();
        HttpComponentsClientHttpRequestFactory requestFactory
                = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);
        return requestFactory;
    }

    /*
     * Build restTemplate from HttpComponentsClientHttpRequestFactory
     * Rather than the default SimpleClientHttpRequestFactory
     */
    @Bean
    public RestOperations restTemplate(@Autowired ClientHttpRequestFactory factory) {
        logger.debug("Http Client Factory:" + factory.getClass().toString());
        RestTemplate template = new RestTemplate(factory);
        template.setErrorHandler(new NoErrorsResponseErrorHandler());
        return template;
    }


    private static final class NoErrorsResponseErrorHandler extends DefaultResponseErrorHandler {
        @Override
        public boolean hasError(ClientHttpResponse response) throws IOException {
            return false;
        }

    }
}