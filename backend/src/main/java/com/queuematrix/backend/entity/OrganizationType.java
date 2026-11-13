package com.queuematrix.backend.entity;

public enum OrganizationType {
    HOSPITAL("Hospital"),
    RESTAURANT("Restaurant"),
    BANK("Bank"),
    GOVERNMENT_OFFICE("Government Office"),
    UNIVERSITY("University"),
    OTHER("Other Service Organization");

    private final String displayName;

    OrganizationType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
