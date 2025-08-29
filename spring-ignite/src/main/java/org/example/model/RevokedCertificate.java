package org.example.model;

import java.time.Instant;

public class RevokedCertificate {
        private String serialNumber;
        private String issuer;
        private Instant revokedAt;

    public RevokedCertificate() {
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public Instant getRevokedAt() {
        return revokedAt;
    }

    public void setRevokedAt(Instant revokedAt) {
        this.revokedAt = revokedAt;
    }

    // getters/setters, equals/hashCode
    }
