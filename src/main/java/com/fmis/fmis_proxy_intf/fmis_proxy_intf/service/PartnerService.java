package com.fmis.fmis_proxy_intf.fmis_proxy_intf.service;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.Partner;

import java.util.Optional;

/**
 * Service interface for managing {@link Partner} entities.
 * Provides methods to create, retrieve, and check the existence of {@link Partner} entities based on different criteria.
 */
public interface PartnerService {

    /**
     * Creates and saves a new {@link Partner} entity.
     *
     * @param partner The {@link Partner} object to be created.
     * @return The created {@link Partner} entity.
     */
    Partner createPartner(Partner partner);

    /**
     * Retrieves a {@link Partner} by its unique ID.
     *
     * @param id The ID of the {@link Partner}.
     * @return An {@link Optional} containing the {@link Partner} if found, or empty if not found.
     */
    Optional<Partner> findById(Long id);

    /**
     * Retrieves a {@link Partner} by its unique code.
     *
     * @param code The unique code associated with the {@link Partner}.
     * @return An {@link Optional} containing the {@link Partner} if found, or empty if not found.
     */
    Optional<Partner> findByCode(String code);

    /**
     * Retrieves the ID of a {@link Partner} by its base64-encoded representation.
     *
     * @param base64 The base64-encoded string representing the {@link Partner}.
     * @return The ID of the {@link Partner} associated with the given base64 encoding.
     */
    Long findIdByBase64(String base64);

    /**
     * Retrieves the ID of a {@link Partner} by its RSA public key.
     *
     * @param rsaPublicKey The RSA public key associated with the {@link Partner}.
     * @return The ID of the {@link Partner} associated with the given RSA public key.
     */
    Long findIdByRsaPublicKey(String rsaPublicKey);

    /**
     * Checks whether a {@link Partner} exists based on its ID.
     *
     * @param id The ID of the {@link Partner}.
     * @return {@code true} if the {@link Partner} exists, otherwise {@code false}.
     */
    boolean existsById(Long id);
}
