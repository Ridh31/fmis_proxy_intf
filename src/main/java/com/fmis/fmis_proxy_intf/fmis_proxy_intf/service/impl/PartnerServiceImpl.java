package com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.impl;

import com.fmis.fmis_proxy_intf.fmis_proxy_intf.constant.ApiResponseConstants;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.model.Partner;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.repository.PartnerRepository;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.service.PartnerService;
import com.fmis.fmis_proxy_intf.fmis_proxy_intf.util.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of the {@link PartnerService} interface.
 * Provides methods to manage {@link Partner} entities.
 */
@Service
public class PartnerServiceImpl implements PartnerService {

    private final PartnerRepository partnerRepository;

    /**
     * Constructs a new {@code PartnerServiceImpl} with the given repository.
     *
     * @param partnerRepository The repository for managing {@code Partner} entities.
     */
    public PartnerServiceImpl(PartnerRepository partnerRepository) {
        this.partnerRepository = partnerRepository;
    }

    /**
     * Creates and saves a new {@code Partner} entity.
     *
     * @param partner The {@code Partner} entity to be saved.
     * @return The saved {@code Partner} entity.
     */
    @Transactional
    @Override
    public Partner createPartner(Partner partner) {
        // Generate the next identifier code
        // String nextIdentifier = generateNextIdentifier();

        // Set the identifier in the partner object
        // partner.setIdentifier(nextIdentifier);

        // Save the partner entity
        return partnerRepository.save(partner);
    }

    /**
     * Generates the next identifier for the partner entity.
     *
     * @return The next identifier, formatted to 6 digits.
     */
    private String generateNextIdentifier() {

        // Query the latest identifier in the database, sorted in descending order
        String latestIdentifier = partnerRepository.findTopByOrderByIdentifierDesc();

        // If no identifier exists, start with "000001"
        if (latestIdentifier == null || latestIdentifier.isEmpty()) {
            return "000001";
        }

        // Increment the numeric part of the identifier
        int currentNumber;
        try {
            currentNumber = Integer.parseInt(latestIdentifier);
        } catch (NumberFormatException e) {
            // Handle the case where the identifier is not a valid number
            throw new IllegalArgumentException("Invalid identifier format in the database.");
        }

        currentNumber++;

        // Format to ensure the identifier is always 6 digits, padded with leading zeros
        return String.format("%06d", currentNumber);
    }

    /**
     * Finds a {@code Partner} by its ID.
     *
     * @param id The ID of the {@code Partner}.
     * @return An {@link Optional} containing the {@code Partner} if found, or empty if not found.
     */
    @Override
    public Optional<Partner> findById(Long id) {
        return partnerRepository.findById(id);
    }

    /**
     * Finds a {@code Partner} by its unique code.
     *
     * @param code The unique code associated with the {@code Partner}.
     * @return An {@link Optional} containing the {@code Partner} if found, or empty if not found.
     */
    @Override
    public Optional<Partner> findByCode(String code) {
        return partnerRepository.findByCode(code);
    }

    /**
     * Finds a {@code Partner} by its RSA public key.
     *
     * @param publicKey The RSA public key associated with the {@code Partner}.
     * @return The ID of the {@code Partner} if found.
     * @throws ResourceNotFoundException If the {@code Partner} is not found.
     */
    @Override
    public Long findIdByPublicKey(String publicKey) {
        return partnerRepository.findIdByPublicKey(publicKey)
                .map(Partner::getId)
                .orElseThrow(() -> new ResourceNotFoundException(ApiResponseConstants.ERROR_PARTNER_TOKEN_NOT_FOUND));
    }

    /**
     * Checks if a {@code Partner} exists by its ID.
     *
     * @param id The ID of the {@code Partner}.
     * @return {@code true} if the {@code Partner} exists, otherwise {@code false}.
     */
    @Override
    public boolean existsById(Long id) {
        return partnerRepository.existsById(id);
    }

    /**
     * Retrieves a paginated list of all partners.
     *
     * @param page The page number (starting from 0).
     * @param size The number of records per page.
     * @return A {@link Page} containing {@code Partner} entities.
     */
    @Override
    public Page<Partner> getAllPartners(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return partnerRepository.getAllPartners(pageable);
    }
}
