package com.mbecker.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "mfa_mobiletokenverfication")
@NamedQueries({ @NamedQuery(name = "findByRealm", query = "from MobileTokenVerification where realm_id = :realmId") })
public class MobileTokenVerification {
    
    @Id
    @Column(name = "CODE", nullable = false)
    private String code;

    @Column(name = "VALIDATED", nullable = false)
    private Boolean validated;

    @Column(name = "UUID", nullable = false)
    private String uuid;

    @Column(name = "TTL", nullable = false)
    private int ttl;

    @Column(name = "CREATEDAT", nullable = false)
    private int createdAt;

    @Column(name = "REALM_ID", nullable = false)
    private String realmId;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Boolean getValidated() {
        return validated;
    }

    public void setValidated(Boolean validated) {
        this.validated = validated;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    
    
    

}
