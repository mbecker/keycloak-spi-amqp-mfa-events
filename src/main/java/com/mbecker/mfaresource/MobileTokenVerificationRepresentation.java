package com.mbecker.mfaresource;

import com.mbecker.jpa.MobileTokenVerification;

public class MobileTokenVerificationRepresentation {
    private String code;
    private int ttl;
    private int createdAt;
    private String realmId;
    private String uuid;

    public MobileTokenVerificationRepresentation(MobileTokenVerification mobileTokenVerification) {
        this.code = mobileTokenVerification.getCode();
        this.ttl = mobileTokenVerification.getTtl();
        this.createdAt = mobileTokenVerification.getCreatedAt();
        this.realmId = mobileTokenVerification.getRealmId();
        this.uuid = mobileTokenVerification.getUuid();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getTtl() {
        return ttl;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }

    public int getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(int createdAt) {
        this.createdAt = createdAt;
    }

    public String getRealmId() {
        return realmId;
    }

    public void setRealmId(String realmId) {
        this.realmId = realmId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

}
