package com.mbecker.gateway;

import com.mbecker.Utils;

import org.keycloak.models.KeycloakSession;

public class GatewayServiceFactory {

    // AMQP
    public static GatewayService get(Utils utils) {
        if (utils.getIsSimulation()) {
            return new SimulationService();
        }
        return new AMQPService(utils);
    }

    // Email
    public static GatewayService get(Utils utils, KeycloakSession session, Notification notification) {
        if (utils.getIsSimulation()) {
            return new SimulationService();
        }
        return new EmailService(session);
    }

}