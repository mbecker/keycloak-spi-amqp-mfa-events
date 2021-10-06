package com.mbecker;

import org.jboss.logging.Logger;
import org.keycloak.Config.Scope;
import org.keycloak.authentication.RequiredActionFactory;
import org.keycloak.authentication.RequiredActionProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class RequiredActionMobileFactory implements RequiredActionFactory {

    private static Logger LOG = Logger.getLogger(RequiredActionFactory.class);
    
    private Utils utils;
    private RequiredActionMobile instance;

    @Override
    public String getDisplayText() {
        return RequiredActionMobile.PROVIDER_ID;
    }

    @Override
    public void close() {
    }

    @Override
    public RequiredActionProvider create(KeycloakSession session) {
        LOG.debug("Create");
        if (instance == null) {
			return new RequiredActionMobile(this.utils);
		}
		return instance;
    }

    @Override
    public String getId() {
        return RequiredActionMobile.PROVIDER_ID;
    }

    @Override
    public void init(Scope config) {
        LOG.debug("Initializing");
        this.utils = new Utils(config);
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        
    }

}
