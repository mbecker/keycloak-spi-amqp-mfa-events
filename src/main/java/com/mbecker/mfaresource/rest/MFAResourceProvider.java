package com.mbecker.mfaresource.rest;

import org.keycloak.models.KeycloakSession;
import org.keycloak.services.resource.RealmResourceProvider;

public class MFAResourceProvider implements RealmResourceProvider {

    private final KeycloakSession session;

    public MFAResourceProvider(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public void close() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Object getResource() {
        return new MFARestResource(this.session);
    }
    
}
