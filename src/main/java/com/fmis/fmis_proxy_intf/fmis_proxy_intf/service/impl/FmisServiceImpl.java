package com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.impl;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.FMIS;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.User;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.repository.FmisRepository;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.FmisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
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
    public FmisServiceImpl(FmisRepository fmisRepository, RestTemplate restTemplate) {
        this.fmisRepository = fmisRepository;
        this.restTemplate = restTemplate;
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
     * Sends XML data to the FMIS system via HTTP POST.
     *
     * @param fmisURL      The FMIS system URL.
     * @param fmisUsername The FMIS username for authentication.
     * @param fmisPassword The FMIS password for authentication.
     * @param xmlPayload   The XML payload to be sent.
     * @return The response from the FMIS system.
     */
    @Override
    public ResponseEntity<String> sendXmlToFmis(String fmisURL, String fmisUsername, String fmisPassword, String xmlPayload) {
        try {
            // Set Basic Authentication Interceptor
            restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(fmisUsername, fmisPassword));

            // Prepare headers and request entity
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_XML);

            HttpEntity<String> requestEntity = new HttpEntity<>(xmlPayload, headers);

            // Send the request and return response
            return restTemplate.exchange(fmisURL, HttpMethod.POST, requestEntity, String.class);

        } catch (HttpClientErrorException e) {
            // Handle specific client error exception
            return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());

        } catch (Exception e) {
            // Handle general exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error: " + e.getMessage());
        }
    }

    /**
     * Retrieves XML data from the FMIS system via HTTP GET.
     *
     * @param fmisURL      The FMIS system URL.
     * @param fmisUsername The FMIS username for authentication.
     * @param fmisPassword The FMIS password for authentication.
     * @return The response containing the XML data from the FMIS system.
     */
    @Override
    public ResponseEntity<String> getXmlFromFmis(String fmisURL, String fmisUsername, String fmisPassword) {
        try {
            // Set Basic Authentication Interceptor
            restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(fmisUsername, fmisPassword));

            // Prepare headers and request entity
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(java.util.Collections.singletonList(org.springframework.http.MediaType.APPLICATION_XML));

            HttpEntity<String> requestEntity = new HttpEntity<>(headers);

            // Send the request and return response
            return restTemplate.exchange(fmisURL, HttpMethod.GET, requestEntity, String.class);

        } catch (HttpClientErrorException e) {
            // Handle specific client error exception
            return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());

        } catch (Exception e) {
            // Handle general exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error: " + e.getMessage());
        }
    }

    /**
     * Retrieves a paginated list of FMIS configuration.
     *
     * @param page The page number (0-based index).
     * @param size The number of records per page.
     * @return A {@link Page} containing FMIS configuration entities.
     */
    @Override
    public Page<FMIS> getConfig(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return fmisRepository.getConfig(pageable);
    }
}