package com.mbecker;


import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.List;

/**
 * @author Mats Becker, mats.becker@gmail.com
 */
public class AuthenticationMobileFactory implements AuthenticatorFactory {

	private static final Logger LOG = Logger.getLogger(AuthenticationMobileFactory.class);

	private Utils utils;
	private AuthenticationMobile instance;

	@Override
	public String getId() {
		return AuthenticationMobile.PROVIDER_ID;
	}

	@Override
	public String getDisplayType() {
		return AuthenticationMobile.PROVIDER_ID;
	}

	@Override
	public String getHelpText() {
		return "Authenticates an OTP sent via SMS to the users mobile phone.";
	}

	@Override
	public String getReferenceCategory() {
		return "otp";
	}

	@Override
	public boolean isConfigurable() {
		return false;
	}

	@Override
	public boolean isUserSetupAllowed() {
		return false;
	}

	@Override
	public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
		return new AuthenticationExecutionModel.Requirement[] {
			AuthenticationExecutionModel.Requirement.REQUIRED,
			AuthenticationExecutionModel.Requirement.ALTERNATIVE,
			AuthenticationExecutionModel.Requirement.DISABLED,
		};
	}

	@Override
	public List<ProviderConfigProperty> getConfigProperties() {
		return null;
	}

	@Override
	public Authenticator create(KeycloakSession session) {
		LOG.debug("Create");
		if (instance == null) {
			return new AuthenticationMobile(this.utils);
		}
		return instance;
	}

	@Override
	public void init(Config.Scope config) {
		LOG.debug("Initializing");
		this.utils = new Utils(config);
	}

	@Override
	public void postInit(KeycloakSessionFactory factory) {
	}

	@Override
	public void close() {
	}

}
