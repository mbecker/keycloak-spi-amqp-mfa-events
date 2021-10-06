package com.mbecker;

import org.jboss.logging.Logger;
import org.keycloak.Config.Scope;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

public class AMQPEventListenerProviderFactory implements EventListenerProviderFactory {

    private static final Logger LOG = Logger.getLogger(AMQPEventListenerProviderFactory.class);
	private static final String ID = "MB_AMQP_EVENTLISTENER";

    private AMQPEventListenerProvider instance;

    private Utils utils;

    @Override
    public EventListenerProvider create(KeycloakSession session) {
        if (instance == null) {
			instance = new AMQPEventListenerProvider(this.utils);
		}
		return instance;
    }

    @Override
    public void init(Scope config) {
        this.utils = new Utils(config);
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        
    }

    @Override
    public void close() {
        LOG.info("close");
    }

    @Override
    public String getId() {
        return ID;
    }
    
}
