package com.mbecker;

import org.jboss.logging.Logger;
import org.keycloak.Config.Scope;
import org.keycloak.authentication.RequiredActionFactory;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class RequiredActionMobileTokenFactory implements RequiredActionFactory {

    public static String PROVIDER_ID = "MB_MOBILE_TOKEN_REQUIREDACTION";
    private static Logger LOG = Logger.getLogger(RequiredActionFactory.class);
    private Utils utils;

    @Override
    public RequiredActionProvider create(KeycloakSession session) {
        LOG.info("Create");
        return new RequiredActionMobileToken(this.utils);
    }

    @Override
    public void init(Scope config) {
        LOG.info("Initializing");
        this.utils = new Utils(config);
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
        return RequiredActionMobileTokenFactory.PROVIDER_ID;
    }

    @Override
    public String getDisplayText() {
        return RequiredActionMobileTokenFactory.PROVIDER_ID;
    }
    
}
