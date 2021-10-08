package com.mbecker.mfaresource.rest;

import org.keycloak.Config.Scope;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.services.resource.RealmResourceProviderFactory;

public class MFAResourceProviderFactory implements RealmResourceProviderFactory {

    public final static String PROVIDER_ID = "mfa-provider";

    private MFAResourceProvider mfaResourceProvider;

    @Override
    public RealmResourceProvider create(KeycloakSession session) {
        if (mfaResourceProvider == null) {
            mfaResourceProvider = new MFAResourceProvider(session);
        }
        return mfaResourceProvider;
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
