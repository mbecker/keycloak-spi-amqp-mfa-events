package com.mbecker.jpa;

import org.keycloak.Config.Scope;
import org.keycloak.connections.jpa.entityprovider.JpaEntityProvider;
import org.keycloak.connections.jpa.entityprovider.JpaEntityProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class MobileTokenVerificationEntityProviderFactory implements JpaEntityProviderFactory {

	protected static final String ID = "mfaverification-provider";


    private MobileTokenVerificationEntityProvider instance;

    @Override
    public JpaEntityProvider create(KeycloakSession session) {
        if(this.instance == null) {
            this.instance = new MobileTokenVerificationEntityProvider();
        }
        return instance;
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
        return ID;
    }
    
}
