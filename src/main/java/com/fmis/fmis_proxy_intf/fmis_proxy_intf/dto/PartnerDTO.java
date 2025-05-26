package com.fmis.fmis_proxy_intf.fmis_proxy_intf.dto;

public class PartnerDTO {
    private Long id;
    private String name;
    private String description;
    private String identifier;

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
