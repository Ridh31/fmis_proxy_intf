package com.fmis.fmis_proxy_intf.fmis_proxy_intf.dto;

/**
 * Data Transfer Object for Partner information.
 * Encapsulates basic details such as ID, name, description, and identifier.
 */
public class PartnerDTO {

    private Long id;
    private String name;
    private String description;
    private String identifier;

    /**
     * Constructs a new PartnerDTO with provided details.
     *
     * @param id          the unique identifier of the partner
     * @param name        the name of the partner
     * @param description a brief description of the partner
     * @param identifier  a unique string used to identify the partner
     */
    public PartnerDTO(Long id, String name, String description, String identifier) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.identifier = identifier;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
}
