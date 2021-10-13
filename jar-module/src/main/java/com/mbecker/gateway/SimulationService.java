package com.mbecker.gateway;

import java.util.Map;

import com.google.gson.Gson;
import org.jboss.logging.Logger;
import org.keycloak.email.EmailException;
import org.keycloak.models.UserModel;

public class SimulationService implements GatewayService {

    private static final Logger LOG = Logger.getLogger(SimulationService.class);
    private Gson gson = new Gson();

    public SimulationService() {
        LOG.info("Initializing SimulationService");
    }

    @Override
    public void send(Notification notification, String routingKey) {
        LOG.warn(String.format("***** SIMULATION MODE ***** Would send SMS to %s with text: %s",
                notification.getReceiver(), notification.getMessage()));

    }

    @Override
    public void send(String notification, String routingKey) {
        LOG.warn(String.format("***** SIMULATION MODE ***** %s", notification));
    }

    @Override
    public void send(Object obj, String routingKey) {
        String json = this.gson.toJson(obj);
        this.send(json, routingKey);
    }

    @Override
    public void sendMail(Map<String, String> config, UserModel user, EmailTemplate emailTemplate)
            throws EmailException {
        LOG.infof("Sending mail:\nSubject: %s\nBody Text: %s\nHTML Body: %s", emailTemplate.getSubject(),
                emailTemplate.getTextBody(), emailTemplate.getHtmlBody());
    }

}
