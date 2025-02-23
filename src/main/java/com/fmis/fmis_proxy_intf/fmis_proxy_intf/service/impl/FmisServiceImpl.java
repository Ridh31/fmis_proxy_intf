package com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.impl;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.FMIS;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.repository.FmisRepository;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.FmisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

/**
 * Implementation of the FmisService interface.
 * Handles both database interactions and external FMIS system integration.
 */
@Service
public class FmisServiceImpl implements FmisService {

    private final FmisRepository fmisRepository;
    private final RestTemplate restTemplate;

    @Autowired
    public FmisServiceImpl(FmisRepository fmisRepository) {
        this.fmisRepository = fmisRepository;
        this.restTemplate = new RestTemplate();
    }

    /**
     * Retrieves an FMIS entity by its ID.
     *
     * @param id The ID of the FMIS entity.
     * @return An Optional containing the FMIS entity if found, or empty if not.
     */
    @Override
    public Optional<FMIS> getFmisUrlById(Long id) {
        return fmisRepository.findById(id);
    }

    /**
     * Sends XML data to the FMIS system using HTTP POST.
     *
     * @param fmisURL        The FMIS interface base URL.
     * @param fmisUsername   The FMIS username for basic authentication.
     * @param fmisPassword   The FMIS password for basic authentication.
     * @param xmlPayload     The XML payload to be sent.
     * @return The response from the FMIS system.
     */
    @Override
    public ResponseEntity<String> sendXmlToFmis(String fmisURL, String fmisUsername, String fmisPassword, String xmlPayload) {
        try {
            restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(fmisUsername, fmisPassword));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_XML);

            HttpEntity<String> requestEntity = new HttpEntity<>(xmlPayload, headers);

            return restTemplate.exchange(
                    fmisURL,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal Server Error: " + e.getMessage());
        }
    }

    /**
     * Retrieves XML data from the FMIS system using HTTP GET.
     *
     * @param fmisURL        The FMIS interface base URL.
     * @param fmisUsername   The FMIS username for basic authentication.
     * @param fmisPassword   The FMIS password for basic authentication.
     * @return The response from the FMIS system.
     */
    @Override
    public ResponseEntity<String> getXmlFromFmis(String fmisURL, String fmisUsername, String fmisPassword) {
        try {
            restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(fmisUsername, fmisPassword));

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(java.util.Collections.singletonList(org.springframework.http.MediaType.APPLICATION_XML));

            HttpEntity<String> requestEntity = new HttpEntity<>(headers);

            return restTemplate.exchange(
                    fmisURL,
                    HttpMethod.GET,
                    requestEntity,
                    String.class
            );
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal Server Error: " + e.getMessage());
        }
    }
}