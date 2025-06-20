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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service implementation for managing {@link Partner} entities.
 */
@Service
public class PartnerServiceImpl implements PartnerService {

    private final PartnerRepository partnerRepository;

    /**
     * Constructs a new {@code PartnerServiceImpl} with the specified repository.
     *
     * @param partnerRepository the repository used for partner data access
     */
    public PartnerServiceImpl(PartnerRepository partnerRepository) {
        this.partnerRepository = partnerRepository;
    }

    /**
     * Creates and saves a new {@link Partner} entity.
     *
     * @param partner the partner entity to be saved
     * @return the saved {@link Partner} entity
     */
    @Transactional
    @Override
    public Partner createPartner(Partner partner) {
        // If you plan to generate identifiers, uncomment and integrate below:
        // String nextIdentifier = generateNextIdentifier();
        // partner.setIdentifier(nextIdentifier);
        return partnerRepository.save(partner);
    }

    /**
     * Generates the next sequential identifier for a partner.
     *
     * @return a six-digit formatted identifier string
     */
    private String generateNextIdentifier() {
        String latestIdentifier = partnerRepository.findTopByOrderByIdentifierDesc();

        if (latestIdentifier == null || latestIdentifier.isEmpty()) {
            return "000001";
        }

        try {
            int currentNumber = Integer.parseInt(latestIdentifier);
            return String.format("%06d", ++currentNumber);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid identifier format in the database.", e);
        }
    }

    /**
     * Retrieves a {@link Partner} by its ID.
     *
     * @param id the partner ID
     * @return an {@link Optional} containing the partner if found, otherwise empty
     */
    @Override
    public Optional<Partner> findById(Long id) {
        return partnerRepository.findById(id);
    }

    /**
     * Retrieves a {@link Partner} by its name.
     *
     * @param name the partner name
     * @return an {@link Optional} containing the partner if found, otherwise empty
     */
    @Override
    public Optional<Partner> findByName(String name) {
        return partnerRepository.findByName(name);
    }

    /**
     * Retrieves a {@link Partner} by its unique identifier.
     *
     * @param identifier the partner's identifier
     * @return an {@link Optional} containing the partner if found, otherwise empty
     */
    @Override
    public Optional<Partner> findByIdentifier(String identifier) {
        return partnerRepository.findByIdentifier(identifier);
    }

    /**
     * Retrieves a partner by its system code.
     *
     * Delegates the lookup to the {@code partnerRepository}.
     *
     * @param systemCode the system code associated with the partner
     * @return an Optional containing the partner if found, otherwise empty
     */
    @Override
    public Optional<Partner> findBySystemCode(String systemCode) {
        return partnerRepository.findBySystemCode(systemCode);
    }

    /**
     * Retrieves a {@link Partner} by its unique code.
     *
     * @param code the partner's unique code
     * @return an {@link Optional} containing the partner if found, otherwise empty
     */
    @Override
    public Optional<Partner> findByCode(String code) {
        return partnerRepository.findByCode(code);
    }

    /**
     * Retrieves the ID of a {@link Partner} using its RSA public key.
     *
     * @param publicKey the partner's RSA public key
     * @return the ID of the matched partner
     * @throws ResourceNotFoundException if no matching partner is found
     */
    @Override
    public Long findIdByPublicKey(String publicKey) {
        return partnerRepository.findIdByPublicKey(publicKey)
                .map(Partner::getId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(ApiResponseConstants.ERROR_PARTNER_TOKEN_NOT_FOUND));
    }

    /**
     * Checks if a {@link Partner} exists by its ID.
     *
     * @param id the partner ID
     * @return {@code true} if the partner exists, otherwise {@code false}
     */
    @Override
    public boolean existsById(Long id) {
        return partnerRepository.existsById(id);
    }

    /**
     * Retrieves a paginated list of active, non-deleted partners.
     *
     * @param page the page number (zero-based)
     * @param size the number of records per page
     * @return a {@link Page} of {@link Partner} entities
     */
    @Override
    public Page<Partner> getAllPartners(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return partnerRepository.getAllPartners(pageable);
    }

    /**
     * Updates and saves an existing {@link Partner} entity.
     *
     * @param partner the partner entity with updated information
     * @return the updated {@link Partner} entity
     */
    @Transactional
    @Override
    public Partner updatePartner(Partner partner) {
        return partnerRepository.save(partner);
    }

    /**
     * Retrieves a paginated list of partners where {@code isBank = true} and {@code isOwn = false},
     * sorted by the {@code identifier} field in ascending order.
     *
     * @param page the page number to retrieve (0-based)
     * @param size the number of records per page
     * @return a {@link Page} of {@link Partner} entities matching the filter criteria
     */
    @Override
    public Page<Partner> getFilteredBankPartners(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("identifier").ascending());
        return partnerRepository.findByIsBankTrueAndIsOwnFalse(pageable);
    }
}