package com.fmis.fmis_proxy_intf.fmis_proxy_intf.service;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.FMIS;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

/**
 * Service interface for managing FMIS (Financial Management Information System) data.
 * Provides methods to interact with FMIS entities and external FMIS integration.
 */
public interface FmisService {

    /**
     * Retrieves the FMIS entity URL by its ID.
     *
     * @param id The ID of the FMIS entity.
     * @return An Optional containing the FMIS entity if found, or empty if no FMIS entity exists with the provided ID.
     */
    Optional<FMIS> getFmisUrlById(Long id);

    /**
     * Sends XML data to the FMIS system using HTTP POST.
     *
     * @param fmisURL        The FMIS interface base URL.
     * @param fmisUsername   The FMIS username for basic authentication.
     * @param fmisPassword   The FMIS password for basic authentication.
     * @param xmlPayload     The XML payload to be sent.
     * @return The response from the FMIS system.
     */
    ResponseEntity<String> sendXmlToFmis(String fmisURL, String fmisUsername, String fmisPassword, String xmlPayload);

    /**
     * Retrieves XML data from the FMIS system using HTTP GET.
     *
     * @param fmisURL        The FMIS interface base URL.
     * @param fmisUsername   The FMIS username for basic authentication.
     * @param fmisPassword   The FMIS password for basic authentication.
     * @return The response from the FMIS system.
     */
    ResponseEntity<String> getXmlFromFmis(String fmisURL, String fmisUsername, String fmisPassword);

    /**
     * Retrieves a paginated list of FMIS configurations.
     *
     * @param page The page number (0-based index).
     * @param size The number of items per page.
     * @return A {@link Page} of {@link FMIS} entities, representing the configuration list.
     */
    Page<FMIS> getConfig(int page, int size);
}
