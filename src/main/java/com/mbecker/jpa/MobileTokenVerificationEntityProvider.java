package com.mbecker.jpa;

import java.util.Collections;
import java.util.List;

import org.keycloak.connections.jpa.entityprovider.JpaEntityProvider;

public class MobileTokenVerificationEntityProvider implements JpaEntityProvider {

    @Override
    public void close() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String getChangelogLocation() {
        return "META-INF/mfa_mobiletokenverification-changelog.xml";
    }

    @Override
    public List<Class<?>> getEntities() {
        return Collections.<Class<?>>singletonList(MobileTokenVerification.class);
    }

    @Override
    public String getFactoryId() {
        return MobileTokenVerificationEntityProviderFactory.ID;
    }
    
}
