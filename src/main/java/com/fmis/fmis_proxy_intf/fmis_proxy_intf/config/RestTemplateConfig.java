package com.fmis.fmis_proxy_intf.fmis_proxy_intf.config;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.util.TimeValue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    /**
     * Creates a customized RestTemplate that bypasses SSL validation.
     *
     * @return a configured RestTemplate instance.
     * @throws KeyStoreException if there is an issue with the key store.
     * @throws NoSuchAlgorithmException if the SSL algorithm is not available.
     * @throws KeyManagementException if there is an issue with key management.
     */
    @Bean
    public RestTemplate restTemplate() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {

        // SSLContext that trusts all certificates
        SSLContext sslContext = SSLContextBuilder.create()
                .loadTrustMaterial((X509Certificate[] chain, String authType) -> true)
                .build();

        // Create SSLConnectionSocketFactory for HTTPS with NoopHostnameVerifier
        SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);

        // Create registry of connection socket factories for HTTP and HTTPS
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", sslSocketFactory)
                .build();

        // Create connection manager with custom registry
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        connectionManager.setMaxTotal(100);
        connectionManager.setDefaultMaxPerRoute(20);
        connectionManager.setValidateAfterInactivity(TimeValue.ofSeconds(30));

        // Create HttpClient with custom connection manager
        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .evictIdleConnections(TimeValue.ofMinutes(5))
                .build();

        // Integrate HttpClient with RestTemplate
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);

        return new RestTemplate(factory);
    }
}
