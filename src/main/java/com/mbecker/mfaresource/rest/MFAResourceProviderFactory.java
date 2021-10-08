package com.mbecker.mfaresource.rest;

import org.keycloak.Config.Scope;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.services.resource.RealmResourceProviderFactory;

public class MFAResourceProviderFactory implements RealmResourceProviderFactory {

    public final static String PROVIDER_ID = "mfa-provider";

    @Override
    public RealmResourceProvider create(KeycloakSession session) {
        return new MFAResourceProvider(session);
    }

    @Override
    public void init(Scope config) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void close() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String getId() {
        return MFAResourceProviderFactory.PROVIDER_ID;
    }
    
}
